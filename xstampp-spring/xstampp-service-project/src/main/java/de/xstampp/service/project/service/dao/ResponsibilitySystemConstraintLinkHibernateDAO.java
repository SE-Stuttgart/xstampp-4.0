package de.xstampp.service.project.service.dao;

import de.xstampp.service.project.data.entity.ResponsibilitySystemConstraintLink;
import de.xstampp.service.project.service.dao.iface.IResponsibilitySystemConstraintLinkDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
@Repository
public class ResponsibilitySystemConstraintLinkHibernateDAO implements IResponsibilitySystemConstraintLinkDAO {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession(){ return sessionFactory.getCurrentSession(); }

    @Override
    public boolean addLink(UUID projectId, int responsibilityId, int systemConstraintId) {

        ResponsibilitySystemConstraintLink link = new ResponsibilitySystemConstraintLink(projectId, systemConstraintId, responsibilityId);
        getSession().persist(link);

        return true;
    }

    @Override
    public boolean addLink(ResponsibilitySystemConstraintLink responsibilitySystemConstraintLink) {
        return addLink(responsibilitySystemConstraintLink.getProjectId(),
                responsibilitySystemConstraintLink.getResponsibilityId(),
                responsibilitySystemConstraintLink.getSystemConstraintId());
    }

    @Override
    public boolean deleteLink(UUID projectId, int responsibilityId, int systemConstraintId) {

        //Initialize Database request.
        CriteriaBuilder builder = getSession().getCriteriaBuilder();
        CriteriaQuery<ResponsibilitySystemConstraintLink> query = builder.createQuery(ResponsibilitySystemConstraintLink.class);
        Root<ResponsibilitySystemConstraintLink> rootLink = query.from(ResponsibilitySystemConstraintLink.class);
        query.select(rootLink);

        //searchQuery
        query.where(builder.and(
                builder.equal(rootLink.get(ResponsibilitySystemConstraintLink.EntityAttributes.RESPONSIBILITY_ID), responsibilityId),
                builder.equal(rootLink.get(ResponsibilitySystemConstraintLink.EntityAttributes.PROJECT_ID),projectId),
                builder.equal(rootLink.get(ResponsibilitySystemConstraintLink.EntityAttributes.SYSTEM_CONSTRAINT_ID), systemConstraintId)
        ));

        //delete Link form Table
        ResponsibilitySystemConstraintLink link = getSession().createQuery(query).getSingleResult();
        getSession().delete(link);

        return true;
    }

    @Override
    public List<ResponsibilitySystemConstraintLink> saveAll(List<ResponsibilitySystemConstraintLink> links) {
        List<ResponsibilitySystemConstraintLink> savedLinks = new LinkedList<>();
        Session session = sessionFactory.getCurrentSession();

        for (ResponsibilitySystemConstraintLink link: links) {
            ResponsibilitySystemConstraintLink savedLink = ResponsibilitySystemConstraintLink.class
                    .cast(session.save(link));
            savedLinks.add(savedLink);
        }

        session.flush();

        return savedLinks;
    }

    @Override
    public List<ResponsibilitySystemConstraintLink> getAllLinks(UUID projectId, boolean sort) {

        // Initialize Database request.
        CriteriaBuilder builder = getSession().getCriteriaBuilder();
        CriteriaQuery<ResponsibilitySystemConstraintLink> query = builder.createQuery(ResponsibilitySystemConstraintLink.class);
        Root<ResponsibilitySystemConstraintLink> rootLink = query.from(ResponsibilitySystemConstraintLink.class);
        query.select(rootLink);

        // Search Query
        query.where(builder.equal(rootLink.get(ResponsibilitySystemConstraintLink.EntityAttributes.PROJECT_ID), projectId));

        if (sort) {
            // Sorts Results after Responsibilities and then after SystemConstraints in ascending order (by id)
            query.orderBy(builder.asc(rootLink.get(ResponsibilitySystemConstraintLink.EntityAttributes.RESPONSIBILITY_ID)),
                    builder.asc(rootLink.get(ResponsibilitySystemConstraintLink.EntityAttributes.SYSTEM_CONSTRAINT_ID)));
        }

        // Execute Query and return results
        Query<ResponsibilitySystemConstraintLink> executedQuery = getSession().createQuery(query);

        return executedQuery.getResultList();
    }

    @Override
    public List<ResponsibilitySystemConstraintLink> getLinksByResponsibilityId(UUID projectId, int responsibilityId, Integer amount, Integer from) {

        //Initialize Database request.
        CriteriaBuilder builder = getSession().getCriteriaBuilder();
        CriteriaQuery<ResponsibilitySystemConstraintLink> query = builder.createQuery(ResponsibilitySystemConstraintLink.class);
        Root<ResponsibilitySystemConstraintLink> rootLink = query.from(ResponsibilitySystemConstraintLink.class);
        query.select(rootLink);

        //searchQuery
        query.where(builder.and(
                builder.equal(rootLink.get(ResponsibilitySystemConstraintLink.EntityAttributes.RESPONSIBILITY_ID), responsibilityId),
                builder.equal(rootLink.get(ResponsibilitySystemConstraintLink.EntityAttributes.PROJECT_ID), projectId)
        ));

        //Sorts Results after SystemConstraints in ascending order (by id)
        query.orderBy(builder.asc(rootLink.get(ResponsibilitySystemConstraintLink.EntityAttributes.SYSTEM_CONSTRAINT_ID)));

        Query<ResponsibilitySystemConstraintLink> executedQuery = getSession().createQuery(query);

        //apply restrictions to amount of entries in ResultList and starting point of the search query
        if (amount != null && from != null) {
            executedQuery.setMaxResults(amount);
            executedQuery.setFirstResult(from);
        }
        return executedQuery.getResultList();
    }

    @Override
    public List<ResponsibilitySystemConstraintLink> getLinksBySystemConstraintId(UUID projectId, int systemConstraintId) {

        //Initialize Database request.
        CriteriaBuilder builder = getSession().getCriteriaBuilder();
        CriteriaQuery<ResponsibilitySystemConstraintLink> query = builder.createQuery(ResponsibilitySystemConstraintLink.class);
        Root<ResponsibilitySystemConstraintLink> rootLink = query.from(ResponsibilitySystemConstraintLink.class);
        query.select(rootLink);

        //searchQuery
        query.where(builder.and(
                builder.equal(rootLink.get(ResponsibilitySystemConstraintLink.EntityAttributes.SYSTEM_CONSTRAINT_ID), systemConstraintId),
                builder.equal(rootLink.get(ResponsibilitySystemConstraintLink.EntityAttributes.PROJECT_ID), projectId)
        ));

        //Sorts Results after Responsibilities and then after SystemConstraints in ascending order (by id)
        query.orderBy(builder.asc(rootLink.get(ResponsibilitySystemConstraintLink.EntityAttributes.RESPONSIBILITY_ID)),
                builder.asc(rootLink.get(ResponsibilitySystemConstraintLink.EntityAttributes.SYSTEM_CONSTRAINT_ID)));

        //Execute Query and return results
        Query<ResponsibilitySystemConstraintLink> executedQuery = getSession().createQuery(query);

        return executedQuery.getResultList();
    }

    @Override
    public ResponsibilitySystemConstraintLink getLink(UUID projectId, int responsibilityId, int systemConstraintId) {

        //Initialize Database request.
        CriteriaBuilder builder = getSession().getCriteriaBuilder();
        CriteriaQuery<ResponsibilitySystemConstraintLink> query = builder.createQuery(ResponsibilitySystemConstraintLink.class);
        Root<ResponsibilitySystemConstraintLink> rootLink = query.from(ResponsibilitySystemConstraintLink.class);
        query.select(rootLink);

        //searchQuery
        query.where(builder.and(
                builder.equal(rootLink.get(ResponsibilitySystemConstraintLink.EntityAttributes.RESPONSIBILITY_ID), responsibilityId),
                builder.equal(rootLink.get(ResponsibilitySystemConstraintLink.EntityAttributes.PROJECT_ID),projectId),
                builder.equal(rootLink.get(ResponsibilitySystemConstraintLink.EntityAttributes.SYSTEM_CONSTRAINT_ID), systemConstraintId)
        ));

        //checks if ResultSet is empty
        Query<ResponsibilitySystemConstraintLink> execQuery = getSession().createQuery(query);
        if(execQuery.getResultList().isEmpty()) {
            return null;
        } else {
            return getSession().createQuery(query).getSingleResult();
        }
    }
}
