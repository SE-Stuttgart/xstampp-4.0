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
import de.xstampp.service.project.data.dto.control_structure.SensorRequestDTO;
import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.control_structure.Sensor;
import de.xstampp.service.project.service.dao.control_structure.iface.ISensorDAO;
import de.xstampp.service.project.service.dao.iface.ILastIdDAO;
import de.xstampp.service.project.service.dao.iface.SortOrder;

@Service
@Transactional
public class SensorDataService {

	@Autowired
	ILastIdDAO lastId;

	@Autowired
	SecurityService securityService;
	
	@Autowired
	ISensorDAO sensorData;

	@Autowired
	IUserDAO userDAO;

	@Autowired
	ControlStructureDataService controlStructureDataService;

	/**
	 * creates a new sensor
	 * 
	 * @param projectId the project id
	 * @param sensorDTO the sensor dto with the new data
	 * @return returns the created Sensor
	 */
	public Sensor createSensor(UUID projectId, SensorRequestDTO sensorDTO) {
		ProjectDependentKey key = new ProjectDependentKey(projectId, lastId.getNewIdForEntity(projectId, Sensor.class));
		
		Sensor sensor = new Sensor();
		sensor.setId(key);
		sensor.setName(sensorDTO.getName());
		sensor.setDescription(sensorDTO.getDescription());

		sensor.setLastEditNow(securityService.getContext().getUserId());
		userDAO.makePersistent(new User(securityService.getContext().getUserId(), securityService.getContext().getDisplayName()));

		if (sensorDTO.getState() != null) {
			sensor.setState(sensorDTO.getState());
		} else {
			sensor.setState("DOING");
		}
		

		return sensorData.makePersistent(sensor);
	}

	/**
	 * alters an existing sensor
	 * 
	 * @param projectId the project id
	 * @param sensorId  the sensor id
	 * @param sensorDTO the sensor dto with the new data
	 * @return the altered sensor
	 */
	public Sensor alterSensor(UUID projectId, int sensorId, SensorRequestDTO sensorDTO) {
		Sensor sensor = sensorData.findById(new ProjectDependentKey(projectId, sensorId), false);
		
		if (sensor != null) {
			sensor.setName(sensorDTO.getName());
			sensor.setDescription(sensorDTO.getDescription());

			sensor.setLastEditNow(securityService.getContext().getUserId());
			userDAO.makePersistent(new User(securityService.getContext().getUserId(), securityService.getContext().getDisplayName()));
			sensor.setState(sensorDTO.getState());
			
			sensor.setLockExpirationTime(Timestamp.from(Instant.now()));
			Sensor result = sensorData.makePersistent(sensor);
			controlStructureDataService.alterBox(projectId, sensorId, "Sensor", result.getName());
			return result;
		}
		return null;
	}

	/**
	 * deletes an exisiting sensor
	 * 
	 * @param projectId the project id
	 * @param sensorId  the sensor id
	 * @return returns true if the deletion was successfully
	 */
	public boolean deleteSensor(UUID projectId, int sensorId) {
		Sensor sensor = sensorData.findById(new ProjectDependentKey(projectId, sensorId), false);
		
		if (sensor != null) {
			sensorData.makeTransient(sensor);
			controlStructureDataService.alterBox(projectId, sensorId, "Sensor", "");
			return true;
		}
		
		return false;
	}

	/**
	 * gets a sensor by its id
	 * 
	 * @param projectId the project id
	 * @param sensorId  the sensor id
	 * @return returns the sensor with this id
	 */
	public Sensor getSensorById(UUID projectId, int sensorId) {
		return sensorData.findById(new ProjectDependentKey(projectId, sensorId), false);
	}

	// TODO: Complete Documentation (filter) @Rico
	/**
	 * returns a list if all sensors
	 * 
	 * @param projectId the project id
	 * @param amount    the amount of results
	 * @param from      skips the first x results
	 * @return returns a list of sensors
	 */
	public List<Sensor> getAllSensors(UUID projectId, Filter filter, Integer amount, Integer from) {
		LinkedHashMap<String, SortOrder> order = new LinkedHashMap<>();
		order.put(Sensor.EntityAttributes.ID, SortOrder.ASC);
		
		if (filter != null && filter.getFieldName() != null && filter.getFieldValue() != null) {			
			if (filter.getFieldName().equals("isUnlinked") && Boolean.valueOf(filter.getFieldValue())) {
				return sensorData.getAllUnlinkedSensor(projectId);
			} 
		}
		
		return sensorData.findFromTo(from, amount, order, projectId);
	}
	
	public Sensor getSensorByBoxId (UUID projectId, String boxId) {
		return sensorData.getByBoxId(projectId, boxId);
	}

	public Sensor setSensorBoxId (UUID projectId, int sensorId, String boxId) {
		return sensorData.setBoxId(projectId, sensorId, boxId);
	}
}
