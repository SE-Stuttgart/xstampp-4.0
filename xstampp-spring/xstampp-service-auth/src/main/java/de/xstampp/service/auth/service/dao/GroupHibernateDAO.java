package de.xstampp.service.auth.service.dao;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import de.xstampp.service.auth.data.Group;

@Repository
public class GroupHibernateDAO extends AbstractGenericHibernateDAO<Group, UUID> implements IGroupDAO {

	@Autowired
	SessionFactory sessionFactory;

	@Override
	public List<Group> findByExample(Group exampleInstance) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public List<Group> findGroupsByIds(UUID projectId, List<UUID> groupIds) {
		if (groupIds.isEmpty()) {
			return Collections.emptyList();
		}
		CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<Group> query = builder.createQuery(Group.class);
		Root<Group> root = query.from(Group.class);
		query.select(root);
		query.where(root.get(Group.EntityAttributes.ID).in(groupIds));
		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}
}
