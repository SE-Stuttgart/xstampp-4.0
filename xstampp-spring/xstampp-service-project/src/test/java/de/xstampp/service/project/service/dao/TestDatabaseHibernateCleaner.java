package de.xstampp.service.project.service.dao;

import static org.junit.Assert.assertEquals;

import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Removes all application specific data from the database. Use this class only
 * for tests. Reference: S. Freeman, N. Pryce: Growing Object-Oriented Software,
 * Guided by Tests, Publisher: Pearson Educaton, Inc., Fourth Printing,
 * Crawfordsville, 2010.
 * </p>
 * <p>
 * <strong>Note:</strong> the methods of this class have to be called in a
 * transactional context.
 * </p>
 */
@Service
public abstract class TestDatabaseHibernateCleaner {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession() {
		return this.sessionFactory.getCurrentSession();
	}
	
	private CriteriaBuilder criteriaBuilder() {
		return sessionFactory.getCurrentSession().getCriteriaBuilder();
	}
	
	/**
	 * <strong>Note:</strong> you have to add your entity.
	 */
	void clean() {
		int amount_of_entities = overallAmountOfEntities();
		Class<?>[] registeredEntityTypes = getRegisteredEntityTypes();
		assertEquals("all entity types have to be deleted.", amount_of_entities, registeredEntityTypes.length);

		for (Class<?> entityType : registeredEntityTypes) {
			deleteEntity(entityType);
		}
	}
	
	protected abstract Class<?>[] getRegisteredEntityTypes();
	
	private int overallAmountOfEntities() {
		int result = 0;
		result = ((EntityManagerFactory)this.sessionFactory).getMetamodel().getEntities().size();
		return result;
	}
	
	private <T> void deleteEntity(Class<T> entityType) {
		CriteriaDelete<T> criteriaDelete = criteriaBuilder().createCriteriaDelete(entityType);
		criteriaDelete.from(entityType);
		getSession().createQuery(criteriaDelete).executeUpdate();
	}
	
}
