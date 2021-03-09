package de.xstampp.service.project.service.dao.control_structure;

import de.xstampp.service.project.data.entity.Box;
import de.xstampp.service.project.service.dao.control_structure.iface.IBoxDAO;
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
public class BoxHibernateDAO implements IBoxDAO {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public List<Box> getBoxByIds(UUID projectId, List<String> boxIdList) {

        // Initalize Database access
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<Box> query = builder.createQuery(Box.class);
        Root<Box> rootLink = query.from(Box.class);
        query.select(rootLink);

        // searchQuery
        query.where(builder.and(builder.equal(rootLink.get(Box.EntityAttributes.PROJECT_ID), projectId),
                rootLink.get(Box.EntityAttributes.ID).in(boxIdList)));
        return sessionFactory.getCurrentSession().createQuery(query).getResultList();
    }

    @Override
    public List<Box> getBoxById(UUID projectId) {
        //Initialize Database access
        CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<Box> boxCriteriaQuery = criteriaBuilder.createQuery(Box.class);
        Root<Box> boxRoot = boxCriteriaQuery.from(Box.class);
        boxCriteriaQuery.select(boxRoot);

        //searchQuery
        boxCriteriaQuery.where(criteriaBuilder.and(
                criteriaBuilder.equal(boxRoot.get(Box.EntityAttributes.PROJECT_ID), projectId)));

        return sessionFactory.getCurrentSession().createQuery(boxCriteriaQuery).getResultList();
    }

    @Override
    public Box getSingleBoxById(UUID projectId, String boxId) {

        // Initalize Database access
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<Box> query = builder.createQuery(Box.class);
        Root<Box> root = query.from(Box.class);
        query.select(root);

        // searchQuery
        query.where(builder.and(builder.equal(root.get(Box.EntityAttributes.PROJECT_ID), projectId),
                builder.equal(root.get(Box.EntityAttributes.ID), boxId)));

        return sessionFactory.getCurrentSession().createQuery(query).getSingleResult();
    }

    @Override
    public List<Box> findAll(UUID projectId) {
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<Box> query = builder.createQuery(Box.class);
        Root<Box> root = query.from(Box.class);
        query.select(root);
        query.where(builder.and(builder.equal(root.get(Box.EntityAttributes.PROJECT_ID), projectId)));

        return sessionFactory.getCurrentSession().createQuery(query).getResultList();
    }

    @Override
    public List<Box> saveAll(List<Box> boxes) {
        List<Box> savedBoxes = new LinkedList<>();
        Session session = sessionFactory.getCurrentSession();

        for (Box arrow : boxes) {
            Box savedArrow = Box.class
                    .cast(session.save(arrow));
            savedBoxes.add(savedArrow);
        }

        session.flush();

        return savedBoxes;
    }

    public List<Box> getBoxByTypeAndParent(UUID projectId, int parentId, String type) {
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<Box> query = builder.createQuery(Box.class);
        Root<Box> root = query.from(Box.class);
        query.select(root);

        // searchQuery
        query.where(builder.and(builder.equal(root.get(Box.EntityAttributes.PROJECT_ID), projectId),
                builder.equal(root.get(Box.EntityAttributes.PARENT), parentId),
                builder.equal(root.get(Box.EntityAttributes.BOX_TYPE), type)));

        return sessionFactory.getCurrentSession().createQuery(query).getResultList();
    }
}
