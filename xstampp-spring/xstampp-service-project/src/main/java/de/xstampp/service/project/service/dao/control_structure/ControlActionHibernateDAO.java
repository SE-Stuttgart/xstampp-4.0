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
import de.xstampp.service.project.data.entity.control_structure.ControlAction;
import de.xstampp.service.project.service.dao.control_structure.iface.IControlActionDAO;
import de.xstampp.service.project.service.dao.generic.pagination.AbstractProjectDependentHibernateDAO;

@Repository
public class ControlActionHibernateDAO extends AbstractProjectDependentHibernateDAO<ControlAction, ProjectDependentKey> implements IControlActionDAO {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    protected String getIdAttributeName() {
        return ControlAction.EntityAttributes.ID;
    }

    @Override
    public List<ControlAction> getByArrowId(UUID projectId, String arrowId) {
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<ControlAction> query = builder.createQuery(ControlAction.class);
        Root<ControlAction> rootControlAction = query.from(ControlAction.class);
        query.select(rootControlAction);
        query.where(builder.and(
                builder.equal(rootControlAction.get(ControlAction.EntityAttributes.ID)
                        .get(ProjectDependentKey.EntityAttributes.PROJECT_ID), projectId),
                builder.equal(rootControlAction.get(ControlAction.EntityAttributes.ARROWID), arrowId)));
        return sessionFactory.getCurrentSession().createQuery(query).getResultList();
    }

    @Override
    public List<ControlAction> getAllUnlinkedControlActions(UUID projectId) {
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<ControlAction> query = builder.createQuery(ControlAction.class);
        Root<ControlAction> rootControlAction = query.from(ControlAction.class);
        query.select(rootControlAction);
        query.where(builder.and(
                builder.equal(rootControlAction.get(ControlAction.EntityAttributes.ID)
                        .get(ProjectDependentKey.EntityAttributes.PROJECT_ID), projectId),
                builder.isNull(rootControlAction.get(ControlAction.EntityAttributes.ARROWID))));
        return sessionFactory.getCurrentSession().createQuery(query).getResultList();
    }

    @Override
    public ControlAction setArrowId(UUID projectID, int controlActionId, String arrowId) {
        ProjectDependentKey key = new ProjectDependentKey(projectID, controlActionId);
        ControlAction controlAction = this.findById(key, false);
        if (controlAction != null) {
            controlAction.setArrowId(arrowId);
            this.makePersistent(controlAction);
        } else {
            throw ErrorsProj.DOES_NOT_EXIST.exc();
        }

        return controlAction;
    }

}
