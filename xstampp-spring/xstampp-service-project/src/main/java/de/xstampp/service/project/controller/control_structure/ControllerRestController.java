package de.xstampp.service.project.controller.control_structure;

import de.xstampp.common.dto.Response;
import de.xstampp.common.dto.project.SearchRequestDTO;
import de.xstampp.common.utils.*;
import de.xstampp.service.project.data.dto.control_structure.ControllerRequestDTO;
import de.xstampp.service.project.data.entity.Responsibility;
import de.xstampp.service.project.data.entity.control_structure.Controller;
import de.xstampp.service.project.service.RequestPushService;
import de.xstampp.service.project.service.RequestPushService.Method;
import de.xstampp.service.project.service.data.ResponsibilityDataService;
import de.xstampp.service.project.service.data.control_structure.ControllerDataService;
import de.xstampp.service.project.util.annotation.CheckLock;
import de.xstampp.service.project.util.annotation.XStamppEntityId;
import de.xstampp.service.project.util.annotation.XStamppProjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/project")
public class ControllerRestController {
	
	private SerializationUtil ser = new SerializationUtil();
	private DeserializationUtil deSer = new DeserializationUtil();
	
	@Autowired
	ControllerDataService controllerDataService;

	@Autowired
	ResponsibilityDataService responsibilityDataService;
	
	@Autowired
	RequestPushService push;
	
	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@RequestMapping(value = "/{id}/controller", method = RequestMethod.POST)
	public String create(@PathVariable("id") @XStamppProjectId String projectId, @RequestBody String body) throws IOException {
		ControllerRequestDTO controllerDTO = deSer.deserialize(body, ControllerRequestDTO.class);
		Controller result = controllerDataService.createController(UUID.fromString(projectId), controllerDTO);
				
		if (result != null) {
			push.notify(String.valueOf(result.getId().getId()), projectId, EntityNameConstants.CONTROLLER, Method.CREATE);
		}
		
		return ser.serialize(result);
	}
	
	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.CONTROLLER)
	@RequestMapping(value = "/{id}/controller/{controllerId}", method = RequestMethod.PUT)
	public String alter(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("controllerId") @XStamppEntityId int id, @RequestBody String body) throws IOException {
		ControllerRequestDTO controllerDTO = deSer.deserialize(body, ControllerRequestDTO.class);
		Controller result = controllerDataService.alterController(UUID.fromString(projectId), id, controllerDTO);
		
		if (result != null) {
			push.notify(String.valueOf(result.getId().getId()), projectId, EntityNameConstants.CONTROLLER, Method.ALTER);
		}
		
		return ser.serialize(result);
	}
	
	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.CONTROLLER)
	@RequestMapping(value = "/{id}/controller/{controllerId}", method = RequestMethod.DELETE)
	public String delete(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("controllerId") @XStamppEntityId int id) throws IOException {
		boolean result = controllerDataService.deleteController(UUID.fromString(projectId), id);
		
		if (result) {
			push.notify(String.valueOf(id), projectId, EntityNameConstants.CONTROLLER, Method.DELETE);
		}
		
		return ser.serialize(new Response(result));
	}
	
	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/controller/{controllerId}", method = RequestMethod.GET)
	public String getById(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("controllerId") int id) throws IOException {
		return ser.serialize(controllerDataService.getControllerById(UUID.fromString(projectId), id));
	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/responsibility/{responsibilityId}/controller", method = RequestMethod.GET)
	public String getControllerByResponsibilityId(@PathVariable("id") @XStamppProjectId String projectId,
												  @PathVariable("responsibilityId") int responsibilityId) throws IOException {
		Responsibility responsibility = responsibilityDataService.getResponsibilityById(UUID.fromString(projectId), responsibilityId);
		return ser.serialize(controllerDataService.getControllerById(UUID.fromString(projectId), responsibility.getControllerId()));
	}
	
	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/controller/search", method = RequestMethod.POST)
	public String getAll(@PathVariable("id") @XStamppProjectId String projectId, @RequestBody String body) throws IOException {
		SearchRequestDTO search = deSer.deserialize(body, SearchRequestDTO.class);
		return ser.serialize(controllerDataService.getAllController(UUID.fromString(projectId), search.getFilter(), search.getAmount(), search.getFrom()));
	}
	
	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/controller/box/{boxId}", method = RequestMethod.GET)
	public String getByBoxId(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("boxId") String boxId) throws IOException {
		return ser.serialize(controllerDataService.getControllerByBoxId(UUID.fromString(projectId), boxId));
	}
	
	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.CONTROL_STRUCTURE)
	@RequestMapping(value = "/{id}/controller/{controllerId}/box/{boxId}", method = RequestMethod.PUT)
	public String setBoxId(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("controllerId") int controllerId,
			@PathVariable("boxId") String boxId) throws IOException {
		// set to real null
		if (boxId.equals("null")) {
			boxId = null;
		}
		Controller result = this.controllerDataService.setControllerBoxId(UUID.fromString(projectId), controllerId, boxId);
		return ser.serialize(result);
	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/process-variable/sourceEntities/{controllerId}", method = RequestMethod.GET)
	public String getSourceEntitiesByControllerId(@PathVariable ("id") @XStamppProjectId String projectId,
												  @PathVariable ("controllerId") int controllerId) throws IOException{

		return ser.serialize(controllerDataService.getSourceBoxByControllerId(UUID.fromString(projectId), controllerId));
	}
}
