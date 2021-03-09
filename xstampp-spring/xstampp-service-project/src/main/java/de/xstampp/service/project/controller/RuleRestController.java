package de.xstampp.service.project.controller;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.xstampp.common.dto.Response;
import de.xstampp.common.dto.project.SearchRequestDTO;
import de.xstampp.common.utils.DeserializationUtil;
import de.xstampp.common.utils.EntityNameConstants;
import de.xstampp.common.utils.PrivilegeCheck;
import de.xstampp.common.utils.Privileges;
import de.xstampp.common.utils.SerializationUtil;
import de.xstampp.service.project.data.dto.RuleRequestDTO;
import de.xstampp.service.project.data.entity.Rule;
import de.xstampp.service.project.service.RequestPushService;
import de.xstampp.service.project.service.RequestPushService.Method;
import de.xstampp.service.project.service.dao.iface.SortOrder;
import de.xstampp.service.project.service.data.RuleDataService;
import de.xstampp.service.project.util.annotation.CheckLock;
import de.xstampp.service.project.util.annotation.XStamppEntityId;
import de.xstampp.service.project.util.annotation.XStamppEntityParentId;
import de.xstampp.service.project.util.annotation.XStamppProjectId;

/**
 * 
 * Rest controller for handling all incoming requests for Rules
 *
 */
@RestController
@RequestMapping("/api/project")
public class RuleRestController {

	@Autowired
	RuleDataService ruleData;

	@Autowired
	RequestPushService push;

	SerializationUtil ser = new SerializationUtil();
	DeserializationUtil deSer = new DeserializationUtil();

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@RequestMapping(value = "/{id}/controller/{controllerId}/rule", method = RequestMethod.POST)
	public String createRule(@PathVariable("id") @XStamppProjectId String projectId,
			@PathVariable("controllerId") int controllerId, @RequestBody String body) throws IOException {
		RuleRequestDTO request = deSer.deserialize(body, RuleRequestDTO.class);

		Rule result = ruleData.createRule(request, UUID.fromString(projectId), controllerId);
		if (result != null) {
			push.notify(String.valueOf(result.getId().getId()), projectId, EntityNameConstants.RULE, Method.CREATE);
		}
		return ser.serialize(result);
	}

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.RULE)
	@RequestMapping(value = "/{id}/controller/{controllerId}/rule/{ruleId}", method = RequestMethod.PUT)
	public String editRule(@PathVariable("id") @XStamppProjectId String projectId,
			@PathVariable("controllerId") @XStamppEntityParentId int controllerId, @PathVariable("ruleId") @XStamppEntityId int ruleId,
			@RequestBody String body) throws IOException {
		RuleRequestDTO request = deSer.deserialize(body, RuleRequestDTO.class);

		Rule result = ruleData.editRule(request, UUID.fromString(projectId), controllerId, ruleId);
		if (result != null) {
			push.notify(String.valueOf(result.getId().getId()), projectId, EntityNameConstants.RULE, Method.ALTER);
		}

		return ser.serialize(result);
	}

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.RULE)
	@RequestMapping(value = "/{id}/controller/{controllerId}/rule/{ruleId}", method = RequestMethod.DELETE)
	public String deleteRule(@PathVariable("id") @XStamppProjectId String projectId,
			@PathVariable("controllerId") @XStamppEntityParentId int controllerId,
			@PathVariable("ruleId") @XStamppEntityId int ruleId) throws IOException {

		boolean result = ruleData.deleteRule(UUID.fromString(projectId), controllerId, ruleId);
		if (result) {
			push.notify(String.valueOf(ruleId), projectId, EntityNameConstants.RULE, Method.DELETE);
		}
		return ser.serialize(new Response(result));
	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/controller/{controllerId}/rule/{ruleId}", method = RequestMethod.GET)
	public String getRuleById(@PathVariable("id") @XStamppProjectId String projectId,
			@PathVariable("controllerId") @XStamppEntityId int controllerId,
			@PathVariable("ruleId") int ruleId) throws IOException {
		return ser.serialize(ruleData.getRuleById(UUID.fromString(projectId), controllerId, ruleId));
	}

	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/controller/{controllerId}/rule/search", method = RequestMethod.POST)
	public String getAllRules(@PathVariable("id") @XStamppProjectId String projectId,
			@PathVariable("controllerId") @XStamppEntityId int controllerId,
			@RequestBody String body)
			throws IOException {
		SearchRequestDTO searchRequest;
		searchRequest = deSer.deserialize(body, SearchRequestDTO.class);
		SortOrder order = SortOrder.valueOfIgnoreCase(searchRequest.getOrderDirection());
		return ser.serialize(ruleData.getAllRules(UUID.fromString(projectId), controllerId, searchRequest.getFilter(),
				searchRequest.getOrderBy(), order, searchRequest.getAmount(), searchRequest.getFrom()));
	}


}
