package de.xstampp.service.project.service.dao.control_structure;

import de.xstampp.service.project.data.entity.control_structure.VectorGraphic;
import de.xstampp.service.project.service.dao.control_structure.iface.IVectorGraphicDAO;
import de.xstampp.service.project.service.data.report.ReportConstructionException;
import de.xstampp.service.project.service.data.report.xmlProcessor.XmlProcessor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.UUID;

@Repository
public class VectorGraphicHibernateDAO implements IVectorGraphicDAO {

    @Autowired
    SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public void saveVectorGraphic(VectorGraphic vectorGraphic) {
        XmlProcessor.svgSecurityCheck(vectorGraphic.getGraphic());
        getSession().saveOrUpdate(vectorGraphic);
    }

    @Override
    public VectorGraphic getVectorGraphic(UUID projectId, boolean colored) {

        //Initialize Database request.
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<VectorGraphic> query = builder.createQuery(VectorGraphic.class);
        Root<VectorGraphic> rootVectorGraphic = query.from(VectorGraphic.class);
        query.select(rootVectorGraphic);

        //searchQuery
        query.where(builder.and(
                builder.equal(rootVectorGraphic.get(VectorGraphic.EntityAttributes.PROJECT_ID), projectId),
                builder.equal(rootVectorGraphic.get(VectorGraphic.EntityAttributes.HAS_COLOUR), colored))
        );
        try {
            return getSession().createQuery(query).getSingleResult();
        } catch (NoResultException ex) {
            throw new ReportConstructionException(
                    "Open the Control Structure edit mode and save it once to be able to export it.",
                    "This project has no Control Structure SVG in the database.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
