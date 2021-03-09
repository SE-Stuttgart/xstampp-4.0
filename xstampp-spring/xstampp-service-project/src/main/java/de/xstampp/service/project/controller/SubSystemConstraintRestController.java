package de.xstampp.service.project.controller;

import de.xstampp.common.dto.Response;
import de.xstampp.common.dto.project.SearchRequestDTO;
import de.xstampp.common.utils.*;
import de.xstampp.service.project.data.dto.SubSystemConstraintRequestDTO;
import de.xstampp.service.project.data.entity.SubSystemConstraint;
import de.xstampp.service.project.service.RequestPushService;
import de.xstampp.service.project.service.RequestPushService.Method;
import de.xstampp.service.project.service.data.SubSystemConstraintDataService;
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
 * Rest controller for handling all incoming requests for the sub system constraints
 *
 */
@RestController
@RequestMapping(value = "/api/project")
public class SubSystemConstraintRestController {
	@Autowired
	SubSystemConstraintDataService subSystemConstraintData;
	
	@Autowired
	RequestPushService push;

	SerializationUtil ser = new SerializationUtil();
	DeserializationUtil deSer = new DeserializationUtil();

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@RequestMapping(value = "/{id}/system-constraint/{constId}/sub-system-constraint", method = RequestMethod.POST)
	public String createSubSystemConstraint(@PathVariable("id") @XStamppProjectId String projectId,
			@PathVariable(value = "constId") int constId, @RequestBody String body) throws IOException {
		SubSystemConstraintRequestDTO subConst = deSer.deserialize(body, SubSystemConstraintRequestDTO.class);
		SubSystemConstraint constraint = subSystemConstraintData.createSubSystemConstraint(UUID.fromString(projectId), constId, subConst);
		
		if (constraint != null) {
			push.notify(String.valueOf(constraint.getId().getId()), projectId, "sub-system-safety-constraint", Method.CREATE);
		}

		return ser.serialize(constraint);
	}

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.SUB_SYSTEM_CONSTRAINT)
	@RequestMapping(value = "{id}/system-constraint/{constId}/sub-system-constraint/{subConstId}", method = RequestMethod.PUT)
	public String alterSubSystemConstraint(@PathVariable(value = "id") @XStamppProjectId String projectId,
			@PathVariable(value = "constId") @XStamppEntityParentId int constId, @PathVariable(value = "subConstId") @XStamppEntityId int subConstId,
			@RequestBody String body) throws IOException {
		SubSystemConstraintRequestDTO subConst = deSer.deserialize(body, SubSystemConstraintRequestDTO.class);
		SubSystemConstraint constraint = subSystemConstraintData.alterSubSystemConstraint(UUID.fromString(projectId), constId,
				subConstId, subConst);
		
		if (constraint != null) {
			push.notify(String.valueOf(constraint.getId().getId()), projectId, "sub-system-safety-constraint", Method.ALTER);
		}
		
		return ser.serialize(constraint);
	}

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.SUB_SYSTEM_CONSTRAINT)
	@RequestMapping(value = "{id}/system-constraint/{constId}/sub-system-constraint/{subConstId}", method = RequestMethod.DELETE)
	public String deleteSubSystemConstraint(@PathVariable(value = "id") @XStamppProjectId String projectId,
			@PathVariable(value = "constId") @XStamppEntityParentId int constId, @PathVariable(value = "subConstId") @XStamppEntityId int subConstId)
			throws IOException {

		boolean result = subSystemConstraintData.deleteSubSystemConstraint(UUID.fromString(projectId), constId,
				subConstId);
		
		if (result) {
			push.notify(String.valueOf(subConstId), projectId, "sub-system-safety-constraint", Method.DELETE);
		}
		
		return ser.serialize(new Response(result));
	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "{id}/system-constraint/{constId}/sub-system-constraint/{subConstId}", method = RequestMethod.GET)
	public String getSystemConstraintById(@PathVariable(value = "id") @XStamppProjectId String projectId,
			@PathVariable(value = "constId") int constId, @PathVariable(value = "subConstId") int subConstId)
			throws IOException {

		return ser.serialize(
				subSystemConstraintData.getSubSystemConstraintById(UUID.fromString(projectId), constId, subConstId));
	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "{id}/system-constraint/{constId}/sub-system-constraint/search", method = RequestMethod.POST)
	public String getAllSystemConstraintes(@PathVariable(value = "id") @XStamppProjectId String projectId,
			@PathVariable(value = "constId") int constId, @RequestBody String body) throws IOException {

		SearchRequestDTO searchRequest;
		searchRequest = deSer.deserialize(body, SearchRequestDTO.class);

		return ser.serialize(subSystemConstraintData.getAllSubSystemConstraints(UUID.fromString(projectId), constId,
				null, searchRequest.getOrderBy(), searchRequest.getOrderDirection(), searchRequest.getAmount(),
				searchRequest.getFrom()));
	}

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@RequestMapping(value = {
			"{id}/system-constraint/{constId}/sub-system-constraint/{subConstId}/link/hazard/{hazardId}/sub-hazard/{subHazardId}",
			"{id}/hazard/{hazardId}/sub-hazard/{subHazardId}/link/system-constraint/{constId}/sub-system-constraint/{subConstId}" }, method = RequestMethod.POST)
	public String createSubHazardSubSystemConstraintLink(@PathVariable(value = "id") @XStamppProjectId String projectId,
			@PathVariable(value = "hazardId") int hazardId, @PathVariable(value = "subHazardId") int subHazardId,
			@PathVariable(value = "constId") int constId, @PathVariable(value = "subConstId") int subConstId)
			throws IOException {

		return ser.serialize(new Response(subSystemConstraintData.createSubHazardSubSystemConstraintLink(
				UUID.fromString(projectId), hazardId, subHazardId, constId, subConstId)));
	}

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@RequestMapping(value = {
			"{id}/hazard/{hazardId}/sub-hazard/{subHazardId}/link/system-constraint/{constId}/sub-system-constraint/{subConstId}",
			"{id}/system-constraint/{constId}/sub-system-constraint/{subConstId}/link/hazard/{hazardId}/sub-hazard/{subHazardId}" }, method = RequestMethod.DELETE)
	public String deleteSubHazardSubSystemConstraintLink(@PathVariable(value = "id") @XStamppProjectId String projectId,
			@PathVariable(value = "hazardId") int hazardId, @PathVariable(value = "subHazardId") int subHazardId,
			@PathVariable(value = "constId") int constId, @PathVariable(value = "subConstId") int subConstId)
			throws IOException {

		return ser.serialize(new Response(subSystemConstraintData.deleteSubHazardSubSystemConstraintLink(
				UUID.fromString(projectId), hazardId, subHazardId, constId, subConstId)));
	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "{id}/system-constraint/hazard/{hazardId}/sub-hazard/{subHazardId}", method = RequestMethod.GET)
	public String getSubSystemContraintBySubHazardId(@PathVariable(value = "id") @XStamppProjectId String projectId,
			@PathVariable(value = "hazardId") int hazardId, @PathVariable(value = "subHazardId") int subHazardId)
			throws IOException {
		return ser.serialize(subSystemConstraintData.getSubSystemConstraintBySubHazardId(UUID.fromString(projectId),
				hazardId, subHazardId));
	}

}
