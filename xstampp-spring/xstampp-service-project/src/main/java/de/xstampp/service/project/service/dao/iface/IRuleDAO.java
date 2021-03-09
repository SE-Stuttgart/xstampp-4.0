package de.xstampp.service.project.service.dao.iface;

import java.util.List;
import java.util.UUID;

import de.xstampp.service.project.data.entity.EntityDependentKey;
import de.xstampp.service.project.data.entity.Rule;

public interface IRuleDAO extends IEntityDependentGenericDAO<Rule, EntityDependentKey> {

	List<Rule> findRulesByIds(List<EntityDependentKey> ruleKeys, Integer from, Integer amount);
	
	List<Rule> getRulesByControllerId(UUID projectId, int controllerId,
			int amount, int from);

}
