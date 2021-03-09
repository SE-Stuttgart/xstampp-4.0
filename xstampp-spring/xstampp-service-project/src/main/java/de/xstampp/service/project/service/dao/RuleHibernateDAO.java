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
import de.xstampp.service.project.data.entity.Rule;
import de.xstampp.service.project.service.dao.generic.pagination.AbstractEntityDependentHibernateDAO;
import de.xstampp.service.project.service.dao.iface.IRuleDAO;

@Repository
public class RuleHibernateDAO extends AbstractEntityDependentHibernateDAO<Rule, EntityDependentKey>
		implements IRuleDAO {

	@Autowired
	SessionFactory sessionFactory;
	
	@Override
	protected String getIdAttributeName() {
		return Rule.EntityAttributes.ID;
	}

	@Override
	public List<Rule> findRulesByIds(List<EntityDependentKey> ruleKeys, Integer from, Integer amount) {
		if (ruleKeys.isEmpty()) {
			return Collections.emptyList();
		}
		CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<Rule> query = builder.createQuery(Rule.class);
		Root<Rule> rootRule = query.from(Rule.class);
		query.select(rootRule);
		query.where(rootRule.get(Rule.EntityAttributes.ID).in(ruleKeys));

		query.orderBy(orderById(builder, rootRule));

		TypedQuery<Rule> res = getSession().createQuery(query);
		res.setFirstResult(from);
		res.setMaxResults(amount);
		List<Rule> result = res.getResultList();

		return result;
	}
	
	@Override
	public List<Rule> getRulesByControllerId(UUID projectId, int controllerId,
			int amount, int from) {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<Rule> query = builder.createQuery(Rule.class);
		Root<Rule> rootRule = query.from(Rule.class);
		query.select(rootRule);
		Predicate p1 = builder.equal(rootRule.get(Rule.EntityAttributes.ID).get(EntityDependentKey.EntityAttributes.PROJECT_ID), projectId);
		Predicate p2 = builder.equal(rootRule.get("controllerId"), controllerId);
		Predicate a1 = builder.and(p1,p2);
		query.where(a1);
		
		query.orderBy(orderById(builder, rootRule));
		
		TypedQuery<Rule> res = getSession().createQuery(query);
		res.setFirstResult(from);
		res.setMaxResults(amount);
		return res.getResultList();
	}
	
	private static Order orderById(CriteriaBuilder cb, Root<Rule> root) {
		return cb.asc(root.get(Rule.EntityAttributes.ID)
				.get(EntityDependentKey.EntityAttributes.ID));
	}

}
