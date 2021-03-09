package de.xstampp.service.auth.controller;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import de.xstampp.common.dto.Response;
import de.xstampp.common.dto.project.PageRequestDTO;
import de.xstampp.common.errors.ErrorsPerm;
import de.xstampp.common.service.SecurityService;
import de.xstampp.common.utils.DeserializationUtil;
import de.xstampp.common.utils.SerializationUtil;
import de.xstampp.service.auth.data.User;
import de.xstampp.service.auth.dto.GroupMembershipRequestDTO;
import de.xstampp.service.auth.dto.GroupRequestDTO;
import de.xstampp.service.auth.service.GroupDataService;
import de.xstampp.service.auth.service.UserDataService;
import de.xstampp.service.auth.util.PrivilegeCheck;

@RestController
@RequestMapping("/api/auth")
public class GroupRestController {

	private SerializationUtil ser = new SerializationUtil();
	private DeserializationUtil deSer = new DeserializationUtil();

	@Autowired
	private GroupDataService service;

	@Autowired
	private UserDataService userData;

	@Autowired
	private SecurityService security;

	@Autowired
	private PrivilegeCheck check;

	@RequestMapping(value = "/group", method = RequestMethod.POST)
	public String createGroup(@RequestBody String body) throws IOException {

		GroupRequestDTO request = deSer.deserialize(body, GroupRequestDTO.class);

		return ser.serialize(service.createGroup(request));
	}

	@RequestMapping(value = "/group/{id}", method = RequestMethod.DELETE)
	public String deleteGroup(@PathVariable(value = "id") String id) throws IOException {

		if (security.getContext() == null
				|| !check.checkGroupEditPrivilege(security.getContext().getUserId(), UUID.fromString(id))) {
			throw ErrorsPerm.NEED_GROUPADM.exc();
		}

		return ser.serialize(new Response(service.deleteGroup(parseGroupId(id))));
	}

	@RequestMapping(value = "/group/{id}/member/{userIdOrEmail}", method = RequestMethod.POST)
	public String joinGroup(@PathVariable("id") String id, @PathVariable("userIdOrEmail") String userIdOrEmail,
			@RequestBody String body) throws IOException {

		if (security.getContext() == null
				|| !check.checkGroupEditPrivilege(security.getContext().getUserId(), UUID.fromString(id))) {
			throw ErrorsPerm.NEED_GROUPADM.exc();
		}

//		System.out.println("debug joinGroup");
		
		GroupMembershipRequestDTO request = deSer.deserialize(body, GroupMembershipRequestDTO.class);
		return ser.serialize(service.addMember(parseGroupId(id), parseUserIdOrEmail(userIdOrEmail), request));
	}

	@RequestMapping(value = "/group/{id}/member/{userIdOrEmail}", method = RequestMethod.DELETE)
	public String leaveGroup(@PathVariable("id") String id, @PathVariable("userIdOrEmail") String userIdOrEmail)
			throws IOException {

		if (security.getContext() == null
				|| !check.checkGroupEditPrivilege(security.getContext().getUserId(), UUID.fromString(id))) {
			throw ErrorsPerm.NEED_GROUPADM.exc();
		}

		return ser.serialize(service.removeMember(parseGroupId(id), parseUserIdOrEmail(userIdOrEmail)));
	}

	@RequestMapping(value = "/group/{id}/member/{userIdOrEmail}", method = RequestMethod.PUT)
	public String changeAccessLevel(@PathVariable("id") String id, @PathVariable("userIdOrEmail") String userIdOrEmail,
			@RequestBody String body) throws IOException {

		if (security.getContext() == null
				|| !check.checkGroupEditPrivilege(security.getContext().getUserId(), UUID.fromString(id))) {
			throw ErrorsPerm.NEED_GROUPADM.exc();
		}

		GroupMembershipRequestDTO request = deSer.deserialize(body, GroupMembershipRequestDTO.class);
		return ser.serialize(service.updateMember(parseGroupId(id), parseUserIdOrEmail(userIdOrEmail), request));
	}

	@RequestMapping(value = "/group/{id}", method = RequestMethod.PUT)
	public String alterGroup(@PathVariable("id") String id, @RequestBody String body) throws IOException {

		if (security.getContext() == null
				|| !check.checkGroupEditPrivilege(security.getContext().getUserId(), UUID.fromString(id))) {
			throw ErrorsPerm.NEED_GROUPADM.exc();
		}

		GroupRequestDTO request = deSer.deserialize(body, GroupRequestDTO.class);
		return ser.serialize(service.alterGroup(parseGroupId(id), request));
	}

	@RequestMapping(value = "/group/{id}/projects", method = RequestMethod.GET)
	public String getAllProjectsForGroup(@PathVariable("id") String id) throws IOException {

		if (security.getContext() == null
				|| !check.checkIfUserIsInGroup(security.getContext().getUserId(), UUID.fromString(id))) {
			throw ErrorsPerm.NEED_GROUP_MEMBER.exc();
		}

		return ser.serialize(service.getAllProjectsForGroup(parseGroupId(id)));
	}

	@RequestMapping(value = "/group/{id}", method = RequestMethod.GET)
	public String getGroupById(@PathVariable(value = "id") String id) throws IOException {

		if (security.getContext() == null
				|| !check.checkIfUserIsInGroup(security.getContext().getUserId(), UUID.fromString(id))) {
			throw ErrorsPerm.NEED_GROUP_MEMBER.exc();
		}

		return ser.serialize(service.getGroupById(parseGroupId(id)));
	}

	@RequestMapping(value = "/user/{userId}/groups", method = RequestMethod.GET)
	public String getAllGroupsOfUser(@PathVariable(value = "userId") String userId) throws IOException {

		if (security.getContext() == null || !check.checkIfUserIdIsAccessorOrSystemAdmin(UUID.fromString(userId))) {
			throw ErrorsPerm.NEED_SYSADM.exc();
		}

		return ser.serialize(service.getAllGroupsOfUser(UUID.fromString(userId)));
	}

	@RequestMapping(value = "/group", method = RequestMethod.GET)
	@ResponseBody
	public Map<UserDataService.DeletionBlockingReason, Set<UUID>> getDeletionBlockingGroups(@RequestParam String blockedUserId) {
		if (security.getContext() == null || !check.checkIfUserIdIsAccessorOrSystemAdmin(UUID.fromString(blockedUserId))) {
			throw ErrorsPerm.NEED_SYSADM.exc();
		}
		return userData.getDeletionBlockingGroups(UUID.fromString(blockedUserId));
	}

	@RequestMapping(value = "/group/{id}/users", method = RequestMethod.GET)
	public String getAllUsersOfGroup(@PathVariable(value = "id") String groupId) throws IOException {

		if (security.getContext() == null || !check.checkGroupEditPrivilege(security.getContext().getUserId(), UUID.fromString(groupId))) {
			throw ErrorsPerm.NEED_GROUPADM.exc();
		}

		return ser.serialize(service.getUsersOfGroup(UUID.fromString(groupId)));
	}
	
	@RequestMapping(value="/group/search", method = RequestMethod.POST)
	public String getAllGroups(@RequestBody String body) throws IOException {
		
		if (!check.checkSystemAdminPrivilege(security.getContext().getUserId())) {
			throw ErrorsPerm.NEED_SYSADM.exc();
		}
		
		PageRequestDTO request = deSer.deserialize(body, PageRequestDTO.class);
		
		return ser.serialize(service.getAllGroups(request));
	}
	
	@RequestMapping(value="/group/search/abandoned", method = RequestMethod.POST)
	public String getAllGroupsWithoutLeader(@RequestBody String body) throws IOException {
		
		if (!check.checkSystemAdminPrivilege(security.getContext().getUserId())) {
			throw ErrorsPerm.NEED_SYSADM.exc();
		}
		
		PageRequestDTO request = deSer.deserialize(body, PageRequestDTO.class);
		
		return ser.serialize(service.getAllAbondendGroups(request));
	}

	private UUID parseGroupId(String stringId) {
		if ("private".equals(stringId)) {
			return security.getContext().getUserId();
		} else {
			return UUID.fromString(stringId);
		}
	}

	private UUID parseUserIdOrEmail(String userIdOrEmail) {
		if (userIdOrEmail.contains("@")) {
			User user = userData.getByEmail(userIdOrEmail);
			return user.getId();
		} else {
			return UUID.fromString(userIdOrEmail);
		}
	}

}
