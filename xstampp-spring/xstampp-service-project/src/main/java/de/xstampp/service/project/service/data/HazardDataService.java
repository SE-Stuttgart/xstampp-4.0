package de.xstampp.service.project.service.data;

import de.xstampp.common.dto.mock.Filter;
import de.xstampp.common.dto.project.PageRequestDTO;
import de.xstampp.common.service.SecurityService;
import de.xstampp.service.project.data.dto.HazardRequestDTO;
import de.xstampp.service.project.data.entity.*;
import de.xstampp.service.project.service.dao.iface.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class HazardDataService {
	@Autowired
	IHazardDAO hazardDAO;

	@Autowired
	IHazardLossLinkDAO hazardLossLinkDAO;
	
	@Autowired
	IUnsafeControlActionHazardLinkDAO ucaHazardLinkDAO;
	
	@Autowired
	IHazardSystemConstraintLinkDAO hazardSystemConstraintLinkDAO;

	@Autowired
	ILastIdDAO lastIdDAO;

	@Autowired
	SecurityService security;

	@Autowired
	IUserDAO userDAO;

	public Hazard createHazard(HazardRequestDTO request, UUID projectId) {
		int hazardId = lastIdDAO.getNewIdForEntity(projectId, Hazard.class);
		Hazard hazard = new Hazard(new ProjectDependentKey(projectId, hazardId));
		hazard.setName(request.getName());
		hazard.setDescription(request.getDescription());
		hazard.setLastEditNow(security.getContext().getUserId());
		userDAO.makePersistent(new User(security.getContext().getUserId(), security.getContext().getDisplayName()));
		hazard.setState(request.getState());

		Hazard result = hazardDAO.makePersistent(hazard);
		return result;
	}

	public Hazard alterHazard(HazardRequestDTO request, UUID projectId, int hazardId) {
		Hazard hazard = new Hazard(new ProjectDependentKey(projectId, hazardId));
		hazard.setName(request.getName());
		hazard.setDescription(request.getDescription());
		hazard.setLastEditNow(security.getContext().getUserId());
		userDAO.makePersistent(new User(security.getContext().getUserId(), security.getContext().getDisplayName()));
		hazard.setState(request.getState());
		hazard.setLockExpirationTime(Timestamp.from(Instant.now()));
		Hazard result = hazardDAO.updateExisting(hazard);
		return result;
	}

	public boolean deleteHazard(int hazardId, UUID projectId) {
		ProjectDependentKey key = new ProjectDependentKey(projectId, hazardId);
		Hazard hazard = hazardDAO.findById(key, false);
		if (hazard != null) {
			hazardDAO.makeTransient(hazard);
			return true;
		}
		return false;
	}

	public Hazard getHazardById(int hazardId, UUID projectId) {
		ProjectDependentKey key = new ProjectDependentKey(projectId, hazardId);
		return hazardDAO.findById(key, false);
	}

	public List<Hazard> getAllHazards(UUID projectId, Filter filter, String orderBy, SortOrder orderDirection,
			int amount, int from) {
		Map<String, SortOrder> options = Map.of(orderBy, orderDirection);
		return hazardDAO.findFromTo(from, amount, options, projectId);
	}

	public List<Hazard> getHazardsByLossId(UUID projectId, int lossId, Integer amount, Integer from) {
		List<Integer> hazardIds = new ArrayList<>();
		List<HazardLossLink> links;
		if (amount != null && from != null) {			
			links = hazardLossLinkDAO.getLinksByLossId(projectId, lossId, amount.intValue(), from.intValue());
		} else {
			links = hazardLossLinkDAO.getLinksByLossId(projectId, lossId, 1000, 0);
		}
		
		for (HazardLossLink link: links) {
			hazardIds.add(link.getHazardId());
		}
		
		return hazardDAO.findHazardsByIds(projectId, hazardIds);
	}

	public boolean createHazardLossLink(UUID projectId, int hazardId, int lossId) {
		return hazardLossLinkDAO.addLink(projectId, hazardId, lossId);
	}

	public boolean deleteHazardLossLink(UUID projectId, int hazardId, int lossId) {
		return hazardLossLinkDAO.deleteLink(projectId, hazardId, lossId);
	}
	
	public boolean createHazardSystemConstraintLink (UUID projectId, int hazardId, int systemConstraintId) {
		if(hazardSystemConstraintLinkDAO.getLink(projectId, hazardId, systemConstraintId) == null){
			return hazardSystemConstraintLinkDAO.addLink(projectId, hazardId, systemConstraintId);
		} else {
			return false;
		}
	}
	
	public boolean deleteHazardSystemConstraintLink (UUID projectId, int hazardId, int systemConstraintId) {
		return hazardSystemConstraintLinkDAO.deleteLink(projectId, hazardId, systemConstraintId);
	}
	
	public List<Hazard> getHazardsBySystemConstraintId(UUID projectId, int systemConstraintId, Integer amount, Integer from){
		List<HazardSystemConstraintLink> links;
		List<Integer> hazardIds = new ArrayList<>();
		
		if (amount != null && from != null) {			
			links = hazardSystemConstraintLinkDAO.getLinksBySystemConstraintId(projectId, systemConstraintId, amount.intValue(), from.intValue());
		} else {
			links = hazardSystemConstraintLinkDAO.getLinksBySystemConstraintId(projectId, systemConstraintId, 1000, 0);
		}
		
		for (HazardSystemConstraintLink link: links) {
			hazardIds.add(link.getHazardId());
		}
		
		return hazardDAO.findHazardsByIds(projectId, hazardIds);		
	}

	public List<Hazard> getHazardsByUnsafeControlActionId(PageRequestDTO request, UUID projectId, int controlActionId, int ucaId) {
		
		List<UnsafeControlActionHazardLink> links = ucaHazardLinkDAO.getLinksByUnsafeControlActionId(projectId, controlActionId, ucaId);
		
		List<Integer> hazardKeys = new ArrayList<>();
		
		for(UnsafeControlActionHazardLink link : links) {
			hazardKeys.add(link.getHazardId());
		}
		
		return hazardDAO.findHazardsByIds(projectId, hazardKeys);
	}
}
