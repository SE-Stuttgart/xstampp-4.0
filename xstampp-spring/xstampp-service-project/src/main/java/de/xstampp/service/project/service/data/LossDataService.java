package de.xstampp.service.project.service.data;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.xstampp.service.project.data.entity.User;
import de.xstampp.service.project.service.dao.iface.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.xstampp.common.dto.mock.Filter;
import de.xstampp.common.service.SecurityService;
import de.xstampp.service.project.data.dto.LossRequestDTO;
import de.xstampp.service.project.data.entity.HazardLossLink;
import de.xstampp.service.project.data.entity.Loss;
import de.xstampp.service.project.data.entity.ProjectDependentKey;

@Service
@Transactional
public class LossDataService {

	@Autowired
	ILossDAO lossDAO;
	
	@Autowired
	IHazardLossLinkDAO hazardLossLink;

	@Autowired
	IUserDAO userDAO;

	@Autowired
	SecurityService security;

	@Autowired
	ILastIdDAO lastIdDAO;

	/**
	 * Generates a new Loss with a new ID and the given parameters
	 * 
	 * @param request   the loss request DTO with the parameters to set
	 * @param projectId the assigned project id
	 * @return returns the new Loss
	 */
	public Loss createLoss(LossRequestDTO request, UUID projectId) {
		int lossId = lastIdDAO.getNewIdForEntity(projectId, Loss.class);
		Loss loss = new Loss(new ProjectDependentKey(projectId, lossId));
		loss.setName(request.getName());
		loss.setDescription(request.getDescription());
		loss.setLastEditNow(security.getContext().getUserId());
		userDAO.makePersistent(new User(security.getContext().getUserId(), security.getContext().getDisplayName()));
		loss.setState(request.getState());
		Loss result = lossDAO.makePersistent(loss);
		return result;
	}

	/**
	 * Alters an existing loss by its id
	 * 
	 * @param request      the new loss which contains all attributes including the
	 *                  altered values
	 * @param lossId    the id of the existing loss which should be altered
	 * @param projectId the project id of the assigned process
	 * @return returns the altered loss including all changes
	 */
	public Loss alterLoss(LossRequestDTO request, UUID projectId, int lossId) {
		Loss loss = new Loss(new ProjectDependentKey(projectId, lossId));
		loss.setName(request.getName());
		loss.setDescription(request.getDescription());
		loss.setState(request.getState());
		loss.setLockExpirationTime(Timestamp.from(Instant.now()));
		loss.setLastEditNow(security.getContext().getUserId());
		userDAO.makePersistent(new User(security.getContext().getUserId(), security.getContext().getDisplayName()));
		Loss result = lossDAO.updateExisting(loss);
		return result;
	}

	/**
	 * deletes a loss by its id
	 * 
	 * @param lossId    the loss id
	 * @param projectId the assigned project id
	 * @return returns true if the loss could be deleted successfully
	 */
	public boolean deleteLoss(int lossId, UUID projectId) {
		ProjectDependentKey key = new ProjectDependentKey(projectId, lossId);
		Loss loss = lossDAO.findById(key, false);
		if (loss != null) {
			lossDAO.makeTransient(loss);
			return true;
		}
		return false;
	}

	/**
	 * Get Loss by id
	 * 
	 * @param lossId    the loss id
	 * @param projectId the project id from a assigned project
	 * @return the loss for the given id
	 */
	public Loss getLossById(int lossId, UUID projectId) {
		ProjectDependentKey key = new ProjectDependentKey(projectId, lossId);
		return lossDAO.findById(key, false);
	}

	/**
	 * returns a list of losses paged by the given parameters
	 * 
	 * @param projectId      the assigned project id
	 * @param filter         currently not used
	 * @param orderBy        the parameter name for which the result should be
	 *                       ordered
	 * @param orderDirection the order direction (ASC or DESC)
	 * @param amount         returns only the first X results
	 * @param from           skips the first X results
	 * @return returns a list of losses reduced by the given criteria
	 */
	public List<Loss> getAllLosses(UUID projectId, Filter filter, String orderBy, String orderDirection,
			Integer amount, Integer from) {

		Map<String, SortOrder> sortOrder = Map.of(orderBy, SortOrder.ASC);
		if (amount != null && from != null) {			
			return lossDAO.findFromTo(from, amount, sortOrder, projectId);
		} else {
			return lossDAO.findFromTo(0, 1000, sortOrder, projectId);
		}
	}

	/**
	 * get list of all losses by hazard id
	 *
	 * @param projectId the assigned project id
	 * @param hazardId  the hazard id
	 * @param amount    returns only the first X results
	 * @param from      skips the first X results
	 * @return All losses that are linked with the given hazard
	 */
	public List<Loss> getAllLossesByHazardId(UUID projectId, int hazardId, Integer amount, Integer from) {
		List<Integer> lossIds = new ArrayList<>();
		List<HazardLossLink> links;
		if (amount != null && from != null) {			
			 links = hazardLossLink.getLinksByHazardId(projectId, hazardId, amount.intValue(), from.intValue());
		} else {
			 links = hazardLossLink.getLinksByHazardId(projectId, hazardId, 1000, 0);
		}
		
		
		//extract list of lossIds
		for (HazardLossLink link: links) {
			lossIds.add(link.getLossId());
		}
		
		return lossDAO.findLossesByIds(projectId, lossIds);
	}
}
