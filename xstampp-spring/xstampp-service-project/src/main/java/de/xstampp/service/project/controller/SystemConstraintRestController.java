package de.xstampp.service.project.controller;

import de.xstampp.common.dto.Response;
import de.xstampp.common.dto.project.PageRequestDTO;
import de.xstampp.common.dto.project.SearchRequestDTO;
import de.xstampp.common.utils.*;
import de.xstampp.service.project.data.dto.SystemConstraintRequestDTO;
import de.xstampp.service.project.data.entity.SystemConstraint;
import de.xstampp.service.project.service.RequestPushService;
import de.xstampp.service.project.service.RequestPushService.Method;
import de.xstampp.service.project.service.data.HazardDataService;
import de.xstampp.service.project.service.data.SystemConstraintDataService;
import de.xstampp.service.project.util.annotation.CheckLock;
import de.xstampp.service.project.util.annotation.XStamppEntityId;
import de.xstampp.service.project.util.annotation.XStamppProjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

/**
 * 
 * Rest controller for handling all incoming requests for the system constraints
 * Passes on requests to services for further processing.
 */
@RestController
@RequestMapping(value = "/api/project")
public class SystemConstraintRestController {

	@Autowired
	SystemConstraintDataService systemConstraintData;

	@Autowired
	HazardDataService hazardData;

	@Autowired
	RequestPushService push;

	SerializationUtil ser = new SerializationUtil();
	DeserializationUtil deSer = new DeserializationUtil();

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@RequestMapping(value = "/{id}/system-constraint", method = RequestMethod.POST)
	public String createSystemConstraint(@PathVariable("id") @XStamppProjectId String projectId, @RequestBody String body)
			throws IOException {
		SystemConstraintRequestDTO request = deSer.deserialize(body, SystemConstraintRequestDTO.class);
		SystemConstraint result = systemConstraintData.createSystemConstraint(request, UUID.fromString(projectId));

		if (result != null) {
			push.notify(String.valueOf(result.getId().getId()), projectId, EntityNameConstants.SYSTEM_CONSTRAINT,
					Method.CREATE);
		}

		return ser.serialize(result);
	}

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.SYSTEM_CONSTRAINT)
	@RequestMapping(value = "{id}/system-constraint/{constId}", method = RequestMethod.PUT)
	public String alterSystemConstraint(@PathVariable(value = "id") @XStamppProjectId String projectId,
			@PathVariable(value = "constId") @XStamppEntityId int constId, @RequestBody String body) throws IOException {
		SystemConstraintRequestDTO request = deSer.deserialize(body, SystemConstraintRequestDTO.class);
		SystemConstraint result = systemConstraintData.alterSystemConstraint(request, UUID.fromString(projectId),
				constId);

		if (result != null) {
			push.notify(String.valueOf(result.getId().getId()), projectId, EntityNameConstants.SYSTEM_CONSTRAINT,
					Method.ALTER);
		}

		return ser.serialize(result);
	}

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.SYSTEM_CONSTRAINT)
	@RequestMapping(value = "{id}/system-constraint/{constId}", method = RequestMethod.DELETE)
	public String deleteSystemConstraint(@PathVariable(value = "id") @XStamppProjectId String projectId,
			@PathVariable(value = "constId") @XStamppEntityId int constId) throws IOException {

		boolean result = systemConstraintData.deleteSystemConstraint(UUID.fromString(projectId), constId);

		if (result) {
			push.notify(String.valueOf(constId), projectId, EntityNameConstants.SYSTEM_CONSTRAINT, Method.CREATE);
		}
		return ser.serialize(new Response(result));
	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "{id}/system-constraint/{constId}", method = RequestMethod.GET)
	public String getSystemConstraintById(@PathVariable(value = "id") @XStamppProjectId String projectId,
			@PathVariable(value = "constId") int constId) throws IOException {

		return ser.serialize(systemConstraintData.getSystemConstraintById(constId, UUID.fromString(projectId)));
	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "{id}/system-constraint/search", method = RequestMethod.POST)
	public String getAllSystemConstraints(@PathVariable(value = "id") @XStamppProjectId String projectId, @RequestBody String body)
			throws IOException {

		SearchRequestDTO searchRequest;
		searchRequest = deSer.deserialize(body, SearchRequestDTO.class);

		return ser.serialize(systemConstraintData.getAllSystemConstraints(UUID.fromString(projectId), null,
				searchRequest.getOrderBy(), searchRequest.getOrderDirection(), searchRequest.getAmount(),
				searchRequest.getFrom()));
	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "{id}/system-constraint/{constId}/hazards", method = RequestMethod.POST)
	public String getHazardsBySystemConstraintId(@PathVariable("id") @XStamppProjectId String projectId,
			@PathVariable("constId") int constId, @RequestBody String body) throws IOException {
		PageRequestDTO page = deSer.deserialize(body, PageRequestDTO.class);

		return ser.serialize(hazardData.getHazardsBySystemConstraintId(UUID.fromString(projectId), constId,
				page.getAmount(), page.getFrom()));
	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/responsibility/{respId}/systemconstraints", method = RequestMethod.POST)
	public String getLinkedSystemConstraintByResponsibility( @PathVariable("id") @XStamppProjectId String projectId,
															 @PathVariable("respId") @XStamppEntityId int responsibilityId, @RequestBody String body) throws IOException{
		PageRequestDTO page = deSer.deserialize(body, PageRequestDTO.class);
		return ser.serialize(systemConstraintData.getLinkedSystemConstraintByResponsibility(UUID.fromString(projectId),responsibilityId,page.getAmount(),page.getFrom()));
	}
}
