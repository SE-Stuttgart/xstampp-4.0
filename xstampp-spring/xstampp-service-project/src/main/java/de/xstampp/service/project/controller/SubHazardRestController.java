package de.xstampp.service.project.controller;

import de.xstampp.common.dto.Response;
import de.xstampp.common.dto.project.PageRequestDTO;
import de.xstampp.common.dto.project.SearchRequestDTO;
import de.xstampp.common.utils.*;
import de.xstampp.service.project.data.dto.SubHazardRequestDTO;
import de.xstampp.service.project.data.entity.SubHazard;
import de.xstampp.service.project.service.RequestPushService;
import de.xstampp.service.project.service.RequestPushService.Method;
import de.xstampp.service.project.service.dao.iface.SortOrder;
import de.xstampp.service.project.service.data.SubHazardDataService;
import de.xstampp.service.project.util.annotation.CheckLock;
import de.xstampp.service.project.util.annotation.XStamppEntityId;
import de.xstampp.service.project.util.annotation.XStamppEntityParentId;
import de.xstampp.service.project.util.annotation.XStamppProjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

/**
 * 
 * Rest controller for handling all incoming requests for the sub hazards
 *
 */
@RestController
@RequestMapping("/api/project")
public class SubHazardRestController {

	@Autowired
	SubHazardDataService data;
	
	@Autowired
	RequestPushService push;

	SerializationUtil ser = new SerializationUtil();
	DeserializationUtil deSer = new DeserializationUtil();
	Logger logger = LoggerFactory.getLogger(SubHazardRestController.class);

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@RequestMapping(value = "/{id}/hazard/{hazardId}/sub-hazard", method = RequestMethod.POST)
	public String createSubHazard(@PathVariable("hazardId") int hazardId, @PathVariable("id") @XStamppProjectId String projectId,
			@RequestBody String body) throws IOException {
		SubHazardRequestDTO request = deSer.deserialize(body, SubHazardRequestDTO.class);
		SubHazard result = data.createSubHazard(request, UUID.fromString(projectId), hazardId);
		
		if (result != null) {
			push.notify(String.valueOf(result.getId().getId()), projectId, EntityNameConstants.SUB_HAZARD, Method.CREATE);
		}
		
		return ser.serialize(result);
	}

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.SUB_HAZARD)
	@RequestMapping(value = "/{id}/hazard/{hazardId}/sub-hazard/{subHazardId}", method = RequestMethod.PUT)
	public String alterSubHazard(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("hazardId") @XStamppEntityParentId int hazardId,
			@PathVariable("subHazardId") @XStamppEntityId int subHazardId, @RequestBody String body) throws IOException {
		SubHazardRequestDTO request = deSer.deserialize(body, SubHazardRequestDTO.class);
		SubHazard result = data.alterSubHazard(request, UUID.fromString(projectId), hazardId, subHazardId);
		
		if (result != null) {
			push.notify(String.valueOf(result.getId().getId()), projectId, EntityNameConstants.SUB_HAZARD, Method.ALTER);
		}
		
		return ser.serialize(result);
	}

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.SUB_HAZARD)
	@RequestMapping(value = "/{id}/hazard/{hazardId}/sub-hazard/{subHazardId}", method = RequestMethod.DELETE)
	public String deleteSubHazard(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("hazardId") @XStamppEntityParentId int hazardId,
			@PathVariable("subHazardId") @XStamppEntityId int subHazardId) throws IOException {

		boolean result = data.deleteSubHazard(subHazardId, hazardId, UUID.fromString(projectId));
		
		if (result) {
			push.notify(String.valueOf(subHazardId), projectId, EntityNameConstants.SUB_HAZARD, Method.ALTER);
		}
		
		return ser.serialize(new Response(result));
	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/hazard/{hazardId}/sub-hazard/{subHazardId}", method = RequestMethod.GET)
	public String getSubHazardById(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("hazardId") int hazardId,
			@PathVariable("subHazardId") int subHazardId) throws IOException {
		return ser.serialize(data.getSubHazardById(subHazardId, hazardId, UUID.fromString(projectId)));
	}

	// TODO limit subHazard selection to given hazard
	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/hazard/{hazardId}/sub-hazard/search", method = RequestMethod.POST)
	public String getAllSubHazardsById(@PathVariable("id") @XStamppProjectId String projectId, @RequestBody String body,
			@PathVariable("hazardId") int hazardId) throws IOException {
		SearchRequestDTO searchRequest;
		searchRequest = deSer.deserialize(body, SearchRequestDTO.class);
		SortOrder order = SortOrder.valueOfIgnoreCase(searchRequest.getOrderDirection());
		return ser.serialize(
				data.getAllSubHazardsByHazadId(UUID.fromString(projectId), hazardId, searchRequest.getFilter(),
						searchRequest.getOrderBy(), order, searchRequest.getAmount(), searchRequest.getFrom()));
	}
	
	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = {"/{id}/ControlAction/{controlActionId}/UCA/{ucaId}/link/subHazard"}, method = RequestMethod.POST)
	public String getSubHazardsByUnsafeControlActionId(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("controlActionId") int controlActionId, @PathVariable("ucaId") int ucaId, @RequestBody String body) throws IOException{
		// TODO Implement subhazatdFindByIds(projectID, subHazardIdList,Hazard) in SubHazardHibernateDao
		PageRequestDTO request = deSer.deserialize(body, PageRequestDTO.class);
		
		return ser.serialize(data.getSubHazardsByUnsafeControlActionId(request,UUID.fromString(projectId),controlActionId,ucaId));
	}
}
