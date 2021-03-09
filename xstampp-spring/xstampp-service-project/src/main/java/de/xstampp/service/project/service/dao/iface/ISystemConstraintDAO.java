package de.xstampp.service.project.service.dao.iface;

import java.util.List;
import java.util.UUID;

import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.SystemConstraint;

/**
 * This Interface is used to extract information out of a table containing System constraints.
 */
public interface ISystemConstraintDAO extends IProjectDependentGenericDAO<SystemConstraint, ProjectDependentKey> {
	/**
	 * returns all System constraints for a List of System constraint Id's
	 *
	 * @param projectId the project Id
	 * @param constIds  a list of System constraint Ids
	 * @return a list of System constraints corresponding to values in the given List
	 */
	public List<SystemConstraint> findSystemConstraintsByIds(UUID projectId, List<Integer> constIds);
}
