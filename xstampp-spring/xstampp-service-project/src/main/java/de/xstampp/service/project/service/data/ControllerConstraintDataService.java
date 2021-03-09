package de.xstampp.service.project.service.data;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import de.xstampp.service.project.data.entity.User;
import de.xstampp.service.project.service.dao.iface.IUserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.xstampp.common.service.SecurityService;
import de.xstampp.service.project.data.dto.ControllerConstraintRequestDTO;
import de.xstampp.service.project.data.entity.ControllerConstraint;
import de.xstampp.service.project.data.entity.EntityDependentKey;
import de.xstampp.service.project.service.dao.iface.IControllerConstraintDAO;

@Service
@Transactional
public class ControllerConstraintDataService {
	@Autowired
	IControllerConstraintDAO controllerConstraintDAO;
	
	@Autowired 
	SecurityService security;

	@Autowired
	IUserDAO userDAO;

	public ControllerConstraint createControllerConstraint(ControllerConstraintRequestDTO request, UUID projectId,
			int controlActionId, int ucaId) {

		ControllerConstraint entity = new ControllerConstraint();
		entity.setId(new EntityDependentKey(projectId, controlActionId, ucaId));
		entity.setDescription(request.getDescription());
		entity.setName(request.getName());
		entity.setLastEditNow(security.getContext().getUserId());
		userDAO.makePersistent(new User(security.getContext().getUserId(), security.getContext().getDisplayName()));
		entity.setState(request.getState());
		controllerConstraintDAO.makePersistent(entity);
		return entity;
	}

	public boolean deleteControllerConstraint(UUID projectId, int controlActionId, int ucaId) {

		ControllerConstraint entity = controllerConstraintDAO
				.findById(new EntityDependentKey(projectId, controlActionId, ucaId), false);
		if (entity != null) {
			controllerConstraintDAO.makeTransient(entity);
			return true;
		} else {
			return false;
		}
	}

	public ControllerConstraint alterControllerConstraint(ControllerConstraintRequestDTO request, UUID projectId,
			int controlActionId, int ucaId) {

		ControllerConstraint entity = controllerConstraintDAO
				.findById(new EntityDependentKey(projectId, controlActionId, ucaId), false);
		entity.setDescription(request.getDescription());
		entity.setName(request.getName());
		entity.setLastEditNow(security.getContext().getUserId());
		userDAO.makePersistent(new User(security.getContext().getUserId(), security.getContext().getDisplayName()));
		entity.setState(request.getState());
		entity.setLockExpirationTime(Timestamp.from(Instant.now()));
		controllerConstraintDAO.updateExisting(entity);

		return entity;
	}

	public ControllerConstraint getControllerConstraint(UUID projectId, int controlActionId, int ucaId) {
		
		ControllerConstraint result = controllerConstraintDAO.findById(new EntityDependentKey(projectId, controlActionId, ucaId), false);
		
		if(result != null) {
			return result;
		} else {
			return null;
		}
	}
}
