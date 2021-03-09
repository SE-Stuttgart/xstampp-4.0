package de.xstampp.service.project.service.dao.last_id;

import de.xstampp.service.project.data.entity.lastId.ConversionLastId;
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
public class ConversionLastIdDAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public ConversionLastIdDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<ConversionLastId> findAll(UUID projectId) {
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<ConversionLastId> query = builder.createQuery(ConversionLastId.class);
        Root<ConversionLastId> conversionLastIdRoot = query.from(ConversionLastId.class);

        query.select(conversionLastIdRoot)
                .where(builder.equal(conversionLastIdRoot.get(ConversionLastId.EntityAttributes.PROJECT_ID), projectId));

        return sessionFactory.getCurrentSession().createQuery(query).getResultList();
    }

    public List<ConversionLastId> saveAll(List<ConversionLastId> conversionLastIds) {
        List<ConversionLastId> savedConversionLastId = new LinkedList<>();
        Session session = sessionFactory.getCurrentSession();

        for (ConversionLastId conversionLastId : conversionLastIds) {
            ConversionLastId savedConversionLastIds = ConversionLastId.class
                    .cast(session.save(conversionLastId));
            savedConversionLastId.add(savedConversionLastIds);
        }

        session.flush();

        return savedConversionLastId;
    }
}
