package de.xstampp.service.project.service.dao.iface;

import java.util.List;
import java.util.UUID;

import de.xstampp.service.project.data.entity.Loss;
import de.xstampp.service.project.data.entity.ProjectDependentKey;

/**
 * Loss DAO for service injection
 * @author Tobias
 *
 */
public interface ILossDAO extends IProjectDependentGenericDAO<Loss, ProjectDependentKey>{

	public List<Loss> findLossesByIds(UUID projectId, List<Integer> lossIds);
}
