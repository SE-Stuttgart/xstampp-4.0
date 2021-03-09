package de.xstampp.service.project.service.dao;

import de.xstampp.service.project.data.entity.ProcessModelProcessVariableLink;
import de.xstampp.service.project.service.dao.iface.IProcessModelProcessVariableLinkDAO;
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
public class ProcessModelProcessVariableLinkHibernateDAO implements IProcessModelProcessVariableLinkDAO {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public ProcessModelProcessVariableLink createLink(UUID projectId, int processModelId, int processVariableId, String processVariableValue) {

        ProcessModelProcessVariableLink link = new ProcessModelProcessVariableLink(projectId, processModelId, processVariableId, processVariableValue);
        getSession().persist(link);

        return link;
    }

    @Override
    public boolean deleteLink(UUID projectId, int processModelId, int processVariableId) {

        // Initialize Database request.
        CriteriaBuilder builder = getSession().getCriteriaBuilder();
        CriteriaQuery<ProcessModelProcessVariableLink> query = builder.createQuery(ProcessModelProcessVariableLink.class);
        Root<ProcessModelProcessVariableLink> rootLink = query.from(ProcessModelProcessVariableLink.class);
        query.select(rootLink);

        // searchQuery
        query.where(builder.and(
                builder.equal(rootLink.get(ProcessModelProcessVariableLink.EntityAttributes.PROCESS_MODEL_ID),
                        processModelId),
                builder.equal(rootLink.get(ProcessModelProcessVariableLink.EntityAttributes.PROJECT_ID), projectId),
                builder.equal(rootLink.get(ProcessModelProcessVariableLink.EntityAttributes.PROCESS_VARIABLE_ID),
                        processVariableId)));

        // delete Link form Table
        ProcessModelProcessVariableLink link = getSession().createQuery(query).getSingleResult();
        getSession().delete(link);

        return true;
    }

    @Override
    public List<ProcessModelProcessVariableLink> saveAll(List<ProcessModelProcessVariableLink> links) {
        List<ProcessModelProcessVariableLink> savedLinks = new LinkedList<>();
        Session session = sessionFactory.getCurrentSession();

        for (ProcessModelProcessVariableLink link: links) {
            ProcessModelProcessVariableLink savedLink = ProcessModelProcessVariableLink.class
                    .cast(session.save(link));
            savedLinks.add(savedLink);
        }

        session.flush();

        return savedLinks;
    }

    @Override
    public List<ProcessModelProcessVariableLink> getLinksByProcessModelId(UUID projectId, int processModelId,
            Integer amount, Integer from) {

        // Initialize Database request.
        CriteriaBuilder builder = getSession().getCriteriaBuilder();
        CriteriaQuery<ProcessModelProcessVariableLink> query = builder
                .createQuery(ProcessModelProcessVariableLink.class);
        Root<ProcessModelProcessVariableLink> rootLink = query.from(ProcessModelProcessVariableLink.class);
        query.select(rootLink);

        // searchQuery
        query.where(builder.and(
                builder.equal(rootLink.get(ProcessModelProcessVariableLink.EntityAttributes.PROCESS_MODEL_ID),
                        processModelId),
                builder.equal(rootLink.get(ProcessModelProcessVariableLink.EntityAttributes.PROJECT_ID), projectId)));

        // Sorts Results after Process Variable in ascending order (A-Z)
        query.orderBy(builder.asc(rootLink.get(ProcessModelProcessVariableLink.EntityAttributes.PROCESS_VARIABLE_ID)));

        Query<ProcessModelProcessVariableLink> executedQuery = getSession().createQuery(query);

        // apply restrictions to amount of entries in ResultList and starting point of
        // the search query
        if (amount != null && from != null) {
            executedQuery.setMaxResults(amount);
            executedQuery.setFirstResult(from);
        }
        return executedQuery.getResultList();
    }

    @Override
    public List<ProcessModelProcessVariableLink> getLinksByProcessVariableId(UUID projectId, int processVariableId) {

        // Initialize Database request.
        CriteriaBuilder builder = getSession().getCriteriaBuilder();
        CriteriaQuery<ProcessModelProcessVariableLink> query = builder
                .createQuery(ProcessModelProcessVariableLink.class);
        Root<ProcessModelProcessVariableLink> rootLink = query.from(ProcessModelProcessVariableLink.class);
        query.select(rootLink);

        // searchQuery
        query.where(builder.and(
                builder.equal(rootLink.get(ProcessModelProcessVariableLink.EntityAttributes.PROCESS_VARIABLE_ID),
                        processVariableId),
                builder.equal(rootLink.get(ProcessModelProcessVariableLink.EntityAttributes.PROJECT_ID), projectId)));

        // Sorts Results after Process Model and then after Process Variable in
        query.orderBy(builder.asc(rootLink.get(ProcessModelProcessVariableLink.EntityAttributes.PROCESS_MODEL_ID)),
                builder.asc(rootLink.get(ProcessModelProcessVariableLink.EntityAttributes.PROCESS_VARIABLE_ID)));

        // Execute Query and return results
        Query<ProcessModelProcessVariableLink> executedQuery = getSession().createQuery(query);

        return executedQuery.getResultList();
    }

    @Override
    public ProcessModelProcessVariableLink getLink(UUID projectId, int processModelId, int processVariableId) {

        // Initialize Database request.
        CriteriaBuilder builder = getSession().getCriteriaBuilder();
        CriteriaQuery<ProcessModelProcessVariableLink> query = builder
                .createQuery(ProcessModelProcessVariableLink.class);
        Root<ProcessModelProcessVariableLink> rootLink = query.from(ProcessModelProcessVariableLink.class);
        query.select(rootLink);

        // searchQuery
        query.where(builder.and(
                builder.equal(rootLink.get(ProcessModelProcessVariableLink.EntityAttributes.PROCESS_MODEL_ID),
                        processModelId),
                builder.equal(rootLink.get(ProcessModelProcessVariableLink.EntityAttributes.PROJECT_ID), projectId),
                builder.equal(rootLink.get(ProcessModelProcessVariableLink.EntityAttributes.PROCESS_VARIABLE_ID),
                        processVariableId)));

        // checks if ResultSet is empty
        Query<ProcessModelProcessVariableLink> execQuery = getSession().createQuery(query);
        if (execQuery.getResultList().isEmpty()) {
            return null;
        } else {
            return getSession().createQuery(query).getSingleResult();
        }
    }

    @Override
    public ProcessModelProcessVariableLink updateLink(UUID projectId, int processModelId, int processVariableId, String newValue) {

        ProcessModelProcessVariableLink link = getLink(projectId, processModelId, processVariableId);
        if (link != null){
            link.setProcessVariableValue(newValue);
            getSession().saveOrUpdate(link);
            return link;
        }
        return createLink(projectId, processModelId, processVariableId, newValue);
    }

    @Override
    public List<ProcessModelProcessVariableLink> getAllLinksByProcessModel(UUID projectId, int processModelId) {

        // Initialize Database request.
        CriteriaBuilder builder = getSession().getCriteriaBuilder();
        CriteriaQuery<ProcessModelProcessVariableLink> query = builder
                .createQuery(ProcessModelProcessVariableLink.class);
        Root<ProcessModelProcessVariableLink> rootLink = query.from(ProcessModelProcessVariableLink.class);
        query.select(rootLink);

        // searchQuery
        query.where(builder.and(
                builder.equal(rootLink.get(ProcessModelProcessVariableLink.EntityAttributes.PROCESS_MODEL_ID),
                        processModelId),
                builder.equal(rootLink.get(ProcessModelProcessVariableLink.EntityAttributes.PROJECT_ID), projectId)));


        Query<ProcessModelProcessVariableLink> executedQuery = getSession().createQuery(query);

        return executedQuery.getResultList();
    }

    @Override
    public List<ProcessModelProcessVariableLink> getAllLinks(UUID projectId) {

        // Initialize Database request.
        CriteriaBuilder builder = getSession().getCriteriaBuilder();
        CriteriaQuery<ProcessModelProcessVariableLink> query = builder
                .createQuery(ProcessModelProcessVariableLink.class);
        Root<ProcessModelProcessVariableLink> rootLink = query.from(ProcessModelProcessVariableLink.class);
        query.select(rootLink);

        // searchQuery
        query.where(builder.and(
                builder.equal(rootLink.get(ProcessModelProcessVariableLink.EntityAttributes.PROJECT_ID), projectId)));

        Query<ProcessModelProcessVariableLink> executedQuery = getSession().createQuery(query);

        return executedQuery.getResultList();
    }
}
