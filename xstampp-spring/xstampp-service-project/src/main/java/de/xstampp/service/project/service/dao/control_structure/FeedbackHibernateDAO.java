package de.xstampp.service.project.service.dao.control_structure;

import java.util.List;
import java.util.UUID;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import de.xstampp.common.errors.ErrorsProj;
import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.control_structure.Feedback;
import de.xstampp.service.project.service.dao.control_structure.iface.IFeedbackDAO;
import de.xstampp.service.project.service.dao.generic.pagination.AbstractProjectDependentHibernateDAO;

@Repository
public class FeedbackHibernateDAO extends AbstractProjectDependentHibernateDAO<Feedback, ProjectDependentKey> implements IFeedbackDAO{

	@Autowired
	SessionFactory sessionFactory;
	
	@Override
	protected String getIdAttributeName() {
		return Feedback.EntityAttributes.ID;
	}

	@Override
	public List<Feedback> getByArrowId(UUID projectId, String arrowId) {
		CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<Feedback> query = builder.createQuery(Feedback.class);
		Root<Feedback> rootFeedback = query.from(Feedback.class);
		query.select(rootFeedback);
		query.where(builder.and(
				builder.equal(rootFeedback.get(Feedback.EntityAttributes.ID)
						.get(ProjectDependentKey.EntityAttributes.PROJECT_ID), projectId),
				builder.equal(rootFeedback.get(Feedback.EntityAttributes.ARROWID), arrowId)));
		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}

	@Override
	public List<Feedback> getAllUnlinkedFeedbacks(UUID projectId) {
		CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<Feedback> query = builder.createQuery(Feedback.class);
		Root<Feedback> rootFeedback = query.from(Feedback.class);
		query.select(rootFeedback);
		query.where(builder.and(
				builder.equal(rootFeedback.get(Feedback.EntityAttributes.ID)
						.get(ProjectDependentKey.EntityAttributes.PROJECT_ID), projectId),
				builder.isNull(rootFeedback.get(Feedback.EntityAttributes.ARROWID))));
		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}

	@Override
	public Feedback setArrowId(UUID projectID, int feedbackId, String arrowId) {
		ProjectDependentKey key = new ProjectDependentKey(projectID, feedbackId);
		Feedback feedback = this.findById(key, false);
		if (feedback != null) {			
			feedback.setArrowId(arrowId);
			this.makePersistent(feedback);
		} else {
			throw ErrorsProj.DOES_NOT_EXIST.exc();
		}
		
		return feedback;
	}

}
