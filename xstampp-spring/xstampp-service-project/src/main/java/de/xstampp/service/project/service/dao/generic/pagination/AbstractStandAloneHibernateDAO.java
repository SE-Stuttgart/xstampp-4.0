package de.xstampp.service.project.service.dao.generic.pagination;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import de.xstampp.service.project.service.dao.generic.AbstractGenericHibernateDAO;
import de.xstampp.service.project.service.dao.generic.pagination.PaginationHibernateImplementation.IPathFinder;
import de.xstampp.service.project.service.dao.generic.pagination.PaginationHibernateImplementation.PageSpecification;
import de.xstampp.service.project.service.dao.iface.IStandAloneGenericDAO;
import de.xstampp.service.project.service.dao.iface.SortOrder;

public abstract class AbstractStandAloneHibernateDAO<T extends Serializable, ID extends Serializable> extends AbstractGenericHibernateDAO<T, ID>
        implements IStandAloneGenericDAO<T, ID> {

    private final PaginationHibernateImplementation<T> paginationImpl = new PaginationHibernateImplementation<>(
            getPersistentClass(), new PathFinder<T>());

    @Override
    public List<T> findAll() {
        return paginationImpl.findAll(getSession());
    }

    @Override
    public long count() {
        return paginationImpl.count(getSession());
    }

    @Override
    public List<T> findFromTo(int from, int amount, Map<String, SortOrder> order) {
        return paginationImpl.findFromTo(getSession(), new PageSpecification(from, amount, order));
    }

    private static class PathFinder<E> implements IPathFinder<E> {

        @Override
        public <X> Path<X> pathTo(String key, Root<E> root) {
            return root.get(key);
        }
    }
}
