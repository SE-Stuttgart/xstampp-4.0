package de.xstampp.service.project.service.dao.control_structure;

import de.xstampp.service.project.data.entity.Arrow;
import de.xstampp.service.project.service.dao.control_structure.iface.IArrowDAO;
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
public class ArrowHibernateDAO implements IArrowDAO {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession(){ return sessionFactory.getCurrentSession(); }

    @Override
    public List<Arrow> AllArrowsByDestination(UUID projectId, String destination) {
        //Initialize Database request
        CriteriaBuilder builder = getSession().getCriteriaBuilder();
        CriteriaQuery<Arrow> query = builder.createQuery(Arrow.class);
        Root<Arrow> rootLink = query.from(Arrow.class);
        query.select(rootLink);

        //searchQuery
        query.where(builder.and(
                builder.equal(rootLink.get(Arrow.EntityAttributes.PROJECT_ID), projectId),
                builder.equal(rootLink.get(Arrow.EntityAttributes.DESTINATION), destination)
        ));

        //Sort Results
        query.orderBy(builder.asc(rootLink.get(Arrow.EntityAttributes.ID)));

        Query<Arrow> execQuery = getSession().createQuery(query);

        return execQuery.getResultList();
    }

    @Override
    public List<Arrow> AllArrowsBySource(UUID projectId, String source) {

        //Initialize Database request
        CriteriaBuilder builder = getSession().getCriteriaBuilder();
        CriteriaQuery<Arrow> query = builder.createQuery(Arrow.class);
        Root<Arrow> rootLink = query.from(Arrow.class);
        query.select(rootLink);

        //searchQuery
        query.where(builder.and(
                builder.equal(rootLink.get(Arrow.EntityAttributes.PROJECT_ID), projectId),
                builder.equal(rootLink.get(Arrow.EntityAttributes.SOURCE), source)
        ));

        //Sort Results
        query.orderBy(builder.asc(rootLink.get(Arrow.EntityAttributes.ID)));

        Query<Arrow> execQuery = getSession().createQuery(query);

        return execQuery.getResultList();
    }

    @Override
    public Arrow getArrowById(UUID projectId, String arrowId) {

        //Initialize Database request
        CriteriaBuilder builder = getSession().getCriteriaBuilder();
        CriteriaQuery<Arrow> query = builder.createQuery(Arrow.class);
        Root<Arrow> rootLink = query.from(Arrow.class);
        query.select(rootLink);

        //searchQuery
        query.where(builder.and(
                builder.equal(rootLink.get(Arrow.EntityAttributes.ID),arrowId),
                builder.equal(rootLink.get(Arrow.EntityAttributes.PROJECT_ID), projectId)
                )
        );

        //checks if ResultSet is empty
        Query<Arrow> execQuery = getSession().createQuery(query);
        if(execQuery.getResultList().isEmpty()){
            return null;
        }else{
            return getSession().createQuery(query).getSingleResult();
        }

    }

    @Override
    public Arrow getArrowBySourceAndDestination(UUID projectId, String source, String destination) {
        //Initialize Database request
        CriteriaBuilder builder = getSession().getCriteriaBuilder();
        CriteriaQuery<Arrow> query = builder.createQuery(Arrow.class);
        Root<Arrow> root = query.from(Arrow.class);
        query.select(root);

        //searchQuery
        query.where(builder.and(
                builder.equal(root.get(Arrow.EntityAttributes.PROJECT_ID),projectId),
                builder.equal(root.get(Arrow.EntityAttributes.SOURCE),source),
                builder.equal(root.get(Arrow.EntityAttributes.DESTINATION),destination)
        ));

        List<Arrow> arrowList = getSession().createQuery(query).getResultList();

        if(arrowList.size() > 0){
            return  arrowList.get(0);
        }else{
            return null;
        }
    }

    @Override
    public List<Arrow> getArrowsByType(UUID projectId, String type) {
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<Arrow> query = builder.createQuery(Arrow.class);
        Root<Arrow> root = query.from(Arrow.class);
        query.select(root);

        // searchQuery
        query.where(builder.and(builder.equal(root.get(Arrow.EntityAttributes.PROJECT_ID), projectId),
                builder.equal(root.get(Arrow.EntityAttributes.ARROW_TYPE), type)));

        return sessionFactory.getCurrentSession().createQuery(query).getResultList();
    }

    @Override
    public List<Arrow> findAll(UUID projectId) {
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<Arrow> query = builder.createQuery(Arrow.class);
        Root<Arrow> root = query.from(Arrow.class);
        query.select(root);
        query.where(builder.and(builder.equal(root.get(Arrow.EntityAttributes.PROJECT_ID), projectId)));

        return sessionFactory.getCurrentSession().createQuery(query).getResultList();
    }

    @Override
    public List<Arrow>  saveAll(List<Arrow> arrows) {
        List<Arrow> savedArrows = new LinkedList<>();
        Session session = getSession();

        for (Arrow arrow: arrows) {
            Arrow savedArrow = Arrow.class
                    .cast(session.save(arrow));
            savedArrows.add(savedArrow);
        }

        session.flush();

        return savedArrows;
    }
}
