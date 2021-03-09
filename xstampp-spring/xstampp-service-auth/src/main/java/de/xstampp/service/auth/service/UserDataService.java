package de.xstampp.service.auth.service;

import de.xstampp.common.errors.ErrorsAuth;
import de.xstampp.common.utils.Privileges;
import de.xstampp.service.auth.data.Group;
import de.xstampp.service.auth.data.GroupMembership;
import de.xstampp.service.auth.data.User;
import de.xstampp.service.auth.data.remote.RemoteUser;
import de.xstampp.service.auth.dto.AdminUserdataRequestDTO;
import de.xstampp.service.auth.dto.LoginRequestDTO;
import de.xstampp.service.auth.dto.UserResponseAdminDTO;
import de.xstampp.service.auth.service.dao.IGroupDAO;
import de.xstampp.service.auth.service.dao.IGroupMembershipDAO;
import de.xstampp.service.auth.service.dao.IUserDAO;
import de.xstampp.service.auth.service.dao.SortOrder;
import de.xstampp.service.auth.service.dao.remote.IRemoteUserDAO;
import de.xstampp.service.auth.util.Hashing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserDataService {

	@Autowired
	private IUserDAO userDAO;

	@Autowired
	private IRemoteUserDAO remoteUserDAO;

	@Autowired
	private IGroupDAO groupDAO;

	@Autowired
	private IGroupMembershipDAO groupMembershipDAO;

	@Autowired
	private GroupDataService groupData;

	private Logger logger;

	public UserDataService() {
		logger = LoggerFactory.getLogger(this.getClass());
	}

	public void addUser(User user) {
		UUID newUid = UUID.randomUUID();
		user.setId(newUid);
		user.setLastLogin(Timestamp.from(Instant.now()));

		if (userDAO.getByEmail(user.getEmail()) != null) {
			throw ErrorsAuth.FAILED_USER_ADD.exc();
		}
		userDAO.saveNew(user);

		Group privateGroup = new Group(newUid);
		privateGroup.setPrivateFlag(true);
		privateGroup.setName("Private projects");
		groupDAO.saveNew(privateGroup);

		GroupMembership membership = new GroupMembership(user.getId(), privateGroup.getId(),
				Privileges.GROUP_ADMIN.toString());
		groupMembershipDAO.saveNew(membership);
	}

	public void updateUser(User user) {
		userDAO.updateExisting(user);
	}

	public boolean deleteUser(UUID id) {
		User user = userDAO.findById(id, false);
		RemoteUser remoteUser = remoteUserDAO.findById(id, false);
		if (user != null) {
			user = this.overwriteUserData(user);
			userDAO.makeTransient(user);
			if (remoteUser != null) {
				remoteUserDAO.makeTransient(remoteUser);
			}
			groupDAO.makeTransient(groupDAO.findById(id, false));
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method prescribes the way a user is deleted if requested by himself. If
	 * the user is member in groups blocking his deletion or isn't found at all a
	 * exception is thrown which will make the controller invoking this method
	 * return an appropriate response status code and error message in the response
	 * body.
	 * 
	 * @param userId UUID of user
	 * @return true iff user isn't member of groups blocking deletion and was
	 *         successfully deleted
	 */
	public boolean deleteUserByUser(UUID userId) {
		User user = userDAO.findById(userId, false);
		RemoteUser remoteUser = remoteUserDAO.findById(userId, false);

		if (user == null) {
			logger.debug("User doesn't exist");
			throw ErrorsAuth.USER_DOESNT_EXIST.exc();
		}

		if (noDeletionBlockingGroupsExist(userId)) {
			user = this.overwriteUserData(user);
			userDAO.makeTransient(user);

			// there is only going to be a remote user if the user ever created or updated
			// an entity stored on the project service
			if (remoteUser != null) {
				remoteUserDAO.makeTransient(remoteUser);
			}
			// delete private group and all projects contained within it
			groupData.deleteGroupWithProjects(userId);
			return true;
		} else {
			logger.debug("User would leave groups without admin or is last user in groups with projects");
			logger.debug(getDeletionBlockingGroups(userId).toString());
			throw ErrorsAuth.GROUPS_BLOCKING_USER_DELETION.exc();
		}
	}

	/**
	 * This method deletes a user even if he has 'problematic' group memberships. If the user isn't found at all an
	 * exception is thrown.
	 * @param userId UUID of user
	 * @return true iff user existed and was successfully deleted
	 */
	public boolean deleteUserNoChecks(UUID userId) {
		User user = userDAO.findById(userId, false);
		RemoteUser remoteUser = remoteUserDAO.findById(userId, false);
		if (user == null) {
			logger.debug("User doesn't exist");
			throw ErrorsAuth.USER_DOESNT_EXIST.exc();
		}
		user = this.overwriteUserData(user);
		userDAO.makeTransient(user);
		//there is only going to be a remote user if the user ever created or updated an entity stored on the project service
		if(remoteUser != null) {
			remoteUserDAO.makeTransient(remoteUser);
		}
		//delete private group and all projects contained within it
		groupData.deleteGroupWithProjects(userId);
		return true;
	}

	/**
	 * Checks if map returned by getDeletionBlockingGroups has only empty sets as values
	 * @param userId UUID of user
	 * @return true iff the user isn't member in any groups blocking deletion
	 */
	public boolean noDeletionBlockingGroupsExist(UUID userId) {
		Map<DeletionBlockingReason, Set<UUID>> deletionBlockingGroups = getDeletionBlockingGroups(userId);
		return deletionBlockingGroups.get(DeletionBlockingReason.ONLY_ADMIN_MULTI_USER).isEmpty()
				&& deletionBlockingGroups.get(DeletionBlockingReason.ONLY_USER_GROUP_WITH_PROJECTS).isEmpty();
	}

	/**
	 * This methods job is to accumulate groups blocking deletion by calling other
	 * methods checking for various reasons that make a group block deletion.
	 * 
	 * @param userId
	 * @return a map from reasons for a group to be blocking user deletion to the
	 *         uuids of the affected groups
	 */
	public Map<DeletionBlockingReason, Set<UUID>> getDeletionBlockingGroups(UUID userId) {
		Map<DeletionBlockingReason, Set<UUID>> deletionBlockingGroups = new HashMap<>();
		deletionBlockingGroups.put(DeletionBlockingReason.ONLY_USER_GROUP_WITH_PROJECTS,
				getOnlyUserGroupsWithProjects(userId));
		deletionBlockingGroups.put(DeletionBlockingReason.ONLY_ADMIN_MULTI_USER, getOnlyAdminMultiUserGroups(userId));
		return deletionBlockingGroups;
	}

	/**
	 * @param userId UUID of user
	 * @return Set of uuids of all groups in which the user is the only member and
	 *         which contain at least one project. The users private group is not
	 *         included even if it fits the preceding description.
	 */
	public Set<UUID> getOnlyUserGroupsWithProjects(UUID userId) {
		List<UUID> onlyUserGroups = groupMembershipDAO.getSingleMemberGroups(userId);
		Set<UUID> onlyUserGroupsWithProjects = new HashSet<>();
		for (UUID groupId : onlyUserGroups) {
			if (groupMembershipDAO.findProjectsOfGroup(groupId).size() > 0) {
				onlyUserGroupsWithProjects.add(groupId);
			}
		}
		// userId is at the same time id of his private group
		onlyUserGroupsWithProjects.remove(userId);
		return onlyUserGroupsWithProjects;
	}

	/**
	 * @param userId UUID of user
	 * @return Set of uuids of all groups in which the user is the only group admin
	 *         and which have members other than him. The private group is not
	 *         included even if it fits the preceding description.
	 */
	public Set<UUID> getOnlyAdminMultiUserGroups(UUID userId) {
		List<UUID> onlyAdminGoups = groupMembershipDAO.getSingleGroupLeaderGroups(userId);
		Set<UUID> onlyAdminMultiUserGroups = new HashSet<>();
		for (UUID groupId : onlyAdminGoups) {
			if (groupMembershipDAO.findUsersOfGroup(groupId).size() > 1) {
				onlyAdminMultiUserGroups.add(groupId);
			}
		}
		// userId is at the same time id of his private group
		onlyAdminMultiUserGroups.remove(userId);
		return onlyAdminMultiUserGroups;
	}

	public User getByEmail(String email) {
		return userDAO.getByEmail(email);
	}

	public User getById(UUID id) {
		return userDAO.findById(id, false);
	}

	public List<UserResponseAdminDTO> listUsers(int amount, int from) {
		List<User> users = userDAO.findFromTo(from, amount, Map.of(User.EntityAttributes.EMAIL, SortOrder.ASC));
		return users.stream().map(UserResponseAdminDTO::new).collect(Collectors.toList());
	}

	// enum of all the reasons which might make a group blocking
	public enum DeletionBlockingReason {
		// reason for group uuids returned by getOnlyUserGroupsWithProjects
		ONLY_USER_GROUP_WITH_PROJECTS,
		// reason for group uuids returned by getOnlyAdminMultiUserGroups
		ONLY_ADMIN_MULTI_USER
	}

	/**
	 * Changes the users icon to the given icon path
	 * 
	 * @param id       The uuid of the user to edit
	 * @param iconPath The icon path to set
	 * @return True if update was successful, false if not
	 */
	public boolean setUserIcon(UUID id, String iconPath) {
		User user = userDAO.findById(id, false);

		// If user exists -> update user
		if (user != null) {
			user.setIcon(iconPath);
			userDAO.updateExisting(user);
			return true;
		}
		return false;
	}

	public Map<UUID, String[]> getUsersDisplay(List<UUID> userIds) {
		Map<UUID, String[]> userDisplayMapping = new HashMap<>();
		/*
		List<User> users = userDAO.findByIds(userIds);
		for(User u: users) {
			if(u != null) {
				userDisplayMapping.put(u.getId(), new String[]{u.getDisplayName(), u.getIconPath()});
			}
		}
		 */
		for(UUID id: userIds) {
			User u = userDAO.findById(id, false);
			if(u != null) {
				userDisplayMapping.put(u.getId(), new String[]{u.getDisplayName(), u.getIconPath()});
			}
		}
		return userDisplayMapping;
	}

	/**
	 * Changes the users theme to the given theme id
	 * 
	 * @param id      The uuid of the user to edit
	 * @param themeId The theme id to set
	 * @return True if update was successful, false if not
	 */
	public boolean setUserTheme(UUID id, String themeId) {
		User user = userDAO.findById(id, false);

		// Check whether themeId is numeric, throw exception with custom message if not
		int themeInt;
		try {
			themeInt = Integer.valueOf(themeId);
		} catch (NumberFormatException e) {
			throw new NumberFormatException("Theme id has to be numeric");
		}

		// If user exists -> update user
		if (user != null) {
			user.setTheme(themeInt);
			userDAO.updateExisting(user);
			return true;
		}
		return false;
	}

	public boolean setEmail(UUID id, String email) {
		User user = userDAO.findById(id, false);

		// If user exists -> update user
		if (user != null) {
			user.setEmail(email);
			userDAO.updateExisting(user);
			return true;
		}
		return false;
	}

	public boolean setPassword(UUID id, String password) {
		User user = userDAO.findById(id, false);

		// If user exists -> update user
		if (user != null) {
			user.setPasswordHash(Hashing.createPbkdf2hash(password));
			userDAO.updateExisting(user);
			return true;
		}
		return false;
	}

	public List<User> getAllUsers(){
		return userDAO.findAll();
	}

	public boolean setPasswordEmailByUser(UUID id, LoginRequestDTO settings) {
		User user = userDAO.findById(id, false);

		// If user exists -> update user
		if (user != null) {
			user.setEmail(settings.getEmail());
			user.setPasswordHash(Hashing.createPbkdf2hash(settings.getPassword()));
			userDAO.updateExisting(user);
			return true;
		}
		return false;
	}

	/**
	 * updates Email and DisplayName for a user
	 * @param userId	ID des Users
	 * @param request	changes to userData
	 * @return	success of change in DB
	 */
	public boolean updateDisplayNameAndEmail(UUID userId, AdminUserdataRequestDTO request){
		User user = userDAO.findById(userId, false);
		if (user != null){
			user.setEmail(request.getEmail());
			user.setDisplayName(request.getDisplayName());
			userDAO.updateExisting(user);
			return true;
		}

		//if no User exists with given user ID
		else{
			return false;
		}
	}

	/**
	 * Overwrites user data in database. Important for privacy reasons!
	 * If not done before makeTransient() call, some data may
	 * persist on disk until overwritten
	 * @param user The user object to overwrite data of
	 * @return The updated user object
	 */
	private User overwriteUserData(User user) {
		user.setDisplayName("NULL");
		user.setEmail("NULL");
		user.setLastLogin(new Timestamp(0));
		user.setLockedUntil(new Timestamp(0));
		user.setPasswordHash("NULL");
		user.setLockedUntil(new Timestamp(0));
		return userDAO.updateExisting(user);
	}
}