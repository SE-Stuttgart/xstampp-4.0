package de.xstampp.service.auth.service.dao;

import java.util.List;
import java.util.UUID;

import de.xstampp.service.auth.data.GroupMembership;
import de.xstampp.service.auth.data.Project;

public interface IGroupMembershipDAO extends IGenericDAO <GroupMembership, UUID> {

	@Override
	List<GroupMembership> findByExample(GroupMembership exampleInstance);
	
	GroupMembership findById(UUID groupId, UUID userId);
	
	List<Project> findProjectsOfGroup(UUID groupId);
	
	List<GroupMembership> findGroupsOfUser (UUID userid);

	List<GroupMembership> findAdminGroupsOfUser(UUID userId);

	List<GroupMembership> findUsersOfGroup(UUID groupId);

	List<GroupMembership> findAllGroupLeaders();

	List<GroupMembership> findGroupLeadersOfGroup(UUID groupId);

	List<UUID> getSingleGroupLeaderGroups(UUID userId);

	List<UUID> getSingleMemberGroups(UUID userId);

	}
