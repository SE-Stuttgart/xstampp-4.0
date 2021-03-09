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
import de.xstampp.service.project.data.entity.control_structure.Input;
import de.xstampp.service.project.service.dao.control_structure.iface.IInputDAO;
import de.xstampp.service.project.service.dao.generic.pagination.AbstractProjectDependentHibernateDAO;

@Repository
public class InputHibernateDAO extends AbstractProjectDependentHibernateDAO<Input, ProjectDependentKey> implements IInputDAO{

	@Autowired
	SessionFactory sessionFactory;
	
	@Override
	protected String getIdAttributeName() {
		return Input.EntityAttributes.ID;
	}

	@Override
	public List<Input> getByArrowId(UUID projectId, String arrowId) {
		CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<Input> query = builder.createQuery(Input.class);
		Root<Input> rootInput = query.from(Input.class);
		query.select(rootInput);
		query.where(builder.and(
				builder.equal(rootInput.get(Input.EntityAttributes.ID)
						.get(ProjectDependentKey.EntityAttributes.PROJECT_ID), projectId),
				builder.equal(rootInput.get(Input.EntityAttributes.ARROWID), arrowId)));
		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}

	@Override
	public List<Input> getAllUnlinkedInputs(UUID projectId) {
		CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<Input> query = builder.createQuery(Input.class);
		Root<Input> rootInput = query.from(Input.class);
		query.select(rootInput);
		query.where(builder.and(
				builder.equal(rootInput.get(Input.EntityAttributes.ID)
						.get(ProjectDependentKey.EntityAttributes.PROJECT_ID), projectId),
				builder.isNull(rootInput.get(Input.EntityAttributes.ARROWID))));
		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}

	@Override
	public Input setArrowId(UUID projectID, int inputId, String arrowId) {
		ProjectDependentKey key = new ProjectDependentKey(projectID, inputId);
		Input input = this.findById(key, false);
		if (input != null) {			
			input.setArrowId(arrowId);
			this.makePersistent(input);
		} else {
			throw ErrorsProj.DOES_NOT_EXIST.exc();
		}
		
		return input;
	}

}
