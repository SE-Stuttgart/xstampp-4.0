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
import de.xstampp.service.project.data.entity.control_structure.ControlledProcess;
import de.xstampp.service.project.service.dao.control_structure.iface.IControlledProcessDAO;
import de.xstampp.service.project.service.dao.generic.pagination.AbstractProjectDependentHibernateDAO;

@Repository
public class ControlledProcessHibernateDAO extends
		AbstractProjectDependentHibernateDAO<ControlledProcess, ProjectDependentKey> implements IControlledProcessDAO {

	@Autowired
	SessionFactory sessionFactory;
	
	@Override
	protected String getIdAttributeName() {
		return ControlledProcess.EntityAttributes.ID;
	}

	@Override
	public ControlledProcess getByBoxId(UUID projectId, String boxId) {
		CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<ControlledProcess> query = builder.createQuery(ControlledProcess.class);
		Root<ControlledProcess> rootControlledProcess = query.from(ControlledProcess.class);
		query.select(rootControlledProcess);
		query.where(builder.and(
				builder.equal(rootControlledProcess.get(ControlledProcess.EntityAttributes.ID)
						.get(ProjectDependentKey.EntityAttributes.PROJECT_ID), projectId),
				builder.equal(rootControlledProcess.get(ControlledProcess.EntityAttributes.BOXID), boxId)));
		return sessionFactory.getCurrentSession().createQuery(query).getSingleResult();
	}

	@Override
	public List<ControlledProcess> getAllUnlinkedControlledProcesses(UUID projectId) {
		CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<ControlledProcess> query = builder.createQuery(ControlledProcess.class);
		Root<ControlledProcess> rootControlledProcess = query.from(ControlledProcess.class);
		query.select(rootControlledProcess);
		query.where(builder.and(
				builder.equal(rootControlledProcess.get(ControlledProcess.EntityAttributes.ID)
						.get(ProjectDependentKey.EntityAttributes.PROJECT_ID), projectId),
				builder.isNull(rootControlledProcess.get(ControlledProcess.EntityAttributes.BOXID))));
		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}

	@Override
	public ControlledProcess setBoxId(UUID projectID, int controlledProcessId, String boxId) {
		ProjectDependentKey key = new ProjectDependentKey(projectID, controlledProcessId);
		ControlledProcess controlledProcess = this.findById(key, false);
		if (controlledProcess != null) {			
			controlledProcess.setBoxId(boxId);
			this.makePersistent(controlledProcess);
		} else {
			throw ErrorsProj.DOES_NOT_EXIST.exc();
		}
		
		return controlledProcess;
	}

}
