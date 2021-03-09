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
import de.xstampp.service.project.data.entity.control_structure.Actuator;
import de.xstampp.service.project.service.dao.control_structure.iface.IActuatorDAO;
import de.xstampp.service.project.service.dao.generic.pagination.AbstractProjectDependentHibernateDAO;

@Repository
public class ActuatorHibernateDAO extends AbstractProjectDependentHibernateDAO<Actuator, ProjectDependentKey>
		implements IActuatorDAO {

	@Autowired
	SessionFactory sessionFactory;

	@Override
	protected String getIdAttributeName() {
		return Actuator.EntityAttributes.ID;
	}

	@Override
	public Actuator getByBoxId(UUID projectId, String boxId) {
		CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<Actuator> query = builder.createQuery(Actuator.class);
		Root<Actuator> rootActuator = query.from(Actuator.class);
		query.select(rootActuator);
		query.where(builder.and(
				builder.equal(rootActuator.get(Actuator.EntityAttributes.ID)
						.get(ProjectDependentKey.EntityAttributes.PROJECT_ID), projectId),
				builder.equal(rootActuator.get(Actuator.EntityAttributes.BOXID), boxId)));
		return sessionFactory.getCurrentSession().createQuery(query).getSingleResult();
	}

	@Override
	public List<Actuator> getAllUnlinkedActuators(UUID projectId) {
		CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<Actuator> query = builder.createQuery(Actuator.class);
		Root<Actuator> rootActuator = query.from(Actuator.class);
		query.select(rootActuator);
		query.where(builder.and(
				builder.equal(rootActuator.get(Actuator.EntityAttributes.ID)
						.get(ProjectDependentKey.EntityAttributes.PROJECT_ID), projectId),
				builder.isNull(rootActuator.get(Actuator.EntityAttributes.BOXID))));
		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}

	@Override
	public Actuator setBoxId(UUID projectID, int actuatorId, String boxId) {
		ProjectDependentKey key = new ProjectDependentKey(projectID, actuatorId);
		Actuator actuator = this.findById(key, false);
		if (actuator != null) {			
			actuator.setBoxId(boxId);
			this.makePersistent(actuator);
		} else {
			throw ErrorsProj.DOES_NOT_EXIST.exc();
		}
		
		return actuator;
	}

}
