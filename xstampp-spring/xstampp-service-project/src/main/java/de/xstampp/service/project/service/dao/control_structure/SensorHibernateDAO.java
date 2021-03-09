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
import de.xstampp.service.project.data.entity.control_structure.Sensor;
import de.xstampp.service.project.service.dao.control_structure.iface.ISensorDAO;
import de.xstampp.service.project.service.dao.generic.pagination.AbstractProjectDependentHibernateDAO;

@Repository
public class SensorHibernateDAO extends AbstractProjectDependentHibernateDAO<Sensor, ProjectDependentKey>
		implements ISensorDAO {

	@Autowired
	SessionFactory sessionFactory;

	@Override
	protected String getIdAttributeName() {
		return Sensor.EntityAttributes.ID;
	}

	@Override
	public Sensor getByBoxId(UUID projectId, String boxId) {
		CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<Sensor> query = builder.createQuery(Sensor.class);
		Root<Sensor> rootSensor = query.from(Sensor.class);
		query.select(rootSensor);
		query.where(
				builder.and(
						builder.equal(rootSensor.get(Sensor.EntityAttributes.ID)
								.get(ProjectDependentKey.EntityAttributes.PROJECT_ID), projectId),
						builder.equal(rootSensor.get(Sensor.EntityAttributes.BOXID), boxId)));
		return sessionFactory.getCurrentSession().createQuery(query).getSingleResult();
	}

	@Override
	public List<Sensor> getAllUnlinkedSensor(UUID projectId) {
		CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<Sensor> query = builder.createQuery(Sensor.class);
		Root<Sensor> rootSensor = query.from(Sensor.class);
		query.select(rootSensor);
		query.where(
				builder.and(
						builder.equal(rootSensor.get(Sensor.EntityAttributes.ID)
								.get(ProjectDependentKey.EntityAttributes.PROJECT_ID), projectId),
						builder.isNull(rootSensor.get(Sensor.EntityAttributes.BOXID))));
		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}

	@Override
	public Sensor setBoxId(UUID projectID, int sensorId, String boxId) {
		ProjectDependentKey key = new ProjectDependentKey(projectID, sensorId);
		Sensor sensor = this.findById(key, false);
		if (sensor != null) {
			sensor.setBoxId(boxId);
			this.makePersistent(sensor);
		} else {
			throw ErrorsProj.DOES_NOT_EXIST.exc();
		}

		return sensor;
	}
}
