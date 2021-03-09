package de.xstampp.service.project.controller;

import de.xstampp.common.dto.Response;
import de.xstampp.common.dto.project.PageRequestDTO;
import de.xstampp.common.dto.project.SearchRequestDTO;
import de.xstampp.common.utils.*;
import de.xstampp.service.project.data.dto.LossRequestDTO;
import de.xstampp.service.project.data.entity.Loss;
import de.xstampp.service.project.service.RequestPushService;
import de.xstampp.service.project.service.RequestPushService.Method;
import de.xstampp.service.project.service.data.HazardDataService;
import de.xstampp.service.project.service.data.LossDataService;
import de.xstampp.service.project.util.annotation.CheckLock;
import de.xstampp.service.project.util.annotation.XStamppEntityId;
import de.xstampp.service.project.util.annotation.XStamppProjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * 
 * Rest controller for handling all incoming requests for the losses
 *
 */
@RestController
@RequestMapping("/api/project")
public class LossRestController {

	@Autowired
	LossDataService lossData;

	@Autowired
	HazardDataService hazardData;

	@Autowired
	RequestPushService push;

	SerializationUtil ser = new SerializationUtil();
	DeserializationUtil deSer = new DeserializationUtil();
	Logger logger = LoggerFactory.getLogger(LossRestController.class);

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@RequestMapping(value = "/{id}/loss", method = RequestMethod.POST)
	public String createLoss(@PathVariable("id") @XStamppProjectId String projectId, @RequestBody String body) throws IOException {
		LossRequestDTO request = deSer.deserialize(body, LossRequestDTO.class);
		Loss loss = lossData.createLoss(request, UUID.fromString(projectId));

		if (loss != null) {
			push.notify(String.valueOf(loss.getId().getId()), projectId, EntityNameConstants.LOSS, Method.ALTER);
		}
		return ser.serialize(loss);
	}

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.LOSS)
	@RequestMapping(value = "{id}/loss/{lossId}", method = RequestMethod.PUT)
	public String alterLoss(@PathVariable(value = "id") @XStamppProjectId String projectId,
			@PathVariable(value = "lossId") @XStamppEntityId int lossId, @RequestBody String body) throws IOException {
		LossRequestDTO loss = deSer.deserialize(body, LossRequestDTO.class);

		Loss result = lossData.alterLoss(loss, UUID.fromString(projectId), lossId);

		if (result != null) {
			push.notify(String.valueOf(lossId), projectId, EntityNameConstants.LOSS, Method.ALTER);
		}
		return ser.serialize(result);
	}

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.LOSS)
	@RequestMapping(value = "{id}/loss/{lossId}", method = RequestMethod.DELETE)
	public String deleteLoss(@PathVariable(value = "id") @XStamppProjectId String projectId,
			@PathVariable(value = "lossId") @XStamppEntityId int lossId) throws IOException {

		String response = "";

		logger.debug("delete loss {} for project {}", lossId, projectId);
		boolean result = lossData.deleteLoss(lossId, UUID.fromString(projectId));
		
		if (result) {
			push.notify(String.valueOf(lossId), projectId, EntityNameConstants.LOSS, Method.DELETE);
		}

		response = ser.serialize(new Response(result));
		return response;
	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "{id}/loss/{lossId}", method = RequestMethod.GET)
	public String getLossById(@PathVariable(value = "id") @XStamppProjectId String projectId, @PathVariable(value = "lossId") int lossId)
			throws IOException {

		logger.debug("get loss by id {} for project {}", lossId, projectId);
		return ser.serialize(lossData.getLossById(lossId, UUID.fromString(projectId)));

	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "{id}/loss/search", method = RequestMethod.POST)
	public String getAllLosses(@PathVariable(value = "id") @XStamppProjectId String projectId, @RequestBody String body)
			throws IOException {

		SearchRequestDTO searchRequest;
		List<Loss> list;
		searchRequest = deSer.deserialize(body, SearchRequestDTO.class);
		logger.debug("search for losses for project {}", projectId);
		list = lossData.getAllLosses(UUID.fromString(projectId), searchRequest.getFilter(), searchRequest.getOrderBy(),
				searchRequest.getOrderDirection(), Integer.valueOf(searchRequest.getAmount()),
				Integer.valueOf(searchRequest.getFrom()));

		return ser.serialize(list);
	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/loss/{lossId}/hazards", method=RequestMethod.POST)
	public String hazardsByLossId (@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("lossId") int lossId, @RequestBody String body) throws IOException {
		PageRequestDTO page = deSer.deserialize(body, PageRequestDTO.class);
		
		return ser.serialize(hazardData.getHazardsByLossId(UUID.fromString(projectId), lossId, page.getAmount(), page.getFrom()));
	}

}
