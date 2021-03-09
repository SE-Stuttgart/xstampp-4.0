package de.xstampp.service.project.service.dao;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import de.xstampp.service.project.data.entity.EntityDependentKey;
import de.xstampp.service.project.data.entity.Conversion;
import de.xstampp.service.project.service.dao.generic.pagination.AbstractEntityDependentHibernateDAO;
import de.xstampp.service.project.service.dao.iface.IConversionDAO;

@Repository
public class ConversionHibernateDAO extends AbstractEntityDependentHibernateDAO<Conversion, EntityDependentKey>
		implements IConversionDAO {

	@Autowired
	SessionFactory sessionFactory;
	
	@Override
	protected String getIdAttributeName() {
		return Conversion.EntityAttributes.ID;
	}

	@Override
	public List<Conversion> findConversionsByIds(List<EntityDependentKey> conversionKeys, Integer from, Integer amount) {
		if (conversionKeys.isEmpty()) {
			return Collections.emptyList();
		}
		CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<Conversion> query = builder.createQuery(Conversion.class);
		Root<Conversion> rootConversion = query.from(Conversion.class);
		query.select(rootConversion);
		query.where(rootConversion.get(Conversion.EntityAttributes.ID).in(conversionKeys));

		query.orderBy(orderById(builder, rootConversion));

		TypedQuery<Conversion> res = getSession().createQuery(query);
		res.setFirstResult(from);
		res.setMaxResults(amount);
		List<Conversion> result = res.getResultList();

		return result;
	}
	
	@Override
	public List<Conversion> getConversionsByActuatorId(UUID projectId, int actuatorId,
			int amount, int from) {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<Conversion> query = builder.createQuery(Conversion.class);
		Root<Conversion> rootConversion = query.from(Conversion.class);
		query.select(rootConversion);
		Predicate p1 = builder.equal(rootConversion.get(Conversion.EntityAttributes.ID).get(EntityDependentKey.EntityAttributes.PROJECT_ID), projectId);
		Predicate p2 = builder.equal(rootConversion.get("actuatorId"), actuatorId);
		Predicate a1 = builder.and(p1,p2);
		query.where(a1);
		
		query.orderBy(orderById(builder, rootConversion));
		
		TypedQuery<Conversion> res = getSession().createQuery(query);
		res.setFirstResult(from);
		res.setMaxResults(amount);
		return res.getResultList();
	}
	
	private static Order orderById(CriteriaBuilder cb, Root<Conversion> root) {
		return cb.asc(root.get(Conversion.EntityAttributes.ID)
				.get(EntityDependentKey.EntityAttributes.ID));
	}

}
