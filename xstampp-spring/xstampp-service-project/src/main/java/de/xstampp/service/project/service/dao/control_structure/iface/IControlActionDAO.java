package de.xstampp.service.project.service.dao.control_structure.iface;

import java.util.List;
import java.util.UUID;

import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.control_structure.ControlAction;
import de.xstampp.service.project.service.dao.iface.IProjectDependentGenericDAO;

/**
 * Interface for Data Access Object for Control Actions
 */
public interface IControlActionDAO extends IProjectDependentGenericDAO<ControlAction, ProjectDependentKey>{
	/**
	 * Retrieves all Control Actions linked to an Arrow
	 * @param projectId	project, Control Actions belong to
	 * @param arrowId	ID of the Arrow
	 * @return	List of all linked Control Actions
	 */
	public List<ControlAction> getByArrowId (UUID projectId, String arrowId);

	/**
	 * Retrieves all Control Actions not linked to an Arrow
	 * @param projectId	project Control Actions belong to
	 * @return	List of all unlinked Control Actions
	 */
	public List<ControlAction> getAllUnlinkedControlActions (UUID projectId);

	/**
	 * Updates arrow-link for an existing Control Action
	 * @param projectID	project Control Action belongs to
	 * @param controlActionId	ID of Control Action to be changed
	 * @param arrowId	new Arrow ID to be linked to COntrol Action
	 * @return	updated Control Action
	 */
	public ControlAction setArrowId (UUID projectID, int controlActionId, String arrowId);
}
