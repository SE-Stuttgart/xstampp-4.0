package de.xstampp.service.project.service.dao.iface;

import java.util.List;
import java.util.UUID;

import de.xstampp.service.project.data.entity.EntityDependentKey;
import de.xstampp.service.project.data.entity.Conversion;

public interface IConversionDAO extends IEntityDependentGenericDAO<Conversion, EntityDependentKey> {

	List<Conversion> findConversionsByIds(List<EntityDependentKey> conversionKeys, Integer from, Integer amount);
	
	List<Conversion> getConversionsByActuatorId(UUID projectId, int actuatorId,
			int amount, int from);

}
