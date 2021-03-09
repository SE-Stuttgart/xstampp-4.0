package de.xstampp.service.project.service.dao.last_id;

import de.xstampp.service.project.data.entity.lastId.UnsafeControlActionLastId;
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
public class UnsafeControlActionLastIdDAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public UnsafeControlActionLastIdDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<UnsafeControlActionLastId> findAll(UUID projectId) {
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<UnsafeControlActionLastId> query = builder.createQuery(UnsafeControlActionLastId.class);
        Root<UnsafeControlActionLastId> unsafeControlActionLastIdRoot = query.from(UnsafeControlActionLastId.class);

        query.select(unsafeControlActionLastIdRoot)
                .where(builder.equal(unsafeControlActionLastIdRoot.get(UnsafeControlActionLastId.EntityAttributes.PROJECT_ID), projectId));

        return sessionFactory.getCurrentSession().createQuery(query).getResultList();
    }

    public List<UnsafeControlActionLastId> saveAll(List<UnsafeControlActionLastId> unsafeControlActionLastIds) {
        List<UnsafeControlActionLastId> savedUnsafeControlActionLastId = new LinkedList<>();
        Session session = sessionFactory.getCurrentSession();

        for (UnsafeControlActionLastId unsafeControlActionLastId : unsafeControlActionLastIds) {
            UnsafeControlActionLastId savedUnsafeControlActionLastIds = UnsafeControlActionLastId.class
                    .cast(session.save(unsafeControlActionLastId));
            savedUnsafeControlActionLastId.add(savedUnsafeControlActionLastIds);
        }

        session.flush();

        return savedUnsafeControlActionLastId;
    }
}
