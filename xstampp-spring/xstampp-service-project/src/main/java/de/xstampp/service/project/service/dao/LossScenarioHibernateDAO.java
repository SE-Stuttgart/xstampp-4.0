package de.xstampp.service.project.service.dao;

import de.xstampp.service.project.data.entity.*;
import de.xstampp.service.project.service.dao.generic.pagination.AbstractProjectDependentHibernateDAO;
import de.xstampp.service.project.service.dao.iface.ILossScenarioDAO;
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
public class LossScenarioHibernateDAO extends
        AbstractProjectDependentHibernateDAO<LossScenario, ProjectDependentKey> implements ILossScenarioDAO {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public List<LossScenario> findLossScenariosByIds(UUID projectId, List<Integer> lossScenarioIds) {
        if (lossScenarioIds.isEmpty()) {
            return Collections.emptyList();
        }

        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<LossScenario> query = builder.createQuery(LossScenario.class);
        Root<LossScenario> rootLossScenario = query.from(LossScenario.class);

        Path<ProjectDependentKey> pathLossScenarioKey = rootLossScenario.get(LossScenario.EntityAttributes.ID);
        query.select(rootLossScenario);
        query.where(builder.and(pathLossScenarioKey.get(ProjectDependentKey.EntityAttributes.ID).in(lossScenarioIds),
                builder.equal(pathLossScenarioKey.get(ProjectDependentKey.EntityAttributes.PROJECT_ID), projectId)));

        query.orderBy(builder.asc(rootLossScenario.get(LossScenario.EntityAttributes.ID)));
        return sessionFactory.getCurrentSession().createQuery(query).getResultList();
    }

    @Override
    protected String getIdAttributeName() {
        return LossScenario.EntityAttributes.ID;
    }

    @Override
    public List<LossScenario> getLossScenariosByUcaId(UUID projectId, Integer ucaId, Integer controlActionId) {
        if (ucaId == null) {
            return Collections.emptyList();
        }

        CriteriaBuilder builder = this.sessionFactory.getCriteriaBuilder();
        CriteriaQuery<LossScenario> query = builder.createQuery(LossScenario.class);
        Root<LossScenario> rootLossScenario = query.from(LossScenario.class);

        query.select(rootLossScenario);

        query.where(builder.and(builder.equal(rootLossScenario.get(LossScenario.EntityAttributes.ID)
                .get(ProjectDependentKey.EntityAttributes.PROJECT_ID), projectId),
                builder.equal(rootLossScenario.get(LossScenario.EntityAttributes.UCA_ID), ucaId),
                builder.equal(rootLossScenario.get(LossScenario.EntityAttributes.CONTROL_ACTION_ID), controlActionId)));

        query.orderBy(builder.asc(rootLossScenario.get(LossScenario.EntityAttributes.ID)));
        return sessionFactory.getCurrentSession().createQuery(query).getResultList();
    }
}
