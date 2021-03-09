package de.xstampp.service.project.service.dao.iface;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Generic interface for the persistence layer. It defines methods to retrieve
 * several entities at once.
 *
 * @param <T>  entity type.
 * @param <ID> type representing the id of the given entity type.
 */
public interface IStandAloneGenericDAO<T extends Serializable, ID extends Serializable> extends IGenericDAO<T, ID> {

    /**
     * Method for retrieval of all entities of the given type.
     *
     * @return all entities in the database.
     */
    List<T> findAll();

    /**
     * Finds entities of the given type within the specified range.
     *
     * @param from   specifies how many rows to skip. Starting from zero.
     * @param amount specifies how many rows to retrieve.
     * @param order  specifies columns for ordering the rows. First all rows are
     *               sorted using the specified columns. The order of insertion of
     *               the elements into the map specifies the precedence of the
     *               columns to sort the rows. The first one has the highest
     *               precedence. The sort order is specified for each column as
     *               {@link SortOrder}. If no sort order is specified for a column
     *               ascending sort order is applied for this column. The sorting is
     *               applied before the selection using the range parameters amount
     *               and from. <strong>Note:</strong> you have to specify at least
     *               one column name for sorting the results to assure predictable
     *               sort order.
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
