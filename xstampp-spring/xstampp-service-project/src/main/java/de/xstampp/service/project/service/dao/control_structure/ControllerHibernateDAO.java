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
import de.xstampp.service.project.data.entity.control_structure.Controller;
import de.xstampp.service.project.service.dao.control_structure.iface.IControllerDAO;
import de.xstampp.service.project.service.dao.generic.pagination.AbstractProjectDependentHibernateDAO;

@Repository
public class ControllerHibernateDAO extends AbstractProjectDependentHibernateDAO<Controller, ProjectDependentKey>
		implements IControllerDAO {
	
	@Autowired
	SessionFactory sessionFactory;

	@Override
	protected String getIdAttributeName() {
		return Controller.EntityAttributes.ID;
	}

	@Override
	public Controller getByBoxId(UUID projectId, String boxId) {
		CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<Controller> query = builder.createQuery(Controller.class);
		Root<Controller> rootController = query.from(Controller.class);
		query.select(rootController);

		query.where(builder.and(
				builder.equal(rootController.get(Controller.EntityAttributes.ID)
						.get(ProjectDependentKey.EntityAttributes.PROJECT_ID), projectId),
				builder.equal(rootController.get(Controller.EntityAttributes.BOXID), boxId)));
		return sessionFactory.getCurrentSession().createQuery(query).getSingleResult();
	}

	@Override
	public List<Controller> getAllUnlinkedControllers(UUID projectId) {
		CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<Controller> query = builder.createQuery(Controller.class);
		Root<Controller> rootController = query.from(Controller.class);
		query.select(rootController);
		query.where(builder.and(
				builder.equal(rootController.get(Controller.EntityAttributes.ID)
						.get(ProjectDependentKey.EntityAttributes.PROJECT_ID), projectId),
				builder.isNull(rootController.get(Controller.EntityAttributes.BOXID))));
		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}

	@Override
	public Controller setBoxId(UUID projectID, int controllerId, String boxId) {
		ProjectDependentKey key = new ProjectDependentKey(projectID, controllerId);
		Controller controller  = this.findById(key, false);
		if (controller != null) {			
			controller.setBoxId(boxId);
			this.makePersistent(controller);
		} else {
			throw ErrorsProj.DOES_NOT_EXIST.exc();
		}
		
		return controller;
	}



}
