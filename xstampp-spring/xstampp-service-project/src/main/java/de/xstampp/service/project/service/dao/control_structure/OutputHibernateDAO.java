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
import de.xstampp.service.project.data.entity.control_structure.Output;
import de.xstampp.service.project.service.dao.control_structure.iface.IOutputDAO;
import de.xstampp.service.project.service.dao.generic.pagination.AbstractProjectDependentHibernateDAO;

@Repository
public class OutputHibernateDAO extends AbstractProjectDependentHibernateDAO<Output, ProjectDependentKey> implements IOutputDAO{

	@Autowired
	SessionFactory sessionFactory;
	
	@Override
	protected String getIdAttributeName() {
		return Output.EntityAttributes.ID;
	}

	@Override
	public List<Output> getByArrowId(UUID projectId, String arrowId) {
		CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<Output> query = builder.createQuery(Output.class);
		Root<Output> rootOutput = query.from(Output.class);
		query.select(rootOutput);
		query.where(builder.and(
				builder.equal(rootOutput.get(Output.EntityAttributes.ID)
						.get(ProjectDependentKey.EntityAttributes.PROJECT_ID), projectId),
				builder.equal(rootOutput.get(Output.EntityAttributes.ARROWID), arrowId)));
		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}

	@Override
	public List<Output> getAllUnlinkedOutputs(UUID projectId) {
		CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<Output> query = builder.createQuery(Output.class);
		Root<Output> rootOutput = query.from(Output.class);
		query.select(rootOutput);
		query.where(builder.and(
				builder.equal(rootOutput.get(Output.EntityAttributes.ID)
						.get(ProjectDependentKey.EntityAttributes.PROJECT_ID), projectId),
				builder.isNull(rootOutput.get(Output.EntityAttributes.ARROWID))));
		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}

	@Override
	public Output setArrowId(UUID projectID, int outputId, String arrowId) {
		ProjectDependentKey key = new ProjectDependentKey(projectID, outputId);
		Output output = this.findById(key, false);
		if (output != null) {			
			output.setArrowId(arrowId);
			this.makePersistent(output);
		} else {
			throw ErrorsProj.DOES_NOT_EXIST.exc();
		}
		
		return output;
	}
}
