package de.xstampp.service.project.service.dao.control_structure.iface;

import java.util.UUID;

import java.util.List;

import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.control_structure.ProcessModel;
import de.xstampp.service.project.service.dao.iface.IProjectDependentGenericDAO;

/**
 * interface for retrieving and manipulating ProcessModels from Database
 */
public interface IProcessModelDAO extends IProjectDependentGenericDAO<ProcessModel, ProjectDependentKey> {


    /**
     * retrieves ProcessModel for specific processModel ID from Database
     *
     * @param projectID      project ProcessModel belongs to
     * @param processModelId ProcessModel to be retrieved
     * @return ProcessModel matching criteria
     */
    public ProcessModel getById(UUID projectID, int processModelId);

    /**
     * retrieves all ProcessModels from Database for a given Project
     *
     * @param projectID project Process Model belong to
     * @return list of ProcessModels which belong to the project
     */
    public List<ProcessModel> getAllProcessModels(UUID projectID);

    /**
     * retrieves All ProjectModels in Database corresponding to the received controllerID
     *
     * @param projectId    project ProcessModel belongs to
     * @param controllerID Controller ProcessModel belongs to
     * @return list of ProcessModels which belong to the given controllerId
     */
    public List<ProcessModel> getAllProcessModelsByControllerID(UUID projectId, int controllerID);


}