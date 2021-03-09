package de.xstampp.service.project.service.dao.last_id;

import de.xstampp.service.project.data.entity.lastId.ProjectEntityLastId;
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
class ProjectEntityLastIdDAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public ProjectEntityLastIdDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<ProjectEntityLastId> findAll(UUID projectId) {
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<ProjectEntityLastId> query = builder.createQuery(ProjectEntityLastId.class);
        Root<ProjectEntityLastId> entityLastIdRoot = query.from(ProjectEntityLastId.class);

        query.select(entityLastIdRoot)
                .where(builder.equal(entityLastIdRoot.get(ProjectEntityLastId.EntityAttributes.PROJECT_ID), projectId));

        return sessionFactory.getCurrentSession().createQuery(query).getResultList();
    }

    public List<ProjectEntityLastId> saveAll(List<ProjectEntityLastId> projectEntityLastIds) {
        List<ProjectEntityLastId> savedProjectEntityLastId = new LinkedList<>();
        Session session = sessionFactory.getCurrentSession();

        for (ProjectEntityLastId projectEntityLastId : projectEntityLastIds) {
            ProjectEntityLastId savedProjectEntityLastIds = ProjectEntityLastId.class
                    .cast(session.save(projectEntityLastId));
            savedProjectEntityLastId.add(savedProjectEntityLastIds);
        }

        session.flush();

        return savedProjectEntityLastId;
    }
}
