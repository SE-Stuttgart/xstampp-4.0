package de.xstampp.service.project.service.dao.iface;

import java.util.List;
import java.util.UUID;

import de.xstampp.common.dto.mock.Filter;
import de.xstampp.service.project.data.entity.ControllerConstraint;
import de.xstampp.service.project.data.entity.EntityDependentKey;
import de.xstampp.service.project.data.entity.UnsafeControlAction;

public interface IUnsafeControlActionDAO extends IEntityDependentGenericDAO<UnsafeControlAction, EntityDependentKey> {

	List<UnsafeControlAction> getUnsafeControlActionsByControlActionId(UUID projectId, int controlActionId,
			int amount, int from);

	List<UnsafeControlAction> findUnsafeControlActionsByControlActionAndType(UUID projectId, int controlActionId,
			Integer amount, Integer from, Filter type);

	int countUnsafeControlActionsByControlActionAndType(UUID projectId, int controlActionId, Filter type);

	List<UnsafeControlAction> findUnsafeControlActionsByIds(List<EntityDependentKey> ucaKeys, Integer from,
			Integer amount);

	/**
	 * There is at most one implementation constraint per UCA. This method retrieves it from the database.
	 * @param projectId id of current project
	 * @param caId id of a a control action
	 * @param ucaId has to be id of a UCA belonging to the one control action with id=controlActionId
	 * @return matching controller constraints
	 */
	ControllerConstraint getControllerConstraintByUnsafeControlAction(UUID projectId, int caId, int ucaId);

}
