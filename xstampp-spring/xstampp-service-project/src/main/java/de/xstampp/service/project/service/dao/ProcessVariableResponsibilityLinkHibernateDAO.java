package de.xstampp.service.project.service.dao;

import de.xstampp.service.project.data.entity.ProcessVariableResponsibilityLink;
import de.xstampp.service.project.service.dao.iface.IProcessVariableResponsibilityLinkDAO;
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
public class ProcessVariableResponsibilityLinkHibernateDAO implements IProcessVariableResponsibilityLinkDAO {
    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public ProcessVariableResponsibilityLink createLink(UUID projectId, int processVariableId, int responsibilityId) {

        ProcessVariableResponsibilityLink link = new ProcessVariableResponsibilityLink(projectId, processVariableId, responsibilityId);
        getSession().persist(link);

        return link;

    }

    @Override
    public boolean deleteLink(UUID projectId, int processVariableId, int responsibilityId) {

        // Initialize Database request.
        CriteriaBuilder builder = getSession().getCriteriaBuilder();
        CriteriaQuery<ProcessVariableResponsibilityLink> query = builder
                .createQuery(ProcessVariableResponsibilityLink.class);
        Root<ProcessVariableResponsibilityLink> rootLink = query.from(ProcessVariableResponsibilityLink.class);
        query.select(rootLink);

        // searchQuery
        query.where(builder.and(
                builder.equal(rootLink.get(ProcessVariableResponsibilityLink.EntityAttributes.PROCESS_VARIABLE_ID),
                        processVariableId),
                builder.equal(rootLink.get(ProcessVariableResponsibilityLink.EntityAttributes.PROJECT_ID), projectId),
                builder.equal(rootLink.get(ProcessVariableResponsibilityLink.EntityAttributes.RESPONSIBILITY_ID),
                        responsibilityId)));

        // delete Link form Table
        ProcessVariableResponsibilityLink link = getSession().createQuery(query).getSingleResult();
        getSession().delete(link);

        return true;
    }

    @Override
    public List<ProcessVariableResponsibilityLink> saveAll(List<ProcessVariableResponsibilityLink> links) {
        List<ProcessVariableResponsibilityLink> savedLinks = new LinkedList<>();
        Session session = sessionFactory.getCurrentSession();

        for (ProcessVariableResponsibilityLink link: links) {
            ProcessVariableResponsibilityLink savedLink = ProcessVariableResponsibilityLink.class
                    .cast(session.save(link));
            savedLinks.add(savedLink);
        }

        session.flush();

        return savedLinks;
    }

    // updated
    @Override
    public List<ProcessVariableResponsibilityLink> getLinksByProcessVariableId(UUID projectId, int processVariableId) {

        //Initialize Database Access
        CriteriaBuilder builder = getSession().getCriteriaBuilder();
        CriteriaQuery<ProcessVariableResponsibilityLink> query = builder.createQuery(ProcessVariableResponsibilityLink.class);
        Root<ProcessVariableResponsibilityLink> root = query.from(ProcessVariableResponsibilityLink.class);
        query.select(root);


        //searchQuery
        query.where(builder.and(
                builder.equal(root.get(ProcessVariableResponsibilityLink.EntityAttributes.PROJECT_ID),projectId),
                builder.equal(root.get(ProcessVariableResponsibilityLink.EntityAttributes.PROCESS_VARIABLE_ID),processVariableId)
        ));



        return getSession().createQuery(query).getResultList();
    }

    @Override
    public List<ProcessVariableResponsibilityLink> getAllLinks(UUID projectId) {

        //Initialize Database Access
        CriteriaBuilder builder = getSession().getCriteriaBuilder();
        CriteriaQuery<ProcessVariableResponsibilityLink> query = builder
                .createQuery(ProcessVariableResponsibilityLink.class);
        Root<ProcessVariableResponsibilityLink> root = query.from(ProcessVariableResponsibilityLink.class);
        query.select(root);

        //searchQuery
        query.where(builder.and(
                builder.equal(root.get(ProcessVariableResponsibilityLink.EntityAttributes.PROJECT_ID),projectId)));

        return getSession().createQuery(query).getResultList();
    }

    @Override
    public ProcessVariableResponsibilityLink getLink(UUID projectId, int processVariableId, int responsibilityId) {

        // Initialize Database request.
        CriteriaBuilder builder = getSession().getCriteriaBuilder();
        CriteriaQuery<ProcessVariableResponsibilityLink> query = builder
                .createQuery(ProcessVariableResponsibilityLink.class);
        Root<ProcessVariableResponsibilityLink> rootLink = query.from(ProcessVariableResponsibilityLink.class);
        query.select(rootLink);

        // searchQuery
        query.where(builder.and(
                builder.equal(rootLink.get(ProcessVariableResponsibilityLink.EntityAttributes.PROCESS_VARIABLE_ID),
                        processVariableId),
                builder.equal(rootLink.get(ProcessVariableResponsibilityLink.EntityAttributes.PROJECT_ID), projectId),
                builder.equal(rootLink.get(ProcessVariableResponsibilityLink.EntityAttributes.RESPONSIBILITY_ID),
                        responsibilityId)));

        // checks if ResultSet is empty
        Query<ProcessVariableResponsibilityLink> execQuery = getSession().createQuery(query);
        if (execQuery.getResultList().isEmpty()) {
            return null;
        } else {
            return getSession().createQuery(query).getSingleResult();
        }
    }
    
    @Override
    public ProcessVariableResponsibilityLink updateLink(UUID projectId, int processVariableId, int oldResponsibilityId, int newResponsibilityId) {

        ProcessVariableResponsibilityLink link = getLink(projectId, processVariableId, oldResponsibilityId);
        if(link != null) {
            // change responsibilityId only if not identical with the current one
            if(oldResponsibilityId != link.getResponsibilityId()) {
                link.setResponsibilityId(newResponsibilityId);
                getSession().saveOrUpdate(link);
                return link;
            }
            return link; // else return the same link
        }
        return createLink(projectId, processVariableId, newResponsibilityId);
    }
}
