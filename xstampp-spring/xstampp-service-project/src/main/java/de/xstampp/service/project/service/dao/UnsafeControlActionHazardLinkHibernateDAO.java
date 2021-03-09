package de.xstampp.service.project.service.dao;

import de.xstampp.service.project.data.entity.UnsafeControlActionHazardLink;
import de.xstampp.service.project.service.dao.iface.IUnsafeControlActionHazardLinkDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Repository
public class UnsafeControlActionHazardLinkHibernateDAO implements IUnsafeControlActionHazardLinkDAO {

	@Autowired
	SessionFactory sessionFactory;
	
	@Override
	public boolean addLink(UnsafeControlActionHazardLink ucaHazardLink) {
		sessionFactory.getCurrentSession().persist(ucaHazardLink);
		return true;
	}

	@Override
	public boolean deleteLink(UUID projectId, int unsafeControlActionId, int hazardId, int controlActionId) {
		CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<UnsafeControlActionHazardLink> query = builder.createQuery(UnsafeControlActionHazardLink.class);
		Root<UnsafeControlActionHazardLink> rootLink= query.from(UnsafeControlActionHazardLink.class);
		query.select(rootLink);
		query.where(	
			builder.and(
				builder.equal(rootLink.get(UnsafeControlActionHazardLink.EntityAttributes.PROJECT_ID), projectId),
				builder.equal(rootLink.get(UnsafeControlActionHazardLink.EntityAttributes.CONTROL_ACTION_ID), controlActionId),
				builder.equal(rootLink.get(UnsafeControlActionHazardLink.EntityAttributes.HAZARD_ID), hazardId),
				builder.equal(rootLink.get(UnsafeControlActionHazardLink.EntityAttributes.UNSAFE_CONTROL_ACTION_ID), unsafeControlActionId)
			)
		);
		UnsafeControlActionHazardLink link = sessionFactory.getCurrentSession().createQuery(query).getSingleResult();
		sessionFactory.getCurrentSession().delete(link);
		return true;
	}

	@Override
	public List<UnsafeControlActionHazardLink> saveAll(List<UnsafeControlActionHazardLink> links) {
		List<UnsafeControlActionHazardLink> savedLinks = new LinkedList<>();
		Session session = sessionFactory.getCurrentSession();

		for (UnsafeControlActionHazardLink link: links) {
			UnsafeControlActionHazardLink savedLink = UnsafeControlActionHazardLink.class
					.cast(session.save(link));
			savedLinks.add(savedLink);
		}

		session.flush();

		return savedLinks;
	}

	@Override
	public List<UnsafeControlActionHazardLink> getAllLinks(UUID projectId) {
		CriteriaBuilder criteriaBuilder = sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<UnsafeControlActionHazardLink> query = criteriaBuilder.createQuery(UnsafeControlActionHazardLink.class);
		Root<UnsafeControlActionHazardLink> rootLink = query.from(UnsafeControlActionHazardLink.class);
		query.select(rootLink);
		query.where(criteriaBuilder.and(
				criteriaBuilder.equal(rootLink.get(UnsafeControlActionHazardLink.EntityAttributes.PROJECT_ID), projectId)));

		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}

	@Override
	public List<UnsafeControlActionHazardLink> getLinksByHazardId(UUID projectId, int hazardId) {
		
		CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<UnsafeControlActionHazardLink> query = builder.createQuery(UnsafeControlActionHazardLink.class);
		Root<UnsafeControlActionHazardLink> root= query.from(UnsafeControlActionHazardLink.class);
		query.select(root);
		query.where(
			builder.and(
				builder.equal(root.get(UnsafeControlActionHazardLink.EntityAttributes.PROJECT_ID), projectId),
				builder.equal(root.get(UnsafeControlActionHazardLink.EntityAttributes.HAZARD_ID), hazardId)
			)
		);
		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}

	@Override
	public List<UnsafeControlActionHazardLink> getLinksByUnsafeControlActionId(UUID projectId, int controlActionId,
			int ucaId) {
		CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<UnsafeControlActionHazardLink> query = builder.createQuery(UnsafeControlActionHazardLink.class);
		Root<UnsafeControlActionHazardLink> root= query.from(UnsafeControlActionHazardLink.class);
		query.select(root);
		query.where(	
			builder.and(
				builder.equal(root.get(UnsafeControlActionHazardLink.EntityAttributes.PROJECT_ID), projectId),
				builder.equal(root.get(UnsafeControlActionHazardLink.EntityAttributes.CONTROL_ACTION_ID), controlActionId),
				builder.equal(root.get(UnsafeControlActionHazardLink.EntityAttributes.UNSAFE_CONTROL_ACTION_ID), ucaId)
			)
		);
		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}
}
