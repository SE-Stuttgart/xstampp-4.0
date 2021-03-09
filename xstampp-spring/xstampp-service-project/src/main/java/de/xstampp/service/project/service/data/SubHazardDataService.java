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
import de.xstampp.common.dto.project.PageRequestDTO;
import de.xstampp.common.service.SecurityService;
import de.xstampp.service.project.data.dto.SubHazardRequestDTO;
import de.xstampp.service.project.data.entity.EntityDependentKey;
import de.xstampp.service.project.data.entity.SubHazard;
import de.xstampp.service.project.data.entity.UnsafeControlActionSubHazardLink;

@Service
@Transactional
public class SubHazardDataService {

	@Autowired
	ISubHazardDAO subHazardDAO;
	
	@Autowired
	ILastIdDAO lastIdDAO;

	@Autowired
	SecurityService security;
	
	@Autowired 
	IUnsafeControlActionSubHazardLinkDAO ucaSubHazardLinkDAO;
	
	@Autowired
	IHazardDAO hazardDAO;

	@Autowired
	IUserDAO userDAO;

	public SubHazard createSubHazard(SubHazardRequestDTO request, UUID projectId, int hazardId) {
		int subHazardId = lastIdDAO.getNewIdForSubHazard(projectId, hazardId);
		SubHazard subHazard = new SubHazard(new EntityDependentKey(projectId, hazardId, subHazardId));
		subHazard.setName(request.getName());
		subHazard.setDescription(request.getDescription());
		subHazard.setLastEditNow(security.getContext().getUserId());
		userDAO.makePersistent(new User(security.getContext().getUserId(), security.getContext().getDisplayName()));
		subHazard.setState(request.getState());
		SubHazard result = subHazardDAO.makePersistent(subHazard);
		return result;
	}
	
	public SubHazard alterSubHazard(SubHazardRequestDTO request, UUID projectId, int hazardId, int subHazardId) {
		SubHazard subHazard = new SubHazard(new EntityDependentKey(projectId, hazardId, subHazardId));
		subHazard.setName(request.getName());
		subHazard.setDescription(request.getDescription());
		subHazard.setLastEditNow(security.getContext().getUserId());
		userDAO.makePersistent(new User(security.getContext().getUserId(), security.getContext().getDisplayName()));
		subHazard.setState(request.getState());

		subHazard.setLockExpirationTime(Timestamp.from(Instant.now()));
		
		SubHazard result = subHazardDAO.updateExisting(subHazard);
		return result;
	}
	
	public boolean deleteSubHazard(int subHazardId, int hazardId, UUID projectId) {
		EntityDependentKey key = new EntityDependentKey(projectId,hazardId, subHazardId);
		SubHazard subHazard = subHazardDAO.findById(key, false);
		if (subHazard != null) {
			subHazardDAO.makeTransient(subHazard);
			return true;
		} else {
			return false;
		}
	}
	
	public SubHazard getSubHazardById(int subHazardId, int hazardId, UUID projectId) {
		EntityDependentKey key = new EntityDependentKey(projectId, hazardId,subHazardId);
		return subHazardDAO.findById(key,false);
	}
	
	public List<SubHazard> getAllSubHazardsByHazadId(UUID projectId, int hazardId, Filter filter, String orderBy, SortOrder orderDirection,
			Integer amount, Integer from) {
		Map<String, SortOrder> options = Map.of(orderBy, orderDirection);
		return subHazardDAO.findFromTo(from, amount, options, projectId, hazardId);
	}

	public List<SubHazard> getSubHazardsByUnsafeControlActionId(PageRequestDTO request, UUID projectId, int controlActionId, int ucaId) {
		
		
		List<UnsafeControlActionSubHazardLink> links = ucaSubHazardLinkDAO.getLinksByUnsafeControlActionId(projectId, controlActionId, ucaId);
		
		if(links.size() == 0) {
			return null;
		}
		
		List<EntityDependentKey> subHazardPKeys = new ArrayList<>();
		
		for(UnsafeControlActionSubHazardLink link : links) {
			subHazardPKeys.add(new EntityDependentKey(projectId, link.getHazardId(), link.getSubHazardId()));
		}
		
		List<SubHazard> subHazards = subHazardDAO.findSubHazardsByIds(subHazardPKeys, request.getAmount(), request.getFrom());
		
		if (subHazards.size() > 0) {	
			return subHazards;
		} else {
			return null;
		}
	}
	
}
