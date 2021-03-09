package de.xstampp.service.project.controller;

import de.xstampp.common.dto.Response;
import de.xstampp.common.dto.project.PageRequestDTO;
import de.xstampp.common.dto.project.SearchRequestDTO;
import de.xstampp.common.utils.*;
import de.xstampp.service.project.data.dto.HazardRequestDTO;
import de.xstampp.service.project.data.entity.Hazard;
import de.xstampp.service.project.service.RequestPushService;
import de.xstampp.service.project.service.RequestPushService.Method;
import de.xstampp.service.project.service.dao.iface.SortOrder;
import de.xstampp.service.project.service.data.HazardDataService;
import de.xstampp.service.project.service.data.LossDataService;
import de.xstampp.service.project.service.data.SystemConstraintDataService;
import de.xstampp.service.project.util.annotation.CheckLock;
import de.xstampp.service.project.util.annotation.XStamppEntityId;
import de.xstampp.service.project.util.annotation.XStamppProjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

/**
 * 
 * Rest controller for handling all incoming requests for the Hazards
 *
 */
@RestController
@RequestMapping("/api/project")
public class HazardRestController {

	@Autowired
	HazardDataService hazardData;

	@Autowired
	SystemConstraintDataService systemConstraintservice;

	@Autowired
	LossDataService lossData;

	@Autowired
	RequestPushService push;

	SerializationUtil ser = new SerializationUtil();
	DeserializationUtil deSer = new DeserializationUtil();
	Logger logger = LoggerFactory.getLogger(HazardRestController.class);

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@RequestMapping(value = "/{id}/hazard", method = RequestMethod.POST)
	public String createHazard(@PathVariable("id") @XStamppProjectId String projectId, @RequestBody String body) throws IOException {
		HazardRequestDTO request = deSer.deserialize(body, HazardRequestDTO.class);

		Hazard result = hazardData.createHazard(request, UUID.fromString(projectId));

		if (result != null) {
			push.notify(String.valueOf(result.getId().getId()), projectId, EntityNameConstants.HAZARD, Method.CREATE);
		}
		return ser.serialize(result);
	}

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.HAZARD)
	@RequestMapping(value = "/{id}/hazard/{hazardId}", method = RequestMethod.PUT)
	public String alterHazard(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("hazardId") @XStamppEntityId int hazardId,
			@RequestBody String body) throws IOException {
		HazardRequestDTO request = deSer.deserialize(body, HazardRequestDTO.class);

		Hazard result = hazardData.alterHazard(request, UUID.fromString(projectId), hazardId);

		if (result != null) {
			push.notify(String.valueOf(result.getId().getId()), projectId, EntityNameConstants.HAZARD, Method.ALTER);
		}

		return ser.serialize(result);
	}

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.HAZARD)
	@RequestMapping(value = "/{id}/hazard/{hazardId}", method = RequestMethod.DELETE)
	public String deleteHazard(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("hazardId") @XStamppEntityId int hazardId)
			throws IOException {

		boolean result = hazardData.deleteHazard(hazardId, UUID.fromString(projectId));
		if (result) {
			push.notify(String.valueOf(hazardId), projectId, EntityNameConstants.HAZARD, Method.DELETE);
		}
		return ser.serialize(new Response(result));
	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/hazard/{hazardId}", method = RequestMethod.GET)
	public String getHazardById(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("hazardId") int hazardId)
			throws IOException {
		return ser.serialize(hazardData.getHazardById(hazardId, UUID.fromString(projectId)));
	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/hazard/search", method = RequestMethod.POST)
	public String getAllHazards(@PathVariable("id") @XStamppProjectId String projectId, @RequestBody String body) throws IOException {
		SearchRequestDTO searchRequest;
		searchRequest = deSer.deserialize(body, SearchRequestDTO.class);
		SortOrder order = SortOrder.valueOfIgnoreCase(searchRequest.getOrderDirection());
		return ser.serialize(hazardData.getAllHazards(UUID.fromString(projectId), searchRequest.getFilter(),
				searchRequest.getOrderBy(), order, searchRequest.getAmount(), searchRequest.getFrom()));
	}

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@RequestMapping(value = { "/{id}/hazard/{hazardId}/link/loss/{lossId}",
			"/{id}/loss/{lossId}/link/hazard/{hazardId}" }, method = RequestMethod.POST)
	public String createHazardLossLink(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("hazardId") int hazardId,
			@PathVariable("lossId") int lossId) throws IOException {
		boolean result = hazardData.createHazardLossLink(UUID.fromString(projectId), hazardId, lossId);
		return ser.serialize(new Response(result));
	}

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@RequestMapping(value = { "/{id}/hazard/{hazardId}/link/loss/{lossId}",
			"/{id}/loss/{lossId}/link/hazards/{hazardId}" }, method = RequestMethod.DELETE)
	public String deleteHazardLossLink(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("hazardId") int hazardId,
			@PathVariable("lossId") int lossId) throws IOException {

		boolean result = hazardData.deleteHazardLossLink(UUID.fromString(projectId), hazardId, lossId);
		return ser.serialize(new Response(result));
	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/hazard/{hazardId}/losses", method = RequestMethod.POST)
	public String getLossesByHazardId(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("hazardId") int hazardId,
			@RequestBody String body) throws IOException {
		PageRequestDTO page = deSer.deserialize(body, PageRequestDTO.class);

		return ser.serialize(lossData.getAllLossesByHazardId(UUID.fromString(projectId), hazardId, page.getAmount(),
				page.getFrom()));
	}

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@RequestMapping(value = { "/{id}/hazard/{hazardId}/link/system-constraint/{constId}",
			"/{id}/system-constraint/{constId}/link/hazard/{hazardId}" }, method = RequestMethod.POST)
	public String createHazardSystemConstraintLink(@PathVariable("id") @XStamppProjectId String projectId,
			@PathVariable("hazardId") int hazardId, @PathVariable("constId") int constId) throws IOException {
		return ser.serialize(new Response(
				hazardData.createHazardSystemConstraintLink(UUID.fromString(projectId), hazardId, constId)));
	}

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@RequestMapping(value = { "/{id}/hazard/{hazardId}/link/system-constraint/{constId}",
			"/{id}/system-constraint/{constId}/link/hazard/{hazardId}" }, method = RequestMethod.DELETE)
	public String deleteHazaradSystemConstraintLink(@PathVariable("id") @XStamppProjectId String projectId,
			@PathVariable("hazardId") int hazardId, @PathVariable("constId") int constId) throws IOException {
		return ser.serialize(new Response(
				hazardData.deleteHazardSystemConstraintLink(UUID.fromString(projectId), hazardId, constId)));
	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = { "/{id}/hazard/{hazardId}/system-constraints" }, method = RequestMethod.POST)
	public String getSystemConstraintsByHazardId(@PathVariable("id") @XStamppProjectId String projectId,
			@PathVariable("hazardId") int hazardId, @RequestBody String body) throws IOException {
		PageRequestDTO page = deSer.deserialize(body, PageRequestDTO.class);

		return ser.serialize(systemConstraintservice.getAllSystemConstraintsByHazardId(UUID.fromString(projectId),
				hazardId, page.getAmount(), page.getFrom()));
	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = { "/{id}/ControlAction/{controlActionId}/UCA/{ucaId}/link/hazard" }, method = RequestMethod.POST)
	public String getHazardsByUnsafeControlActionId(@PathVariable("id") @XStamppProjectId String projectId,
			@PathVariable("controlActionId") int controlActionId, @PathVariable("ucaId") int ucaId,
			@RequestBody String body) throws IOException {

		PageRequestDTO request = deSer.deserialize(body, PageRequestDTO.class);

		return ser.serialize(hazardData.getHazardsByUnsafeControlActionId(request, UUID.fromString(projectId),
				controlActionId, ucaId));
	}
}
