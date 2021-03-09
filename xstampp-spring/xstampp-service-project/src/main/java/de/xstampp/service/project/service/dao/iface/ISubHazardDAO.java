package de.xstampp.service.project.service.dao.iface;

import de.xstampp.service.project.data.entity.EntityDependentKey;
import de.xstampp.service.project.data.entity.SubHazard;

import java.util.List;


public interface ISubHazardDAO extends IEntityDependentGenericDAO<SubHazard, EntityDependentKey> {

	List<SubHazard> findSubHazardsByIds(List<EntityDependentKey> subHazardPKeys, Integer amount,
			Integer amount2);
	
}
