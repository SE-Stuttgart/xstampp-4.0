package de.xstampp.service.project.service.dao.iface;

import de.xstampp.service.project.data.entity.DiscreteProcessVariableValue;

import java.util.List;
import java.util.UUID;

/**
 * This Interface is used to retrieve and manipulate all possible Values for discrete process variables out of a table in the Database
 */
public interface IDiscreteProcessVariableValueDAO {

    /**
     * retrieves all possible process variable values for a specific process variable from the Database
     *
     * @param projectId         project Variable relates to
     * @param processVariableId ID of the process variable
     * @return List of possible process variable values
     */
    public List<DiscreteProcessVariableValue> getAllValuesById(UUID projectId, int processVariableId);

    /**
     * retrieves all possible process variable values for a projectId
     *
     * @param projectId the projectId
     * @return List of matching process variable values
     */
    public List<DiscreteProcessVariableValue> getAllValuesById(UUID projectId);

    /**
     * creates a new Entry in the table
     *
     * @param projectId         project ID
     * @param processVariableId ProcessVariable ID
     * @param value             new Valuetype to be added
     * @return Entry
     */
    public DiscreteProcessVariableValue createNewValue(UUID projectId, int processVariableId, String value);

    /**
     * deletes an Entry in the table
     *
     * @param projectId         Project Id
     * @param processVariableId ProcessVariable ID
     * @param value             Value of Variable
     * @return success of deletion (true/false)
     */
    public boolean deleteValue(UUID projectId, int processVariableId, String value);

    /**
     * deletes all Entries relating to a process Variable in the table
     *
     * @param projectId         Project ID
     * @param processVariableId Process Variable ID
     * @return success of deletion (yes/no)
     */
    public boolean deleteAllValues(UUID projectId, int processVariableId);

    /**
     * searches table for specific Entry and retrieves it from Database
     *
     * @param projectId         Project ID
     * @param processVariableId ProcessVariable ID
     * @param value             Value for ProcessVariable
     * @return Entry
     */
    public DiscreteProcessVariableValue getValue(UUID projectId, int processVariableId, String value);

    List<DiscreteProcessVariableValue> saveAll(List<DiscreteProcessVariableValue> discreteProcessVariableValues);
}
