package de.xstampp.service.auth.service.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * Implements methods for basic database operations for the entity of the given
 * type.
 * </p>
 * <p>
 * Reference: C. Bauer, G. King: Java Persistence mit Hibernate, Publisher:
 * Manning Publications, german edition, Munich, 2007.
 * </p>
 * 
 * @param <T> entity type.
 * @param <ID> type representing the id of the given entity type.
 */
public abstract class AbstractGenericHibernateDAO<T, ID extends Serializable> implements IGenericDAO<T, ID> {

	private Class<T> persistentClass;

	@Autowired
	private SessionFactory sessionFactory;

	@SuppressWarnings("unchecked")
	public AbstractGenericHibernateDAO() {
		super();
		this.persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
	}

	protected final Class<T> getPersistentClass() {
		return persistentClass;
	}

	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public T findById(ID id, boolean lock) {
		T entity;
		if (lock) {
			entity = (T) getSession().get(getPersistentClass(), id, LockMode.PESSIMISTIC_WRITE);
		} else {
			entity = (T) getSession().get(getPersistentClass(), id);
		}
		return entity;
	}

	@Override
	public List<T> findAll() {
		CriteriaQuery<T> criteria = getSession().getCriteriaBuilder().createQuery(getPersistentClass());
		Root<T> root = criteria.from(getPersistentClass());
		criteria = criteria.select(root);
		return getSession().createQuery(criteria).getResultList();
	}

	@Override
	public T makePersistent(T entity) {
		getSession().saveOrUpdate(entity);
		return entity;
	}

	@Override
	public T saveNew(T entity) {
		Session session = getSession();
		session.save(entity);
		return entity;
	}

	@Override
	public T updateExisting(T entity) {
		getSession().update(entity);
		return entity;
	}

	@Override
	public void makeTransient(T entity) {
		getSession().delete(entity);
	}
	
	@Override
	public List<T> findFromTo(int from, int amount, Map<String, SortOrder> order) {
		CriteriaBuilder cb = getSession().getCriteriaBuilder();
		CriteriaQuery<T> criteria = cb.createQuery(getPersistentClass());
		Root<T> root = criteria.from(getPersistentClass());
		criteria = criteria.select(root);
		List<Order> listOfOrders = new ArrayList<>();
		for (Entry<String, SortOrder> entry : order.entrySet()) {
			if (entry.getValue() == SortOrder.ASC) {
				listOfOrders.add(cb.asc(root.get(entry.getKey())));
			} else if (entry.getValue() == SortOrder.DESC) {
				listOfOrders.add(cb.desc(root.get(entry.getKey())));
			}
		}
		criteria.orderBy(listOfOrders);
		TypedQuery<T> query = getSession().createQuery(criteria);
		query.setFirstResult(from);
		query.setMaxResults(amount);
		return query.getResultList();
	}

	@Override
	public long count() {
		CriteriaBuilder cb = getSession().getCriteriaBuilder();
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		criteria = criteria.select(cb.count(criteria.from(getPersistentClass())));
		TypedQuery<Long> query = getSession().createQuery(criteria);
		return query.getSingleResult();
	}

}
