package de.xstampp.service.project.service.dao.iface;

import de.xstampp.service.project.data.entity.ProcessVariableResponsibilityLink;

import java.util.List;
import java.util.UUID;

public interface IProcessVariableResponsibilityLinkDAO {
    /**
     * creates a new link between a process variable and a responsibility
     *
     * @param projectId         the project id
     * @param processVariableId the process variable id
     * @param responsibilityId  the responsibility id
     * @return                  returns the new created link
     */
    public ProcessVariableResponsibilityLink createLink(UUID projectId, int processVariableId, int responsibilityId);

    /**
     * deletes an existing link
     *
     * @param projectId         the project id
     * @param processVariableId the process variable id
     * @param responsibilityId  the responsibility id
     * @return                  returns true if the deletion was successfully
     */
    public boolean deleteLink(UUID projectId, int processVariableId, int responsibilityId);

    /**
     * saves all links
     *
     * @param links              the links to save
     * @return                   returns a list of saved links
     */
    public List<ProcessVariableResponsibilityLink> saveAll(List<ProcessVariableResponsibilityLink> links);

    /**
     * returns all links for a specific process variable id
     *
     * @param projectId          the project id
     * @param processVariableId  the process model id
     * @return                   returns a list of links which matches the parameters
     */
    public List<ProcessVariableResponsibilityLink> getLinksByProcessVariableId(UUID projectId, int processVariableId);

    /**
     * returns all links for a project
     *
     * @param projectId          the project id
     * @return                   returns a list of all links
     */
    public List<ProcessVariableResponsibilityLink> getAllLinks(UUID projectId);

    /**
     * returns the link for a specific process variable- responsibility - pair
     *
     * @param projectId         the project id
     * @param processVariableId the process variable id
     * @param responsibilityId  the responsibility id
     * @return
     */
    ProcessVariableResponsibilityLink getLink(UUID projectId,  int processVariableId, int responsibilityId);

    /**
     * replaces the responsibility in process variable responsibility link if newResponsibilityId does not match the current responsibilityId in the link else returns the old link(no changes)
     * (currently not used)
     *
     * @param projectId             the project id
     * @param processVariableId     the process variable id
     * @param oldResponsibilityId   the old responsibility id
     * @param newResponsibilityId   the new(replacing the old if not identical) responsibility
     * @return
     */
    public ProcessVariableResponsibilityLink updateLink(UUID projectId, int processVariableId, int oldResponsibilityId, int newResponsibilityId);

}
