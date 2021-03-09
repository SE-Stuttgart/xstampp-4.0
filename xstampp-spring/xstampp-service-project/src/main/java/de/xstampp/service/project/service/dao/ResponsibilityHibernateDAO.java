package de.xstampp.service.project.service.dao;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.persistence.criteria.*;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.Responsibility;
import de.xstampp.service.project.service.dao.generic.pagination.AbstractProjectDependentHibernateDAO;
import de.xstampp.service.project.service.dao.iface.IResponsibilityDAO;

@Repository
public class ResponsibilityHibernateDAO extends
		AbstractProjectDependentHibernateDAO<Responsibility, ProjectDependentKey> implements IResponsibilityDAO {

	@Autowired
	SessionFactory sessionFactory;

	@Override
	protected String getIdAttributeName() {
		return Responsibility.EntityAttributes.ID;
	}

	@Override
	public List<Responsibility> getListByControllerId(UUID projectId, int controllerId) {

		//Initialize Database request.
		CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<Responsibility> query = builder.createQuery(Responsibility.class);
		Root<Responsibility> rootResponsibility = query.from(Responsibility.class);
		query.select(rootResponsibility);

		//searchQuery
		query.where(
				builder.and(builder.equal(rootResponsibility.get(Responsibility.EntityAttributes.ID)
						.get(ProjectDependentKey.EntityAttributes.PROJECT_ID), projectId)),
				builder.equal(rootResponsibility.get(Responsibility.EntityAttributes.CONTROLLER_ID), controllerId));

		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}

	@Override
	public List<Responsibility> getListByResponsibilityIds(UUID projectId, List<Integer> responsibilityIdList) {

		//check if List is empty
		if(responsibilityIdList == null || responsibilityIdList.isEmpty())
			return Collections.emptyList();

		//Initialize Database request.
		CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<Responsibility> query = builder.createQuery(Responsibility.class);
		Root<Responsibility> rootResponsibility = query.from(Responsibility.class);
		query.select(rootResponsibility);
		Path<ProjectDependentKey> pathResponsibilityKey = rootResponsibility.get(Responsibility.EntityAttributes.ID);

		//searchQuery
		query.where(
				builder.and(
						builder.equal(pathResponsibilityKey.get(ProjectDependentKey.EntityAttributes.PROJECT_ID), projectId),
						pathResponsibilityKey.get(ProjectDependentKey.EntityAttributes.ID).in(responsibilityIdList)
				)
		);

		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}

}
