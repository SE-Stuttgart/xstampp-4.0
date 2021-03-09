package de.xstampp.service.project.service.data.control_structure;

import de.xstampp.common.dto.mock.Filter;
import de.xstampp.common.service.SecurityService;
import de.xstampp.service.project.data.entity.User;
import de.xstampp.service.project.service.dao.iface.IUserDAO;
import de.xstampp.service.project.data.dto.control_structure.ControlAlgorithmRequestDTO;
import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.ControlAlgorithm;
import de.xstampp.service.project.service.dao.control_structure.iface.IControlAlgorithmDAO;
import de.xstampp.service.project.service.dao.iface.ILastIdDAO;
import de.xstampp.service.project.service.dao.iface.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ControlAlgorithmDataService {

	@Autowired
	ILastIdDAO lastId;

	@Autowired
	SecurityService securityService;

	@Autowired
	IControlAlgorithmDAO controlAlgorithmData;

	@Autowired
	IUserDAO userDAO;

	public ControlAlgorithm createControlAlgorithm(UUID projectId, ControlAlgorithmRequestDTO controlAlgorithmDTO) {
		ProjectDependentKey key = new ProjectDependentKey(projectId,
				lastId.getNewIdForEntity(projectId, ControlAlgorithm.class));

		ControlAlgorithm controlAlgorithm = new ControlAlgorithm();
		controlAlgorithm.setId(key);
		controlAlgorithm.setName(controlAlgorithmDTO.getName());
		controlAlgorithm.setDescription(controlAlgorithmDTO.getDescription());
		controlAlgorithm.setState(controlAlgorithmDTO.getState());

		controlAlgorithm.setLastEditNow(securityService.getContext().getUserId());
		userDAO.makePersistent(new User(securityService.getContext().getUserId(), securityService.getContext().getDisplayName()));

		ControlAlgorithm result =  controlAlgorithmData.makePersistent(controlAlgorithm);
		
		return result;
	}

	public ControlAlgorithm alterControlAlgorithm(UUID projectId, int controlAlgoritmId,
			ControlAlgorithmRequestDTO controlAlgorithmDTO) {
		ControlAlgorithm controlAlgorithm = controlAlgorithmData.findById(new ProjectDependentKey(), false);

		if (controlAlgorithm != null) {
			controlAlgorithm.setName(controlAlgorithmDTO.getName());
			controlAlgorithm.setDescription(controlAlgorithmDTO.getDescription());
			controlAlgorithm.setState(controlAlgorithmDTO.getState());

			controlAlgorithm.setLastEditNow(securityService.getContext().getUserId());
			userDAO.makePersistent(new User(securityService.getContext().getUserId(), securityService.getContext().getDisplayName()));

			controlAlgorithm.setLockExpirationTime(Timestamp.from(Instant.now()));
			ControlAlgorithm result = controlAlgorithmData.makePersistent(controlAlgorithm);
			return result;
		}

		return controlAlgorithm;
	}

	public boolean deleteControlAlgorithm(UUID projectId, int controlAlgoritmId) {
		ControlAlgorithm controlAlgorithm = controlAlgorithmData.findById(new ProjectDependentKey(), false);
		if (controlAlgorithm != null) {
			controlAlgorithmData.makeTransient(controlAlgorithm);
			return true;
		}
		return false;
	}

	public ControlAlgorithm getControlAlgorithmById(UUID projectId, int controlAlgoritmId) {
		return controlAlgorithmData.findById(new ProjectDependentKey(), false);
	}

	public List<ControlAlgorithm> getAllControlAlgorithm(UUID projectId, Filter filter, Integer amount, Integer from) {
		LinkedHashMap<String, SortOrder> order = new LinkedHashMap<>();
		order.put(ControlAlgorithm.EntityAttributes.ID, SortOrder.ASC);
		if (filter != null && filter.getFieldName() != null && filter.getFieldValue() != null) {			
			if (filter.getFieldName().equals("isUnlinked") && Boolean.valueOf(filter.getFieldValue())) {
				return controlAlgorithmData.getAllUnlinkedControlAlgorithm(projectId);
			} 
		}
		return controlAlgorithmData.findFromTo(from, amount, order, projectId);
	}
	
	public ControlAlgorithm getControlAlgorithmByBoxId (UUID projectId, String boxId) {
		return controlAlgorithmData.getByBoxId(projectId, boxId);
	}
	
	public ControlAlgorithm setControlAlgorithmBoxId (UUID projectId, int controlAlgorithmId, String boxId) {
		return controlAlgorithmData.setBoxId(projectId, controlAlgorithmId, boxId);
	}
}
