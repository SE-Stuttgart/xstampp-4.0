package de.xstampp.service.project.service.dao.last_id;

import de.xstampp.service.project.data.entity.lastId.SubSystemConstraintLastId;
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
class SubSystemConstraintLastIdDAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public SubSystemConstraintLastIdDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<SubSystemConstraintLastId> findAll(UUID projectId) {
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<SubSystemConstraintLastId> query = builder.createQuery(SubSystemConstraintLastId.class);
        Root<SubSystemConstraintLastId> subSystemConstraintLastIdRoot = query.from(SubSystemConstraintLastId.class);

        query.select(subSystemConstraintLastIdRoot)
                .where(builder.equal(subSystemConstraintLastIdRoot.get(SubSystemConstraintLastId.EntityAttributes.PROJECT_ID), projectId));

        return sessionFactory.getCurrentSession().createQuery(query).getResultList();
    }

    public List<SubSystemConstraintLastId> saveAll(List<SubSystemConstraintLastId> subSystemConstraintLastIds) {
        List<SubSystemConstraintLastId> savedSubSystemConstraintLastId = new LinkedList<>();
        Session session = sessionFactory.getCurrentSession();

        for (SubSystemConstraintLastId subSystemConstraintLastId : subSystemConstraintLastIds) {
            SubSystemConstraintLastId savedSubSystemConstraintLastIds = SubSystemConstraintLastId.class
                    .cast(session.save(subSystemConstraintLastId));
            savedSubSystemConstraintLastId.add(savedSubSystemConstraintLastIds);
        }

        session.flush();

        return savedSubSystemConstraintLastId;
    }
}
