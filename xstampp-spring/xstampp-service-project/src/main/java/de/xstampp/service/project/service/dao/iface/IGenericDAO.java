package de.xstampp.service.project.service.dao.iface;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * Generic interface for the persistence layer. It defines basic methods for
 * retrieval, creation, modification and deletion of entities (e.g. CRUD
 * operations).
 * </p>
 * <p>
 * Reference: C. Bauer, G. King: Java Persistence mit Hibernate, Publisher:
 * Manning Publications, german edition, Munich, 2007.
 * </p>
 *
 * @param <T>  entity type.
 * @param <ID> type representing the id of the given entity type.
 */
public interface IGenericDAO<T extends Serializable, ID extends Serializable> {

    /**
     * Method for retrieval of a single entity by its id from the database.
     *
     * @param id   id of the entity.
     * @param lock if true the entity will be locked for the write operation during
     *             the current session. If false the entity won't be locked for the
     *             write operation.
     * @return the entity for the given id.
     */
    T findById(ID id, boolean lock);

    /**
     * Saves the given entities in the database. An exception will be thrown
     * if an entity already exists in the database.
     *
     * @param entities
     * @return
     */
    List<ID> saveAll(List<T> entities);

    /**
     * Updates the given entities in the database. An exception will be thrown
     * if an entity does not exist in the database.
     *
     * @param entities
     */
    void updateAll(List<T> entities);

    /**
     * Stores the given entity in the database. If the entity is available in the
     * database it will be updated.
     *
     * @param entity entity to store or update.
     * @return the same entity if the operation was successful.
     */
    T makePersistent(T entity);

    /**
     * Deletes the entity of the given type from the database.
     *
     * @param entity entity to delete.
     */
    void makeTransient(T entity);

    /**
     * Updates an existing entity in the database. Works like
     * {@link IGenericDAO#makePersistent(Serializable)} (Object)} except if there is no such entity
     * in the database, an exception will be thrown.
     *
     * @param entity the entity to update
     * @return the same entity if the operation was successful
     */
    T updateExisting(T entity);

    /**
     * Checks whether the entity exists for the given id.
     *
     * @param id id of the entity.
     * @return true if the entity exists. False otherwise.
     */
    boolean exist(ID id);

    boolean lockEntity(ID id, UUID userId, String userName, Timestamp expirationTime);

    boolean unlockEntity(ID id, UUID userId, String userName);
}
