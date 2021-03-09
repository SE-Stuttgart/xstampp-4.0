package de.xstampp.service.project.service.data;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.xstampp.service.project.data.entity.User;
import de.xstampp.service.project.service.dao.iface.IUserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.xstampp.common.dto.mock.Filter;
import de.xstampp.common.service.SecurityService;
import de.xstampp.service.project.data.dto.RuleRequestDTO;
import de.xstampp.service.project.data.entity.EntityDependentKey;
import de.xstampp.service.project.data.entity.Rule;
import de.xstampp.service.project.service.dao.control_structure.iface.IControlActionDAO;
import de.xstampp.service.project.service.dao.iface.ILastIdDAO;
import de.xstampp.service.project.service.dao.iface.IRuleDAO;
import de.xstampp.service.project.service.dao.iface.SortOrder;

@Service
@Transactional
public class RuleDataService {
	
	@Autowired
	IRuleDAO ruleDAO;
	
	@Autowired
	IControlActionDAO controlActionDAO;

	@Autowired
	SecurityService security;

	@Autowired
	ILastIdDAO lastIdDAO;

	@Autowired
	IUserDAO userDAO;
	
	/**
	 * Generates a new Rule with a new ID and the given parameters
	 * 
	 * @param request   the rule request DTO with the parameters to set
	 * @param projectId the assigned project ID
	 * @param controllerId the assigned controller ID
	 * @return returns the new Rule
	 */
	public Rule createRule(RuleRequestDTO request, UUID projectId, int controllerId) {
		int ruleId = lastIdDAO.getNewIdForRule(projectId, controllerId);
		Rule rule = new Rule(new EntityDependentKey(projectId, controllerId, ruleId));
		rule.setRule(request.getRule());
		rule.setControlActionId(request.getControlActionId());
		rule.setLastEditNow(security.getContext().getUserId());
		userDAO.makePersistent(new User(security.getContext().getUserId(), security.getContext().getDisplayName()));
		rule.setState(request.getState());

		Rule result = ruleDAO.makePersistent(rule);
		return result;
	}
	
	/**
	 * Edits an existing rule
	 * 
	 * @param request      the new rule containing all attributes including the
	 *                  edited values
	 * @param ruleId    the ID of the existing rule to be edited
	 * @param controllerId the ID of the assigned controller
	 * @param projectId the project ID of the assigned process
	 * @return returns the edited rule including all changes
	 */
	public Rule editRule(RuleRequestDTO request, UUID projectId, int controllerId, int ruleId) {
		Rule rule = new Rule(new EntityDependentKey(projectId, controllerId, ruleId));
		rule.setRule(request.getRule());
		rule.setControlActionId(request.getControlActionId());
		rule.setLastEditNow(security.getContext().getUserId());
		userDAO.makePersistent(new User(security.getContext().getUserId(), security.getContext().getDisplayName()));
		rule.setState(request.getState());
		rule.setLockExpirationTime(Timestamp.from(Instant.now()));
		
		Rule result = ruleDAO.updateExisting(rule);
		return result;
	}
	
	/**
	 * deletes a rule
	 * 
	 * @param ruleId    the rule ID
	 * @param controllerId the controller ID 
	 * @param projectId the assigned project ID
	 * @return returns true if the rule was deleted successfully
	 */
	public boolean deleteRule(UUID projectId, int controllerId, int ruleId) {
		EntityDependentKey key = new EntityDependentKey(projectId, controllerId, ruleId);
		Rule rule = ruleDAO.findById(key, false);
		if (rule != null) {
			ruleDAO.makeTransient(rule);
			return true;
		}
		return false;
	}
	
	/**
	 * Get Rule by id
	 * 
	 * @param ruleId    the rule ID
	 * @param projectId the project ID
	 * @param controllerId the controller ID 
	 * @return the rule for the given ID
	 */
	public Rule getRuleById(UUID projectId, int controllerId, int ruleId) {
		EntityDependentKey key = new EntityDependentKey(projectId, controllerId, ruleId);
		return ruleDAO.findById(key, false);
	}
	
	/**
	 * returns a list of rules paged by the given parameters
	 * 
	 * @param projectId      the assigned project ID
	 * @param controllerId   the assigned controller ID
	 * @param filter         currently not used
	 * @param orderBy        the parameter name for which the result should be
	 *                       ordered
	 * @param orderDirection the order direction (ASC or DESC)
	 * @param amount         returns only the first X results
	 * @param from           skips the first X results
	 * @return returns a list of rules filtered by the given criteria
	 */
	public List<Rule> getAllRules(UUID projectId, int controllerId, Filter filter, String orderBy, SortOrder orderDirection,
			Integer amount, Integer from) {

		Map<String, SortOrder> sortOrder = Map.of(orderBy, SortOrder.ASC);
		if (amount != null && from != null) {
			return ruleDAO.getRulesByControllerId(projectId, controllerId, amount, from);
		} else {
			return ruleDAO.getRulesByControllerId(projectId, controllerId, 0, 1000);
		}
	}



}
