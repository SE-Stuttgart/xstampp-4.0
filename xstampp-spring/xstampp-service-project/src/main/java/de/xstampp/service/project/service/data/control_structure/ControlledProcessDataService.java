package de.xstampp.service.project.service.data.control_structure;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import de.xstampp.service.project.data.entity.User;
import de.xstampp.service.project.service.dao.iface.IUserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.xstampp.common.dto.mock.Filter;
import de.xstampp.common.service.SecurityService;
import de.xstampp.service.project.data.dto.control_structure.ControlledProcessRequestDTO;
import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.control_structure.ControlledProcess;
import de.xstampp.service.project.service.dao.control_structure.iface.IControlledProcessDAO;
import de.xstampp.service.project.service.dao.iface.ILastIdDAO;
import de.xstampp.service.project.service.dao.iface.SortOrder;
import de.xstampp.service.project.service.data.ControlStructureDataService;

@Service
@Transactional
public class ControlledProcessDataService {

	@Autowired
	ILastIdDAO lastId;

	@Autowired
	SecurityService securityService;
	
	@Autowired
	IControlledProcessDAO controlledProcessData;

	@Autowired
	IUserDAO userDAO;

	@Autowired
	ControlStructureDataService controlStructureDataService;

	public ControlledProcess createControlledProcess (UUID projectId, ControlledProcessRequestDTO cpDTO) {
		ProjectDependentKey key = new ProjectDependentKey(projectId,
				lastId.getNewIdForEntity(projectId, ControlledProcess.class));

		ControlledProcess controlledProcess = new ControlledProcess();
		controlledProcess.setId(key);
		controlledProcess.setName(cpDTO.getName());
		controlledProcess.setDescription(cpDTO.getDescription());

		if (cpDTO.getState() != null) {
			controlledProcess.setState(cpDTO.getState());
		} else {
			controlledProcess.setState("DOING");
		}

		controlledProcess.setLastEditNow(securityService.getContext().getUserId());
		userDAO.makePersistent(new User(securityService.getContext().getUserId(), securityService.getContext().getDisplayName()));

		ControlledProcess result = controlledProcessData.makePersistent(controlledProcess);
		return result;
	}
	
	public ControlledProcess alterControlledProcess (UUID projectId, int cpId, ControlledProcessRequestDTO cpDTO) {
		ControlledProcess controlledProcess = controlledProcessData.findById(new ProjectDependentKey(projectId, cpId), false);
		
		if (controlledProcess != null) {
			controlledProcess.setName(cpDTO.getName());
			controlledProcess.setDescription(cpDTO.getDescription());
			controlledProcess.setState(cpDTO.getState());

			controlledProcess.setLastEditNow(securityService.getContext().getUserId());
			userDAO.makePersistent(new User(securityService.getContext().getUserId(), securityService.getContext().getDisplayName()));
			
			controlledProcess.setLockExpirationTime(Timestamp.from(Instant.now()));
			ControlledProcess result = controlledProcessData.makePersistent(controlledProcess);

			controlStructureDataService.alterBox(projectId, cpId, "ControlledProcess", result.getName());
			return result;
		}
		return null;
	}
	
	public boolean deleteControlledProcess (UUID projectId, int cpId) {
		ControlledProcess controlledProcess = controlledProcessData.findById(new ProjectDependentKey(projectId, cpId), false);
		
		if (controlledProcess != null) {
			controlledProcessData.makeTransient(controlledProcess);
			controlStructureDataService.alterBox(projectId, cpId, "ControlledProcess", "");
			return true;
		}
		return false;
	}
	
	public ControlledProcess getControlledProcessById (UUID projectId, int cpId) {
		return controlledProcessData.findById(new ProjectDependentKey(projectId, cpId), false);
	}
	
	public List<ControlledProcess> getAllControlledProcess (UUID projectId, Filter filter, Integer amount, Integer from) {
		LinkedHashMap<String, SortOrder> order = new LinkedHashMap<>();
		order.put(ControlledProcess.EntityAttributes.ID, SortOrder.ASC);
		
		if (filter != null && filter.getFieldName() != null && filter.getFieldValue() != null) {			
			if (filter.getFieldName().equals("isUnlinked") && Boolean.valueOf(filter.getFieldValue())) {
				return controlledProcessData.getAllUnlinkedControlledProcesses(projectId);
			} 
		}
		
		return controlledProcessData.findFromTo(from, amount, order, projectId);
	}
	
	public ControlledProcess getControlledProcessByBoxId(UUID projectId, String boxId) {
		return controlledProcessData.getByBoxId(projectId, boxId);
	}
	
	public ControlledProcess setControlledProcessBoxId (UUID projectId, int controlledProcessId, String boxId) {
		return controlledProcessData.setBoxId(projectId, controlledProcessId, boxId);
	}
}
