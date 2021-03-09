package de.xstampp.service.project.service.data;

import de.xstampp.common.dto.mock.Filter;
import de.xstampp.common.service.SecurityService;
import de.xstampp.service.project.data.dto.SubSystemConstraintRequestDTO;
import de.xstampp.service.project.data.entity.EntityDependentKey;
import de.xstampp.service.project.data.entity.User;
import de.xstampp.service.project.service.dao.iface.IUserDAO;
import de.xstampp.service.project.data.entity.SubSystemConstraint;
import de.xstampp.service.project.service.dao.iface.ILastIdDAO;
import de.xstampp.service.project.service.dao.iface.ISubSystemConstraintDAO;
import de.xstampp.service.project.service.dao.iface.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class SubSystemConstraintDataService {

	@Autowired
	ISubSystemConstraintDAO subSystemConstraintDAO;
	
	@Autowired
	SecurityService securityService;

	@Autowired
	ILastIdDAO lastIdDAO;

	@Autowired
	IUserDAO userDAO;


	public SubSystemConstraint createSubSystemConstraint(UUID projectId, int constId, SubSystemConstraintRequestDTO constRequestDTO) {
		int subConstId = lastIdDAO.getNewIdForSubSystemConstraint(projectId, constId);
		SubSystemConstraint constraint = new SubSystemConstraint(new EntityDependentKey(projectId, constId, subConstId));
		constraint.setDescription(constRequestDTO.getDescription());
		constraint.setHazardId(constRequestDTO.getHazardId());
		constraint.setName(constRequestDTO.getName());
		constraint.setState(constRequestDTO.getState());
		
		if (constRequestDTO.getHazardId() != null && constRequestDTO.getSubHazardId() != null) {
			constraint.setSubHazardId(constRequestDTO.getSubHazardId());
			constraint.setHazardId(constRequestDTO.getHazardId());
			constraint.setSubHazardProjectId(projectId);
		}

		constraint.setLastEditNow(securityService.getContext().getUserId());
		userDAO.makePersistent(new User(securityService.getContext().getUserId(), securityService.getContext().getDisplayName()));
		
		SubSystemConstraint result = subSystemConstraintDAO.makePersistent(constraint);
		return result;
		
	}

	public SubSystemConstraint alterSubSystemConstraint(UUID projectId, int constId, int subConstId, SubSystemConstraintRequestDTO constDTO) {
		EntityDependentKey key = new EntityDependentKey(projectId, constId, subConstId);
		SubSystemConstraint constraint = subSystemConstraintDAO.findById(key, false);
		constraint.setName(constDTO.getName());
		constraint.setDescription(constDTO.getDescription());
		constraint.setState(constDTO.getState());
		
		if (constDTO.getHazardId() != null && constDTO.getSubHazardId() != null) {
			constraint.setSubHazardId(constDTO.getSubHazardId());
			constraint.setHazardId(constDTO.getHazardId());
			constraint.setSubHazardProjectId(projectId);
		} else {
			constraint.setSubHazardId(null);
			constraint.setHazardId(null);
			constraint.setSubHazardProjectId(null);
		}

		constraint.setLastEditNow(securityService.getContext().getUserId());
		userDAO.makePersistent(new User(securityService.getContext().getUserId(), securityService.getContext().getDisplayName()));
		
		constraint.setLockExpirationTime(Timestamp.from(Instant.now()));
		
		SubSystemConstraint result = subSystemConstraintDAO.updateExisting(constraint);
		return result;
	}

	public boolean deleteSubSystemConstraint(UUID projectId, int constId, int subConstId) {
		EntityDependentKey key = new EntityDependentKey(projectId, constId, subConstId);
		SubSystemConstraint constraint = subSystemConstraintDAO.findById(key, false);
		
		if (constraint != null) {			
			subSystemConstraintDAO.makeTransient(constraint);
			return true;
		}
		return false;
	}

	public SubSystemConstraint getSubSystemConstraintById(UUID projectId, int constId, int subConstId) {
		EntityDependentKey key = new EntityDependentKey(projectId, constId, subConstId);
		return subSystemConstraintDAO.findById(key, false);
	}

	public List<SubSystemConstraint> getAllSubSystemConstraints(UUID projectId, int constId, Filter filter,
			String orderBy, String orderDirection, Integer amount, Integer from) {

		Map<String, SortOrder> map = new LinkedHashMap<>();
		if (orderBy != null && orderDirection != null) {
			map.put(orderBy, SortOrder.ASC);
		}
		
		if (amount != null && from != null) {
			return subSystemConstraintDAO.findFromTo(from.intValue(), amount.intValue(), map, projectId, constId);
		} else {
			return subSystemConstraintDAO.findFromTo(0, 100, map, projectId, constId);
		}
	}

  public boolean createSubHazardSubSystemConstraintLink (UUID projectId, int hazardId, int subHazardId, int systemConstraintId, int subSystemConstraintId) {
	  SubSystemConstraint subConstraint = subSystemConstraintDAO.findById(new EntityDependentKey(projectId, systemConstraintId, subSystemConstraintId), false);
	  
	  if (subConstraint != null) {
		  
		  subConstraint.setHazardId(hazardId);
		  subConstraint.setSubHazardId(subHazardId);
		  
		  subSystemConstraintDAO.makePersistent(subConstraint);
		  return true;
	  }
		  return false;
	  
  }
  
  public boolean deleteSubHazardSubSystemConstraintLink (UUID projectId, int hazardId, int subHazardId, int systemConstraintId, int subSystemConstraintId) {
	  SubSystemConstraint subConstraint = subSystemConstraintDAO.findById(new EntityDependentKey(projectId, systemConstraintId, subSystemConstraintId), false);
	  
	  if (subConstraint != null) {
		  subConstraint.setHazardId(null);
		  subConstraint.setSubHazardId(null);
		  
		  subSystemConstraintDAO.makePersistent(subConstraint);
		  return true;
	  }
		  return false;
  }
  
  public SubSystemConstraint getSubSystemConstraintBySubHazardId (UUID projectId, int hazardId, int subHazardId) {
	  return subSystemConstraintDAO.getSubSystemConstraintBySubHazardId(projectId, hazardId, subHazardId);
  }
}
