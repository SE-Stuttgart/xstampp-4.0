package de.xstampp.service.project.controller;

import de.xstampp.common.dto.Response;
import de.xstampp.common.dto.project.PageRequestDTO;
import de.xstampp.common.dto.project.SearchRequestDTO;
import de.xstampp.common.utils.*;
import de.xstampp.service.project.data.dto.UnsafeControlActionRequestDTO;
import de.xstampp.service.project.data.entity.UnsafeControlAction;
import de.xstampp.service.project.service.RequestPushService;
import de.xstampp.service.project.service.RequestPushService.Method;
import de.xstampp.service.project.service.data.UnsafeControlActionDataService;
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
 * Rest controller for handling all incoming requests for the unsafe control action
 *
 */
@RestController
@RequestMapping("/api/project")
public class UnsafeControlActionController {

	SerializationUtil ser = new SerializationUtil();
	DeserializationUtil deSer = new DeserializationUtil();

	@Autowired
	UnsafeControlActionDataService ucaData;

	@Autowired
	RequestPushService pushService;

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@RequestMapping(value = "/{projectId}/ControlAction/{controlActionId}/UCA", method = RequestMethod.POST)
	public String createUnsafeControlAction(@RequestBody String body,
			@PathVariable("projectId") @XStamppProjectId String projectId,
			@PathVariable("controlActionId") int controlActionId) throws IOException {
		UnsafeControlActionRequestDTO request = deSer.deserialize(body, UnsafeControlActionRequestDTO.class);
		UnsafeControlAction uca = ucaData.createUnsafeControlAction(request, UUID.fromString(projectId),
				controlActionId);

		if (uca != null) {
			pushService.notify(String.valueOf(uca.getId().getId()), projectId, "unsafe-control-action", Method.CREATE);
		}

		return ser.serialize(uca);
	}

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.UNSAFE_CONTROL_ACTION)
	@RequestMapping(value = "{projectId}/ControlAction/{controlActionId}/UCA/{ucaId}", method = RequestMethod.DELETE)
	public String deleteUnsafeControlAction(@PathVariable("projectId") @XStamppProjectId String projectId,
			@PathVariable("controlActionId") @XStamppEntityParentId int controlActionId,
			@PathVariable("ucaId") @XStamppEntityId int ucaId) throws IOException {

		boolean result = ucaData.deleteUnsafeControlAction(UUID.fromString(projectId), controlActionId, ucaId);

		if (result) {
			pushService.notify(String.valueOf(ucaId), projectId, "unsafe-control-action", Method.DELETE);
		}

		return ser.serialize(new Response(result));
	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "{projectId}/ControlAction/{controlActionId}/UCA/{ucaId}", method = RequestMethod.GET)
	public String getUnsafeControlAction(@PathVariable("projectId") @XStamppProjectId String projectId,
			@PathVariable("controlActionId") int controlActionId, @PathVariable("ucaId") int ucaId) throws IOException {

		return ser.serialize(ucaData.getUnsafeControlAction(UUID.fromString(projectId), controlActionId, ucaId));
	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "{projectId}/ControlAction/{controlActionId}/UCAs", method = RequestMethod.POST)
	public String getUnsafeControlActionsByControlActionId(
			@PathVariable("projectId") @XStamppProjectId String projectId,
			@PathVariable("controlActionId") int controlActionId, @RequestBody String body) throws IOException {
		PageRequestDTO page = deSer.deserialize(body, PageRequestDTO.class);

		return ser.serialize(ucaData.getUnsafeControlActionsByControlActionId(UUID.fromString(projectId),
				controlActionId, page.getAmount(), page.getFrom()));
	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "{projectId}/ControlAction/{controlActionId}/type/UCAs", method = RequestMethod.POST)
	public String getUnsafeControlActionsByControlActionIdAndType(@RequestBody String body,
			@PathVariable("projectId") @XStamppProjectId String projectId,
			@PathVariable("controlActionId") int controlActionId) throws IOException {
		SearchRequestDTO search = deSer.deserialize(body, SearchRequestDTO.class);

		return ser.serialize(ucaData.getUnsafeControlActionsByControlActionIdAndType(UUID.fromString(projectId),
				controlActionId, search.getAmount(), search.getFrom(), search.getFilter()));
	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "{projectId}/ControlAction/{controlActionId}/UCAs/count", method = RequestMethod.POST)
	public String getUnsafeControlActionsCountByControlActionIdAndType(@RequestBody String body,
			@PathVariable("projectId") @XStamppProjectId String projectId,
			@PathVariable("controlActionId") int controlActionId) throws IOException {
		SearchRequestDTO search = deSer.deserialize(body, SearchRequestDTO.class);

		return ser.serialize(ucaData.getUnsafeControlActionsCountByControlActionIdAndType(UUID.fromString(projectId),
				controlActionId, search.getFilter()));
	}

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.UNSAFE_CONTROL_ACTION)
	@RequestMapping(value = "{projectId}/ControlAction/{controlActionId}/UCA/{ucaId}", method = RequestMethod.PUT)
	public String alterUnsafeControlAction(@PathVariable("projectId") @XStamppProjectId String projectId,
			@PathVariable("controlActionId") @XStamppEntityParentId int controlActionId,
			@PathVariable("ucaId") @XStamppEntityId int ucaId, @RequestBody String body) throws IOException {
		UnsafeControlActionRequestDTO request = deSer.deserialize(body, UnsafeControlActionRequestDTO.class);
		UnsafeControlAction uca = ucaData.alterUnsafeControlAction(request, UUID.fromString(projectId), controlActionId, ucaId);
		
		if (uca != null) {
			pushService.notify(String.valueOf(ucaId), projectId, "unsafe-control-action", Method.ALTER);
		}
		
		return ser.serialize(uca);
	}

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@RequestMapping(value = "{projectId}/ControlAction/{controlActionId}/UCA/{ucaId}/link/hazard/{hazardId}", method = RequestMethod.POST)
	public String createUnsafeControlActionHazardLink(@PathVariable("projectId") @XStamppProjectId String projectId,
			@PathVariable("controlActionId") int controlActionId, @PathVariable("ucaId") int ucaId,
			@PathVariable("hazardId") int hazardId) throws IOException {
		return ser.serialize(new Response(ucaData.createUnsafeControlActionHazardLink(UUID.fromString(projectId),
				controlActionId, ucaId, hazardId)));
	}

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@RequestMapping(value = "{projectId}/ControlAction/{controlActionId}/UCA/{ucaId}/link/hazard/{hazardId}", method = RequestMethod.DELETE)
	public String deleteUnsafeControlActionHazardLink(@PathVariable("projectId") @XStamppProjectId String projectId,
			@PathVariable("controlActionId") int controlActionId, @PathVariable("ucaId") int ucaId,
			@PathVariable("hazardId") int hazardId) throws IOException {
		return ser.serialize(new Response(ucaData.deleteUnsafeControlActionHazardLink(UUID.fromString(projectId),
				controlActionId, ucaId, hazardId)));
	}

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@RequestMapping(value = "{projectId}/ControlAction/{controlActionId}/UCA/{ucaId}/link/hazard/{hazardId}/subHazard/{subHazardId}", method = RequestMethod.POST)
	public String createUnsafeControlActionSubHazardLink(@PathVariable("projectId") @XStamppProjectId String projectId,
			@PathVariable("controlActionId") int controlActionId, @PathVariable("ucaId") int ucaId,
			@PathVariable("hazardId") int hazardId, @PathVariable("subHazardId") int subHazardId) throws IOException {
		return ser.serialize(new Response(ucaData.createUnsafeControlActionSubHazardLink(UUID.fromString(projectId),
				controlActionId, ucaId, hazardId, subHazardId)));
	}

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@RequestMapping(value = "{projectId}/ControlAction/{controlActionId}/UCA/{ucaId}/link/hazard/{hazardId}/subHazard/{subHazardId}", method = RequestMethod.DELETE)
	public String deleteUnsafeControlActionSubHazardLink(@PathVariable("projectId") @XStamppProjectId String projectId,
			@PathVariable("controlActionId") int controlActionId, @PathVariable("ucaId") int ucaId,
			@PathVariable("hazardId") int hazardId, @PathVariable("subHazardId") int subHazardId) throws IOException {
		return ser.serialize(new Response(ucaData.deleteUnsafeControlSubActionHazardLink(UUID.fromString(projectId),
				controlActionId, ucaId, hazardId, subHazardId)));
	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "{projectId}/hazard/{hazardId}/link/UCA", method = RequestMethod.POST)
	public String getUnsafeControlActionsByHazardId(@PathVariable("projectId") @XStamppProjectId String projectId,
			@PathVariable("hazardId") int hazardId, @RequestBody String body) throws IOException {
		PageRequestDTO request = deSer.deserialize(body, PageRequestDTO.class);

		return ser.serialize(ucaData.getUnsafeControlActionsByHazard(UUID.fromString(projectId), hazardId,
				request.getAmount(), request.getFrom()));
	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "{projectId}/hazard/{hazardId}/subHazard/{subHazardId}/link/UCA", method = RequestMethod.POST)
	public String getUnsafeControlActionsBySubHazardId(@PathVariable("projectId") @XStamppProjectId String projectId,
			@PathVariable("hazardId") int hazardId, @PathVariable("subHazardId") int subHazardId,
			@RequestBody String body) throws IOException {
		PageRequestDTO request = deSer.deserialize(body, PageRequestDTO.class);

		return ser.serialize(ucaData.getUnsafeControlActionsBySubHazard(UUID.fromString(projectId), hazardId,
				subHazardId, request.getAmount(), request.getFrom()));
	}

	/**
	 * There is an arbitrary number of UCAs per project. This method retrieves all UCAs associated with the project with
	 * id projectId.
	 * @param projectId id of current project
	 * @param body ordering and paging parameters
	 * @return json representing retrieved UCAs
	 * @throws IOException
	 */
	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/uca/search", method = RequestMethod.POST)
	public String getAllUnsafeControllActions(@PathVariable("id") @XStamppProjectId String projectId, @RequestBody String body) throws IOException {
		SearchRequestDTO search = deSer.deserialize(body, SearchRequestDTO.class);
		return ser.serialize(ucaData.getAllUCA(UUID.fromString(projectId), search.getFilter(), search.getOrderBy(), search.getOrderDirection(),search.getAmount(), search.getFrom()));
	}

	/**
	 * There is at most one implementation constraint per UCA. This method retrieves it.
	 * @param projectId id of current project
	 * @param controlActionId id of a control action
	 * @param ucaId has to be id of a UCA belonging to the control action with id=controlActionId
	 * @return json representing at most one controller constraint
	 * @throws IOException
	 */
	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "{projectId}/ControlAction/{controlActionId}/UCA/{ucaId}/controller-constraint", method = RequestMethod.GET)
	public String getControllerConstraintByUnsafeControlAction(@PathVariable("projectId") @XStamppProjectId String projectId,
										 @PathVariable("controlActionId") int controlActionId, @PathVariable("ucaId") int ucaId) throws IOException {
		return ser.serialize(ucaData.getControllerConstraintByUnsafeControlAction(UUID.fromString(projectId), controlActionId, ucaId));
	}
}
