package de.xstampp.service.project.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.xstampp.common.dto.Response;
import de.xstampp.common.utils.*;
import de.xstampp.service.project.data.dto.ControllerConstraintRequestDTO;
import de.xstampp.service.project.data.entity.ControllerConstraint;
import de.xstampp.service.project.service.RequestPushService;
import de.xstampp.service.project.service.RequestPushService.Method;
import de.xstampp.service.project.service.data.ControllerConstraintDataService;
import de.xstampp.service.project.util.annotation.CheckLock;
import de.xstampp.service.project.util.annotation.XStamppEntityId;
import de.xstampp.service.project.util.annotation.XStamppEntityParentId;
import de.xstampp.service.project.util.annotation.XStamppProjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

/**
 * 
 * Rest controller for handling all incoming requests for the controller constraints
 *
 */
@RestController
@RequestMapping("/api/project")
public class ControllerConstraintController {

	@Autowired
	ControllerConstraintDataService cCData;

	@Autowired
	RequestPushService push;

	SerializationUtil ser = new SerializationUtil();
	DeserializationUtil deSer = new DeserializationUtil();

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@RequestMapping(value = "/{projectId}/control-action/{controlActionId}/UCA/{ucaId}/controller-constraint", method = RequestMethod.POST)
	public String createControllerConstraint(@RequestBody String body, @PathVariable("projectId") @XStamppProjectId String projectId,
			@PathVariable("controlActionId") int controlActionId, @PathVariable("ucaId") int ucaId) throws IOException {
		ControllerConstraintRequestDTO request = deSer.deserialize(body, ControllerConstraintRequestDTO.class);

		ControllerConstraint result = cCData.createControllerConstraint(request, UUID.fromString(projectId),
				controlActionId, ucaId);

		if (result != null) {
			push.notify(String.valueOf(controlActionId), projectId, EntityNameConstants.CONTROLLER_CONSTRAINT,
					Method.CREATE);
		}
		return ser.serialize(result);
	}

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.CONTROLLER_CONSTRAINT)
	@RequestMapping(value = "{projectId}/control-action/{controlActionId}/UCA/{ucaId}/controller-constraint/{id}", method = RequestMethod.DELETE)
	public String deleteControllerConstraint(@PathVariable("projectId") @XStamppProjectId String id,
			@PathVariable("controlActionId") @XStamppEntityParentId int controlActionId, @PathVariable("ucaId") @XStamppEntityId int ucaId) throws IOException {

		boolean result = cCData.deleteControllerConstraint(UUID.fromString(id), controlActionId, ucaId);

		if (result) {
			push.notify(String.valueOf(controlActionId), id, EntityNameConstants.CONTROLLER_CONSTRAINT, Method.DELETE);
		}

		return ser.serialize(new Response(result));
	}

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.CONTROLLER_CONSTRAINT)
	@RequestMapping(value = "/{projectId}/control-action/{controlActionId}/UCA/{ucaId}/controller-constraint/{id}", method = RequestMethod.PUT)
	public String alterControllerConstraint(@RequestBody String body, @PathVariable("projectId") @XStamppProjectId String projectId,
			@PathVariable("controlActionId") @XStamppEntityParentId int controlActionId, @PathVariable("ucaId") @XStamppEntityId int ucaId,
			@PathVariable("id") int controllerConstraintId) throws IOException {
		ControllerConstraintRequestDTO request = deSer.deserialize(body, ControllerConstraintRequestDTO.class);

		ControllerConstraint result = cCData.alterControllerConstraint(request, UUID.fromString(projectId),
				controlActionId, ucaId);

		if (result != null) {
			push.notify(String.valueOf(controlActionId), projectId, EntityNameConstants.CONTROLLER_CONSTRAINT,
					Method.ALTER);
		}

		return ser.serialize(result);
	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{projectId}/control-action/{controlActionId}/UCA/{ucaId}/controller-constraint/{id}", method = RequestMethod.GET)
	public String getControllerConstraint(@PathVariable("projectId") @XStamppProjectId String projectId,
			@PathVariable("controlActionId") int controlActionId, @PathVariable("ucaId") int ucaId,
			@PathVariable("id") int controllerConstraintId) throws IOException {
		return ser.serialize(cCData.getControllerConstraint(UUID.fromString(projectId), controlActionId, ucaId));
	}

}
