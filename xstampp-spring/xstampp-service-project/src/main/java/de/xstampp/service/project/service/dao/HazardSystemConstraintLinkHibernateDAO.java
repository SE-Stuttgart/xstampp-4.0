package de.xstampp.service.project.service.dao;

import de.xstampp.service.project.data.entity.HazardSystemConstraintLink;
import de.xstampp.service.project.service.dao.iface.IHazardSystemConstraintLinkDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Repository
public class HazardSystemConstraintLinkHibernateDAO implements IHazardSystemConstraintLinkDAO {

	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public boolean addLink(UUID projectId, int hazardId, int systemConstraintId) {
		HazardSystemConstraintLink link = new HazardSystemConstraintLink(projectId, systemConstraintId, hazardId);
		getSession().persist(link);
		return true;
	}

	@Override
	public boolean addLink(HazardSystemConstraintLink hazardSystemConstraintLink) {
		return addLink(hazardSystemConstraintLink.getProjectId(),
				hazardSystemConstraintLink.getHazardId(),
				hazardSystemConstraintLink.getSystemConstraintId());
	}

	@Override
	public boolean deleteLink(UUID projectId, int hazardId, int systemConstraintId) {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<HazardSystemConstraintLink> query = builder.createQuery(HazardSystemConstraintLink.class);
		Root<HazardSystemConstraintLink> rootLink = query.from(HazardSystemConstraintLink.class);
		query.select(rootLink);
		query.where(builder.and(
				builder.equal(rootLink.get(HazardSystemConstraintLink.EntityAttributes.HAZARD_ID), hazardId),
				builder.equal(rootLink.get(HazardSystemConstraintLink.EntityAttributes.PROJECT_ID), projectId),
				builder.equal(rootLink.get(HazardSystemConstraintLink.EntityAttributes.SYSTEM_CONSTRAINT_ID),
						systemConstraintId)));

		HazardSystemConstraintLink link = getSession().createQuery(query).getSingleResult();
		getSession().delete(link);
		return true;
	}

	@Override
	public List<HazardSystemConstraintLink> saveAll(List<HazardSystemConstraintLink> hazardSystemConstraintLinks) {
		List<HazardSystemConstraintLink> savedLinks = new LinkedList<>();
		Session session = sessionFactory.getCurrentSession();

		for (HazardSystemConstraintLink link: hazardSystemConstraintLinks) {
			HazardSystemConstraintLink savedLink = HazardSystemConstraintLink.class
					.cast(session.save(link));
			savedLinks.add(savedLink);
		}

		session.flush();

		return savedLinks;
	}

	@Override
	public List<HazardSystemConstraintLink> getAllLinks(UUID projectId) {
		CriteriaBuilder criteriaBuilder = getSession().getCriteriaBuilder();
		CriteriaQuery<HazardSystemConstraintLink> query = criteriaBuilder.createQuery(HazardSystemConstraintLink.class);
		Root<HazardSystemConstraintLink> rootLink = query.from(HazardSystemConstraintLink.class);
		query.select(rootLink);
		query.where(criteriaBuilder.and(
				criteriaBuilder.equal(rootLink.get(HazardSystemConstraintLink.EntityAttributes.PROJECT_ID), projectId)));

		return getSession().createQuery(query).getResultList();
	}

	@Override
	public List<HazardSystemConstraintLink> getLinksByHazardId(UUID projectId, int hazardId, Integer amount,
			Integer from) {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<HazardSystemConstraintLink> query = builder.createQuery(HazardSystemConstraintLink.class);
		Root<HazardSystemConstraintLink> rootLink = query.from(HazardSystemConstraintLink.class);
		query.select(rootLink);
		query.where(builder.and(
				builder.equal(rootLink.get(HazardSystemConstraintLink.EntityAttributes.HAZARD_ID), hazardId),
				builder.equal(rootLink.get(HazardSystemConstraintLink.EntityAttributes.PROJECT_ID),
						projectId)));
		
		query.orderBy(builder.asc(rootLink.get(HazardSystemConstraintLink.EntityAttributes.SYSTEM_CONSTRAINT_ID)));

		Query<HazardSystemConstraintLink> execQuery = getSession().createQuery(query);
		if (amount != null && from != null) {
			execQuery.setMaxResults(amount);
			execQuery.setFirstResult(from);
		}
		return execQuery.getResultList();
	}
	
	@Override
	public HazardSystemConstraintLink getLink(UUID projectId, int hazardId, int systemConstraintLinkId) {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<HazardSystemConstraintLink> query = builder.createQuery(HazardSystemConstraintLink.class);
		Root<HazardSystemConstraintLink> rootLink = query.from(HazardSystemConstraintLink.class);
		query.select(rootLink);
		query.where(builder.and(
				builder.equal(rootLink.get(HazardSystemConstraintLink.EntityAttributes.HAZARD_ID), hazardId),
				builder.equal(rootLink.get(HazardSystemConstraintLink.EntityAttributes.PROJECT_ID),projectId),
				builder.equal(rootLink.get(HazardSystemConstraintLink.EntityAttributes.SYSTEM_CONSTRAINT_ID),systemConstraintLinkId)));

		Query<HazardSystemConstraintLink> execQuery = getSession().createQuery(query);
		if(execQuery.getResultList().isEmpty()) {
			return null;
		} else {
			return getSession().createQuery(query).getSingleResult();
		}
	}

	@Override
	public List<HazardSystemConstraintLink> getLinksBySystemConstraintId(UUID projectId, int systemConstraintId,
                                                                         Integer amount, Integer from) {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<HazardSystemConstraintLink> query = builder.createQuery(HazardSystemConstraintLink.class);
		Root<HazardSystemConstraintLink> rootLink = query.from(HazardSystemConstraintLink.class);
		query.select(rootLink);
		query.where(builder.and(
				builder.equal(rootLink.get(HazardSystemConstraintLink.EntityAttributes.SYSTEM_CONSTRAINT_ID),
						systemConstraintId),
				builder.equal(rootLink.get(HazardSystemConstraintLink.EntityAttributes.PROJECT_ID),
						projectId)));
		
		query.orderBy(builder.asc(rootLink.get(HazardSystemConstraintLink.EntityAttributes.HAZARD_ID)));

		Query<HazardSystemConstraintLink> execQuery = getSession().createQuery(query);
		if (amount != null && from != null) {
			execQuery.setMaxResults(amount);
			execQuery.setFirstResult(from);
		}
		return execQuery.getResultList();
	}
}
