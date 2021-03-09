package de.xstampp.service.auth.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import de.xstampp.common.errors.ErrorsGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.xstampp.common.dto.project.PageRequestDTO;
import de.xstampp.common.errors.ErrorsAuth;
import de.xstampp.common.service.SecurityService;
import de.xstampp.common.utils.Privileges;
import de.xstampp.service.auth.data.Group;
import de.xstampp.service.auth.data.GroupMembership;
import de.xstampp.service.auth.data.Project;
import de.xstampp.service.auth.data.User;
import de.xstampp.service.auth.dto.GroupAndAccessLevelResponseDTO;
import de.xstampp.service.auth.dto.GroupMembershipRequestDTO;
import de.xstampp.service.auth.dto.GroupRequestDTO;
import de.xstampp.service.auth.dto.UserInGroupResponseDTO;
import de.xstampp.service.auth.service.RequestPushService.Method;
import de.xstampp.service.auth.service.dao.IGroupDAO;
import de.xstampp.service.auth.service.dao.IGroupMembershipDAO;
import de.xstampp.service.auth.service.dao.IProjectDAO;
import de.xstampp.service.auth.service.dao.IUserDAO;
import de.xstampp.service.auth.service.dao.SortOrder;

@Service
@Transactional
public class GroupDataService {

	@Autowired
	private SecurityService security;

	@Autowired
	private IGroupDAO groupDAO;

	@Autowired
	private IProjectDAO projectDAO;

	@Autowired
	private ProjectDataService projectData;

	@Autowired
	private IUserDAO userDAO;

	@Autowired
	private RequestPushService push;

	@Autowired
	private IGroupMembershipDAO groupMembershipDAO;

	public Group createGroup(GroupRequestDTO request) {
		Group group = new Group(UUID.randomUUID());
		group.setName(request.getName());
		group.setDescription(request.getDescription());
		groupDAO.saveNew(group);
		GroupMembership groupMembership = new GroupMembership();
		groupMembership.setUserId(security.getContext().getUserId());
		groupMembership.setGroupId(group.getId());
		groupMembership.setAccessLevel(Privileges.GROUP_ADMIN.toString());
		groupMembershipDAO.saveNew(groupMembership);
		push.notify(group.getId().toString(), null, "group", Method.CREATE);
		return group;
	}

	public boolean deleteGroup(UUID groupId) {
		Group entity = groupDAO.findById(groupId, false);
		if(entity == null || entity.getPrivateFlag()) return false;
		//check if group contains projects and abort deletion to avoid fkey constraint violation in project table
		if(!groupMembershipDAO.findProjectsOfGroup(groupId).isEmpty()) {
			throw ErrorsGroup.DELETE_GROUP_WITH_PROJECTS.exc();
		} else {
				groupDAO.makeTransient(entity);
				push.notify(groupId.toString(), null, "group", Method.DELETE);
				return true;
		}
	}

	/**
	 * @param groupId UUID of group to be deleted together with contained projects
	 * @return true if a private group with id groupId existed and was successfully deleted across services
	 */
	public boolean deleteGroupWithProjects(UUID groupId) {
		Group entity = groupDAO.findById(groupId, false);
		if (entity != null && entity.getPrivateFlag()) {
			List<Project> projects = getAllProjectsForGroup(groupId);
			for(Project project:projects) {
				projectData.deleteProjectFully(project.getId());
			}
			groupDAO.makeTransient(entity);
			return true;
		} else {
			return false;
		}
	}

	public GroupMembership addMember(UUID groupId, UUID userId, GroupMembershipRequestDTO request) {
		if (groupDAO.findById(groupId, false).getPrivateFlag()) {
			throw ErrorsAuth.PRIVATE_GROUP_INVALID.exc();
		}

		// try cast to validate privileges
		Privileges p = Privileges.valueOf(request.getAccessLevel());

		GroupMembership groupMembership = new GroupMembership();
		groupMembership.setUserId(userId);
		groupMembership.setGroupId(groupId);
		groupMembership.setAccessLevel(p.toString());
		GroupMembership result = groupMembershipDAO.saveNew(groupMembership);

		if (result != null) {
			push.notify(result.getUserId().toString(), null, "member", Method.CREATE);
			return result;
		} else {
			return null;
		}
	}

	public boolean removeMember(UUID groupId, UUID userId) {
		if (groupId.equals(userId)) {
			throw ErrorsAuth.PRIVATE_GROUP_INVALID.exc();
		}
		GroupMembership entity = groupMembershipDAO.findById(groupId, userId);

		if (entity != null) {
			groupMembershipDAO.makeTransient(entity);
			push.notify(entity.getUserId().toString(), null, "member", Method.DELETE);
			return true;
		} else {
			return false;
		}
	}

	public GroupMembership updateMember(UUID groupId, UUID userId, GroupMembershipRequestDTO request) {
		GroupMembership groupMembership = new GroupMembership();
		groupMembership.setUserId(userId);
		groupMembership.setGroupId(groupId);
		groupMembership.setAccessLevel(request.getAccessLevel());
		GroupMembership result = groupMembershipDAO.updateExisting(groupMembership);

		if (result != null) {
			push.notify(result.getUserId().toString(), null, "member", Method.ALTER);
			return result;
		} else {
			return null;
		}
	}

	public Group alterGroup(UUID id, GroupRequestDTO request) {
		Group entity = groupDAO.findById(id, false);
		entity.setName(request.getName());
		entity.setDescription(request.getDescription());
		Group result = groupDAO.updateExisting(entity);

		if (result != null) {
			push.notify(result.getId().toString(), null, "group", Method.ALTER);
			return result;
		} else {
			return null;
		}
	}

	public GroupMembership getMember(UUID groupId, UUID userId) {
		return groupMembershipDAO.findById(groupId, userId);
	}

	public List<Project> getAllProjectsForGroup(UUID groupId) {
		return groupMembershipDAO.findProjectsOfGroup(groupId);
	}

	public Group getGroupById(UUID groupId) {
		return groupDAO.findById(groupId, false);
	}

	public Group getGroupByProjectId(UUID projectId) {
		Project project = projectDAO.findById(projectId, false);

		if (project != null) {
			List<Group> list = groupDAO.findGroupsByIds(projectId, Arrays.asList(project.getGroupId()));
			if (list.size() == 1) {
				return list.get(0);
			}
		}

		return null;
	}

	public List<UserInGroupResponseDTO> getUsersOfGroup(UUID groupID) {

		List<GroupMembership> userMemberships = groupMembershipDAO.findUsersOfGroup(groupID);
		List<UserInGroupResponseDTO> response = new ArrayList<>(userMemberships.size());

		for (GroupMembership membership : userMemberships) {
			User user = userDAO.findById(membership.getUserId(), false);
			UserInGroupResponseDTO userDTO = new UserInGroupResponseDTO(user.getId(), user.getEmail(),
					user.getDisplayName(), membership.getAccessLevel());
			response.add(userDTO);
		}

		return response;

	}

	public List<GroupAndAccessLevelResponseDTO> getAllGroupsOfUser(UUID userId) {
		List<GroupMembership> userMemberships = groupMembershipDAO.findGroupsOfUser(userId);
		List<GroupAndAccessLevelResponseDTO> groups = new ArrayList<>(userMemberships.size());

		for (GroupMembership membership : userMemberships) {
			Group group = getGroupById(membership.getGroupId());
			groups.add(new GroupAndAccessLevelResponseDTO(group, membership.getAccessLevel()));
		}

		return groups;
	}

	public List<Group> getAllGroups(PageRequestDTO request) {
		Map<String, SortOrder> options = Map.of(Group.EntityAttributes.ID, SortOrder.ASC);
		List<Group> groups = groupDAO.findFromTo(request.getFrom(), request.getAmount(), options);
		
		// TODO rework with search query implementation
		groups = groups.stream().filter(g -> !g.getPrivateFlag()).collect(Collectors.toList());
		
		return groups;
	}

	public List<Group> getAllAbondendGroups(PageRequestDTO request) {
		// TODO pagination

		List<GroupMembership> groupMembership = groupMembershipDAO.findAllGroupLeaders();
		List<Group> groups = groupDAO.findAll();
		Group searchGroup = new Group();
		for (GroupMembership membership : groupMembership) {
			searchGroup.setId(membership.getGroupId());
			if (groups.contains(searchGroup)) {
				groups.remove(searchGroup);
			}
		}
		return groups;
	}


}

