package de.xstampp.service.project.service.dao;

import org.springframework.stereotype.Repository;

import de.xstampp.service.project.data.entity.ControllerConstraint;
import de.xstampp.service.project.data.entity.EntityDependentKey;
import de.xstampp.service.project.service.dao.generic.pagination.AbstractEntityDependentHibernateDAO;
import de.xstampp.service.project.service.dao.iface.IControllerConstraintDAO;

@Repository
public class ControllerConstraintHibernateDAO
		extends AbstractEntityDependentHibernateDAO<ControllerConstraint, EntityDependentKey>
		implements IControllerConstraintDAO {

	@Override
	protected String getIdAttributeName() {
		return ControllerConstraint.EntityAttributes.ID;
	}

}
