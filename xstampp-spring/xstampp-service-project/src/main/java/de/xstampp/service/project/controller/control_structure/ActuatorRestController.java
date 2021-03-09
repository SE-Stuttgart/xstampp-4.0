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
import de.xstampp.service.project.data.dto.control_structure.ActuatorRequestDTO;
import de.xstampp.service.project.data.entity.control_structure.Actuator;
import de.xstampp.service.project.service.RequestPushService;
import de.xstampp.service.project.service.RequestPushService.Method;
import de.xstampp.service.project.service.data.control_structure.ActuatorDataService;
import de.xstampp.service.project.util.annotation.CheckLock;
import de.xstampp.service.project.util.annotation.XStamppEntityId;
import de.xstampp.service.project.util.annotation.XStamppProjectId;

@RestController
@RequestMapping("/api/project")
public class ActuatorRestController {

	private SerializationUtil ser = new SerializationUtil();
	private DeserializationUtil deSer = new DeserializationUtil();

	@Autowired
	ActuatorDataService actuatorDataService;

	@Autowired
	RequestPushService push;

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@RequestMapping(value = "/{id}/actuator", method = RequestMethod.POST)
	public String create(@PathVariable("id") @XStamppProjectId String projectId, @RequestBody String body) throws IOException {
		ActuatorRequestDTO actuatorDTO = deSer.deserialize(body, ActuatorRequestDTO.class);
		Actuator result = actuatorDataService.createActuator(UUID.fromString(projectId), actuatorDTO);

		if (result != null) {
			push.notify(String.valueOf(result.getId().getId()), projectId, EntityNameConstants.ACTUATOR, Method.CREATE);
		}
		return ser.serialize(result);
	}

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.ACTUATOR)
	@RequestMapping(value = "/{id}/actuator/{actuatorId}", method = RequestMethod.PUT)
	public String alter(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("actuatorId") @XStamppEntityId int id,
			@RequestBody String body) throws IOException {
		ActuatorRequestDTO actuatorDTO = deSer.deserialize(body, ActuatorRequestDTO.class);
		Actuator result = actuatorDataService.alterActuator(UUID.fromString(projectId), id, actuatorDTO);

		if (result != null) {
			push.notify(String.valueOf(result.getId().getId()), projectId, EntityNameConstants.ACTUATOR, Method.ALTER);
		}

		return ser.serialize(result);
	}

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.ACTUATOR)
	@RequestMapping(value = "/{id}/actuator/{actuatorId}", method = RequestMethod.DELETE)
	public String delete(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("actuatorId") @XStamppEntityId int id) throws IOException {
		boolean result = actuatorDataService.deleteActuator(UUID.fromString(projectId), id);

		if (result) {
			push.notify(String.valueOf(id), projectId, EntityNameConstants.ACTUATOR, Method.DELETE);
		}

		return ser.serialize(new Response());
	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/actuator/{actuatorId}", method = RequestMethod.GET)
	public String getById(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("actuatorId") int id) throws IOException {
		return ser.serialize(actuatorDataService.getActuatorById(UUID.fromString(projectId), id));
	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/actuator/search", method = RequestMethod.POST)
	public String getAll(@PathVariable("id") @XStamppProjectId String projectId, @RequestBody String body) throws IOException {
		SearchRequestDTO search = deSer.deserialize(body, SearchRequestDTO.class);
		return ser.serialize(actuatorDataService.getAllActuators(UUID.fromString(projectId), search.getFilter(),
				search.getAmount(), search.getFrom()));
	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/actuator/box/{boxId}", method = RequestMethod.GET)
	public String getByBoxId(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("boxId") String boxId)
			throws IOException {
		return ser.serialize(actuatorDataService.getActuatorByBoxId(UUID.fromString(projectId), boxId));
	}

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.CONTROL_STRUCTURE)
	@RequestMapping(value = "/{id}/actuator/{actuatorId}/box/{boxId}", method = RequestMethod.PUT)
	public String setBoxId(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("actuatorId") int actuatorId,
			@PathVariable("boxId") String boxId) throws IOException {
		
		// set to real null
		if (boxId.equals("null")) {
			boxId = null;
		}
		
		Actuator result = this.actuatorDataService.setActuatorBoxId(UUID.fromString(projectId), actuatorId, boxId);
		return ser.serialize(result);
	}
}
