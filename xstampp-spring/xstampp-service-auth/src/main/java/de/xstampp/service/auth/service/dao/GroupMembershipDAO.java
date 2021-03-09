package de.xstampp.service.auth.service.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import de.xstampp.common.utils.Privileges;
import de.xstampp.service.auth.data.GroupMembership;
import de.xstampp.service.auth.data.Project;

@Repository
public class GroupMembershipDAO extends AbstractGenericHibernateDAO<GroupMembership, UUID> implements IGroupMembershipDAO {

	@Autowired
	SessionFactory sessionFactory;

	@Override
	public List<GroupMembership> findByExample(GroupMembership exampleInstance) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public GroupMembership findById(UUID groupId, UUID userId) {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<GroupMembership> query = builder.createQuery(GroupMembership.class);
		Root<GroupMembership> root = query.from(GroupMembership.class);
		query.select(root);
		List<Predicate> predicates = new ArrayList<>();
		Predicate p1 = builder.equal(root.get(GroupMembership.EntityAttributes.USER_ID), userId);
		predicates.add(p1);
		Predicate p2 = builder.equal(root.get(GroupMembership.EntityAttributes.GROUP_ID), groupId);
		predicates.add(p2);
		Predicate a1 = builder.and(p1,p2);
		query.where(a1);
		
		List<GroupMembership> res = getSession().createQuery(query).getResultList();
		if (res.size() > 2) {
			throw new IllegalStateException("A User cannot be part of the same group twice");
		} else if (res.size() == 1) {
			return res.get(0);
		} else {
			return null;
		}
	}

	@Override
	public List<Project> findProjectsOfGroup(UUID groupId) {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<Project> query = builder.createQuery(Project.class);
		Root<Project> root = query.from(Project.class);
		query.select(root);
		query.where(builder.equal(root.get(Project.EntityAttributes.GROUP_ID), groupId));
		return getSession().createQuery(query).getResultList();
		
	}

	@Override
	public List<GroupMembership> findGroupsOfUser(UUID userId) {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<GroupMembership> query = builder.createQuery(GroupMembership.class);
		Root<GroupMembership> root = query.from(GroupMembership.class);
		query.select(root);
		query.where(builder.equal(root.get(GroupMembership.EntityAttributes.USER_ID), userId));
		return getSession().createQuery(query).getResultList();
	}

	/**
	 * @param userId UUID of user
	 * @return list containing a GroupMembership object for all groups in which user with userId is group admin
	 */
	@Override
	public List<GroupMembership> findAdminGroupsOfUser(UUID userId) {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<GroupMembership> query = builder.createQuery(GroupMembership.class);
		Root<GroupMembership> root = query.from(GroupMembership.class);
		query.select(root);
		query.where(builder.and(builder.equal(root.get(GroupMembership.EntityAttributes.USER_ID), userId),
				builder.equal(root.get(GroupMembership.EntityAttributes.ACCESS_LEVEL), Privileges.GROUP_ADMIN.toString())));
		return getSession().createQuery(query).getResultList();
	}
	
	@Override
	public List<GroupMembership> findUsersOfGroup(UUID groupId){
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<GroupMembership> query = builder.createQuery(GroupMembership.class);
		Root<GroupMembership> root = query.from(GroupMembership.class);
		query.select(root);
		query.where(builder.equal(root.get(GroupMembership.EntityAttributes.GROUP_ID), groupId));
		return getSession().createQuery(query).getResultList();
	}

	/**
	 * Find users with admin privileges in the group with id groupId
	 * @param groupId UUID of group
	 * @return list containing a membership m iff m.getAccessLevel()==Privileges.GROUP_ADMIN.toString() and
	 * m.getGroupId()==groupId
	 */
	@Override
	public List<GroupMembership> findGroupLeadersOfGroup(UUID groupId) {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<GroupMembership> query = builder.createQuery(GroupMembership.class);
		Root<GroupMembership> root = query.from(GroupMembership.class);
		query.select(root);
		query.where(builder.and(builder.equal(root.get(GroupMembership.EntityAttributes.GROUP_ID), groupId),
				builder.equal(root.get(GroupMembership.EntityAttributes.ACCESS_LEVEL), Privileges.GROUP_ADMIN.toString())));
		return getSession().createQuery(query).getResultList();
	}

	/**
	 * Finds groups where user with userId is the only admin. The word 'leader' is used synonymously.
	 * @param userId
	 * @return list containing a given groupId iff user with userId is only user with
	 * membership in list returned by findGroupLeadersofGroup(groupId)
	 */
	@Override
	public List<UUID> getSingleGroupLeaderGroups(UUID userId) {
		List<UUID> wouldBeAbandonedGroups = new ArrayList<UUID>();
		List<UUID> groups = findAdminGroupsOfUser(userId).stream().map(membership -> membership.getGroupId()).collect(Collectors.toList());
		for(UUID groupId: groups) {
			if(findGroupLeadersOfGroup(groupId).size() <= 1) {
				wouldBeAbandonedGroups.add(groupId);
			}
		}
		return wouldBeAbandonedGroups;
	}

	/**
	 * Finds groups where user with id userId is only member
	 * @param userId UUID of user
	 * @return list containing a group uuid iff user with userId is only member of group
	 */
	@Override
	public List<UUID> getSingleMemberGroups(UUID userId) {
		List<UUID> wouldBeEmptyGroups = new ArrayList<UUID>();
		Set<UUID> groups = findGroupsOfUser(userId).stream().map((membership) -> membership.getGroupId()).
				collect(Collectors.toSet());
		for(UUID groupId: groups) {
			if(findUsersOfGroup(groupId).size() == 1) {
				wouldBeEmptyGroups.add(groupId);
			}
		}
		return wouldBeEmptyGroups;
	}


	@Override
	public List<GroupMembership> findAllGroupLeaders() {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<GroupMembership> query = builder.createQuery(GroupMembership.class);
		Root<GroupMembership> root = query.from(GroupMembership.class);
		query.select(root);
		query.where(builder.equal(root.get(GroupMembership.EntityAttributes.ACCESS_LEVEL), Privileges.GROUP_ADMIN.toString()));
		return getSession().createQuery(query).getResultList();
	}

}
