package de.xstampp.service.auth.service.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

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
public interface IGenericDAO<T, ID extends Serializable> {

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
     * Method for retrieval of all entities of the given type.
     *
     * @return all entities in the database.
     */
    List<T> findAll();

    /**
     * Finds entities of the given type using parameters set in the example
     * instance. It is comparable with the regular expression search.
     *
     * @param exampleInstance the entity with the properties for which you would
     *                        like to search.
     * @return all entities which match the given parameters.
     */
    List<T> findByExample(T exampleInstance);

    /**
     * Stores the given entity in the database. If the entity is available in the
     * database it will be updated.
     *
     * @param entity entity to store or update.
     * @return the same entity if the operation was successful.
     */
    T makePersistent(T entity);

    // TODO: Complete Documentation @Rico

    /**
     * Saves a new entity. Like {@link #makePersistent} but errors if it already
     * exists
     */
    T saveNew(T entity);

    // TODO: Complete Documentation @Rico

    /**
     * Updates an entity. Like {@link #makePersistent} but errors if it doesn't
     * exist
     */
    T updateExisting(T entity);

    /**
     * Deletes the entity of the given type from the database.
     *
     * @param entity entity to delete.
     */
    void makeTransient(T entity);

    /**
     * Finds entities of the given type within the specified range.
     *
     * @param from   specifies how many rows to skip.
     * @param amount specifies how many rows to retrieve.
     * @param order  specifies columns for ordering the rows. First all rows are
     *               sorted using the specified columns. The order of insertion of
     *               the elements into the map specifies the precedence of the
     *               columns to sort the rows. The first one has the highest
     *               precedence. The sort order is specified for each column as
     *               {@link SortOrder}. The sorting is applied before
     *               the selection using the range parameters amount and from.
     * @return a list of entities according to the specified parameters.
     */
    List<T> findFromTo(int from, int amount, Map<String, SortOrder> order);

    /**
     * Counts all entities of the given type in the database.
     *
     * @return amount of all entities in the database.
     */
    long count();

}
