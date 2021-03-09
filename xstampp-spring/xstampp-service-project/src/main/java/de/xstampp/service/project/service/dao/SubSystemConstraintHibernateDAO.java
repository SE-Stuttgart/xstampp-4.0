package de.xstampp.service.project.service.dao;

import java.util.UUID;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import de.xstampp.service.project.data.entity.EntityDependentKey;
import de.xstampp.service.project.data.entity.SubSystemConstraint;
import de.xstampp.service.project.service.dao.generic.pagination.AbstractEntityDependentHibernateDAO;
import de.xstampp.service.project.service.dao.iface.ISubSystemConstraintDAO;

@Repository
public class SubSystemConstraintHibernateDAO
		extends AbstractEntityDependentHibernateDAO<SubSystemConstraint, EntityDependentKey>
		implements ISubSystemConstraintDAO {

	@Autowired
	SessionFactory sessionFactory;

	@Override
	public SubSystemConstraint getSubSystemConstraintBySubHazardId(UUID projectId, int hazardId, int subHazardId) {
		CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<SubSystemConstraint> query = builder.createQuery(SubSystemConstraint.class);
		Root<SubSystemConstraint> subSysRoot = query.from(SubSystemConstraint.class);
		query.select(subSysRoot);
		query.where(builder.and(builder.equal(subSysRoot.get("id").get(EntityDependentKey.EntityAttributes.PROJECT_ID), projectId),
				builder.equal(subSysRoot.get(SubSystemConstraint.EntityAttributes.HAZARD_ID), hazardId),
				builder.equal(subSysRoot.get(SubSystemConstraint.EntityAttributes.SUB_HAZARD_ID), subHazardId)));
		
		TypedQuery<SubSystemConstraint> res = sessionFactory.getCurrentSession().createQuery(query);
		if(res.getResultList().size() > 0) {
			return sessionFactory.getCurrentSession().createQuery(query).getSingleResult();
		} else {
			return null;
		}
		
	}

	@Override
	protected String getIdAttributeName() {
		return SubSystemConstraint.EntityAttributes.ID;
	}

}
