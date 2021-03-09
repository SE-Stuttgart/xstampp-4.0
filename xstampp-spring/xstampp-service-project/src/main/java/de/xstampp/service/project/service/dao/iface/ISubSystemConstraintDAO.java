package de.xstampp.service.project.service.dao.iface;

import java.util.UUID;

import de.xstampp.service.project.data.entity.EntityDependentKey;
import de.xstampp.service.project.data.entity.SubSystemConstraint;

public interface ISubSystemConstraintDAO
    extends IEntityDependentGenericDAO<SubSystemConstraint, EntityDependentKey> {
	
	public SubSystemConstraint getSubSystemConstraintBySubHazardId (UUID projectId, int hazardId, int subHazardId);
}

