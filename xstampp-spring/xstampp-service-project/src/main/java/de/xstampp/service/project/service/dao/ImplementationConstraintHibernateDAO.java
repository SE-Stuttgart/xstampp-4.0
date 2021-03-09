package de.xstampp.service.project.service.dao;

import de.xstampp.service.project.data.entity.ImplementationConstraint;
import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.service.dao.generic.pagination.AbstractProjectDependentHibernateDAO;
import de.xstampp.service.project.service.dao.iface.IImplementationConstraintDAO;
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
public class ImplementationConstraintHibernateDAO extends
        AbstractProjectDependentHibernateDAO<ImplementationConstraint, ProjectDependentKey> implements IImplementationConstraintDAO {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public List<ImplementationConstraint> findImplementationConstraintsByIds(UUID projectId, List<Integer> implementationConstraintIds) {
        if (implementationConstraintIds.isEmpty()) {
            return Collections.emptyList();
        }
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<ImplementationConstraint> query = builder.createQuery(ImplementationConstraint.class);
        Root<ImplementationConstraint> rootImplementationConstraint = query.from(ImplementationConstraint.class);
        Path<ProjectDependentKey> pathImplementationConstraintKey = rootImplementationConstraint.get(ImplementationConstraint.EntityAttributes.ID);
        query.select(rootImplementationConstraint);
        query.where(builder.and(pathImplementationConstraintKey.get(ProjectDependentKey.EntityAttributes.ID).in(implementationConstraintIds),
                builder.equal(pathImplementationConstraintKey.get(ProjectDependentKey.EntityAttributes.PROJECT_ID), projectId)));

        query.orderBy(builder.asc(rootImplementationConstraint.get(ImplementationConstraint.EntityAttributes.ID)));
        return sessionFactory.getCurrentSession().createQuery(query).getResultList();
    }

    @Override
    protected String getIdAttributeName() {
        return ImplementationConstraint.EntityAttributes.ID;
    }

    @Override
    public List<ImplementationConstraint> getImplementationConstraintsByLossScenarioId(UUID projectId, Integer lossScenarioId) {
        if (lossScenarioId == null) {
            return Collections.emptyList();
        }
        CriteriaBuilder builder = this.sessionFactory.getCriteriaBuilder();
        CriteriaQuery<ImplementationConstraint> query = builder.createQuery(ImplementationConstraint.class);
        Root<ImplementationConstraint> rootImplementationConstraint = query.from(ImplementationConstraint.class);
        query.select(rootImplementationConstraint);
        query.where(builder.and(builder.equal(rootImplementationConstraint.get(ImplementationConstraint.EntityAttributes.ID)
                .get(ProjectDependentKey.EntityAttributes.PROJECT_ID), projectId),
                builder.equal(rootImplementationConstraint.get(ImplementationConstraint.EntityAttributes.LOSS_SCENARIO_ID), lossScenarioId)));
        query.orderBy(builder.asc(rootImplementationConstraint.get(ImplementationConstraint.EntityAttributes.ID)));
        return sessionFactory.getCurrentSession().createQuery(query).getResultList();
    }
}
