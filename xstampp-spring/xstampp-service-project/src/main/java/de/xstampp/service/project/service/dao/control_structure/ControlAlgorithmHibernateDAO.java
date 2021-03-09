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
import de.xstampp.service.project.data.entity.ControlAlgorithm;
import de.xstampp.service.project.service.dao.control_structure.iface.IControlAlgorithmDAO;
import de.xstampp.service.project.service.dao.generic.pagination.AbstractProjectDependentHibernateDAO;

@Repository
public class ControlAlgorithmHibernateDAO extends AbstractProjectDependentHibernateDAO<ControlAlgorithm, ProjectDependentKey> implements IControlAlgorithmDAO{

	@Autowired
	SessionFactory sessionFactory;
	
	@Override
	protected String getIdAttributeName() {
		return ControlAlgorithm.EntityAttributes.ID;
	}

	@Override
	public ControlAlgorithm getByBoxId(UUID projectId, String boxId) {
		CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<ControlAlgorithm> query = builder.createQuery(ControlAlgorithm.class);
		Root<ControlAlgorithm> rootControlAlgorithm = query.from(ControlAlgorithm.class);
		query.select(rootControlAlgorithm);
		query.where(builder.and(
				builder.equal(rootControlAlgorithm.get(ControlAlgorithm.EntityAttributes.ID)
						.get(ProjectDependentKey.EntityAttributes.PROJECT_ID), projectId),
				builder.equal(rootControlAlgorithm.get(ControlAlgorithm.EntityAttributes.BOXID), boxId)));
		return sessionFactory.getCurrentSession().createQuery(query).getSingleResult();
	}

	@Override
	public List<ControlAlgorithm> getAllUnlinkedControlAlgorithm(UUID projectId) {
		CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<ControlAlgorithm> query = builder.createQuery(ControlAlgorithm.class);
		Root<ControlAlgorithm> rootControlAlgorithm = query.from(ControlAlgorithm.class);
		query.select(rootControlAlgorithm);
		query.where(builder.and(
				builder.equal(rootControlAlgorithm.get(ControlAlgorithm.EntityAttributes.ID)
						.get(ProjectDependentKey.EntityAttributes.PROJECT_ID), projectId),
				builder.isNull(rootControlAlgorithm.get(ControlAlgorithm.EntityAttributes.BOXID))));
		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}

	@Override
	public ControlAlgorithm setBoxId(UUID projectID, int controlAlgorithmId, String boxId) {
		ProjectDependentKey key = new ProjectDependentKey(projectID, controlAlgorithmId);
		ControlAlgorithm controlAlgorithm = this.findById(key, false);
		if (controlAlgorithm != null) {			
			controlAlgorithm.setBoxId(boxId);
			this.makePersistent(controlAlgorithm);
		} else {
			throw ErrorsProj.DOES_NOT_EXIST.exc();
		}
		
		return controlAlgorithm;
	}

}
