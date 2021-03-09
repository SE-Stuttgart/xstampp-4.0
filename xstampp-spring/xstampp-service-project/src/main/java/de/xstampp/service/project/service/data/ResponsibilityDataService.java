package de.xstampp.service.project.service.data;

import de.xstampp.common.dto.mock.Filter;
import de.xstampp.common.service.SecurityService;
import de.xstampp.service.project.data.dto.ResponsibilityCreationDTO;
import de.xstampp.service.project.data.dto.ResponsibilityRequestDTO;
import de.xstampp.service.project.data.entity.*;
import de.xstampp.service.project.service.dao.iface.*;
import de.xstampp.service.project.service.data.control_structure.ControllerDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This service is used to retrieve and manipulate Data related to Responsibilities distributed over different tables.
 */
@Service
@Transactional
public class ResponsibilityDataService {

    //Data Access Objects the Service uses to retrieve Data from Database
	@Autowired
	ILastIdDAO lastId;
	
	@Autowired
	IResponsibilityDAO responsibilityData;

	@Autowired
	SystemConstraintDataService systemConstraintDataService;

	@Autowired
	ControllerDataService controllerDataService;

	@Autowired
	IResponsibilitySystemConstraintLinkDAO responsibilitySystemConstraintLinkDAO;
	
	@Autowired
	SecurityService securityService;

	@Autowired
	IUserDAO userDAO;

	/**
	 * Creates a new Responsibility and saves it to the Database.
	 * @param projectId Project the Responsibility should belong to
	 * @param responsibilityDTO Data for the new Responsibility
	 * @return Newly created Responsibility with received data
	 */
	public Responsibility createResponsibility(UUID projectId, ResponsibilityCreationDTO responsibilityDTO) {
		ProjectDependentKey key = new ProjectDependentKey(projectId, lastId.getNewIdForEntity(projectId, Responsibility.class));
		Responsibility resp = new Responsibility();
		resp.setId(key);
		resp.setName(responsibilityDTO.getName());
		resp.setDescription(responsibilityDTO.getDescription());
		resp.setState(responsibilityDTO.getState());
		
		if (responsibilityDTO.getControllerId() != null) {
			resp.setControllerId(responsibilityDTO.getControllerId());
			resp.setControllerProjectId(projectId);
		}

		resp.setLastEditNow(securityService.getContext().getUserId());
		userDAO.makePersistent(new User(securityService.getContext().getUserId(), securityService.getContext().getDisplayName()));

		boolean creationSuccessful = responsibilityData.makePersistent(resp).equals(resp);

		if (creationSuccessful) {
			// Create associated System Constraint links
			for (Integer systemConstraintId : responsibilityDTO.getSystemConstraintIds()) {
				this.createResponsibilitySystemConstraintLink(projectId, resp.getId().getId(), systemConstraintId);
			}
		}

		return creationSuccessful ? resp : null;
	}

	/**
	 * Makes changes to an existing Responsibility
	 * @param projectId project Responsibility belongs to
	 * @param responsibilityId Responsibility to be changed
	 * @param responsibilityDTO changes to Responsibility
	 * @return updated Responsibility
	 */
	public Responsibility alterResponsibility (UUID projectId, int responsibilityId, ResponsibilityRequestDTO responsibilityDTO) {
		ProjectDependentKey key = new ProjectDependentKey(projectId, responsibilityId);
		Responsibility resp = responsibilityData.findById(key, false);
		
		if (resp != null) {
			resp.setId(key);
			resp.setName(responsibilityDTO.getName());
			resp.setDescription(responsibilityDTO.getDescription());
			resp.setState(responsibilityDTO.getState());
			
			if (responsibilityDTO.getControllerId() != null) {
				resp.setControllerId(responsibilityDTO.getControllerId()); 
				resp.setControllerProjectId(projectId);
			} else {
				resp.setControllerId(null); 
				resp.setControllerProjectId(null);
			}

			resp.setLastEditNow(securityService.getContext().getUserId());
			userDAO.makePersistent(new User(securityService.getContext().getUserId(), securityService.getContext().getDisplayName()));
			
			resp.setLockExpirationTime(Timestamp.from(Instant.now()));

			return responsibilityData.makePersistent(resp);
		}

		return null;
	}

	/**
	 * Deletes a Responsibility from the Database
	 * @param projectId project the Responsibility belongs to
	 * @param responsibilityId Responsibility
	 * @return success of deletion (true/false)
	 */
	public boolean deleteResponsibility (UUID projectId, int responsibilityId) {
		ProjectDependentKey key = new ProjectDependentKey(projectId, responsibilityId);
		Responsibility resp = responsibilityData.findById(key, false);
		
		if (resp != null) {
			List<SystemConstraint> systemConstraints = this.systemConstraintDataService.getLinkedSystemConstraintByResponsibility(projectId, responsibilityId, null, null);
			systemConstraints.forEach(systemConstraint -> this.deleteResponsibilitySystemConstraintLink(projectId, responsibilityId, systemConstraint.getId().getId()));
			responsibilityData.makeTransient(resp);
			return true;
		}
		
		return false;
	}

	/**
	 * Retrieves a Responsibility from the Database for a given Responsibility ID.
	 * @param projectId project Responsibility belongs to
	 * @param responsibilityId ID of Responsibility
	 * @return Responsibility corresponding to received ID
	 */
	public Responsibility getResponsibilityById (UUID projectId, int responsibilityId) {
		ProjectDependentKey key = new ProjectDependentKey(projectId, responsibilityId);
		if (projectId != null) {			
			return responsibilityData.findById(key, false);
		} else {
			return null;
		}
	}

	/**
	 * Retrieves all Responsibilities that are connected to the given System Constraint and the given Controller.
	 * Both criteria can get ignored separately. If both get ignored, all Responsibilities in that project get
	 * listed.
	 *
	 * @param projectId          The uuid of the project, the responsibilities are in
	 * @param systemConstraintId The id of the System Constraint, all resulting Responsibilities should be
	 *                           connected to. This value can be null, in which case Responsibilities get ignored.
	 * @param controllerId       The id of the Controller, all resulting Responsibilities should be connected to. This
	 *                           value can be null, in which case Controllers get ignored.
	 * @return The resulting list of Responsibilities that fit all criteria.
	 */
	public List<Responsibility> getResponsibilitiesBySystemConstraintAndController(UUID projectId,
																				   Integer systemConstraintId,
																				   Integer controllerId) {
		List<Responsibility> responsibilities;

		// Retrieve and filter by controller
		if (controllerId == null) {
			Map<String, SortOrder> sortOrder = Map.of(Responsibility.EntityAttributes.ID, SortOrder.ASC);
			responsibilities = responsibilityData.findFromTo(0, Integer.MAX_VALUE, sortOrder, projectId);
		} else {
			responsibilities = responsibilityData.getListByControllerId(projectId, controllerId);
		}

		// Filter by system constraint
		if (systemConstraintId != null) {
			List<ResponsibilitySystemConstraintLink> links = responsibilitySystemConstraintLinkDAO
					.getLinksBySystemConstraintId(projectId, systemConstraintId);

			ArrayList<Responsibility> filteredResponsibilities = new ArrayList<>();
			for (Responsibility responsibility : responsibilities) {
				for (ResponsibilitySystemConstraintLink link : links) {
					if (link.getResponsibilityId() == responsibility.getId().getId()) {
						filteredResponsibilities.add(responsibility);
						break;
					}
				}
			}
			responsibilities = filteredResponsibilities;
		}

		return responsibilities;
	}

	/**
	 * Counts all Responsibilities that are linked to the given System Constraint and the given Controller.
	 * Both criteria can get ignored separately. If both get ignored, the total number of all Responsibilities in that
	 * project gets calculated
	 * 
	 * @param projectId The uuid of the project, the responsibilities are in
	 * @param systemConstraintId The id of the System Constraint, all counting Responsibilities should be 
	 * 							 connected to. This value can be null, in which case Responsibilities get ignored.
	 * @param controllerId The id of the Controller, all resulting Responsibilities should be connected to. This 
	 * 					   value can be null, in which case Controllers get ignored.
	 * @return The number of Responsibilities that fit all criteria.
	 */
	public Integer getResponsibilityCountBySystemConstraintAndController(UUID projectId, Integer systemConstraintId,
																		 Integer controllerId) {
		return getResponsibilitiesBySystemConstraintAndController(projectId, systemConstraintId, controllerId).size();
	}

	// TODO: Investigate usage of parameters, they might be unnecessary @Rico
	/**
	 * Retrieves all Responsibilities that fit the filtering criteria from the Database.
	 * @param projectId project the data should be relate to
	 * @param filter filter applied to search query
	 * @param orderBy specifies how list should be sorted
	 * @param orderDirection specifies sorting order
	 * @param amount specifies the maximum size the returned list is allowed to be
	 * @param from specifies starting point of search
	 * @return All Responsibilities that fit the filtering criteria.
	 */
	public List<Responsibility> getAllResponsibilities (UUID projectId, Filter filter, String orderBy, String orderDirection,
			Integer amount, Integer from) {
		Map<String, SortOrder> sortOrder = Map.of(Responsibility.EntityAttributes.ID, SortOrder.ASC);
		return responsibilityData.findFromTo(from, amount, sortOrder, projectId);
	}

	/**
	 * Retrieves all Responsibilities linked to a given Controller
	 * @param projectId project Responsibility and Controller belong to
	 * @param controllerId ID of the Controller
	 * @return List of all Responsibilities Linked to the Controller
	 */
	public List<Responsibility> getListByControllerId (UUID projectId, int controllerId) {
		return responsibilityData.getListByControllerId(projectId, controllerId);
	}

	/**
	 * Creates a new Link between Responsibility and System constraint
	 * @param projectId project System constraint and Responsibility belong to
	 * @param responsibilityId ID of Responsibility
	 * @param systemConstraintId ID of System constraint
	 * @return success of creation (true/false)
	 */
	public boolean createResponsibilitySystemConstraintLink (UUID projectId, int responsibilityId, int systemConstraintId){
		if (responsibilitySystemConstraintLinkDAO.getLink(projectId, responsibilityId, systemConstraintId) == null) {
			return responsibilitySystemConstraintLinkDAO.addLink(projectId, responsibilityId, systemConstraintId);
		} else {
			return false;
		}
	}

	/**
	 * Severs a new Link between Responsibility and System constraint
	 * @param projectId project System constraint and Responsibility belong to
	 * @param responsibilityId ID of Responsibility
	 * @param systemConstraintId ID of System constraint
	 * @return success of creation (true/false)
	 */
	public boolean deleteResponsibilitySystemConstraintLink (UUID projectId, int responsibilityId, int systemConstraintId){
		return responsibilitySystemConstraintLinkDAO.deleteLink(projectId, responsibilityId, systemConstraintId);
	}

	/**
	 * Retrieves all Responsibilities linked to a given System constraint from the Database.
	 * @param projectId project System constraint and Responsibilities belong to
	 * @param systemConstraintId ID of System constraint
	 * @return List of all Responsibilities linked to the given System constraint
	 */
	public List<Responsibility> getResponsibilitiesBySystemConstraint (UUID projectId, int systemConstraintId){
		List<ResponsibilitySystemConstraintLink> links;
		List<Integer> responsibilityIds = new ArrayList<Integer>();

			links = responsibilitySystemConstraintLinkDAO.getLinksBySystemConstraintId(projectId, systemConstraintId);

		//extracts all Responsibility Id's
		for (ResponsibilitySystemConstraintLink link: links) {
			responsibilityIds.add(link.getResponsibilityId());
		}

		return responsibilityData.getListByResponsibilityIds(projectId, responsibilityIds);
	}
}
