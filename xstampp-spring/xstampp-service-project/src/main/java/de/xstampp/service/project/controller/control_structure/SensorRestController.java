package de.xstampp.service.project.controller.control_structure;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.xstampp.common.dto.Response;
import de.xstampp.common.dto.project.SearchRequestDTO;
import de.xstampp.common.utils.DeserializationUtil;
import de.xstampp.common.utils.EntityNameConstants;
import de.xstampp.common.utils.PrivilegeCheck;
import de.xstampp.common.utils.Privileges;
import de.xstampp.common.utils.SerializationUtil;
import de.xstampp.service.project.data.dto.control_structure.SensorRequestDTO;
import de.xstampp.service.project.data.entity.control_structure.Sensor;
import de.xstampp.service.project.service.RequestPushService;
import de.xstampp.service.project.service.RequestPushService.Method;
import de.xstampp.service.project.service.data.control_structure.SensorDataService;
import de.xstampp.service.project.util.annotation.CheckLock;
import de.xstampp.service.project.util.annotation.XStamppEntityId;
import de.xstampp.service.project.util.annotation.XStamppProjectId;

@RestController
@RequestMapping("/api/project")
public class SensorRestController {
	
	private SerializationUtil ser = new SerializationUtil();
	private DeserializationUtil deSer = new DeserializationUtil();
	
	@Autowired
	SensorDataService sensorDataService;
	
	@Autowired
	RequestPushService push;
	
	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@RequestMapping(value = "/{id}/sensor", method = RequestMethod.POST)
	public String create (@PathVariable("id") @XStamppProjectId String projectId, @RequestBody String body) throws IOException {
		SensorRequestDTO sensorDTO = deSer.deserialize(body, SensorRequestDTO.class);
		Sensor result = sensorDataService.createSensor(UUID.fromString(projectId), sensorDTO);
		
		if (result != null) {
			push.notify(String.valueOf(result.getId().getId()), projectId, EntityNameConstants.SENSOR, Method.CREATE);
		}
		
		return ser.serialize(result);
	}
	
	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.SENSOR)
	@RequestMapping(value = "/{id}/sensor/{sensorId}", method = RequestMethod.PUT)
	public String alter (@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("sensorId") @XStamppEntityId int id, @RequestBody String body) throws IOException {
		SensorRequestDTO sensorDTO = deSer.deserialize(body, SensorRequestDTO.class);
		Sensor result = sensorDataService.alterSensor(UUID.fromString(projectId), id, sensorDTO);
		
		if (result != null) {
			push.notify(String.valueOf(result.getId().getId()), projectId, EntityNameConstants.SENSOR, Method.ALTER);
		}
		
		return ser.serialize(result);
	}
	
	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.SENSOR)
	@RequestMapping(value = "/{id}/sensor/{sensorId}", method = RequestMethod.DELETE)
	public String delete (@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("sensorId") @XStamppEntityId int id) throws IOException {
		boolean result = sensorDataService.deleteSensor(UUID.fromString(projectId), id);
		
		if (result) {
			push.notify(String.valueOf(id), projectId, EntityNameConstants.SENSOR, Method.DELETE);
		}
		
		return ser.serialize(new Response(result));
	}
	
	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/sensor/{sensorId}", method = RequestMethod.GET)
	public String getById (@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("sensorId") int id) throws IOException {
		return ser.serialize(sensorDataService.getSensorById(UUID.fromString(projectId), id));
	}
	
	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/sensor/search", method = RequestMethod.POST)
	public String getAll(@PathVariable("id") @XStamppProjectId String projectId, @RequestBody String body) throws IOException {
		SearchRequestDTO search = deSer.deserialize(body, SearchRequestDTO.class);
		return ser.serialize(sensorDataService.getAllSensors(UUID.fromString(projectId), search.getFilter(), search.getAmount(), search.getFrom()));
	}
	
	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/sensor/box/{boxId}", method = RequestMethod.GET)
	public String getByBoxId (@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("boxId") String boxId) throws IOException {
		return ser.serialize(sensorDataService.getSensorByBoxId(UUID.fromString(projectId), boxId));
	}
	
	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.CONTROL_STRUCTURE)
	@RequestMapping(value = "/{id}/sensor/{sensorId}/box/{boxId}", method = RequestMethod.PUT)
	public String setBoxId(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("sensorId") int sensorId,
			@PathVariable("boxId") String boxId) throws IOException {
		// set to real null
		if (boxId.equals("null")) {
			boxId = null;
		}
		Sensor result = this.sensorDataService.setSensorBoxId(UUID.fromString(projectId), sensorId, boxId);
		return ser.serialize(result);
	}

}
