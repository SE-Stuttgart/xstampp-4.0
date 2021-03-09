package de.xstampp.service.project.service.dao.generic;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.xstampp.service.project.service.dao.iface.IGenericDAO;

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
public abstract class AbstractGenericHibernateDAO<T extends Serializable, ID extends Serializable>
		implements IGenericDAO<T, ID> {

	private Class<T> persistentClass;

	private Class<ID> idClass;

	@Autowired
	private SessionFactory sessionFactory;

	@SuppressWarnings("unchecked")
	public AbstractGenericHibernateDAO() {
		super();
		this.persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
		this.idClass = (Class<ID>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
	}

	protected final Class<T> getPersistentClass() {
		return persistentClass;
	}
	
	protected final Class<ID> getIdClass() {
		return idClass;
	}

	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	@Override
	public T findById(ID id, boolean lock) {
		T entity;
		if (lock) {
			entity = getSession().get(getPersistentClass(), id, LockMode.PESSIMISTIC_WRITE);
		} else {
			entity = getSession().get(getPersistentClass(), id);
		}
		return entity;
	}

	@Override
	public List<ID> saveAll(List<T> entities) {
		List<ID> ids = new LinkedList<>();
		Session session = getSession();

		for (T entity : entities) {
			ID id = getIdClass()
					.cast(session.save(entity));
			ids.add(id);
		}

		session.flush();

		return ids;
	}

	@Override
	public void updateAll(List<T> entities) {
		Session session = getSession();

		for (T entity : entities) {
			session.update(entity);
		}

		session.flush();
	}

	/*
	 * FIXME: In case of database autoincrement: This couldn't be more wrong. The entity that's returned by
	 *  a save should always be the new one from the database. Not the old one.
	 *  And the combination with update should be solved differently.
	 */
	@Override
	public T makePersistent(T entity) {
		getSession().saveOrUpdate(entity);
		return entity;
	}

	@Override
	public void makeTransient(T entity) {
		getSession().delete(entity);
	}

	@Override
	public T updateExisting(T entity) {
		getSession().update(entity);
		return entity;
	}

	@Override
	public boolean exist(ID id) {
		CriteriaBuilder cb = getSession().getCriteriaBuilder();
		CriteriaQuery<ID> criteria = cb.createQuery(getIdClass());
		Root<T> root = criteria.from(getPersistentClass());
		criteria = criteria.select(root.<ID>get(getIdAttributeName()))
				.where(cb.equal(root.get(getIdAttributeName()), id));
		List<ID> result = getSession().createQuery(criteria).getResultList();
		return !result.isEmpty();
	}
	
	protected abstract String getIdAttributeName();

}
