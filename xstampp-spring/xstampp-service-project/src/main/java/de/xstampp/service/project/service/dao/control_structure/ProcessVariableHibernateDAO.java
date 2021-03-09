package de.xstampp.service.project.service.dao.control_structure;

import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.control_structure.ProcessVariable;
import de.xstampp.service.project.service.dao.control_structure.iface.IProcessVariableDAO;
import de.xstampp.service.project.service.dao.generic.pagination.AbstractProjectDependentHibernateDAO;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Repository
public class ProcessVariableHibernateDAO extends
		AbstractProjectDependentHibernateDAO<ProcessVariable, ProjectDependentKey> implements IProcessVariableDAO {

	@Autowired
	SessionFactory sessionFactory;

	@Override
	protected String getIdAttributeName() {
		return ProcessVariable.EntityAttributes.ID;
	}



	@Override
	public List<ProcessVariable> getAllProcessVariablesByArrowIds(UUID projectId, List<String> arrowIdList) {

		// check if List is empty
		if (arrowIdList == null || arrowIdList.isEmpty()) {
			return Collections.emptyList();
		}

		// Initialize Database request
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<ProcessVariable> query = builder.createQuery(ProcessVariable.class);
		Root<ProcessVariable> rootLink = query.from(ProcessVariable.class);


		query.select(rootLink);
		Path<ProjectDependentKey> key = rootLink.get(ProcessVariable.EntityAttributes.ID);
		// searchQuery
		query.where(builder.and(
				builder.equal(key.get(ProjectDependentKey.EntityAttributes.PROJECT_ID), projectId),
				rootLink.get(ProcessVariable.EntityAttributes.ARROW_ID).in(arrowIdList)));

		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}

	@Override
	public List<ProcessVariable> getAllProcessVariablesFromList(UUID projectId, List<Integer> processVariables) {

		//initialize Database request
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<ProcessVariable> query = builder.createQuery(ProcessVariable.class);
		Root<ProcessVariable> root = query.from(ProcessVariable.class);
		query.select(root);
		Path<ProjectDependentKey> key = root.get(ProcessVariable.EntityAttributes.ID);

		//searchQuery
		query.where(builder.and(
				builder.equal(key.get(ProjectDependentKey.EntityAttributes.PROJECT_ID), projectId),
				key.get(ProjectDependentKey.EntityAttributes.ID).in(processVariables)
		));

		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}

	@Override
	public List<ProcessVariable> getAllProcessVariablesForProject(UUID projectId) {

		//initialize Database request
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<ProcessVariable> query = builder.createQuery(ProcessVariable.class);
		Root<ProcessVariable> root = query.from(ProcessVariable.class);
		query.select(root);
		Path<ProjectDependentKey> key = root.get(ProcessVariable.EntityAttributes.ID);

		//searchQuery
		query.where(
				builder.equal(key.get(ProjectDependentKey.EntityAttributes.PROJECT_ID), projectId)
		);

		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}

	@Override
	public List<ProcessVariable> getAllProcessVariablesByType(UUID projectId, String type) {

		//initialize Database request
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<ProcessVariable> query = builder.createQuery(ProcessVariable.class);
		Root<ProcessVariable> root = query.from(ProcessVariable.class);
		query.select(root);
		Path<ProjectDependentKey> key = root.get(ProcessVariable.EntityAttributes.ID);

		//searchQuery
		query.where(builder.and(
				builder.equal(key.get(ProjectDependentKey.EntityAttributes.PROJECT_ID),projectId),
				builder.equal(root.get(ProcessVariable.EntityAttributes.VARIABLE_TYPE),type)
		));

		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}
}
