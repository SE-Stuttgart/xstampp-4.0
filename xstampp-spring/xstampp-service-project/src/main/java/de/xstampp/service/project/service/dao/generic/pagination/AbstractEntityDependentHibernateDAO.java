package de.xstampp.service.project.service.dao.generic.pagination;

import de.xstampp.service.project.data.XStamppDependentEntity;
import de.xstampp.service.project.data.entity.EntityDependentKey;
import de.xstampp.service.project.exceptions.ProjectException;
import de.xstampp.service.project.service.dao.generic.AbstractGenericHibernateDAO;
import de.xstampp.service.project.service.dao.generic.pagination.PaginationHibernateImplementation.IHasCriteria;
import de.xstampp.service.project.service.dao.generic.pagination.PaginationHibernateImplementation.PageSpecification;
import de.xstampp.service.project.service.dao.iface.IEntityDependentGenericDAO;
import de.xstampp.service.project.service.dao.iface.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractEntityDependentHibernateDAO<T extends XStamppDependentEntity, ID extends EntityDependentKey>
        extends AbstractGenericHibernateDAO<T, ID> implements IEntityDependentGenericDAO<T, ID> {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PaginationHibernateImplementation<T> paginationImpl = new PaginationHibernateImplementation<>(
            getPersistentClass(), new DependentEntityHibernatePathFinder<T>());

    @Override
    public List<T> findAll(UUID projectId) {
        return paginationImpl.findAll(getSession(), new ProjectCriteria<T>(projectId));
    }

    @Override
    public List<T> findAll(UUID projectId, int parentId) {
        return paginationImpl.findAll(getSession(), new EntityCriteria<T>(projectId, parentId));
    }

    @Override
    public long count(UUID projectId, int parentId) {
        return paginationImpl.count(getSession(), new EntityCriteria<T>(projectId, parentId));
    }

    @Override
    public List<T> findFromTo(int from, int amount, Map<String, SortOrder> order, UUID projectId,
                              int parentId) {
        IHasCriteria<T> addtionalCriteria = new EntityCriteria<>(projectId, parentId);
        PageSpecification pageSpec = new PageSpecification(from, amount, order);
        return paginationImpl.findFromTo(getSession(), addtionalCriteria, pageSpec);
    }

    /**
     * Allows to find all entities in a project not constrained to children of a parent entity.
     * This is useful even though all entities inheriting from this class depend on a parent entity.
     */
    @Override
    public List<T> findFromTo(int from, int amount, Map<String, SortOrder> order, UUID projectId) {
        IHasCriteria<T> additionalCriteria = new EntityCriteria<>(projectId, -1);
        PageSpecification pageSpec = new PageSpecification(from, amount, order);
        return paginationImpl.findFromTo(getSession(), additionalCriteria, pageSpec);
    }

    @Override
    public boolean lockEntity(ID id, UUID userId, String userName, Timestamp expirationTime) {
        T loadedEntity = findById(id, false);

        // check if entity is null
        if (loadedEntity != null) {

            // check if entity is not locked by another user
            if (loadedEntity.getLockExpirationTime() == null
                    || loadedEntity.getLockExpirationTime().before(Timestamp.from(Instant.now()))
                    || loadedEntity.getLockHolderId().equals(userId)) {

                loadedEntity.setLockExpirationTime(expirationTime);
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

    private static class EntityCriteria<E> implements IHasCriteria<E> {

        private final UUID projectId;
        private final int parentId;

        EntityCriteria(UUID projectId, int parentId) {
            super();
            this.projectId = projectId;
            this.parentId = parentId;
        }

        @Override
        public <X> CriteriaQuery<X> addCriteriaTo(CriteriaQuery<X> query, CriteriaBuilder cb, Root<E> root) {
            //if no parent id is supplied we include all entities in project
            if (parentId == -1) {
                return query.where(cb.equal(root.get(XStamppDependentEntity.DEPENDENT_KEY_ATTRIBUTE)
                        .get(EntityDependentKey.EntityAttributes.PROJECT_ID), projectId));
            } else {
                //limit criteria to entities in project whose parentId matches the one supplied
                return query.where(cb.and(
                        cb.equal(root.get(XStamppDependentEntity.DEPENDENT_KEY_ATTRIBUTE)
                                .get(EntityDependentKey.EntityAttributes.PROJECT_ID), projectId),
                        cb.equal(root.get(XStamppDependentEntity.DEPENDENT_KEY_ATTRIBUTE)
                                .get(EntityDependentKey.EntityAttributes.PARENT_ID), parentId)));
            }
        }
    }

    private static class ProjectCriteria<E> implements IHasCriteria<E> {

        private final UUID projectId;

        public ProjectCriteria(UUID projectId) {
            this.projectId = projectId;
        }

        @Override
        public <X> CriteriaQuery<X> addCriteriaTo(CriteriaQuery<X> query, CriteriaBuilder cb, Root<E> root) {
            return query.where(cb.and(
                    cb.equal(root.get(XStamppDependentEntity.DEPENDENT_KEY_ATTRIBUTE)
                            .get(EntityDependentKey.EntityAttributes.PROJECT_ID), projectId)));
        }
    }
}
