package de.xstampp.service.project.service.dao.iface;

import de.xstampp.service.project.data.entity.HazardSystemConstraintLink;

import java.util.List;
import java.util.UUID;

public interface IHazardSystemConstraintLinkDAO {

    /**
     * creates a new link between a hazard and a system constraint
     *
     * @param projectId          the project id
     * @param hazardId           the hazard id
     * @param systemConstraintId the system constraint id
     * @return returns the new created link
     */
    public boolean addLink(UUID projectId, int hazardId, int systemConstraintId);

    /**
     * creates a new link between a hazard and a system constraint
     *
     * @param hazardSystemConstraintLink the hazard system constraint link
     * @return returns the new created link
     */
    public boolean addLink(HazardSystemConstraintLink hazardSystemConstraintLink);

    /**
     * deletes an existing link
     *
     * @param projectId          the project id
     * @param hazardId           the hazard id
     * @param systemConstraintId the system constraint id
     * @return returns true if the deletion was successfully
     */
    public boolean deleteLink(UUID projectId, int hazardId, int systemConstraintId);

    /**
     * saves all given HazardSystemConstraintLink
     * @param hazardSystemConstraintLinks the list of HazardSystemConstraintLinks
     * @return returns the list of saved HazardSystemConstraintLinks
     */
    public List<HazardSystemConstraintLink> saveAll(List<HazardSystemConstraintLink> hazardSystemConstraintLinks);

	/**
	 * returns all links matching the projectId
	 *
	 * @param projectId the id of the project
	 * @return returns a list of all matching links
	 */
	List<HazardSystemConstraintLink> getAllLinks(UUID projectId);

    /**
     * returns all links for a specific hazard id
     *
     * @param projectId the project id
     * @param hazardId  the hazard id
     * @param amount    returns only the x first parameters
     * @param from      skips the first x parameters
     * @return returns a list of links which matches the parameters
     */
    public List<HazardSystemConstraintLink> getLinksByHazardId(UUID projectId, int hazardId, Integer amount,
                                                               Integer from);

    /**
     * returns all links for a specific hazard id
     *
     * @param projectId          the project id
     * @param systemConstraintId the hazard id
     * @param amount             returns only the x first parameters
     * @param from               skips the first x parameters
     * @return returns a list of links which matches the parameters
     */
    public List<HazardSystemConstraintLink> getLinksBySystemConstraintId(UUID projectId, int systemConstraintId,
                                                                         Integer amount, Integer from);

    HazardSystemConstraintLink getLink(UUID projectId, int hazardId, int systemConstraintLinkId);
}
