package de.xstampp.service.project.service.dao.last_id;

import de.xstampp.service.project.data.entity.lastId.SubHazardLastId;
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
public class SubHazardLastIdDAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public SubHazardLastIdDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<SubHazardLastId> findAll(UUID projectId) {
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<SubHazardLastId> query = builder.createQuery(SubHazardLastId.class);
        Root<SubHazardLastId> subHazardLastIdRoot = query.from(SubHazardLastId.class);

        query.select(subHazardLastIdRoot)
                .where(builder.equal(subHazardLastIdRoot.get(SubHazardLastId.EntityAttributes.PROJECT_ID), projectId));

        return sessionFactory.getCurrentSession().createQuery(query).getResultList();
    }

    public List<SubHazardLastId> saveAll(List<SubHazardLastId> subHazardLastIds) {
        List<SubHazardLastId> savedSubHazardLastId = new LinkedList<>();
        Session session = sessionFactory.getCurrentSession();

        for (SubHazardLastId subHazardLastId : subHazardLastIds) {
            SubHazardLastId savedSubHazardLastIds = SubHazardLastId.class
                    .cast(session.save(subHazardLastId));
            savedSubHazardLastId.add(savedSubHazardLastIds);
        }

        session.flush();

        return savedSubHazardLastId;
    }
}
