package de.xstampp.service.project.controller;

import de.xstampp.common.dto.Response;
import de.xstampp.common.dto.project.SearchRequestDTO;
import de.xstampp.common.utils.*;
import de.xstampp.service.project.data.dto.ResponsibilityCreationDTO;
import de.xstampp.service.project.data.dto.ResponsibilityFilterRequestDTO;
import de.xstampp.service.project.data.dto.ResponsibilityRequestDTO;
import de.xstampp.service.project.data.entity.Responsibility;
import de.xstampp.service.project.service.RequestPushService;
import de.xstampp.service.project.service.RequestPushService.Method;
import de.xstampp.service.project.service.data.ResponsibilityDataService;
import de.xstampp.service.project.service.data.ResponsibilityFilterPreviewDataService;
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
 * Rest controller for handling all incoming requests for the Responsibilities.
 * Passes on requests to services for further processing.
 *
 */
@RestController
@RequestMapping("/api/project")
public class ResponsibilityRestController {

	@Autowired
	ResponsibilityDataService responsibilityDataService;

	@Autowired
	ResponsibilityFilterPreviewDataService responsibilityFilterPreviewDataService;

	@Autowired
	RequestPushService push;

	SerializationUtil ser = new SerializationUtil();
	DeserializationUtil deSer = new DeserializationUtil();
	Logger logger = LoggerFactory.getLogger(ResponsibilityRestController.class);

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@RequestMapping(value = "/{id}/responsibility", method = RequestMethod.POST)
	public String createResponsibility(@PathVariable("id") @XStamppProjectId String projectId, @RequestBody String body)
			throws IOException {
		ResponsibilityCreationDTO responsibility = deSer.deserialize(body, ResponsibilityCreationDTO.class);
		Responsibility result = responsibilityDataService.createResponsibility(UUID.fromString(projectId),
				responsibility);

		if (result != null) {
			push.notify(String.valueOf(responsibility.getId()), projectId, EntityNameConstants.RESPONSIBILITY, Method.CREATE);
		}
		return ser.serialize(result);
	}

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.RESPONSIBILITY)
	@RequestMapping(value = "/{id}/responsibility/{respId}", method = RequestMethod.PUT)
	public String alterResponsibility(@PathVariable("id") @XStamppProjectId String projectId,
			@PathVariable("respId") @XStamppEntityId int responsibilityId, @RequestBody String body)
			throws IOException {
		ResponsibilityRequestDTO responsibility = deSer.deserialize(body, ResponsibilityRequestDTO.class);
		Responsibility result = responsibilityDataService.alterResponsibility(UUID.fromString(projectId),
				responsibilityId, responsibility);

		if (result != null) {
			push.notify(String.valueOf(responsibility.getId()), projectId, EntityNameConstants.RESPONSIBILITY, Method.ALTER);
		}

		return ser.serialize(result);
	}

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.RESPONSIBILITY)
	@RequestMapping(value = "/{id}/responsibility/{respId}", method = RequestMethod.DELETE)
	public String deleteResponsibility(@PathVariable("id") @XStamppProjectId String projectId,
			@PathVariable("respId") @XStamppEntityId int responsibilityId) throws IOException {
		boolean result = responsibilityDataService.deleteResponsibility(UUID.fromString(projectId), responsibilityId);

		if (result) {
			push.notify(String.valueOf(responsibilityId), projectId, EntityNameConstants.RESPONSIBILITY, Method.DELETE);
		}

		return ser.serialize(new Response(result));
	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/responsibility/search", method = RequestMethod.POST)
	public String getAllResponsibilities(@PathVariable("id") @XStamppProjectId String projectId, @RequestBody String body)
			throws IOException {
		SearchRequestDTO search = deSer.deserialize(body, SearchRequestDTO.class);
		return ser.serialize(
				responsibilityDataService.getAllResponsibilities(UUID.fromString(projectId), search.getFilter(),
						search.getOrderBy(), search.getOrderDirection(), search.getAmount(), search.getFrom()));
	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/responsibility/{respId}", method = RequestMethod.GET)
	public String getResponsibilityById(@PathVariable("id") @XStamppProjectId String projectId,
			@PathVariable("respId") int responsibilityId) throws IOException {
		return ser.serialize(
				responsibilityDataService.getResponsibilityById(UUID.fromString(projectId), responsibilityId));
	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/responsibility/controller/{controllerId}", method = RequestMethod.GET)
	public String getResponsibilityByControllerId(@PathVariable("id") @XStamppProjectId String projectId,
												  @PathVariable("controllerId") int controllerId) throws IOException {
		return ser.serialize(responsibilityDataService.getListByControllerId(UUID.fromString(projectId), controllerId));
	}

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@RequestMapping(value = "/{id}/responsibility/{respId}/system-constraint/{constId}", method = RequestMethod.POST)
	public String createResponsibilitySystemConstraintLink(@PathVariable("id") @XStamppProjectId String projectId,
														   @PathVariable("respId") int responsibilityId, @PathVariable("constId") int constraintId) throws IOException{
		return ser.serialize(new Response(
				responsibilityDataService.createResponsibilitySystemConstraintLink(UUID.fromString(projectId), responsibilityId, constraintId)));
	}

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@RequestMapping(value = "/{id}/responsibility/{respId}/system-constraint/{constId}", method = RequestMethod.DELETE)
	public String deleteResponsibilitySystemConstraintLink(@PathVariable("id") @XStamppProjectId String projectId,
														   @PathVariable("respId") int responsibilityId, @PathVariable("constId") int constraintId)throws IOException{
		return ser.serialize(new Response(
				responsibilityDataService.deleteResponsibilitySystemConstraintLink(UUID.fromString(projectId), responsibilityId, constraintId)));
	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/responsibility/system-constraint/{constId}", method = RequestMethod.POST)
	public String getResponsibilitiesBySystemConstraint (@PathVariable("id") @XStamppProjectId String projectId,
														 @PathVariable("constId") int constraintId) throws IOException{
		return ser.serialize(responsibilityDataService.getResponsibilitiesBySystemConstraint(UUID.fromString(projectId),constraintId));
	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/responsibility/system-constraint-and-controller", method = RequestMethod.POST)
	public String getResponsibilitiesBySystemConstraintAndController(
			@PathVariable("id") @XStamppProjectId String projectId, @RequestBody String body) throws IOException {

		ResponsibilityFilterRequestDTO dto = deSer.deserialize(body, ResponsibilityFilterRequestDTO.class);
		return ser.serialize(responsibilityDataService.getResponsibilitiesBySystemConstraintAndController(
				UUID.fromString(projectId), dto.getSystemConstraintId(), dto.getControllerId()));
	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/responsibility/filter-preview/specific", method = RequestMethod.POST)
	public String getResponsibilityCountBySystemConstraintAndController(
			@PathVariable("id") @XStamppProjectId String projectId, @RequestBody String body) throws IOException {

		ResponsibilityFilterRequestDTO dto = deSer.deserialize(body, ResponsibilityFilterRequestDTO.class);
		return ser.serialize(responsibilityDataService.getResponsibilityCountBySystemConstraintAndController(
				UUID.fromString(projectId), dto.getSystemConstraintId(), dto.getControllerId()));
	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/responsibility/filter-preview/complete", method = RequestMethod.POST)
	public String getCompleteResponsibilityFilterPreview(
			@PathVariable("id") @XStamppProjectId String projectId, @RequestBody String body) throws IOException {

		ResponsibilityFilterRequestDTO dto = deSer.deserialize(body, ResponsibilityFilterRequestDTO.class);
		return ser.serialize(responsibilityFilterPreviewDataService.getResponsibilityFilterPreview(
				UUID.fromString(projectId), dto.getSystemConstraintId(), dto.getControllerId()));
	}
}

