package de.xstampp.service.project.service.dao;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.SystemConstraint;
import de.xstampp.service.project.service.dao.generic.pagination.AbstractProjectDependentHibernateDAO;
import de.xstampp.service.project.service.dao.iface.ISystemConstraintDAO;

@Repository
public class SystemConstraintHibernateDAO extends AbstractProjectDependentHibernateDAO<SystemConstraint, ProjectDependentKey> implements ISystemConstraintDAO {

	@Autowired
	SessionFactory sessionFactory;
	
	@Override
	protected String getIdAttributeName() {
		return SystemConstraint.EntityAttributes.ID;
	}

	@Override
	public List<SystemConstraint> findSystemConstraintsByIds(UUID projectId, List<Integer> constIds) {

		if (constIds == null || constIds.isEmpty()) {
			return Collections.emptyList();
		}

		//Initialize Database request.
		CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<SystemConstraint> query = builder.createQuery(SystemConstraint.class);
		Root<SystemConstraint> rootSystemConstraint = query.from(SystemConstraint.class);
		Path<ProjectDependentKey> pathSystemConstraintKey = rootSystemConstraint.get(SystemConstraint.EntityAttributes.ID);
		query.select(rootSystemConstraint);

		//searchQuery
		query.where(builder.and(pathSystemConstraintKey.get(ProjectDependentKey.EntityAttributes.ID).in(constIds)),
				builder.equal(pathSystemConstraintKey.get(ProjectDependentKey.EntityAttributes.PROJECT_ID), projectId));

		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}
}
