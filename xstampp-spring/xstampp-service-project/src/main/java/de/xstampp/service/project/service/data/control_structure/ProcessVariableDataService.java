package de.xstampp.service.project.service.data.control_structure;

import de.xstampp.common.service.SecurityService;
import de.xstampp.service.project.data.dto.control_structure.ProcessVariableRequestDTO;
import de.xstampp.service.project.data.entity.*;
import de.xstampp.service.project.data.entity.control_structure.Controller;
import de.xstampp.service.project.data.entity.control_structure.ProcessVariable;
import de.xstampp.service.project.service.dao.control_structure.iface.IArrowDAO;
import de.xstampp.service.project.service.dao.control_structure.iface.IBoxDAO;
import de.xstampp.service.project.service.dao.control_structure.iface.IControllerDAO;
import de.xstampp.service.project.service.dao.iface.*;
import de.xstampp.service.project.service.dao.control_structure.iface.IProcessVariableDAO;
import de.xstampp.service.project.service.dao.iface.IDiscreteProcessVariableValueDAO;
import de.xstampp.service.project.service.dao.iface.ILastIdDAO;
import de.xstampp.service.project.service.dao.iface.IProcessModelProcessVariableLinkDAO;
import de.xstampp.service.project.service.dao.iface.IProcessVariableResponsibilityLinkDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Service
@Transactional
public class ProcessVariableDataService {

    private static String DISCRETE_PROCESS_VARIABLE = "DISCREET";

    private SecurityService securityService;

    private ILastIdDAO lastIdDAO;
    private IProcessModelProcessVariableLinkDAO processModelDataLinkDAO;
    private IProcessVariableResponsibilityLinkDAO responsibilityDataLinkDAO;

    private IBoxDAO boxData;
    private IArrowDAO arrowData;
    private IControllerDAO controllerData;
    private IProcessVariableDAO processVariableData;
    private IDiscreteProcessVariableValueDAO discreteData;

    @Autowired
    public ProcessVariableDataService(SecurityService securityService, ILastIdDAO lastIdDAO,
                                      IProcessModelProcessVariableLinkDAO processModelDataLinkDAO,
                                      IProcessVariableResponsibilityLinkDAO responsibilityDataLinkDAO,
                                      IProcessVariableDAO processVariableData,
                                      IDiscreteProcessVariableValueDAO discreteData, IArrowDAO arrowData,
                                      IBoxDAO boxData, IControllerDAO controllerData) {
        this.securityService = securityService;
        this.lastIdDAO = lastIdDAO;
        this.processModelDataLinkDAO = processModelDataLinkDAO;
        this.responsibilityDataLinkDAO = responsibilityDataLinkDAO;
        this.processVariableData = processVariableData;
        this.discreteData = discreteData;
        this.arrowData = arrowData;
        this.boxData = boxData;
        this.controllerData = controllerData;
    }

    @Autowired
    IUserDAO userDAO;

    /**
     * retrieves all possible discrete variable values from the Database
     *
     * @param projectId         Project ID
     * @param processVariableId Process variable ID
     * @return String-Array of all possible values
     */
    private String[] getAllDiscreteVariableValues(UUID projectId, int processVariableId) {

        List<DiscreteProcessVariableValue> allEntries = discreteData.getAllValuesById(projectId, processVariableId);

        String[] allValues = new String[allEntries.size()];
        for (int i = 0; i < allEntries.size(); i++) {
            allValues[i] = allEntries.get(i).getVariableValue();
        }
        return allValues;
    }

    /**
     * retrives the startingpoint of an arrow
     *
     * @param projectId project box and arrow belong to
     * @param arrowId   arrow ID
     * @return Box Entity
     */
    private Box getSource(UUID projectId, String arrowId) {
        Arrow arrow = arrowData.getArrowById(projectId, arrowId);
        if (arrow != null) {
            return boxData.getSingleBoxById(projectId, arrow.getSource());
        }
        return null;

    }

    /**
     * supportive Method to get an arrow ID
     *
     * @param projectId    project arrow Belongs to
     * @param source       source of the Arrow
     * @param controllerId controller Arrow points to
     * @return arrow ID
     */
    private String getArrowId(UUID projectId, Box source, int controllerId) {

        Controller controller = controllerData.findById(new ProjectDependentKey(projectId, controllerId), false);
        return arrowData.getArrowBySourceAndDestination(projectId, source.getId(), controller.getBoxId()).getId();
    }

    /**
     * retrieves all Responsibilities linked to a process variable from the Database
     *
     * @param processVariable process variable, responsibilities relate to
     * @return array of all responsibility IDs linked to the process variable
     */
    private Integer[] getResponsibilityIdsForProcessVariable(ProcessVariable processVariable) {

        List<ProcessVariableResponsibilityLink> respList = responsibilityDataLinkDAO.getLinksByProcessVariableId(processVariable.getId().getProjectId(), processVariable.getId().getId());
        Integer[] respArray = new Integer[respList.size()];
        for (int i = 0; i < respList.size(); i++) {
            respArray[i] = respList.get(i).getResponsibilityId();
        }

        return respArray;
    }

    /**
     * creates the Initial response DTO for a process variable without  values and links
     *
     * @param processVariable process variable DTO is to be created from
     * @return responseDTO
     */
    private ProcessVariableRequestDTO createInitialProcessVariableDTO(ProcessVariable processVariable) {

        ProcessVariableRequestDTO responseDTO = new ProcessVariableRequestDTO();
        //prepare response Element
        responseDTO.setId(processVariable.getId().getId());
        responseDTO.setName(processVariable.getName());
        responseDTO.setProjectId(processVariable.getId().getProjectId().toString());
        responseDTO.setVariable_type(processVariable.getVariable_type());
        responseDTO.setSource(getSource(processVariable.getId().getProjectId(), processVariable.getArrowId()));
        responseDTO.setDescription(processVariable.getDescription());
        responseDTO.setState(processVariable.getState());

        //get last editor and time
        responseDTO.setLast_edited(processVariable.getLastEdited());
        responseDTO.setLast_editor_id(processVariable.getLastEditorId());


        //gets all possible Values for discrete Variables
        if (processVariable.getVariable_type().equals(DISCRETE_PROCESS_VARIABLE)) {

            responseDTO.setValueStates(
                    getAllDiscreteVariableValues(processVariable.getId().getProjectId(), processVariable.getId().getId()));
        }
        return responseDTO;
    }

    /**
     * supportive Method to retrieve all the Values for a List of ProcessVariables for a specific ProcessMOdel from the Database
     *
     * @param processVariableList List with ProcessVariables
     * @param projectId           Project everything belongs to
     * @return a List of ProcessVariables with Variablevalues
     */
    private List<ProcessVariableRequestDTO> getProcessVariableValues(List<ProcessVariable> processVariableList, UUID projectId) {
        List<ProcessVariableRequestDTO> responseList = new ArrayList<>();

        if (!processVariableList.isEmpty()) {
            //get ProcessVariableValues
            for (ProcessVariable processVariable : processVariableList) {

                List<ProcessModelProcessVariableLink> linkList = processModelDataLinkDAO.getLinksByProcessVariableId(projectId, processVariable.getId().getId());

                Integer[] respArray = getResponsibilityIdsForProcessVariable(processVariable);

                Integer[] pmIDList = new Integer[linkList.size()];
                for (int i = 0; i < linkList.size(); i++) {
                    pmIDList[i] = linkList.get(i).getProcessModelId();
                }

                for (ProcessModelProcessVariableLink link : linkList) {

                    ProcessVariableRequestDTO response = createInitialProcessVariableDTO(processVariable);

                    //add links and current value
                    response.setVariable_value(link.getProcessVariableValue());
                    response.setCurrentProcessModel(link.getProcessModelId());
                    response.setProcess_models(pmIDList);
                    response.setResponsibilityIds(respArray);

                    responseList.add(response);
                }
            }

        }

        return responseList;
    }

    /**
     * supportive Method to retrieve all the Values for a List of ProcessVariables for a specific ProcessModel from the Database filtered by a ProcessModel ID
     *
     * @param processVariableList List with ProcessVariables
     * @param projectId           Project everything belongs to
     * @return a List of ProcessVariables with Variablevalues
     */
    private List<ProcessVariableRequestDTO> getProcessVariableValues(List<ProcessVariable> processVariableList, UUID projectId, int processModelId) {
        List<ProcessVariableRequestDTO> responseList = new ArrayList<>();

        if (!processVariableList.isEmpty()) {
            //get ProcessVariableValues
            for (ProcessVariable processVariable : processVariableList) {

                List<ProcessModelProcessVariableLink> linkList = processModelDataLinkDAO.getLinksByProcessVariableId(projectId, processVariable.getId().getId());

                ProcessModelProcessVariableLink link = processModelDataLinkDAO.getLink(projectId, processModelId, processVariable.getId().getId());

                Integer[] pmIDList = new Integer[linkList.size()];
                for (int i = 0; i < linkList.size(); i++) {
                    pmIDList[i] = linkList.get(i).getProcessModelId();
                }

                ProcessVariableRequestDTO response = createInitialProcessVariableDTO(processVariable);

                //add links and current value
                response.setCurrentProcessModel(processModelId);
                response.setVariable_value(link.getProcessVariableValue());
                response.setProcess_models(pmIDList);
                response.setResponsibilityIds(getResponsibilityIdsForProcessVariable(processVariable));

                responseList.add(response);
            }

        }

        return responseList;
    }

    /**
     * creates new ProcessVariable Entry with Data provided in Database
     *
     * @param projectId          project ProcessVariable belongs to
     * @param processVariableDTO information for new ProcessVariable
     * @return saved result
     */
    public List<ProcessVariableRequestDTO> createProcessVariable(UUID projectId, int controllerId, ProcessVariableRequestDTO processVariableDTO) {
        ProjectDependentKey key = new ProjectDependentKey(projectId, lastIdDAO.getNewIdForEntity(projectId, ProcessVariable.class));

        //ProcessVariable Object to be made persistent
        ProcessVariable processVariable = new ProcessVariable();
        processVariable.setId(key);
        processVariable.setName(processVariableDTO.getName());
        processVariable.setDescription(processVariableDTO.getDescription());
        processVariable.setState(processVariableDTO.getState());
        processVariable.setArrowId(getArrowId(projectId, processVariableDTO.getSource(), controllerId));
        processVariable.setVariable_type(processVariableDTO.getVariable_type());
        processVariable.setLastEditNow(securityService.getContext().getUserId());
        userDAO.makePersistent(new User(securityService.getContext().getUserId(), securityService.getContext().getDisplayName()));

        ProcessVariable result = processVariableData.makePersistent(processVariable);

        Integer[] modelIdList = processVariableDTO.getProcess_models();

        //create Link Between ProcessVariable and ProcessModel
        for (Integer processModelId : modelIdList) {
            ProcessModelProcessVariableLink resultLink = processModelDataLinkDAO.createLink(projectId, processModelId, key.getId(), processVariableDTO.getVariable_value());
        }

        //create Links between process variable and responsibility
        if (processVariableDTO.getResponsibilityIds() != null) {
            for (Integer respId : processVariableDTO.getResponsibilityIds()) {
                responsibilityDataLinkDAO.createLink(projectId, processVariable.getId().getId(), respId);
            }
        }

        if (processVariableDTO.getVariable_type().equals(DISCRETE_PROCESS_VARIABLE)) {

            if (processVariableDTO.getValueStates().length == 0) {
                discreteData.createNewValue(projectId, key.getId(), processVariableDTO.getVariable_value());
            } else if (processVariableDTO.getValueStates().length == 1) {
                discreteData.createNewValue(projectId, key.getId(), processVariableDTO.getValueStates()[0]);
            } else {

                for (String state : processVariableDTO.getValueStates()) {
                    discreteData.createNewValue(projectId, key.getId(), state);
                }
            }
        }
        List<ProcessVariableRequestDTO> resultList = new ArrayList<>();
        resultList.addAll(getProcessVariableValues(new ArrayList<>(Arrays.asList(result)), projectId));

        return resultList;
    }

    /**
     * Alters Values of an existing ProcessVariable in the Database
     *
     * @param projectId          project variable belongs to
     * @param processVariableId  Variable ID
     * @param processVariableDTO Data Transfer Object containing all the Data for changes
     * @return updated ProcessVariable
     */
    public List<ProcessVariableRequestDTO> alterProcessVariable(UUID projectId, int controllerId, int processModelId, int processVariableId, ProcessVariableRequestDTO processVariableDTO) {
        ProcessVariable processVariable = processVariableData.findById(new ProjectDependentKey(projectId, processVariableId), false);

        if (processVariable != null) {

            //update ProcessVariable main table
            processVariable.setName(processVariableDTO.getName());
            processVariable.setDescription(processVariableDTO.getDescription());
            processVariable.setState(processVariableDTO.getState());
            processVariable.setArrowId(getArrowId(projectId, processVariableDTO.getSource(), controllerId));
            processVariable.setVariable_type(processVariableDTO.getVariable_type());
            processVariable.setLastEditNow(securityService.getContext().getUserId());
            userDAO.makePersistent(new User(securityService.getContext().getUserId(), securityService.getContext().getDisplayName()));
            processVariable.setLockExpirationTime(Timestamp.from(Instant.now()));

            ProcessVariable result = processVariableData.makePersistent(processVariable);

            //updates ProcessVariable value in Link Between ProcessVariable and ProcessModel
            ProcessModelProcessVariableLink resultLink = processModelDataLinkDAO.updateLink(projectId, processModelId, processVariableId, processVariableDTO.getVariable_value());

            //check if ProcessModels assigned changed
            List<ProcessModelProcessVariableLink> linkList = processModelDataLinkDAO.getLinksByProcessVariableId(projectId, processVariableId);
            List<Integer> linkListId = new ArrayList<Integer>();
            List<Integer> currentProcessModelList = new ArrayList<Integer>(Arrays.asList(processVariableDTO.getProcess_models()));

            //deletes all removed ProcessModel Links from database
            for (ProcessModelProcessVariableLink link : linkList) {
                linkListId.add(link.getProcessModelId());
                if (!currentProcessModelList.contains(link.getProcessModelId())) {
                    processModelDataLinkDAO.deleteLink(projectId, link.getProcessModelId(), processVariableId);
                }
            }
            //creates new Links for each new ProcessModel assigned
            for (Integer pmId : currentProcessModelList) {
                if (!linkListId.contains(pmId)) {
                    ProcessModelProcessVariableLink newLink = processModelDataLinkDAO.createLink(projectId, pmId, processVariableId, processVariableDTO.getVariable_value());
                }
            }

            // Updates link(s) between process variable and responsibility

            if (processVariableDTO.getResponsibilityIds() != null) {
                List<ProcessVariableResponsibilityLink> responsibilityLinks = responsibilityDataLinkDAO.getLinksByProcessVariableId(projectId, processVariableId);
                List<Integer> respIdList = new ArrayList<>();
                List<Integer> currentRespIdList = new ArrayList<Integer>(Arrays.asList(processVariableDTO.getResponsibilityIds()));
                for (ProcessVariableResponsibilityLink respLink : responsibilityLinks) {
                    respIdList.add(respLink.getResponsibilityId());
                    if (!currentRespIdList.contains(respLink.getResponsibilityId())) {
                        responsibilityDataLinkDAO.deleteLink(projectId, respLink.getProcessVariableId(), respLink.getResponsibilityId());
                    }
                }
                for (Integer respId : currentRespIdList) {
                    if (!respIdList.contains(respId)) {
                        responsibilityDataLinkDAO.createLink(projectId, processVariableId, respId);
                    }
                }
            }

            //adds all possible discrete Variable values if Variable is of type discrete
            if (processVariableDTO.getVariable_type().equals(DISCRETE_PROCESS_VARIABLE)) {

                //get all Saved discrete variable values for Process variable
                List<DiscreteProcessVariableValue> savedDPVV = discreteData.getAllValuesById(projectId, processVariableId);

                //Lists containing received Data and saved Data in Database
                List<String> savedValues = new ArrayList<>();
                List<String> currentValues = new ArrayList<>();

                //add received values to List
                for (int i = 0; i < processVariableDTO.getValueStates().length; i++) {
                    currentValues.add(processVariableDTO.getValueStates()[i]);
                }

                //add saved values from Database
                for (DiscreteProcessVariableValue discreteVariable : savedDPVV) {
                    savedValues.add(discreteVariable.getVariableValue());
                }

                //delete removed discrete Variable values
                for (String savedValue : savedValues) {
                    if (!currentValues.contains(savedValue)) {
                        discreteData.deleteValue(projectId, processVariableId, savedValue);
                    }
                }
                //create new discrete Variable values
                for (String value : processVariableDTO.getValueStates()) {
                    if (!savedValues.contains(value)) {
                        discreteData.createNewValue(projectId, processVariableId, value);
                    }
                }

            }

            List<ProcessVariableRequestDTO> resultList = new ArrayList<>();

            resultList.addAll(getProcessVariableValues(new ArrayList<>(Arrays.asList(result)), projectId, processModelId));

            return resultList;
        }

        return null;
    }

    /**
     * deletes a ProcessVariable in a ProcessModel from the Database.
     *
     * @param projectId         Project ProcessVariable belongs to
     * @param processVariableId ID of ProcessVariable
     * @return success of deletion (true/false)
     */
    public boolean deleteProcessVariable(UUID projectId, int processVariableId) {
        ProcessVariable processVariable = processVariableData.findById(new ProjectDependentKey(projectId, processVariableId), false);

        if (processVariable != null) {
            List<ProcessModelProcessVariableLink> links = processModelDataLinkDAO.getLinksByProcessVariableId(projectId, processVariableId);
            if (links != null && !links.isEmpty())
                for (ProcessModelProcessVariableLink link : links) {
                    processModelDataLinkDAO.deleteLink(projectId, link.getProcessModelId(), link.getProcessVariableId());
                }

            // Delete link(s) between process variable and responsibility
            List<ProcessVariableResponsibilityLink> responsibilityLinks = responsibilityDataLinkDAO.getLinksByProcessVariableId(projectId, processVariableId);
            if (responsibilityLinks != null && !responsibilityLinks.isEmpty()) {
                for (ProcessVariableResponsibilityLink responsibilityLink : responsibilityLinks) {
                    responsibilityDataLinkDAO.deleteLink(responsibilityLink.getProjectId(), responsibilityLink.getProcessVariableId(), responsibilityLink.getResponsibilityId());
                }
            }
            processVariableData.makeTransient(processVariable);
            return true;
        }
        return false;
    }

    /**
     * retrieves a specific ProcessVariable from the Database
     *
     * @param projectId         projectId
     * @param processVariableId ID belonging to searched ProcessVariable
     * @return ProcessVariable matching the given ID
     */
    public ProcessVariableRequestDTO getProcessVariableById(UUID projectId, int processVariableId, int processModelId) {
        ProcessVariable processVariable = processVariableData.findById(new ProjectDependentKey(projectId, processVariableId), false);
        if (processVariable != null) {
            ArrayList<ProcessVariable> variableList = new ArrayList();
            variableList.add(processVariable);
            List<ProcessVariableRequestDTO> resultList = getProcessVariableValues(variableList, projectId);

            for (ProcessVariableRequestDTO dto : resultList) {
                if (dto.getCurrentProcessModel() == processModelId) {
                    return dto;
                }
            }
        }
        return null;
    }

    /**
     * retrieves all ProcessVariables connected to the specified source and process model from the
     * Database.
     *
     * @param projectId      project ProcessVariables belong to
     * @param source         source of the processVariables
     * @param processModelId the process model id
     * @return a List of all ProcessVariables that match the specified source
     */
    public List<ProcessVariableRequestDTO> getProcessVariableBySourceAndProcessModel(UUID projectId, String source, Integer processModelId) {
        List<ProcessVariable> resultList = new ArrayList<>();
        List<String> arrowIdList = new ArrayList<>();
        List<Arrow> arrowList = arrowData.AllArrowsBySource(projectId, source);

        // get Arrow IDs
        for (Arrow arrow : arrowList) {
            arrowIdList.add(arrow.getId());
        }

        //get all ProcessModel Links
        List<ProcessModelProcessVariableLink> processModelProcessVariableLinks = processModelDataLinkDAO.getLinksByProcessModelId(projectId, processModelId, 1000, 0);

        if (processModelProcessVariableLinks != null && !processModelProcessVariableLinks.isEmpty()) {
            //get processVariableID's from Links
            List<Integer> processVariableIds = new ArrayList<>();
            for (ProcessModelProcessVariableLink link : processModelProcessVariableLinks) {
                processVariableIds.add(link.getProcessVariableId());
            }

            //get all Variables from the Links
            List<ProcessVariable> processVariableList = processVariableData.getAllProcessVariablesFromList(projectId, processVariableIds);

            //filter for Variables matching the criteria
            for (ProcessVariable variable : processVariableList) {
                if (arrowIdList.contains(variable.getArrowId())) {
                    resultList.add(variable);
                }
            }
            //convert to DTO
            return getProcessVariableValues(resultList, projectId, processModelId);
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Retrieves all ProcessVariables from the Database for a given Project
     *
     * @param projectId Project ID
     * @return List of ProcessVariables matching ProjectId
     */
    public List<ProcessVariableRequestDTO> getAllProcessVariables(UUID projectId) {

        List<ProcessVariable> processVariableList = processVariableData.getAllProcessVariablesForProject(projectId);

        return getProcessVariableValues(processVariableList, projectId);
    }

    private List<ProcessVariableRequestDTO> getUnlinkedProcessVariable(ProcessVariable processVariable, String filter) {



        List<ProcessVariableRequestDTO> responseList = new ArrayList<>();
        switch (filter) {

            case "process-model":
                ProcessVariableRequestDTO noPmResponse = createInitialProcessVariableDTO(processVariable);
                noPmResponse.setVariable_value(null);
                noPmResponse.setCurrentProcessModel(null);
                noPmResponse.setProcess_models(new Integer[0]);
                noPmResponse.setResponsibilityIds(getResponsibilityIdsForProcessVariable(processVariable));
                responseList.add(noPmResponse);
                break;
            case "input":
                List<ProcessModelProcessVariableLink> linkList = processModelDataLinkDAO.getLinksByProcessVariableId(processVariable.getId().getProjectId(), processVariable.getId().getId());
                Integer[] pmIDList = new Integer[linkList.size()];
                for (int i = 0; i < linkList.size(); i++) {
                    pmIDList[i] = linkList.get(i).getProcessModelId();
                }

                for (ProcessModelProcessVariableLink link : linkList) {
                    ProcessVariableRequestDTO noInputResponse = createInitialProcessVariableDTO(processVariable);

                    noInputResponse.setVariable_value(link.getProcessVariableValue());
                    noInputResponse.setCurrentProcessModel(link.getProcessModelId());
                    noInputResponse.setProcess_models(pmIDList);
                    noInputResponse.setResponsibilityIds(getResponsibilityIdsForProcessVariable(processVariable));
                    responseList.add(noInputResponse);
                }
                break;
        }
        return responseList;
    }

    public List<ProcessVariableRequestDTO> getAllUnlinkedProcessVariables(UUID projectId, String filter) {

        List<ProcessVariableRequestDTO> unlinkedProcessVariables = new ArrayList<>();
        List<ProcessVariable> allVariables = processVariableData.getAllProcessVariablesForProject(projectId);
        for (ProcessVariable variable : allVariables) {

            switch (filter.trim()) {
                case "all":

                    List<ProcessModelProcessVariableLink> resultPM = processModelDataLinkDAO.getLinksByProcessVariableId(variable.getId().getProjectId(), variable.getId().getId());
                    Arrow arrow = arrowData.getArrowById(projectId, variable.getArrowId());

                    if ((resultPM == null || resultPM.isEmpty()) && arrow == null){
                        unlinkedProcessVariables.addAll(getUnlinkedProcessVariable(variable, "process-model"));
                    }

                    break;
                case "process-model":
                    List<ProcessModelProcessVariableLink> resultPM1 = processModelDataLinkDAO.getLinksByProcessVariableId(variable.getId().getProjectId(), variable.getId().getId());
                    if (resultPM1 == null || resultPM1.isEmpty()){
                        unlinkedProcessVariables.addAll(getUnlinkedProcessVariable(variable, "process-model"));
                    }

                    break;
                case "input":
                    Arrow inputArrow = arrowData.getArrowById(projectId, variable.getArrowId());
                    if (inputArrow == null){
                        unlinkedProcessVariables.addAll(getUnlinkedProcessVariable(variable, "input"));
                    }

                    break;
            }
        }
        return unlinkedProcessVariables;
    }
}
