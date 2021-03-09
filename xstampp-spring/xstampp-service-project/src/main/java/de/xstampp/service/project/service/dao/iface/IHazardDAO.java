package de.xstampp.service.project.service.dao.iface;

import java.util.List;
import java.util.UUID;

import de.xstampp.service.project.data.entity.Hazard;
import de.xstampp.service.project.data.entity.ProjectDependentKey;

public interface IHazardDAO extends IProjectDependentGenericDAO<Hazard, ProjectDependentKey> {

	public List<Hazard> findHazardsByIds(UUID projectId, List<Integer> hazardIds);
}
