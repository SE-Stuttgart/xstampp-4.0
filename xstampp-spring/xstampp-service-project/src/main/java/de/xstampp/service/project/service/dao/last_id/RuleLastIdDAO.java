package de.xstampp.service.project.service.dao.last_id;

import de.xstampp.service.project.data.entity.lastId.RuleLastId;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Repository
public class RuleLastIdDAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public RuleLastIdDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<RuleLastId> findAll(UUID projectId) {
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<RuleLastId> query = builder.createQuery(RuleLastId.class);
        Root<RuleLastId> ruleLastIdRoot = query.from(RuleLastId.class);

        query.select(ruleLastIdRoot)
                .where(builder.equal(ruleLastIdRoot.get(RuleLastId.EntityAttributes.PROJECT_ID), projectId));

        return sessionFactory.getCurrentSession().createQuery(query).getResultList();
    }

    public List<RuleLastId> saveAll(List<RuleLastId> ruleLastIds) {
        List<RuleLastId> savedRuleLastId = new LinkedList<>();
        Session session = sessionFactory.getCurrentSession();

        for (RuleLastId ruleLastId : ruleLastIds) {
            RuleLastId savedRuleLastIds = RuleLastId.class
                    .cast(session.save(ruleLastId));
            savedRuleLastId.add(savedRuleLastIds);
        }

        session.flush();

        return savedRuleLastId;
    }
}
