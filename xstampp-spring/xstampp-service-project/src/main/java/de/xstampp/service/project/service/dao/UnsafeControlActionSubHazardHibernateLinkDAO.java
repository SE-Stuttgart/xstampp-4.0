package de.xstampp.service.project.service.dao;

import de.xstampp.service.project.data.entity.UnsafeControlActionSubHazardLink;
import de.xstampp.service.project.service.dao.iface.IUnsafeControlActionSubHazardLinkDAO;
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
public class UnsafeControlActionSubHazardHibernateLinkDAO implements IUnsafeControlActionSubHazardLinkDAO {
	
	@Autowired
	SessionFactory sessionFactory;

	@Override
	public boolean deleteLink(UUID projectId, int unsafeControlActionId, int hazardId, int subHazardId, int controlActionId) {
		CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<UnsafeControlActionSubHazardLink> query = builder.createQuery(UnsafeControlActionSubHazardLink.class);
		Root<UnsafeControlActionSubHazardLink> rootLink= query.from(UnsafeControlActionSubHazardLink.class);
		query.select(rootLink);
		query.where(	
			builder.and(
				builder.equal(rootLink.get(UnsafeControlActionSubHazardLink.EntityAttributes.PROJECT_ID), projectId),
				builder.equal(rootLink.get(UnsafeControlActionSubHazardLink.EntityAttributes.CONTROL_ACTION_ID), controlActionId),
				builder.equal(rootLink.get(UnsafeControlActionSubHazardLink.EntityAttributes.HAZARD_ID), hazardId),
				builder.equal(rootLink.get(UnsafeControlActionSubHazardLink.EntityAttributes.SUB_HAZARD_ID), subHazardId),
				builder.equal(rootLink.get(UnsafeControlActionSubHazardLink.EntityAttributes.UNSAFE_CONTROL_ACTION_ID), unsafeControlActionId)
			)
		);
		UnsafeControlActionSubHazardLink link = sessionFactory.getCurrentSession().createQuery(query).getSingleResult();
		sessionFactory.getCurrentSession().delete(link);
		return true;
	}

	@Override
	public List<UnsafeControlActionSubHazardLink> getAllLinks(UUID projectId) {
		CriteriaBuilder criteriaBuilder = sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<UnsafeControlActionSubHazardLink> query = criteriaBuilder.createQuery(UnsafeControlActionSubHazardLink.class);
		Root<UnsafeControlActionSubHazardLink> rootLink = query.from(UnsafeControlActionSubHazardLink.class);
		query.select(rootLink);
		query.where(criteriaBuilder.and(
				criteriaBuilder.equal(rootLink.get(UnsafeControlActionSubHazardLink.EntityAttributes.PROJECT_ID), projectId)));

		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}

	@Override
	public List<UnsafeControlActionSubHazardLink> saveAll(List<UnsafeControlActionSubHazardLink> links) {
		List<UnsafeControlActionSubHazardLink> savedLinks = new LinkedList<>();
		Session session = sessionFactory.getCurrentSession();

		for (UnsafeControlActionSubHazardLink link: links) {
			UnsafeControlActionSubHazardLink savedLink = UnsafeControlActionSubHazardLink.class
					.cast(session.save(link));
			savedLinks.add(savedLink);
		}

		session.flush();

		return savedLinks;
	}

	@Override
	public List<UnsafeControlActionSubHazardLink> getLinksByUnsafeControlActionId(UUID projectId, int controlActionId,
			int ucaId) {
		CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<UnsafeControlActionSubHazardLink> query = builder.createQuery(UnsafeControlActionSubHazardLink.class);
		Root<UnsafeControlActionSubHazardLink> root= query.from(UnsafeControlActionSubHazardLink.class);
		query.select(root);
		query.where(	
			builder.and(
				builder.equal(root.get(UnsafeControlActionSubHazardLink.EntityAttributes.PROJECT_ID), projectId),
				builder.equal(root.get(UnsafeControlActionSubHazardLink.EntityAttributes.CONTROL_ACTION_ID), controlActionId),
				builder.equal(root.get(UnsafeControlActionSubHazardLink.EntityAttributes.UNSAFE_CONTROL_ACTION_ID), ucaId)
			)
		);
		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}

	@Override
	public List<UnsafeControlActionSubHazardLink> getLinksBySubHazardId(UUID projectId,	int hazardId, int subHazardId) {
		CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<UnsafeControlActionSubHazardLink> query = builder.createQuery(UnsafeControlActionSubHazardLink.class);
		Root<UnsafeControlActionSubHazardLink> root= query.from(UnsafeControlActionSubHazardLink.class);
		query.select(root);
		query.where(
			builder.and(
				builder.equal(root.get(UnsafeControlActionSubHazardLink.EntityAttributes.PROJECT_ID), projectId),
				builder.equal(root.get(UnsafeControlActionSubHazardLink.EntityAttributes.HAZARD_ID), hazardId),
				builder.equal(root.get(UnsafeControlActionSubHazardLink.EntityAttributes.SUB_HAZARD_ID), subHazardId)
			)
		);
		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}

	@Override
	public boolean addLink(UnsafeControlActionSubHazardLink ucaSubHazardLink) {
		sessionFactory.getCurrentSession().persist(ucaSubHazardLink);
		return true;
	}

}
