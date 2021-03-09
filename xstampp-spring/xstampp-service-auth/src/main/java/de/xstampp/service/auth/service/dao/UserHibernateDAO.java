package de.xstampp.service.auth.service.dao;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import de.xstampp.service.auth.data.Icon;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import de.xstampp.service.auth.data.User;

@Repository
public class UserHibernateDAO extends AbstractGenericHibernateDAO<User, UUID> implements IUserDAO {


	@Autowired
	SessionFactory sessionFactory;

	@Override
	public List<User> findByExample(User exampleInstance) {
		throw new UnsupportedOperationException();
	}

	@Override
	public User getByEmail(String email) {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<User> query = builder.createQuery(User.class);
		Root<User> root = query.from(User.class);
		query.select(root);
		query.where(builder.equal(root.get(User.EntityAttributes.EMAIL), email));

		List<User> res = getSession().createQuery(query).getResultList();
		if (res.size() > 2) {
			throw new IllegalStateException("No two users with the same email address may exist.");
		} else if (res.size() == 1) {
			return res.get(0);
		} else {
			return null;
		}
	}


	@Override
	public List<User> findGUsersByIds(List<UUID> userIds) {
		if (userIds.isEmpty()) {
			return Collections.emptyList();
		}
		CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<User> query = builder.createQuery(User.class);
		Root<User> root = query.from(User.class);
		query.select(root);
		query.where(root.get(User.EntityAttributes.ID).in(userIds));
		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}


}
