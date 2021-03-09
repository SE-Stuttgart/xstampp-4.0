package de.xstampp.service.project.service.data;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.xstampp.service.project.data.entity.*;
import de.xstampp.service.project.service.dao.iface.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.xstampp.common.dto.mock.Filter;
import de.xstampp.common.service.SecurityService;
import de.xstampp.service.project.data.dto.SystemConstraintRequestDTO;
import de.xstampp.service.project.data.entity.HazardSystemConstraintLink;
import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.ResponsibilitySystemConstraintLink;
import de.xstampp.service.project.data.entity.SystemConstraint;

/**
 * This service is used to retrieve and manipulate Data related to System Constraints distributed over different tables.
 */
@Service
@Transactional
public class SystemConstraintDataService {

	//Data Access Objects the Service uses to retrieve Data from Database
	@Autowired
	ISystemConstraintDAO systemConstraintDAO;
	
	@Autowired
	IHazardDAO hazardDAO;
	
	@Autowired
	IHazardSystemConstraintLinkDAO hazardSystemConstraintLinkDAO;

	@Autowired
	IResponsibilitySystemConstraintLinkDAO responsibilitySystemConstraintLinkDAO;

	@Autowired
	ILastIdDAO lastIdDAO;

	@Autowired
	SecurityService security;

	@Autowired
	IUserDAO userDAO;


	/**
	 * Creates a new System constraint and saves it to Database
	 *
	 * @param request 	Contains all received data required to create new System constraint
	 * @param projectId project ID System constraint should belong to
	 * @return 	saved System constraint
	 */
	public SystemConstraint createSystemConstraint(SystemConstraintRequestDTO request, UUID projectId) {
		int constId = lastIdDAO.getNewIdForEntity(projectId, SystemConstraint.class);
		SystemConstraint constraint = new SystemConstraint(new ProjectDependentKey(projectId, constId));
		constraint.setName(request.getName());
		constraint.setDescription(request.getDescription());
		constraint.setLastEditNow(security.getContext().getUserId());
		userDAO.makePersistent(new User(security.getContext().getUserId(), security.getContext().getDisplayName()));
		constraint.setState(request.getState());

		SystemConstraint result = systemConstraintDAO.makePersistent(constraint);
		return result;
		
	}

	/**
	 * Deletes a System constraint from Database
	 * @param projectId project Systemconstraint belongs to
	 * @param constId ID of System constraint
	 * @return true if deletion was successful. Otherwise false
	 */
	public boolean deleteSystemConstraint(UUID projectId, int constId) {
		ProjectDependentKey key = new ProjectDependentKey(projectId, constId);
		SystemConstraint constraint = systemConstraintDAO.findById(key, false);
		if (constraint != null) {
			systemConstraintDAO.makeTransient(constraint);
			return true;
		}
		return false;
	}

	/**
	 * makes Changes to existing System constraint
	 * @param request contains all the data for an existing System constraint including changes made
	 * @param projectId project System constraint belongs to
	 * @param constId System constraint to be changed
	 * @return updated System constraint
	 */
	public SystemConstraint alterSystemConstraint(SystemConstraintRequestDTO request, UUID projectId, int constId) {
		SystemConstraint constraint = new SystemConstraint(new ProjectDependentKey(projectId, constId));
		constraint.setName(request.getName());
		constraint.setDescription(request.getDescription());
		constraint.setLastEditNow(security.getContext().getUserId());
		userDAO.makePersistent(new User(security.getContext().getUserId(), security.getContext().getDisplayName()));
		constraint.setState(request.getState());
		constraint.setLockExpirationTime(Timestamp.from(Instant.now()));
		
		SystemConstraint result = systemConstraintDAO.updateExisting(constraint);
		return result;
	}

	/**
	 * returns a System constraint for a given System constraint ID
	 * @param constId System constraint ID
	 * @param projectId project System constraint belongs to
	 * @return System constraint
	 */
	public SystemConstraint getSystemConstraintById(int constId, UUID projectId) {
		ProjectDependentKey key = new ProjectDependentKey(projectId, constId);
		return systemConstraintDAO.findById(key, false);
	}

	/**
	 * retrieves all System constraints that fit the filtering criteria from the Database.
	 * @param projectId project the data should be relate to
	 * @param filter filter applied to search query
	 * @param orderBy specifies how list should be sorted
	 * @param orderDirection specifies sorting order
	 * @param amount specifies the maximum size the returned list is allowed to be
	 * @param from specifies starting point of search
	 * @return list of System constraints
	 */
	public List<SystemConstraint> getAllSystemConstraints (UUID projectId, Filter filter, String orderBy, String orderDirection, Integer amount, Integer from) {
		
		Map<String, SortOrder> map = new LinkedHashMap<>();
		if (orderBy != null && orderDirection != null) {
			map.put(orderBy, SortOrder.ASC);
		}
		
		if (amount != null && from != null) {
			return systemConstraintDAO.findFromTo(from.intValue(), amount.intValue(), map, projectId);
		} else {
			return systemConstraintDAO.findAll(projectId);
		}
	}

	/**
	 * retrieves all System constraints for a given Hazard ID from the Database.
	 * @param projectId project the retrieved data should relate to
	 * @param hazardId Hazard, System constraints are linked
	 * @param amount specifies the maximum size the returned list is allowed to be
	 * @param from specifies starting point of search
	 * @return list of all System constraints linked to the given Hazard
	 */
	public List<SystemConstraint> getAllSystemConstraintsByHazardId (UUID projectId, int hazardId, Integer amount, Integer from) {
		List<Integer> constIds = new ArrayList<>();
		List<HazardSystemConstraintLink> links;
		
		if (amount != null && from != null) {			
			links = hazardSystemConstraintLinkDAO.getLinksByHazardId(projectId, hazardId, amount.intValue(), from.intValue());
		} else {
			links = hazardSystemConstraintLinkDAO.getLinksByHazardId(projectId, hazardId, 1000, 0);
		}
		
		//extract list of lossIds
		for (HazardSystemConstraintLink link: links) {
			constIds.add(link.getSystemConstraintId());
		}
		
		return systemConstraintDAO.findSystemConstraintsByIds(projectId, constIds);
	}

	/**
	 * retrieves all System constraints for a given Responsibility ID from the Database.
	 * @param projectId project the retrieved data should relate to
	 * @param responsibilityId Responsibility, System constraints are linked
	 * @param amount specifies the maximum size the returned list is allowed to be
	 * @param from specifies starting point of search
	 * @return list of all System constraints linked to the given Responsibility
	 */
	public List<SystemConstraint> getLinkedSystemConstraintByResponsibility (UUID projectId, int responsibilityId, Integer amount, Integer from){

		List<ResponsibilitySystemConstraintLink> links;
		List<Integer> systemConstraintIds = new ArrayList<Integer>();

		if (amount !=null && from !=null){
			links = responsibilitySystemConstraintLinkDAO.getLinksByResponsibilityId(projectId, responsibilityId, amount, from);
		}else {
			links = responsibilitySystemConstraintLinkDAO.getLinksByResponsibilityId(projectId, responsibilityId, 1000, 0);
		}

		//extracts all SystemConstraint Id's
		for (ResponsibilitySystemConstraintLink link: links) {
			systemConstraintIds.add(link.getSystemConstraintId());
		}

		return systemConstraintDAO.findSystemConstraintsByIds(projectId, systemConstraintIds);
	}
}
