package de.xstampp.service.project.service.dao.iface;

import de.xstampp.service.project.data.entity.ResponsibilitySystemConstraintLink;

import java.util.List;
import java.util.UUID;

/**
 * This interface is used to extract &amp; manipulate information out of a table in the database containing Links
 * between System constraints and Responsibilities.
 */
public interface IResponsibilitySystemConstraintLinkDAO {

    /**
     * creates a new link between a responsibility and a system constraint
     *
     * @param projectId          the project id
     * @param responsibilityId   the responsibility id
     * @param systemConstraintId the system constraint id
     * @return returns the new created link
     */
    public boolean addLink(UUID projectId, int responsibilityId, int systemConstraintId);

    /**
     * creates a new link between a responsibility and a system constraint
     *
     * @param responsibilitySystemConstraintLink the responsibility system constraint link
     * @return returns the new created link
     */
    public boolean addLink(ResponsibilitySystemConstraintLink responsibilitySystemConstraintLink);

    /**
     * deletes an existing link
     *
     * @param projectId          the project id
     * @param responsibilityId   the responsibility id
     * @param systemConstraintId the system constraint id
     * @return returns true if the deletion was successfully
     */
    public boolean deleteLink(UUID projectId, int responsibilityId, int systemConstraintId);

    /**
     * saves all given ResponsibilitySystemConstraintLinks
     *
     * @param links the list of ResponsibilitySystemConstraintLink to save
     * @return the list of saved ResponsibilitySystemConstraintLinks
     */
    public List<ResponsibilitySystemConstraintLink> saveAll(List<ResponsibilitySystemConstraintLink> links);

    /**
     * Returns all links in the specified project
     *
     * @param projectId The project, the links are in
     * @param sort Whether to sort
     * @return All links
     */
    public List<ResponsibilitySystemConstraintLink> getAllLinks(UUID projectId, boolean sort);

    /**
     * returns all links for a specific responsibility id
     *
     * @param projectId the project id
     * @param responsibilityId  the responsibility id
     * @param amount    returns only the x first parameters
     * @param from      skips the first x parameters
     * @return returns a list of links which matches the parameters
     */
    public List<ResponsibilitySystemConstraintLink> getLinksByResponsibilityId(UUID projectId, int responsibilityId,
                                                                               Integer amount, Integer from);

    /**
     * returns all links for a specific responsibility id
     *
     * @param projectId          the project id
     * @param systemConstraintId the system constraint id
     * @return returns a list of links which matches the parameters
     */
    public List<ResponsibilitySystemConstraintLink> getLinksBySystemConstraintId(UUID projectId, int systemConstraintId);

    /**
     * returns the link for a specific responsibility-SystemConstraint-pair
     * @param projectId             the project id
     * @param responsibilityId      the responsibility id
     * @param systemConstraintId    the system constraint id
     * @return the corresponding link
     */
    ResponsibilitySystemConstraintLink getLink(UUID projectId, int responsibilityId, int systemConstraintId);
}
