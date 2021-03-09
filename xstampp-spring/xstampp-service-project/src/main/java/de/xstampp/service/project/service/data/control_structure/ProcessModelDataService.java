package de.xstampp.service.project.service.data.control_structure;

import de.xstampp.common.service.SecurityService;
import de.xstampp.service.project.data.dto.control_structure.ProcessModelRequestDTO;
import de.xstampp.service.project.data.entity.ProcessModelProcessVariableLink;
import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.User;
import de.xstampp.service.project.data.entity.control_structure.Controller;
import de.xstampp.service.project.data.entity.control_structure.ProcessModel;
import de.xstampp.service.project.service.dao.control_structure.iface.IControllerDAO;
import de.xstampp.service.project.service.dao.control_structure.iface.IProcessModelDAO;
import de.xstampp.service.project.service.dao.iface.ILastIdDAO;
import de.xstampp.service.project.service.dao.iface.IProcessModelProcessVariableLinkDAO;
import de.xstampp.service.project.service.dao.iface.IUserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProcessModelDataService {

    @Autowired
    SecurityService securityService;

    @Autowired
    ILastIdDAO lastIdDAO;

    @Autowired
    IProcessModelDAO processModelDAO;

    @Autowired
    IProcessModelProcessVariableLinkDAO processModelProcessVariableLinkDAO;

    @Autowired
    IProcessModelProcessVariableLinkDAO linkedVariablesData;

    @Autowired
    IUserDAO userDAO;

    @Autowired
    IControllerDAO controllerDAO;

    /**
     * supportive function to persist ProcessModel to Database
     *
     * @param key                    DependantKey
     * @param processModel           target ProcessModel
     * @param processModelRequestDTO ProcessModel with most recent Data
     * @return ProcessModel Object with most recent Data
     */
    private ProcessModel updateDB(ProjectDependentKey key, ProcessModel processModel, ProcessModelRequestDTO processModelRequestDTO){
        processModel.setId(key);
        processModel.setControllerId(processModelRequestDTO.getControllerId());
        processModel.setName(processModelRequestDTO.getName());
        processModel.setDescription(processModelRequestDTO.getDescription());
        processModel.setState(processModelRequestDTO.getState());

        processModel.setLastEditNow(securityService.getContext().getUserId());
        userDAO.makePersistent(new User(securityService.getContext().getUserId(), securityService.getContext().getDisplayName()));
        processModel.setLockExpirationTime(Timestamp.from(Instant.now()));

        ProcessModel result = processModelDAO.makePersistent(processModel);

        return result;
    }

    /**
     * saves a new ProcessModel to the Database
     *
     * @param projectId              Project ProcessModel belongs to
     * @param processModelRequestDTO new ProcessModel
     * @return saved ProcessModel
     */
    public ProcessModel createProcessModel(UUID projectId, ProcessModelRequestDTO processModelRequestDTO) {

        ProjectDependentKey key = new ProjectDependentKey(projectId, lastIdDAO.getNewIdForEntity(projectId, ProcessModel.class));
        ProcessModel processModel = new ProcessModel();

        return updateDB(key, processModel, processModelRequestDTO);
    }

    /**
     * alters ProcessModel Data in Database
     *
     * @param projectId              Project ProcessModel belongs to
     * @param processModelId         ProcessModel to be changed
     * @param processModelRequestDTO ProcessModelEntity containing newest changes
     * @return altered ProcessModel
     */
    public ProcessModel alterProcessModel(UUID projectId, int processModelId, ProcessModelRequestDTO processModelRequestDTO) {
        ProjectDependentKey key = new ProjectDependentKey(projectId, processModelId);
        ProcessModel processModel = processModelDAO.findById(key, false);

        if (processModel != null) {

            return updateDB(key, processModel, processModelRequestDTO);
        }
        return null;
    }

    /**
     * deletes a ProcessModel in the Database
     *
     * @param projectId      Project ProcessModel belongs to
     * @param processModelId ProcessModel to be deleted
     * @return success of deletion
     */
    public boolean deleteProcessModel(UUID projectId, int processModelId) {
        ProjectDependentKey key = new ProjectDependentKey(projectId, processModelId);
        ProcessModel processModel = processModelDAO.findById(key, false);

        if (processModel != null) {
            List<ProcessModelProcessVariableLink> links = linkedVariablesData.getAllLinksByProcessModel(projectId, processModelId);
            for (ProcessModelProcessVariableLink link : links) {
                linkedVariablesData.deleteLink(projectId, link.getProcessModelId(), link.getProcessVariableId());
            }
            processModelDAO.makeTransient(processModel);
            return true;
        }
        return false;
    }

    /**
     * returns a List of all ProcessModels for a given Controller ID
     *
     * @param projectId    Project ProcessModels belong to
     * @param controllerId Controller ProcessModels belong to
     * @return all ProcessModels that match the given IDs
     */
    public List<ProcessModel> getProcessModelsByControllerId (UUID projectId, int controllerId){
        List<ProcessModel> resultList = processModelDAO.getAllProcessModelsByControllerID(projectId,controllerId);
        if (resultList == null)
            return Collections.emptyList();
        return resultList;
    }

    /**
     * searches Database vor a given ID and returns a ProcessModel
     *
     * @param projectId      Project ProcessModel belongs to
     * @param processModelId ProcessModel ID
     * @return ProcessModel containing given ID
     */
    public ProcessModel getProcessModel(UUID projectId, int processModelId) {
        return processModelDAO.getById(projectId, processModelId);
    }

    /**
     * searches Database for all unlinked process models
     *
     * @param projectId project, process models belong to
     * @return list of all unlinked process models
     */
    public List<ProcessModel> getUnlinkedProcessModels(UUID projectId) {
        List<ProcessModel> responseList = new ArrayList<>();
        List<ProcessModel> allModels = processModelDAO.getAllProcessModels(projectId);

        List<Integer> searchedIds = new ArrayList<>();
        List<Integer> notFoundIds = new ArrayList<>();

        for (ProcessModel model : allModels) {

            if (!searchedIds.contains(model.getControllerId())) {
                Controller controller = controllerDAO.findById(new ProjectDependentKey(projectId, model.getControllerId()), false);
                searchedIds.add(model.getControllerId());
                if (controller == null) {
                    notFoundIds.add(model.getControllerId());
                    responseList.add(model);
                }

            } else if (notFoundIds.contains(model.getControllerId())) {
                responseList.add(model);
            }
        }
        return responseList;
    }
}
