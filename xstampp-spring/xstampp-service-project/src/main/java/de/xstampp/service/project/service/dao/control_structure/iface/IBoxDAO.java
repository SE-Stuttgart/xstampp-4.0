package de.xstampp.service.project.service.dao.control_structure.iface;

import de.xstampp.service.project.data.entity.Box;

import java.util.List;
import java.util.UUID;

/**
 * This Interface is used to allow direct access to the box table
 */
public interface IBoxDAO {

    /**
     * retrieves all Box Entries that match the Box IDs in the given List
     *
     * @param projectId project data relates to
     * @param BoxIdList List of controller IDs
     * @return a list of Box Entities
     */
    List<Box> getBoxByIds(UUID projectId, List<String> BoxIdList);

    /**
     * retrieves all Box Entries that match the projectId
     *
     * @param projectId project data relates to
     * @return a list of Box Entities
     */
    List<Box> getBoxById(UUID projectId);

    /**
     * retrieves a single Box through its ID
     *
     * @param projectId project Box belongs to
     * @param boxId     ID of Box
     * @return Box Object
     */
    Box getSingleBoxById(UUID projectId, String boxId);

    List<Box> getBoxByTypeAndParent(UUID projectId, int parentId, String type);

    List<Box> findAll(UUID projectId);

    List<Box> saveAll(List<Box> boxes);
}
