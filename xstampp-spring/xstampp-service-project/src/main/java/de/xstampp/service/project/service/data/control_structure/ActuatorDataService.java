package de.xstampp.service.project.service.data.control_structure;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import de.xstampp.service.project.data.entity.User;
import de.xstampp.service.project.service.dao.iface.IUserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.xstampp.common.dto.mock.Filter;
import de.xstampp.common.service.SecurityService;
import de.xstampp.service.project.data.dto.control_structure.ActuatorRequestDTO;
import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.control_structure.Actuator;
import de.xstampp.service.project.service.dao.control_structure.iface.IActuatorDAO;
import de.xstampp.service.project.service.dao.iface.ILastIdDAO;
import de.xstampp.service.project.service.dao.iface.SortOrder;
import de.xstampp.service.project.service.data.ControlStructureDataService;

@Service
@Transactional
public class ActuatorDataService {

	@Autowired
	ILastIdDAO lastId;

	@Autowired
	SecurityService securityService;

	@Autowired
	IActuatorDAO actuatorData;

	@Autowired
	IUserDAO userDAO;

	@Autowired
	ControlStructureDataService controlStructureDataService;

	/**
	 * creates a new actuator
	 * 
	 * @param projectId   the project id
	 * @param actuatorDTO the actuator dto with the data
	 * @return returns the new created actuator
	 */
	public Actuator createActuator(UUID projectId, ActuatorRequestDTO actuatorDTO) {
		ProjectDependentKey key = new ProjectDependentKey(projectId,
				lastId.getNewIdForEntity(projectId, Actuator.class));

		Actuator actuator = new Actuator();
		actuator.setId(key);
		actuator.setName(actuatorDTO.getName());
		actuator.setDescription(actuatorDTO.getDescription());

		if (actuatorDTO.getState() != null) {
			actuator.setState(actuatorDTO.getState());
		} else {
			actuator.setState("DOING");
		}


		actuator.setLastEditNow(securityService.getContext().getUserId());
		userDAO.makePersistent(new User(securityService.getContext().getUserId(), securityService.getContext().getDisplayName()));

		return actuatorData.makePersistent(actuator);
	}

	/**
	 * alters an existing actuator
	 * 
	 * @param projectId   the project id
	 * @param actuatorId  the actuator id
	 * @param actuatorDTO the actuator dto with the new data
	 * @return returns the altered actuator
	 */
	public Actuator alterActuator(UUID projectId, int actuatorId, ActuatorRequestDTO actuatorDTO) {
		Actuator actuator = actuatorData.findById(new ProjectDependentKey(projectId, actuatorId), false);

		if (actuator != null) {
			actuator.setName(actuatorDTO.getName());
			actuator.setDescription(actuatorDTO.getDescription());
			actuator.setState(actuatorDTO.getState());

			actuator.setLastEditNow(securityService.getContext().getUserId());
			userDAO.makePersistent(new User(securityService.getContext().getUserId(), securityService.getContext().getDisplayName()));
			
			actuator.setLockExpirationTime(Timestamp.from(Instant.now()));

			Actuator result = actuatorData.makePersistent(actuator);
			controlStructureDataService.alterBox(projectId, actuatorId, "Actuator", result.getName());
			return result;
		}
		return null;
	}

	/**
	 * deletes an existing actuator
	 * 
	 * @param projectId  the project id
	 * @param actuatorId the actuator id
	 * @return returns true if the deletion was successfully
	 */
	public boolean deleteActuator(UUID projectId, int actuatorId) {
		Actuator actuator = actuatorData.findById(new ProjectDependentKey(projectId, actuatorId), false);

		if (actuator != null) {
			actuatorData.makeTransient(actuator);
			controlStructureDataService.alterBox(projectId, actuatorId, "Actuator", "");
			return true;
		}

		return false;
	}

	/**
	 * get an actuator by its id
	 * 
	 * @param projectId  the project id
	 * @param actuatorId the actuator id
	 * @return returns the actuator with this id
	 */
	public Actuator getActuatorById(UUID projectId, int actuatorId) {
		return actuatorData.findById(new ProjectDependentKey(projectId, actuatorId), false);
	}

	// TODO: Complete Documentation (filter) @Rico
	/**
	 * returns a list of actuators with the given parameters
	 * 
	 * @param projectId the project id
	 * @param amount    the amount of results
	 * @param from      skips the first x results
	 * @return returns a list if actuators
	 */
	public List<Actuator> getAllActuators(UUID projectId, Filter filter, Integer amount, Integer from) {
		LinkedHashMap<String, SortOrder> order = new LinkedHashMap<>();
		order.put(Actuator.EntityAttributes.ID, SortOrder.ASC);
		
		if (filter != null && filter.getFieldName() != null && filter.getFieldValue() != null) {			
			if (filter.getFieldName().equals("isUnlinked") && Boolean.valueOf(filter.getFieldValue())) {
				return actuatorData.getAllUnlinkedActuators(projectId);
			} 
		}
			return actuatorData.findFromTo(from, amount, order, projectId);
	}

	public Actuator getActuatorByBoxId(UUID projectId, String boxId) {
		return actuatorData.getByBoxId(projectId, boxId);
	}
	
	public Actuator setActuatorBoxId (UUID projectId, int actuatorId, String boxId) {
		return actuatorData.setBoxId(projectId, actuatorId, boxId);
	}
}
