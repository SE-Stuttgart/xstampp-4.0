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

import de.xstampp.service.project.data.entity.Hazard;
import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.service.dao.generic.pagination.AbstractProjectDependentHibernateDAO;
import de.xstampp.service.project.service.dao.iface.IHazardDAO;

@Repository
public class HazardHibernateDAO extends AbstractProjectDependentHibernateDAO<Hazard, ProjectDependentKey>
		implements IHazardDAO {

	@Autowired
	SessionFactory sessionFactory;

	@Override
	protected String getIdAttributeName() {
		return Hazard.EntityAttributes.ID;
	}

	@Override
	public List<Hazard> findHazardsByIds(UUID projectId, List<Integer> hazardIds) {
		if (hazardIds == null || hazardIds.isEmpty()) {
			return Collections.emptyList();
		}
		CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<Hazard> query = builder.createQuery(Hazard.class);
		Root<Hazard> rootHazard = query.from(Hazard.class);
		Path<ProjectDependentKey> pathHazardKey = rootHazard.get(Hazard.EntityAttributes.ID);
		query.select(rootHazard);
		query.where(builder.and(
				builder.equal(pathHazardKey.get(ProjectDependentKey.EntityAttributes.PROJECT_ID), projectId),
				pathHazardKey.get(ProjectDependentKey.EntityAttributes.ID).in(hazardIds)));
		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}

}
