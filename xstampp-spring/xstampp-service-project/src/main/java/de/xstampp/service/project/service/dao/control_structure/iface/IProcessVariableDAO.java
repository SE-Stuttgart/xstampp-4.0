package de.xstampp.service.project.service.dao.control_structure.iface;

import java.util.List;
import java.util.UUID;

import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.control_structure.ProcessVariable;
import de.xstampp.service.project.service.dao.iface.IProjectDependentGenericDAO;

/**
 * Interface for retrieving and manipulating ProcessVariables from the Database
 */
public interface IProcessVariableDAO extends IProjectDependentGenericDAO<ProcessVariable, ProjectDependentKey> {

	/**
	 * retrieves a list of ProcessVariables corresponding to the received List of
	 * Arrow IDs from the Database
	 * 
	 * @param projectId   project ProcessVariables belong to
	 * @param arrowIdList List with Arrow IDs
	 * @return List of ProcessVariables containing min. one Arrow ID in the List
	 */
	public List<ProcessVariable> getAllProcessVariablesByArrowIds(UUID projectId, List<String> arrowIdList);


	/**
	 * retrieves all ProcessVariables for a list of IDs from the Database
	 * @param projectId	Project ProcessModel &amp; ProcessVariables belong to
	 * @param processVariables	List of ProcessVariableIDs
	 * @return	List of all ProcessVariables that match the IDs in List
	 */
	public List<ProcessVariable> getAllProcessVariablesFromList (UUID projectId, List<Integer> processVariables);

	/**
	 * retrieves all ProcessVariables for a given project from the Database
	 * @param projectId	Project ProcessVariables belong to
	 * @return	all ProcessVariables matching given projectId
	 */
	public List<ProcessVariable> getAllProcessVariablesForProject (UUID projectId);

	/**
	 * retrieves all ProcessVariables of a certain type from the Database
	 * @param projectId	project ProcessVariables belong to
	 * @param type	type of the Variable
	 * @return	all ProcessVariables of given type
	 */
	public List<ProcessVariable> getAllProcessVariablesByType (UUID projectId, String type);

}
