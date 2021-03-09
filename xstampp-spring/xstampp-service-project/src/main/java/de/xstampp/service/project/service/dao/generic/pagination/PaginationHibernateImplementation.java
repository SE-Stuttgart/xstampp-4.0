package de.xstampp.service.project.service.dao.generic.pagination;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import de.xstampp.common.errors.ErrorsProj;
import de.xstampp.service.project.service.dao.iface.SortOrder;

/**
 * <p>
 * Implements pagination feature for the entity of the given type. You can
 * specify additional criteria like distinct project id to restrict the result
 * set.
 * </p>
 * <p>
 * You have to specify a path finder which can access all relevant attributes of
 * the given entity type.
 * </p>
 * 
 * @param <T> type of the entity.
 */
final class PaginationHibernateImplementation<T> {

	private final Class<T> persistentClass;

	private final IPathFinder<T> pathFinder;

	/**
	 * Create instances of this class only in data access objects.
	 * 
	 * @param persistentClass type of the entity for pagination.
	 * @param pathFinder      implements access to all attributes of the entity
	 *                        which are relevant for sorting.
	 */
	PaginationHibernateImplementation(Class<T> persistentClass, IPathFinder<T> pathFinder) {
		super();
		this.persistentClass = persistentClass;
		this.pathFinder = pathFinder;
	}

	/**
	 * Finds all entities of the given type in the database.
	 * 
	 * @param pSession current hibernate session.
	 * @return all entities of the given type.
	 */
	List<T> findAll(Session pSession) {
		return findAll(pSession, null);
	}

	/**
	 * Finds all entities of the given type in the database which meet the given
	 * criteria.
	 * 
	 * @param session  current hibernate session.
	 * @param criteria additional criteria to restrict the result set.
	 * @return all entities of the given type which meet the given criteria.
	 */
	List<T> findAll(Session session, IHasCriteria<T> criteria) {
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<T> query = cb.createQuery(persistentClass);
		Root<T> root = query.from(persistentClass);
		query = query.select(root);
		if (criteria != null) {
			query = criteria.addCriteriaTo(query, cb, root);
		}
		return session.createQuery(query).getResultList();
	}

	/**
	 * Counts all entities of the given type in the database.
	 * 
	 * @param session current hibernate session.
	 * @return amount of the entities of the given type in the database.
	 */
	long count(Session session) {
		return count(session, null);
	}

	/**
	 * Counts all entities of the given type in the database which meet the given
	 * criteria.
	 * 
	 * @param session  current hibernate session.
	 * @param criteria additional criteria to count only entities which meet given
	 *                 criteria.
	 * @return amount of the entities of the given type in the database which meet
	 *         the given criteria.
	 */
	long count(Session session, IHasCriteria<T> criteria) {
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);
		Root<T> root = query.from(persistentClass);
		query = query.select(cb.count(root));
		if (criteria != null) {
			query = criteria.addCriteriaTo(query, cb, root);
		}
		TypedQuery<Long> executableQuery = session.createQuery(query);
		return executableQuery.getSingleResult();
	}

	/**
	 * Selects the specified page with the entities of the given type from the
	 * database.
	 * 
	 * @param session  the current hibernate session.
	 * @param pageSpec specifies the page to select.
	 * @return entities of the given type for the given page specification.
	 */
	List<T> findFromTo(Session session, PageSpecification pageSpec) {
		return findFromTo(session, null, pageSpec);
	}

	/**
	 * Selects the specified page with the entities of the given type from the
	 * database which meet the given criteria.
	 * 
	 * @param session  current hibernate session.
	 * @param criteria additional criteria to restrict the result set.
	 * @param pageSpec specifies the page to select.
	 * @return entities of the given type for the given page specification which
	 *         meet the given criteria.
	 */
	List<T> findFromTo(Session session, IHasCriteria<T> criteria, PageSpecification pageSpec) {
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<T> query = cb.createQuery(persistentClass);
		Root<T> root = query.from(persistentClass);
		query.select(root);
		if (criteria != null) {
			query = criteria.addCriteriaTo(query, cb, root);
		}
		specifySortOrder(pageSpec, query, cb, root);
		return executeQuery(session, query, pageSpec.from, pageSpec.amount);
	}

	private void specifySortOrder(PageSpecification pageSpec, CriteriaQuery<T> query, CriteriaBuilder cb, Root<T> root) {
		checkSortOrder(pageSpec.order);
		List<Order> result = new ArrayList<>();
		for (Entry<String, SortOrder> entry : pageSpec.order.entrySet()) {
			if (SortOrder.ASC == entry.getValue() || entry.getValue() == null) {
				result.add(cb.asc(pathFinder.pathTo(entry.getKey(), root)));
			} else if (SortOrder.DESC == entry.getValue()) {
				result.add(cb.desc(pathFinder.pathTo(entry.getKey(), root)));
			}
		}
		query.orderBy(result);
	}

	private static void checkSortOrder(Map<String, SortOrder> order) {
		if (order == null || order.isEmpty()) {
			throw ErrorsProj.NEED_SORT_ARG.exc();
		}
	}

	private List<T> executeQuery(Session session, CriteriaQuery<T> query, int from, int amount) {
		TypedQuery<T> executableQuery = session.createQuery(query);
		executableQuery.setFirstResult(from);
		executableQuery.setMaxResults(amount);
		return executableQuery.getResultList();
	}

	/**
	 * Adds additional criteria to the search query.
	 * 
	 * @param <T> type of the entity.
	 */
	interface IHasCriteria<T> {

		/**
		 * Adds additional criteria to the given search query.
		 * 
		 * @param query hibernate search query to which the criteria are added.
		 * @param cb    hibernate criteria builder to construct search queries for the
		 *              new criteria.
		 * @param root  hibernate root representing the entity type in the database.
		 * @return hibernate criteria query with the added criteria.
		 */
		<X> CriteriaQuery<X> addCriteriaTo(CriteriaQuery<X> query, CriteriaBuilder cb, Root<T> root);
	}

	/**
	 * Instances of this interface implement access to all relevant attributes of
	 * the given entity type.
	 * 
	 * @param <T> type of the entity.
	 */
	interface IPathFinder<T> {

		/**
		 * Finds the path to the given attribute in the given entity type.
		 * 
		 * @param key  the name of the attribute.
		 * @param root hibernate root representing the entity type in the database.
		 * @return hibernate path to the given attribute.
		 */
		<X> Path<X> pathTo(String key, Root<T> root);
	}

	/**
	 * Wrapper type for the parameters specifying a page.
	 */
	static class PageSpecification {
		private final int from;
		private final int amount;
		private final Map<String, SortOrder> order;

		PageSpecification(int from, int amount, Map<String, SortOrder> order) {
			super();
			this.from = from;
			this.amount = amount;
			this.order = order;
		}

		@Override
		public String toString() {
			return "Page [from=" + from + ", amount=" + amount + ", order=" + order + "]";
		}

	}

}
