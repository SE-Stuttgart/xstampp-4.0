package de.xstampp.service.project.service.dao.iface;

import java.util.List;
import java.util.UUID;

import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.Responsibility;

/**
 * This Interface is used to extract information out of a table containing Responsibilities in the database.
 */
public interface IResponsibilityDAO extends IProjectDependentGenericDAO<Responsibility, ProjectDependentKey>{

	/**
	 * returns all responsibilities for a given controller
	 *
	 * @param projectId			the project Id
	 * @param controllerId		the controller Id
	 * @return					a list of Responsibilities matching the controller Id
	 */
	public List<Responsibility> getListByControllerId (UUID projectId, int controllerId);

	/**
	 * returns all Responsibilities for a List of responsibility Id's
	 *
	 * @param projectId				the project Id
	 * @param responsibilityList	a list of Responsibility Ids
	 * @return						a list of Responsibilities corresponding to values in the given List
	 */
	public List<Responsibility> getListByResponsibilityIds (UUID projectId, List<Integer> responsibilityList);

}
