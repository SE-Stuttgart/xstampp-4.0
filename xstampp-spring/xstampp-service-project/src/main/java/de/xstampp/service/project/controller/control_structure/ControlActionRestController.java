package de.xstampp.service.project.controller.control_structure;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.xstampp.common.dto.project.SearchRequestDTO;
import de.xstampp.common.utils.DeserializationUtil;
import de.xstampp.common.utils.EntityNameConstants;
import de.xstampp.common.utils.PrivilegeCheck;
import de.xstampp.common.utils.Privileges;
import de.xstampp.common.utils.SerializationUtil;
import de.xstampp.service.project.data.dto.control_structure.ControlActionRequestDTO;
import de.xstampp.service.project.data.entity.control_structure.ControlAction;
import de.xstampp.service.project.service.RequestPushService;
import de.xstampp.service.project.service.RequestPushService.Method;
import de.xstampp.service.project.service.data.control_structure.ControlActionDataService;
import de.xstampp.service.project.util.annotation.CheckLock;
import de.xstampp.service.project.util.annotation.XStamppEntityId;
import de.xstampp.service.project.util.annotation.XStamppProjectId;

@RestController
@RequestMapping("/api/project")
public class ControlActionRestController {
	
	private SerializationUtil ser = new SerializationUtil();
	private DeserializationUtil deSer = new DeserializationUtil();
	
	@Autowired
	ControlActionDataService controlActionDataService;
	
	@Autowired
	RequestPushService push;

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@RequestMapping(value = "/{id}/control-action", method = RequestMethod.POST)
	public String create (@PathVariable("id") @XStamppProjectId String projectId, @RequestBody String body) throws IOException {
		ControlActionRequestDTO controlActionDTO = deSer.deserialize(body, ControlActionRequestDTO.class);
		ControlAction result = controlActionDataService.createControlAction(UUID.fromString(projectId), controlActionDTO); 
		
		if (result != null) {
			push.notify(String.valueOf(result.getId().getId()), projectId, EntityNameConstants.CONTROL_ACTION, Method.CREATE);
		}
		
		return ser.serialize(result);
	}
	
	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.CONTROL_ACTION)
	@RequestMapping(value = "/{id}/control-action/{controlActionId}", method = RequestMethod.PUT)
	public String alter (@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("controlActionId") @XStamppEntityId int id, @RequestBody String body) throws IOException {
		ControlActionRequestDTO controlActionDTO = deSer.deserialize(body, ControlActionRequestDTO.class);
		ControlAction result = controlActionDataService.alterControlAction(UUID.fromString(projectId), id, controlActionDTO);
		
		if (result != null) {
			push.notify(String.valueOf(result.getId().getId()), projectId, EntityNameConstants.CONTROL_ACTION, Method.CREATE);
		}
		
		return ser.serialize(result);
	}
	
	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.CONTROL_ACTION)
	@RequestMapping(value = "/{id}/control-action/{controlActionId}", method = RequestMethod.DELETE)
	public String delete (@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("controlActionId") @XStamppEntityId int id) throws IOException {
		boolean result = controlActionDataService.deleteControlAction(UUID.fromString(projectId), id);
		if (result) {
			push.notify(String.valueOf(id), projectId, EntityNameConstants.CONTROL_ACTION, Method.CREATE);
		}
		return ser.serialize(result);
	}
	
	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/control-action/{controlActionId}", method = RequestMethod.GET)
	public String getById (@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("controlActionId") int id) throws IOException {
		return ser.serialize(controlActionDataService.getControlActionById(UUID.fromString(projectId), id));
	}
	
	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/control-action/search", method = RequestMethod.POST)
	public String getAll(@PathVariable("id") @XStamppProjectId String projectId, @RequestBody String body) throws IOException {
		SearchRequestDTO search = deSer.deserialize(body, SearchRequestDTO.class);
		return ser.serialize(controlActionDataService.getAllControlActions(UUID.fromString(projectId), search.getFilter(), search.getAmount(), search.getFrom()));
	}
	
	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/control-action/arrow/{arrowId}", method = RequestMethod.GET)
	public String getByArrowId (@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("arrowId") String arrowId) throws IOException {
		return ser.serialize(controlActionDataService.getControlActionByArrowId(UUID.fromString(projectId), arrowId));
	}
	
	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.CONTROL_STRUCTURE)
	@RequestMapping(value = "/{id}/control-action/{controlActionId}/arrow/{arrowId}", method = RequestMethod.PUT)
	public String setArrowId(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("controlActionId") int controlActionId,
			@PathVariable("arrowId") String arrowId) throws IOException {
		
		// set to real null
		if (arrowId.equals("null")) {
			arrowId = null;
		}
		ControlAction result = this.controlActionDataService.setControlActionArrowId(UUID.fromString(projectId), controlActionId, arrowId);
		return ser.serialize(result);
	}
}
