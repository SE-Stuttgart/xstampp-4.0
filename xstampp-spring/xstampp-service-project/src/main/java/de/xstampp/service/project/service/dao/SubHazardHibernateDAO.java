package de.xstampp.service.project.service.dao;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import de.xstampp.service.project.data.entity.EntityDependentKey;
import de.xstampp.service.project.data.entity.SubHazard;
import de.xstampp.service.project.service.dao.generic.pagination.AbstractEntityDependentHibernateDAO;
import de.xstampp.service.project.service.dao.iface.ISubHazardDAO;

@Repository
public class SubHazardHibernateDAO extends AbstractEntityDependentHibernateDAO<SubHazard, EntityDependentKey> 
	implements ISubHazardDAO {
	
	@Autowired
	SessionFactory sessionFactory;

	@Override
	public List<SubHazard> findSubHazardsByIds(List<EntityDependentKey> subHazardPKeys, Integer amount, Integer from) {
		
		CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<SubHazard> query = builder.createQuery(SubHazard.class);
		Root<SubHazard> rootSubHazard = query.from(SubHazard.class);
		query.select(rootSubHazard);
		query.where(rootSubHazard.get(SubHazard.EntityAttributes.ID).in(subHazardPKeys));
		
		return getSession().createQuery(query).getResultList();
	}

	@Override
	protected String getIdAttributeName() {
		return SubHazard.EntityAttributes.ID;
	}

}
