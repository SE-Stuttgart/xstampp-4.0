package de.xstampp.service.project.service.data.control_structure;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import de.xstampp.service.project.data.entity.User;
import de.xstampp.service.project.service.dao.iface.IUserDAO;
import de.xstampp.service.project.service.data.ControlStructureDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.xstampp.common.dto.mock.Filter;
import de.xstampp.common.service.SecurityService;
import de.xstampp.service.project.data.dto.control_structure.InputRequestDTO;
import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.control_structure.Input;
import de.xstampp.service.project.service.dao.control_structure.iface.IInputDAO;
import de.xstampp.service.project.service.dao.iface.ILastIdDAO;
import de.xstampp.service.project.service.dao.iface.SortOrder;

@Service
@Transactional
public class InputDataService {

	@Autowired
	ILastIdDAO lastId;

	@Autowired
	SecurityService securityService;
	
	@Autowired
	IInputDAO inputData;

	@Autowired
	IUserDAO userDAO;

	@Autowired
	ControlStructureDataService controlStructureDataService;

	public Input createInput (UUID projectId, InputRequestDTO inputDTO) {
		ProjectDependentKey key = new ProjectDependentKey(projectId,
				lastId.getNewIdForEntity(projectId, Input.class));

		Input input = new Input();
		input.setId(key);
		input.setName(inputDTO.getName());
		input.setDescription(inputDTO.getDescription());

		if (inputDTO.getState() != null) {
			input.setState(inputDTO.getState());
		} else {
			input.setState("DOING");
		}

		input.setLastEditNow(securityService.getContext().getUserId());
		userDAO.makePersistent(new User(securityService.getContext().getUserId(), securityService.getContext().getDisplayName()));

		Input result = inputData.makePersistent(input);
		return result;
	}
	
	public Input alterInput (UUID projectId, int inputId, InputRequestDTO inputDTO) {
		Input input = inputData.findById(new ProjectDependentKey(projectId, inputId), false);
		String oldName = input.getName();
		
		if (input != null) {
			input.setName(inputDTO.getName());
			input.setDescription(inputDTO.getDescription());
			input.setState(inputDTO.getState());

			input.setLastEditNow(securityService.getContext().getUserId());
			userDAO.makePersistent(new User(securityService.getContext().getUserId(), securityService.getContext().getDisplayName()));
			
			input.setLockExpirationTime(Timestamp.from(Instant.now()));
			Input result = inputData.makePersistent(input);

			controlStructureDataService.alterArrow(projectId, inputId, "Input", oldName, result.getName());
			return result;
		}
		
		return input;
	}
	
	public boolean deleteInput (UUID projectId, int inputId) {
		Input input = inputData.findById(new ProjectDependentKey(projectId, inputId), false);
		String name = input.getName();
		
		if (input != null) {
			inputData.makeTransient(input);
			controlStructureDataService.deleteNameInArrowLabel(projectId, inputId, "Input", name);
			return true;
		}
		
		return false;
	}
	
	public Input getInputById (UUID projectId, int inputId) {
		return inputData.findById(new ProjectDependentKey(projectId, inputId), false);
	}
	
	public List<Input> getAllInputs (UUID projectId, Filter filter, Integer amount, Integer from) {
		LinkedHashMap<String, SortOrder> order = new LinkedHashMap<>();
		order.put(Input.EntityAttributes.ID, SortOrder.ASC);
		
		if (filter != null && filter.getFieldName() != null && filter.getFieldValue() != null) {			
			if (filter.getFieldName().equals("isUnlinked") && Boolean.valueOf(filter.getFieldValue())) {
				return inputData.getAllUnlinkedInputs(projectId);
			} 
		}
		
		return inputData.findFromTo(from, amount, order, projectId);
	}
	
	public List<Input> getInputByArrowId (UUID projectId, String arrowId) {
		return inputData.getByArrowId(projectId, arrowId);
	}
	
	public Input setInputArrowId (UUID projectId, int inputId, String arrowId) {
		return inputData.setArrowId(projectId, inputId, arrowId);
	}
}
