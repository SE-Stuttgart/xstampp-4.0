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
import de.xstampp.service.project.data.dto.control_structure.OutputRequestDTO;
import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.control_structure.Output;
import de.xstampp.service.project.service.dao.control_structure.iface.IOutputDAO;
import de.xstampp.service.project.service.dao.iface.ILastIdDAO;
import de.xstampp.service.project.service.dao.iface.SortOrder;
import de.xstampp.service.project.service.data.ControlStructureDataService;

@Service
@Transactional
public class OutputDataService {

	@Autowired
	ILastIdDAO lastId;

	@Autowired
	SecurityService securityService;
	
	@Autowired
	IOutputDAO outputData;

	@Autowired
	IUserDAO userDAO;

	@Autowired
	ControlStructureDataService controlStructureDataService;

	public Output createOutput (UUID projectId, OutputRequestDTO outputDTO) {
		ProjectDependentKey key = new ProjectDependentKey(projectId,
				lastId.getNewIdForEntity(projectId, Output.class));

		Output output = new Output();
		output.setId(key);
		output.setName(outputDTO.getName());
		output.setDescription(outputDTO.getDescription());

		if (outputDTO.getState() != null) {
			output.setState(outputDTO.getState());
		} else {
			output.setState("DOING");
		}

		output.setLastEditNow(securityService.getContext().getUserId());
		userDAO.makePersistent(new User(securityService.getContext().getUserId(), securityService.getContext().getDisplayName()));

		Output result = outputData.makePersistent(output);
		return result;
	}
	
	public Output alterOutput (UUID projectId, int outputId, OutputRequestDTO outputDTO) {
		Output output = outputData.findById(new ProjectDependentKey(projectId, outputId), false);
		String oldName = output.getName();
		
		if (output != null) {
			output.setName(outputDTO.getName());
			output.setDescription(outputDTO.getDescription());
			output.setState(outputDTO.getState());

			output.setLastEditNow(securityService.getContext().getUserId());
			userDAO.makePersistent(new User(securityService.getContext().getUserId(), securityService.getContext().getDisplayName()));
			
			output.setLockExpirationTime(Timestamp.from(Instant.now()));
			Output result = outputData.makePersistent(output);

			controlStructureDataService.alterArrow(projectId, outputId, "Output", oldName, result.getName());
			return result;
		}
		
		return output;
	}
	
	public boolean deleteOutput (UUID projectId, int outputId) {
		Output output = outputData.findById(new ProjectDependentKey(projectId, outputId), false);
		String name = output.getName();
		
		if (output != null) {
			outputData.makeTransient(output);
			controlStructureDataService.deleteNameInArrowLabel(projectId, outputId, "Output", name);
			return true;
		}
		
		return false;
	}
	
	public Output getOutputById (UUID projectId, int outputId) {
		return outputData.findById(new ProjectDependentKey(projectId, outputId), false);
	}
	
	public List<Output> getAllOutputs (UUID projectId, Filter filter, Integer amount, Integer from) {
		LinkedHashMap<String, SortOrder> order = new LinkedHashMap<>();
		order.put(Output.EntityAttributes.ID, SortOrder.ASC);
		
		if (filter != null && filter.getFieldName() != null && filter.getFieldValue() != null) {			
			if (filter.getFieldName().equals("isUnlinked") && Boolean.valueOf(filter.getFieldValue())) {
				return outputData.getAllUnlinkedOutputs(projectId);
			} 
		}
		
		return outputData.findFromTo(from, amount, order, projectId);
	}
	
	public List<Output> getOutputByArrowId (UUID projectId, String arrowId) {
		return outputData.getByArrowId(projectId, arrowId);
	}
	
	public Output setOutputArrowId (UUID projectId, int outputId, String arrowId) {
		return outputData.setArrowId(projectId, outputId, arrowId);
	}
}
