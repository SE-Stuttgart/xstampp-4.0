package de.xstampp.service.auth.util;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import de.xstampp.service.auth.service.dao.IGroupMembershipDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.xstampp.common.service.SecurityService;
import de.xstampp.common.utils.Privileges;
import de.xstampp.service.auth.data.Group;
import de.xstampp.service.auth.data.GroupMembership;
import de.xstampp.service.auth.data.User;
import de.xstampp.service.auth.service.GroupDataService;
import de.xstampp.service.auth.service.UserDataService;

import javax.transaction.Transactional;

/**
 * This class provides several methods to check the needed privileges. This
 * helps to identify if the user is allowed to perform a certain task
 * 
 * @author Tobias Weiß
 *
 */
@Transactional
@Component
public class PrivilegeCheck {

	@Autowired
	GroupDataService groupDataService;

	@Autowired
	UserDataService userDataService;

	@Autowired
	SecurityService securityService;

	@Autowired
	private IGroupMembershipDAO groupMembershipDAO;

	Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * This method checks if the user is member of a group and has at least group
	 * admin rights
	 * 
	 * @param userId  the user id
	 * @param groupId the group id
	 * @return returns true if the user has all needed privileges
	 */
	public boolean checkGroupEditPrivilege(UUID userId, UUID groupId) {

		if (userId == null || groupId == null) {
			logger.warn("invalid input values for privileges check");
			return false;
		}

		GroupMembership membership = groupDataService.getMember(groupId, userId);
		User u = userDataService.getById(userId);

		if (u == null) {
			return false;
		}
		Privileges p;

		if (membership != null) {
			String role = membership.getAccessLevel();

			try {
				p = Privileges.valueOf(role);
			} catch (IllegalArgumentException iae) {
				p = Privileges.GUEST;
			}

			// check if user has at least group admin rights
			return Privileges.GROUP_ADMIN.ordinal() <= p.ordinal() || u.isSystemAdmin();
		}
		return u.isSystemAdmin();
	}

	public boolean checkProjectEditPrivilege(UUID userId, UUID projectId) {

		Group g = groupDataService.getGroupByProjectId(projectId);

		if (g != null && userId != null) {
			GroupMembership gm = groupDataService.getMember(g.getId(), userId);

			Privileges p;

			try {
				p = Privileges.valueOf(gm.getAccessLevel());
			} catch (IllegalArgumentException iae) {
				p = Privileges.GUEST;
			}

			return Privileges.DEVELOPER.ordinal() <= p.ordinal();
		}
		return false;
	}

	public boolean checkProjectDeleteOrCreatePrivilegeByGroup(UUID userId, UUID groupId) {
		
		// target is private group
		if (groupId == null) {
			return true;
		}
		
		GroupMembership groupMembership = groupDataService.getMember(groupId, userId);
		User user = userDataService.getById(userId);
		Group g = groupDataService.getGroupById(groupId);

		if (g.getPrivateFlag() && groupId.equals(userId)) {
			return true;
		}
		
		if (user == null) {
			return false;
		} else {
			if (user.isSystemAdmin()) {
				return true;
			}
		}

		if (groupMembership != null) {
			Privileges p = Privileges.valueOf(groupMembership.getAccessLevel());
			return Privileges.ANALYST.ordinal() <= p.ordinal();
		}

		return false;
	}
	
	public boolean checkProjectDeleteOrCreatePrivilegeByProject (UUID userId, UUID projectId) {
		Group g = groupDataService.getGroupByProjectId(projectId);
		if (g == null) {
			return false;
		}
		return checkProjectDeleteOrCreatePrivilegeByGroup(userId, g.getId());
	}

	// TODO: Complete Documentation @Rico
	/**
	 * Checks if the user is a System Admin privilege
	 * 
	 * @param userId
	 * @return
	 */
	public boolean checkSystemAdminPrivilege(UUID userId) {

		if (userId == null) {
			logger.warn("invalid input value for userID");
			return false;
		}

		User user = userDataService.getById(userId);

		if (user == null) {
			logger.warn("no user found for privileges check");
			return false;
		}

		return user.isSystemAdmin();
	}

	/**
	 * This method checks if the accessor is the same as the given User Id or if its
	 * the system administrator
	 * 
	 * @param userId the userid
	 * @return returns true if the accessor has the same user id or the system
	 *         administrator
	 */
	public boolean checkIfUserIdIsAccessorOrSystemAdmin(UUID userId) {
		UUID contextUserId = securityService.getContext().getUserId();
		return contextUserId.toString().equals(userId.toString()) || checkSystemAdminPrivilege(userId);
	}

	/**
	 * This method checks if the accessing user has the id userId
	 * @param userId UUID of user
	 * @return true iff the id of the user for whom this methods is executed equals userId
	 */
	public boolean checkIfUserIdIsAccessor(UUID userId) {
		UUID contextUserId = securityService.getContext().getUserId();
		return contextUserId.toString().equals(userId.toString());
	}

	/**
	 * checks if a user is member if the given group
	 * 
	 * @param userId  the id of the user
	 * @param groupId the id of the group
	 * @return returns true if the user is member of the group
	 */
	public boolean checkIfUserIsInGroup(UUID userId, UUID groupId) {
		GroupMembership membership = groupDataService.getMember(groupId, userId);

		return membership != null;
	}
	
	public boolean checkIfUserHasAccessToProject (UUID userId, UUID projectId) {
		Group g = groupDataService.getGroupByProjectId(projectId);
		GroupMembership membership = groupDataService.getMember(g.getId(), userId);

		return membership != null;
	}

	public boolean checkIfAllUsersInAccessorsProject(List<UUID> users) {
		Group g = groupDataService.getGroupByProjectId(UUID.fromString(securityService.getContext().getProjectId()));
		if(g != null) {
			List<GroupMembership> userMemberships = groupMembershipDAO.findUsersOfGroup(g.getId());
			List<UUID> projectGroupUsers = userMemberships.stream().map((GroupMembership m) -> m.getUserId())
					.collect(Collectors.toList());
			return projectGroupUsers.containsAll(users);
		}
		return false;
	}
}
