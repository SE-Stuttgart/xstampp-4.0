package de.xstampp.service.project.service.dao;

import de.xstampp.service.project.data.XStamppDependentEntity;
import de.xstampp.service.project.data.entity.EntityDependentKey;
import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.service.dao.iface.IIncompleteEntitiesDAO;
import de.xstampp.service.project.util.StateControl;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import java.util.UUID;

@Repository
public class IncompleteEntitiesHibernateDAO implements IIncompleteEntitiesDAO {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public <T> T findEntityByClassAndId(Class<T> clazz, ProjectDependentKey id) {
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(clazz);
        Root<T> rootEntity = query.from(clazz);

        query.select(rootEntity);
        query.where(
                builder.and(builder.equal(rootEntity.get(XStamppDependentEntity.DEPENDENT_KEY_ATTRIBUTE)
                        .get(ProjectDependentKey.EntityAttributes.ID), id.getId()),

                builder.equal(rootEntity.get(XStamppDependentEntity.DEPENDENT_KEY_ATTRIBUTE)
                        .get(ProjectDependentKey.EntityAttributes.PROJECT_ID), id.getProjectId()))
        );

        return sessionFactory.getCurrentSession().createQuery(query).getResultList().get(0);
    }

    @Override
    public <T> T findEntityByClassAndId(Class<T> clazz, EntityDependentKey id) {
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(clazz);
        Root<T> rootEntity = query.from(clazz);

        query.select(rootEntity);
        query.where(
                builder.and(builder.equal(rootEntity.get(XStamppDependentEntity.DEPENDENT_KEY_ATTRIBUTE)
                                .get(EntityDependentKey.EntityAttributes.ID), id.getId()),

                        builder.equal(rootEntity.get(XStamppDependentEntity.DEPENDENT_KEY_ATTRIBUTE)
                                .get(EntityDependentKey.EntityAttributes.PROJECT_ID), id.getProjectId())
                )
        );

        return sessionFactory.getCurrentSession().createQuery(query).getResultList().get(0);
    }

    @Override
    public <T> T findEntityByClassAndId(Class<T> clazz, UUID projectId, String id) {
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(clazz);
        Root<T> rootEntity = query.from(clazz);

        query.select(rootEntity);
        query.where(
                builder.and(
                        builder.equal(rootEntity.get(XStamppDependentEntity.DEPENDENT_KEY_ATTRIBUTE), id),
                        builder.equal(rootEntity.get(XStamppDependentEntity.DEPENDENT_KEY_ATTRIBUTE), projectId)
                )
        );

        return sessionFactory.getCurrentSession().createQuery(query).getResultList().get(0);
    }

    @Override
    public <T> boolean updateStateForEntity(Class<T> clazz, ProjectDependentKey id, StateControl.STATE state) {
        // Create builder and update
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaUpdate<T> update = builder.createCriteriaUpdate(clazz);

        // Set the root class
        Root root = update.from(clazz);

        // Set update and where clause
        update.set("state", state.toString());
        update.where(builder.and(
                builder.equal(root.get(XStamppDependentEntity.DEPENDENT_KEY_ATTRIBUTE).get(ProjectDependentKey.EntityAttributes.ID), id.getId()),
                builder.equal(root.get(XStamppDependentEntity.DEPENDENT_KEY_ATTRIBUTE).get(ProjectDependentKey.EntityAttributes.PROJECT_ID), id.getProjectId()))
        );

        // perform update
        sessionFactory.getCurrentSession().createQuery(update).executeUpdate();
        return true;
    }

    @Override
    public <T> boolean updateStateForEntity(Class<T> clazz, EntityDependentKey id, StateControl.STATE state) {
        // Create builder and update
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaUpdate<T> update = builder.createCriteriaUpdate(clazz);

        // Set the root class
        Root root = update.from(clazz);

        // Set update and where clause
        update.set("state", state.toString());
        update.where(
                builder.and(builder.equal(root.get(XStamppDependentEntity.DEPENDENT_KEY_ATTRIBUTE)
                                .get(EntityDependentKey.EntityAttributes.ID), id.getId()),

                        builder.equal(root.get(XStamppDependentEntity.DEPENDENT_KEY_ATTRIBUTE)
                                .get(EntityDependentKey.EntityAttributes.PROJECT_ID), id.getProjectId()),

                        builder.equal(root.get(XStamppDependentEntity.DEPENDENT_KEY_ATTRIBUTE)
                                .get(EntityDependentKey.EntityAttributes.PARENT_ID), id.getParentId())
                )
        );

        // perform update
        sessionFactory.getCurrentSession().createQuery(update).executeUpdate();
        return true;
    }

    @Override
    public <T> boolean updateStateForEntity(Class<T> clazz, UUID projectId, String id, StateControl.STATE state) {
        // Create builder and update
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaUpdate<T> update = builder.createCriteriaUpdate(clazz);

        // Set the root class
        Root root = update.from(clazz);

        // Set update and where clause
        update.set("state", state.toString());
        update.where(
                builder.and(
                        builder.equal(root.get(XStamppDependentEntity.DEPENDENT_KEY_ATTRIBUTE), id),
                        builder.equal(root.get(XStamppDependentEntity.DEPENDENT_KEY_ATTRIBUTE), projectId)
                )
        );

        // perform update
        sessionFactory.getCurrentSession().createQuery(update).executeUpdate();
        return true;
    }
}
