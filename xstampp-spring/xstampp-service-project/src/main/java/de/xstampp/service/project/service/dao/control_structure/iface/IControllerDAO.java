package de.xstampp.service.project.service.dao.control_structure.iface;

import java.util.List;
import java.util.UUID;

import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.control_structure.Controller;
import de.xstampp.service.project.service.dao.iface.IProjectDependentGenericDAO;

public interface IControllerDAO extends IProjectDependentGenericDAO<Controller, ProjectDependentKey>{

	/**
	 * retrieves a Controller for a given BoxId from the Database.
	 * @param projectId	project the Controller and Box is located in
	 * @param boxId ID of Box Entity
	 * @return	a single Controller Entity
	 */
	public Controller getByBoxId (UUID projectId, String boxId);

	/**
	 * retrieves All Controllers from the Database that are not linked to any Box Entity
	 * @param projectId	Project data should be retrieved from
	 * @return	All unlinked Controllers
	 */
	public List<Controller> getAllUnlinkedControllers (UUID projectId);

	/**
	 * retrieves a specific Controller for a given ControllerId and BoxId from the Database.
	 * @param projectID	project the Controller and Box is located in
	 * @param controllerId	ID of Controller
	 * @param boxId ID of Box Entity
	 * @return	a single Controller Entity
	 */
	public Controller setBoxId (UUID projectID, int controllerId, String boxId);

}
