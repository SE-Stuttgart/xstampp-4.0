package de.xstampp.service.project.service.dao.generic.pagination;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.xstampp.service.project.data.XStamppDependentEntity;
import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.exceptions.ProjectException;
import de.xstampp.service.project.service.dao.generic.AbstractGenericHibernateDAO;
import de.xstampp.service.project.service.dao.generic.pagination.PaginationHibernateImplementation.IHasCriteria;
import de.xstampp.service.project.service.dao.generic.pagination.PaginationHibernateImplementation.PageSpecification;
import de.xstampp.service.project.service.dao.iface.IProjectDependentGenericDAO;
import de.xstampp.service.project.service.dao.iface.SortOrder;

public abstract class AbstractProjectDependentHibernateDAO<T extends XStamppDependentEntity, ID extends ProjectDependentKey>
		extends AbstractGenericHibernateDAO<T, ID> implements IProjectDependentGenericDAO<T, ID> {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final PaginationHibernateImplementation<T> paginationImpl = new PaginationHibernateImplementation<>(
			getPersistentClass(), new DependentEntityHibernatePathFinder<T>());

	@Override
	public List<T> findAll(UUID projectId) {
		return paginationImpl.findAll(getSession(), new ProjectCriteria<T>(projectId));
	}
	
	@Override
	public long count(UUID projectId) {
		return paginationImpl.count(getSession(), new ProjectCriteria<T>(projectId));
	}

	@Override
	public List<T> findFromTo(int from, int amount, Map<String, SortOrder> order, UUID projectId) {
		IHasCriteria<T> additionalCriteria = new ProjectCriteria<>(projectId);
		PageSpecification pageSpec = new PageSpecification(from, amount, order);
		return paginationImpl.findFromTo(getSession(), additionalCriteria, pageSpec);
	}
	
	@Override
	public boolean lockEntity(ID id, UUID userId, String userName, Timestamp timestamp) {
		T loadedEntity = findById(id, false);

		// check if entity is null
		if (loadedEntity != null) {

			// check if entity is not locked by another user 
			if (loadedEntity.getLockExpirationTime() == null
					|| loadedEntity.getLockExpirationTime().before(Timestamp.from(Instant.now()))
					|| loadedEntity.getLockHolderId().equals(userId)) {

				loadedEntity.setLockExpirationTime(timestamp);
				loadedEntity.setLockHolderId(userId);
				loadedEntity.setLockHolderDisplayName(userName);
				
				makePersistent(loadedEntity);
				return true;
			} else {
				logger.error("entity currently locked by another user");
			}
			return false;
		} else {
			logger.error("entity with the given id not found");
			return false;
		}
	}

	@Override
	public boolean unlockEntity(ID id, UUID userId, String userName) {
		T loadedEntity = findById(id, false);

		// check if entity is null
		if (loadedEntity != null) {

			if (loadedEntity.getLockExpirationTime() == null) {
				throw new ProjectException("No such lock exists.");
			}

			// check if entity is still locked by the same user
			if (loadedEntity.getLockExpirationTime().after(Timestamp.from(Instant.now()))
					&& loadedEntity.getLockHolderId().equals(userId)) {

				loadedEntity.setLockExpirationTime(Timestamp.from(Instant.now()));
				
				makePersistent(loadedEntity);
				return true;
			} else {
				logger.error("entity currently locked by another user");
			}
			return false;
		} else {
			logger.error("entity with the given id not found");
			return false;
		}
	}
	
	private static class ProjectCriteria<E> implements IHasCriteria<E> {
		
		private final UUID projectId;
		
		ProjectCriteria(UUID projectId) {
			super();
			this.projectId = projectId;
		}

		@Override
		public <X> CriteriaQuery<X> addCriteriaTo(CriteriaQuery<X> query, CriteriaBuilder cb, Root<E> root) {
			return query.where(cb.equal(
					root.get(XStamppDependentEntity.DEPENDENT_KEY_ATTRIBUTE).get(ProjectDependentKey.EntityAttributes.PROJECT_ID),
					projectId));
		}
		
	}
	
}
