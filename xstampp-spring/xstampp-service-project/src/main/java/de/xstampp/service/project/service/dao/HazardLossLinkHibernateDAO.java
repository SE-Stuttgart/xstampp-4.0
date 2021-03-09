package de.xstampp.service.project.service.dao;

import de.xstampp.service.project.data.entity.HazardLossLink;
import de.xstampp.service.project.service.dao.iface.IHazardLossLinkDAO;
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
public class HazardLossLinkHibernateDAO implements IHazardLossLinkDAO {

	@Autowired
	SessionFactory sessionFactory;

	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public boolean addLink(UUID projectId, int hazardId, int lossId) {
		HazardLossLink link = new HazardLossLink(projectId, lossId, hazardId);
		getSession().saveOrUpdate(link);
		return true;
	}

	@Override
	public boolean addLink(HazardLossLink hazardLossLink) {
		return addLink(hazardLossLink.getProjectId(), hazardLossLink.getHazardId(), hazardLossLink.getLossId());
	}

	@Override
	public boolean deleteLink(UUID projectId, int hazardId, int lossId) {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<HazardLossLink> query = builder.createQuery(HazardLossLink.class);
		Root<HazardLossLink> rootLink = query.from(HazardLossLink.class);
		query.select(rootLink);
		query.where(builder.and(
				builder.equal(rootLink.get(HazardLossLink.EntityAttributes.HAZARD_ID), hazardId),
				builder.equal(rootLink.get(HazardLossLink.EntityAttributes.PROJECT_ID), projectId),
				builder.equal(rootLink.get(HazardLossLink.EntityAttributes.LOSS_ID),
						lossId)));
		
		HazardLossLink link = getSession().createQuery(query).getSingleResult();
		getSession().delete(link);
		return true;
	}

	@Override
	public List<HazardLossLink> saveAll(List<HazardLossLink> hazardLossLinks) {
		List<HazardLossLink> savedLinks = new LinkedList<>();
		Session session = sessionFactory.getCurrentSession();

		for (HazardLossLink link: hazardLossLinks) {
			HazardLossLink savedLink = HazardLossLink.class
					.cast(session.save(link));
			savedLinks.add(savedLink);
		}

		session.flush();

		return savedLinks;
	}

	@Override
	public List<HazardLossLink> getAllLinks(UUID projectId) {
		CriteriaBuilder criteriaBuilder = getSession().getCriteriaBuilder();
		CriteriaQuery<HazardLossLink> query = criteriaBuilder.createQuery(HazardLossLink.class);
		Root<HazardLossLink> rootLink = query.from(HazardLossLink.class);
		query.select(rootLink);
		query.where(criteriaBuilder.and(
				criteriaBuilder.equal(rootLink.get(HazardLossLink.EntityAttributes.PROJECT_ID), projectId)));

		return getSession().createQuery(query).getResultList();
	}

	@Override
	public List<HazardLossLink> getLinksByLossId(UUID projectId, int lossId, int amount, int from) {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<HazardLossLink> query = builder.createQuery(HazardLossLink.class);
		Root<HazardLossLink> root = query.from(HazardLossLink.class);
		query.select(root);
		query.where(builder.and(builder.equal(root.get(HazardLossLink.EntityAttributes.LOSS_ID), lossId),
				builder.equal(root.get(HazardLossLink.EntityAttributes.PROJECT_ID), projectId)));
		query.orderBy(builder.asc(root.get(HazardLossLink.EntityAttributes.HAZARD_ID)));
		return getSession().createQuery(query).setMaxResults(amount).setFirstResult(from).getResultList();
	}

	@Override
	public List<HazardLossLink> getLinksByHazardId(UUID projectId, int hazardId, int amount, int from) {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<HazardLossLink> query = builder.createQuery(HazardLossLink.class);
		Root<HazardLossLink> root = query.from(HazardLossLink.class);
		query.select(root);
		query.where(builder.and(builder.equal(root.get(HazardLossLink.EntityAttributes.HAZARD_ID), hazardId),
				builder.equal(root.get(HazardLossLink.EntityAttributes.PROJECT_ID), projectId)));
		query.orderBy(builder.asc(root.get(HazardLossLink.EntityAttributes.LOSS_ID)));
		return getSession().createQuery(query).setMaxResults(amount).setFirstResult(from).getResultList();
	}

}
