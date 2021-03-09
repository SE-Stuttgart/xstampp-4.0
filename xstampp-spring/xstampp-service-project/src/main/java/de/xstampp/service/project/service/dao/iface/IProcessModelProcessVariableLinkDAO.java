package de.xstampp.service.project.service.dao.iface;

import de.xstampp.service.project.data.entity.ProcessModelProcessVariableLink;

import java.util.List;
import java.util.UUID;

/**
 * This interface is used to extract &amp; manipulate information out of a table in
 * the database containing Links between Process Models and Process Variables.
 */
public interface IProcessModelProcessVariableLinkDAO {

    /**
     * creates a new link between a process model and a process variable
     *
     * @param projectId         the project id
     * @param processModelId    the Process Model id
     * @param processVariableId the process variable id
     * @return returns the new created link
     */
    public ProcessModelProcessVariableLink createLink(UUID projectId, int processModelId, int processVariableId, String processVariableValue);

    /**
     * deletes an existing link
     *
     * @param projectId         the project id
     * @param processModelId    the Process Model id
     * @param processVariableId the process variable id
     * @return returns true if the deletion was successfully
     */
    public boolean deleteLink(UUID projectId, int processModelId, int processVariableId);

    /**
     * saves all given links
     *
     * @param links the links to save
     * @return returns a list of saved links
     */
    public List<ProcessModelProcessVariableLink> saveAll(List<ProcessModelProcessVariableLink> links);

    /**
     * returns all links for a specific process model id
     *
     * @param projectId      the project id
     * @param processModelId the process model id
     * @param amount         returns only the x first parameters
     * @param from           skips the first x parameters
     * @return returns a list of links which matches the parameters
     */
    public List<ProcessModelProcessVariableLink> getLinksByProcessModelId(UUID projectId, int processModelId,
            Integer amount, Integer from);

    /**
     * returns all links for a specific process variable id
     *
     * @param projectId the project id
     * @param processVariableId   variable the process variable id
     * @return returns a list of links which matches the parameters
     */
    public List<ProcessModelProcessVariableLink> getLinksByProcessVariableId(UUID projectId, int processVariableId);

    /**
     * returns the link for a specific process model- process variable - pair
     * 
     * @param projectId         the project id
     * @param processModelId    the Process Model id
     * @param processVariableId the process variable id
     * @return
     */
    public ProcessModelProcessVariableLink getLink(UUID projectId, int processModelId, int processVariableId);

    /**
     * Updates the Value of a ProcessVariable for a ProcessModel in the Database
     * @param projectId projectId Data relates to
     * @param processModelId    ProcessModel ID
     * @param processVariableId ProcessVariable ID
     * @param newValue  processVariable_value to be changed
     * @return  updated Entry
     */
    public ProcessModelProcessVariableLink updateLink (UUID projectId, int processModelId, int processVariableId, String newValue);

    /**
     * retrieves all ProcessVariableLinks for a ProcessModel from the Database
     * @param projectId project Data belongs to
     * @param processModelId    ProcessModel
     * @return  all Links for the ProcessModel ID
     */
    public List <ProcessModelProcessVariableLink> getAllLinksByProcessModel (UUID projectId, int processModelId);

    /**
     * retrieves all ProcessVariableLinks for a project
     * @param projectId the UUID of the project
     * @return  all Links of the project
     */
    public List <ProcessModelProcessVariableLink> getAllLinks (UUID projectId);

}
