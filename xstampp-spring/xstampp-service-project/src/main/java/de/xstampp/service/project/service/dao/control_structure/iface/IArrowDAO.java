package de.xstampp.service.project.service.dao.control_structure.iface;

import de.xstampp.service.project.data.entity.Arrow;

import java.util.List;
import java.util.UUID;

/**
 * This Interface is used to allow direct access to the arrow table
 */
public interface IArrowDAO {

    /**
     * retrieves a List of Arrows that match a certain destination
     *
     * @param projectId   project the arrows belong to
     * @param destination destination for arrows
     * @return List of all Arrows that have the specified destination
     */
    List<Arrow> AllArrowsByDestination (UUID projectId, String destination);

    /**
     * retrieves a List of Arrows that match a certain source
     *
     * @param projectId project the arrows belong to
     * @param source    destination for arrows
     * @return List of all Arrows that have the specified source
     */
    List<Arrow> AllArrowsBySource (UUID projectId, String source);

    /**
     * retrieves an arrow matching a specified arrow ID from the Database
     *
     * @param projectId project arrow belongs to
     * @param arrowId   ID of arrow
     * @return Arrow matching specified ID
     */
    Arrow getArrowById (UUID projectId, String arrowId);

    /**
     * retrieves all arrows by for the same source, destination of a given project
     *
     * @param projectId   Project Arrow belongs to
     * @param source      starting point of the arrow
     * @param destination end point of the arrow
     * @return Arrow matching the source and destination
     */
    Arrow getArrowBySourceAndDestination (UUID projectId, String source, String destination);

    /**
     * retrieves all arrows for given project id and type
     * @param projectId The project id
     * @param type The type of the arrow (i.e. control action)
     * @return A list with arrows matching the parameters
     */
    List<Arrow> getArrowsByType(UUID projectId, String type);

    List<Arrow> findAll(UUID projectId);

    List<Arrow> saveAll(List<Arrow> arrows);
}
