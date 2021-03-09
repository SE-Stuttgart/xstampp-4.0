package de.xstampp.service.project.service.dao.control_structure;

import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.control_structure.ProcessModel;
import de.xstampp.service.project.service.dao.control_structure.iface.IProcessModelDAO;
import de.xstampp.service.project.service.dao.generic.pagination.AbstractProjectDependentHibernateDAO;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.UUID;

@Repository
public class ProcessModelHibernateDAO extends AbstractProjectDependentHibernateDAO<ProcessModel, ProjectDependentKey>
        implements IProcessModelDAO {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public String getIdAttributeName() {
        return ProcessModel.EntityAttributes.ID;
    }


    @Override
    public ProcessModel getById(UUID projectID, int processModelId) {

        //Initialize Database Request
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<ProcessModel> query = builder.createQuery(ProcessModel.class);
        Root<ProcessModel> root = query.from(ProcessModel.class);
        query.select(root);
        Path<ProjectDependentKey> pathProcessModelKey = root.get(ProcessModel.EntityAttributes.ID);

        //searchQuery
        query.where(builder.and(
                builder.equal(root.get(ProcessModel.EntityAttributes.ID).get(ProjectDependentKey.EntityAttributes.PROJECT_ID), projectID),
                builder.equal(pathProcessModelKey.get(ProjectDependentKey.EntityAttributes.ID), processModelId)
        ));

        return sessionFactory.getCurrentSession().createQuery(query).getSingleResult();
    }

    @Override
    public List<ProcessModel> getAllProcessModels(UUID projectID) {

        //Initialize Database Request
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<ProcessModel> query = builder.createQuery(ProcessModel.class);
        Root<ProcessModel> rootProcessModel = query.from(ProcessModel.class);
        query.select(rootProcessModel);

        //searchQuery
        query.where(builder.equal(rootProcessModel.get(ProcessModel.EntityAttributes.ID).get(ProjectDependentKey.EntityAttributes.PROJECT_ID), projectID));

        return sessionFactory.getCurrentSession().createQuery(query).getResultList();
    }


    @Override
    public List<ProcessModel> getAllProcessModelsByControllerID(UUID projectId, int controllerID) {
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<ProcessModel> query = builder.createQuery(ProcessModel.class);
        Root<ProcessModel> rootProcessModel = query.from(ProcessModel.class);
        query.select(rootProcessModel);
        query.where(builder.and(
                builder.equal(rootProcessModel.get(ProcessModel.EntityAttributes.ID).get(ProjectDependentKey.EntityAttributes.PROJECT_ID), projectId),
                builder.equal(rootProcessModel.get(ProcessModel.EntityAttributes.CONTROLLERID), controllerID)));

        return sessionFactory.getCurrentSession().createQuery(query).getResultList();
    }


}