package de.xstampp.service.project.service.data.control_structure;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import de.xstampp.service.project.data.entity.User;
import de.xstampp.service.project.service.dao.iface.IUserDAO;
import de.xstampp.service.project.service.data.ControlStructureDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.xstampp.common.dto.mock.Filter;
import de.xstampp.common.service.SecurityService;
import de.xstampp.service.project.data.dto.control_structure.ControlActionRequestDTO;
import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.control_structure.ControlAction;
import de.xstampp.service.project.service.dao.control_structure.iface.IControlActionDAO;
import de.xstampp.service.project.service.dao.iface.ILastIdDAO;
import de.xstampp.service.project.service.dao.iface.SortOrder;

@Service
@Transactional
public class ControlActionDataService {

	@Autowired
	ILastIdDAO lastId;

	@Autowired
	SecurityService securityService;

	@Autowired
	IControlActionDAO controlActionData;

	@Autowired
	IUserDAO userDAO;

	@Autowired
	ControlStructureDataService controlStructureDataService;

	/**
	 * creates a new control action
	 * 
	 * @param projectId        the project id
	 * @param controlActionDTO the dto object which contains the received
	 *                         information
	 * @return returns the new created control action
	 */
	public ControlAction createControlAction(UUID projectId, ControlActionRequestDTO controlActionDTO) {
		ProjectDependentKey key = new ProjectDependentKey(projectId,
				lastId.getNewIdForEntity(projectId, ControlAction.class));

		ControlAction action = new ControlAction();
		action.setId(key);
		action.setName(controlActionDTO.getName());
		action.setDescription(controlActionDTO.getDescription());

		if (controlActionDTO != null) {
			action.setState(controlActionDTO.getState());
		} else {
			action.setState("DOING");
		}

		action.setLastEditNow(securityService.getContext().getUserId());
		userDAO.makePersistent(new User(securityService.getContext().getUserId(), securityService.getContext().getDisplayName()));

		return controlActionData.makePersistent(action);
	}

	/**
	 * alters an exiting control action
	 * 
	 * @param projectId        the project id
	 * @param controlActionId  the id of the control action
	 * @param controlActionDTO the dto with the new data
	 * @return returns the altered feedback
	 */
	public ControlAction alterControlAction(UUID projectId, int controlActionId,
			ControlActionRequestDTO controlActionDTO) {
		ControlAction action = controlActionData.findById(new ProjectDependentKey(projectId, controlActionId), false);
		String oldName = action.getName();

		if (action != null) {
			action.setName(controlActionDTO.getName());
			action.setDescription(controlActionDTO.getDescription());
			action.setState(controlActionDTO.getState());

			action.setLastEditNow(securityService.getContext().getUserId());
			userDAO.makePersistent(new User(securityService.getContext().getUserId(), securityService.getContext().getDisplayName()));
			
			action.setLockExpirationTime(Timestamp.from(Instant.now()));
			ControlAction result = controlActionData.makePersistent(action);

			controlStructureDataService.alterArrow(projectId, controlActionId, "ControlAction", oldName, result.getName());
			return result;
		}

		return action;
	}

	/**
	 * deletes an existing control action
	 * 
	 * @param projectId       the project id
	 * @param controlActionId the control action id
	 * @return returns true if the control action was deleted successfully
	 */
	public boolean deleteControlAction(UUID projectId, int controlActionId) {
		ControlAction action = controlActionData.findById(new ProjectDependentKey(projectId, controlActionId), false);
		String name = action.getName();

		if (action != null) {
			controlActionData.makeTransient(action);
			controlStructureDataService.deleteNameInArrowLabel(projectId, controlActionId, "ControlAction", name);
			return true;
		}

		return false;
	}

	/**
	 * returns a control action by its id
	 * 
	 * @param projectId       the project id
	 * @param controlActionId the control action id
	 * @return returns the control action with the id
	 */
	public ControlAction getControlActionById(UUID projectId, int controlActionId) {
		return controlActionData.findById(new ProjectDependentKey(projectId, controlActionId), false);
	}

	// TODO: Complete Documentation (filter) @Rico
	/**
	 * returns all control actions paged by the given parameters
	 * 
	 * @param projectId the project id
	 * @param amount    the amount of results
	 * @param from      skips the first x values
	 * @return returns a list of control actions
	 */
	public List<ControlAction> getAllControlActions(UUID projectId, Filter filter, Integer amount, Integer from) {
		LinkedHashMap<String, SortOrder> order = new LinkedHashMap<>();
		order.put(ControlAction.EntityAttributes.ID, SortOrder.ASC);
		
		if (filter != null && filter.getFieldName() != null && filter.getFieldValue() != null) {			
			if (filter.getFieldName().equals("isUnlinked") && Boolean.valueOf(filter.getFieldValue())) {
				return controlActionData.getAllUnlinkedControlActions(projectId);
			} 
		}
		
		return controlActionData.findFromTo(from, amount, order, projectId);
	}
	
	public List<ControlAction> getControlActionByArrowId (UUID projectId, String arrowId) {
		return controlActionData.getByArrowId(projectId, arrowId);
	}
	
	public ControlAction setControlActionArrowId (UUID projectId, int controlActionId, String arrowId) {
		return controlActionData.setArrowId(projectId, controlActionId, arrowId);
	}
}
