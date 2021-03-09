package de.xstampp.service.project.service.dao;

import java.util.List;
import java.util.UUID;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

import de.xstampp.service.project.data.entity.*;
import org.springframework.stereotype.Repository;

import de.xstampp.common.dto.mock.Filter;
import de.xstampp.service.project.service.dao.generic.pagination.AbstractEntityDependentHibernateDAO;
import de.xstampp.service.project.service.dao.iface.IUnsafeControlActionDAO;

@Repository
public class UnsafeControlActionHibernateDAO extends AbstractEntityDependentHibernateDAO<UnsafeControlAction, EntityDependentKey>
		implements IUnsafeControlActionDAO {

	@Override
	protected String getIdAttributeName() {
		return UnsafeControlAction.EntityAttributes.ID;
	}

	@Override
	public List<UnsafeControlAction> getUnsafeControlActionsByControlActionId(UUID projectId, int controlActionId,
			int amount, int from) {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<UnsafeControlAction> query = builder.createQuery(UnsafeControlAction.class);
		Root<UnsafeControlAction> rootUnsafeControlAction = query.from(UnsafeControlAction.class);
		query.select(rootUnsafeControlAction);
		Predicate p1 = builder.equal(rootUnsafeControlAction.get(UnsafeControlAction.EntityAttributes.ID).get(EntityDependentKey.EntityAttributes.PROJECT_ID), projectId);
		Predicate p2 = builder.equal(rootUnsafeControlAction.get(UnsafeControlAction.EntityAttributes.ID).get(EntityDependentKey.EntityAttributes.PARENT_ID),controlActionId);
		Predicate a1 = builder.and(p1,p2);
		query.where(a1);
		
		query.orderBy(orderById(builder, rootUnsafeControlAction));
		
		TypedQuery<UnsafeControlAction> res = getSession().createQuery(query);
		res.setFirstResult(from);
		res.setMaxResults(amount);
		return res.getResultList();
	}

	@Override
	public List<UnsafeControlAction> findUnsafeControlActionsByIds(List<EntityDependentKey> ucaPKeys ,Integer from, Integer amount) {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<UnsafeControlAction> query = builder.createQuery(UnsafeControlAction.class);
		Root<UnsafeControlAction> rootUnsafeControlAction = query.from(UnsafeControlAction.class);
		query.select(rootUnsafeControlAction);
		query.where(rootUnsafeControlAction.get(UnsafeControlAction.EntityAttributes.ID).in(ucaPKeys));
		
		query.orderBy(orderById(builder, rootUnsafeControlAction));
		
		TypedQuery<UnsafeControlAction> res = getSession().createQuery(query);
		res.setFirstResult(from);
		res.setMaxResults(amount);
		List<UnsafeControlAction> result = res.getResultList();
		
		return result;
	}

	@Override
	public List<UnsafeControlAction> findUnsafeControlActionsByControlActionAndType(UUID projectId, int controlActionId,
			Integer amount, Integer from, Filter type) {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<UnsafeControlAction> query = builder.createQuery(UnsafeControlAction.class);
		Root<UnsafeControlAction> rootUnsafeControlAction = query.from(UnsafeControlAction.class);
		query.select(rootUnsafeControlAction);
		Predicate p1 = builder.equal(rootUnsafeControlAction.get(UnsafeControlAction.EntityAttributes.ID).get(EntityDependentKey.EntityAttributes.PROJECT_ID),projectId);
		Predicate p2 = builder.equal(rootUnsafeControlAction.get(UnsafeControlAction.EntityAttributes.ID).get(EntityDependentKey.EntityAttributes.PARENT_ID),controlActionId);
		Predicate p3 = builder.equal(rootUnsafeControlAction.get(UnsafeControlAction.EntityAttributes.CATEGORY),type.getFieldValue());
		Predicate a1 = builder.and(p1,p2,p3);
		query.where(a1);
		
		query.orderBy(orderById(builder, rootUnsafeControlAction));
		
		TypedQuery<UnsafeControlAction> res = getSession().createQuery(query);
		res.setFirstResult(from);
		res.setMaxResults(amount);
		return res.getResultList();
	}

	@Override
	public int countUnsafeControlActionsByControlActionAndType(UUID projectId, int controlActionId, Filter type) {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<UnsafeControlAction> query = builder.createQuery(UnsafeControlAction.class);
		Root<UnsafeControlAction> rootUnsafeControlAction = query.from(UnsafeControlAction.class);
		query.select(rootUnsafeControlAction);
		Predicate p1 = builder.equal(rootUnsafeControlAction.get(UnsafeControlAction.EntityAttributes.ID).get(EntityDependentKey.EntityAttributes.PROJECT_ID),projectId);
		Predicate p2 = builder.equal(rootUnsafeControlAction.get(UnsafeControlAction.EntityAttributes.ID).get(EntityDependentKey.EntityAttributes.PARENT_ID),controlActionId);
		Predicate p3 = builder.equal(rootUnsafeControlAction.get(UnsafeControlAction.EntityAttributes.CATEGORY),type.getFieldValue());
		Predicate a1 = builder.and(p1,p2,p3);
		query.where(a1);
		return getSession().createQuery(query).getResultList().size();
	}
	
	private static Order orderById(CriteriaBuilder cb, Root<UnsafeControlAction> root) {
		return cb.asc(root.get(UnsafeControlAction.EntityAttributes.ID)
				.get(EntityDependentKey.EntityAttributes.ID));
	}

	@Override
	public ControllerConstraint getControllerConstraintByUnsafeControlAction(UUID projectId, int caId, int ucaId) {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<ControllerConstraint> query = builder.createQuery(ControllerConstraint.class);
		Root<ControllerConstraint> root = query.from(ControllerConstraint.class);

		Path<EntityDependentKey> pathKey = root.get(ControllerConstraint.EntityAttributes.ID);
		query.select(root);

		query.where(
				builder.and(
						builder.equal(pathKey.get(EntityDependentKey.EntityAttributes.PROJECT_ID), projectId),
						builder.equal(pathKey.get(EntityDependentKey.EntityAttributes.PARENT_ID), caId),
						builder.equal(pathKey.get(EntityDependentKey.EntityAttributes.ID), ucaId)
				)
		);
		List<ControllerConstraint> results = getSession().createQuery(query).getResultList();
		if(results.size() == 1) {
			return results.get(0);
		}
		return null;
	}
}
