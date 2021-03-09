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

import de.xstampp.service.project.data.entity.Loss;
import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.service.dao.generic.pagination.AbstractProjectDependentHibernateDAO;
import de.xstampp.service.project.service.dao.iface.ILossDAO;

@Repository
public class LossHibernateDAO extends AbstractProjectDependentHibernateDAO<Loss, ProjectDependentKey> implements ILossDAO {

	@Autowired
	SessionFactory sessionFactory;
	
	@Override
	public List<Loss> findLossesByIds(UUID projectId, List<Integer> lossIds) {
		if (lossIds.isEmpty()) {
			return Collections.emptyList();
		}
		CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<Loss> query = builder.createQuery(Loss.class);
		Root<Loss> rootLoss = query.from(Loss.class);
		Path<ProjectDependentKey> pathLossKey = rootLoss.get(Loss.EntityAttributes.ID);
		query.select(rootLoss);
		query.where(builder.and(pathLossKey.get(ProjectDependentKey.EntityAttributes.ID).in(lossIds),
				builder.equal(pathLossKey.get(ProjectDependentKey.EntityAttributes.PROJECT_ID), projectId)));
		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}
	
	@Override
	protected String getIdAttributeName() {
		return Loss.EntityAttributes.ID;
	}

}
