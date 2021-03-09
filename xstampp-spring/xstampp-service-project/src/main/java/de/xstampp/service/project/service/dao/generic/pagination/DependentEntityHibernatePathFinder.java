package de.xstampp.service.project.service.dao.generic.pagination;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import de.xstampp.service.project.data.XStamppDependentEntity;
import de.xstampp.service.project.service.dao.generic.pagination.PaginationHibernateImplementation.IPathFinder;

/**
 * Implements access to all relevant attributes of dependent entities. The
 * dependent entities have a composite primary key which references primary keys
 * of other entities.
 * 
 * @param <T> type of the entity.
 */
class DependentEntityHibernatePathFinder<T> implements IPathFinder<T> {
	
	DependentEntityHibernatePathFinder() {
		super();
	}
	
	@Override
	public <X> Path<X> pathTo(String key, Root<T> root) {
		Path<X> result = null;
		if (XStamppDependentEntity.DEPENDENT_KEY_ATTRIBUTE.equals(key)) {
			result = root.get(XStamppDependentEntity.DEPENDENT_KEY_ATTRIBUTE).get(key);
		} else {
			result = root.get(key);
		}
		return result;
	}
	
}
