package de.xstampp.service.project.controller.control_structure;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.xstampp.common.dto.Response;
import de.xstampp.common.dto.project.SearchRequestDTO;
import de.xstampp.common.utils.DeserializationUtil;
import de.xstampp.common.utils.EntityNameConstants;
import de.xstampp.common.utils.PrivilegeCheck;
import de.xstampp.common.utils.Privileges;
import de.xstampp.common.utils.SerializationUtil;
import de.xstampp.service.project.data.dto.control_structure.OutputRequestDTO;
import de.xstampp.service.project.data.entity.control_structure.Output;
import de.xstampp.service.project.service.RequestPushService;
import de.xstampp.service.project.service.RequestPushService.Method;
import de.xstampp.service.project.service.data.control_structure.OutputDataService;
import de.xstampp.service.project.util.annotation.CheckLock;
import de.xstampp.service.project.util.annotation.XStamppEntityId;
import de.xstampp.service.project.util.annotation.XStamppProjectId;

@RestController
@RequestMapping("/api/project")
public class OutputRestController {
	
	private SerializationUtil ser = new SerializationUtil();
	private DeserializationUtil deSer = new DeserializationUtil();
	
	@Autowired
	OutputDataService outputDataService;
	
	@Autowired
	RequestPushService push;

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@RequestMapping(value = "/{id}/output", method = RequestMethod.POST)
	public String create (@PathVariable("id") @XStamppProjectId String projectId, @RequestBody String body) throws IOException {
		OutputRequestDTO outputDTO = deSer.deserialize(body, OutputRequestDTO.class);
		Output result = outputDataService.createOutput(UUID.fromString(projectId), outputDTO);
		
		if (result != null) {
			push.notify(String.valueOf(result.getId().getId()), projectId, EntityNameConstants.OUTPUT, Method.CREATE);
		}
		
		return ser.serialize(result);
	}
	
	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.OUTPUT)
	@RequestMapping(value = "/{id}/output/{outputId}", method = RequestMethod.PUT)
	public String alter (@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("outputId") @XStamppEntityId int id, @RequestBody String body) throws IOException {
		OutputRequestDTO outputDTO = deSer.deserialize(body, OutputRequestDTO.class);
		Output result = outputDataService.alterOutput(UUID.fromString(projectId), id, outputDTO);
		
		if (result != null) {
			push.notify(String.valueOf(result.getId().getId()), projectId, EntityNameConstants.OUTPUT, Method.ALTER);
		}
		
		return ser.serialize(result);
	}
	
	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.OUTPUT)
	@RequestMapping(value = "/{id}/output/{outputId}", method = RequestMethod.DELETE)
	public String delete (@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("outputId") @XStamppEntityId int id) throws IOException {
		boolean result = outputDataService.deleteOutput(UUID.fromString(projectId), id);
		
		if (result) {
			push.notify(String.valueOf(id), projectId, EntityNameConstants.OUTPUT, Method.DELETE);
		}
		
		return ser.serialize(new Response(result));
	}
	
	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/output/{outputId}", method = RequestMethod.GET)
	public String getById (@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("outputId") int id) throws IOException {
		return ser.serialize(outputDataService.getOutputById(UUID.fromString(projectId), id));
	}
	
	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/output/search", method = RequestMethod.POST)
	public String getAll(@PathVariable("id") @XStamppProjectId String projectId, @RequestBody String body) throws IOException {
		SearchRequestDTO search = deSer.deserialize(body, SearchRequestDTO.class);
		return ser.serialize(outputDataService.getAllOutputs(UUID.fromString(projectId), search.getFilter(), search.getAmount(), search.getFrom()));
	}
	
	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/output/arrow/{arrowId}", method = RequestMethod.GET)
	public String getByArrowId (@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("arrowId") String arrowId) throws IOException {
		return ser.serialize(outputDataService.getOutputByArrowId(UUID.fromString(projectId), arrowId));
	}
	
	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.CONTROL_STRUCTURE)
	@RequestMapping(value = "/{id}/output/{outputId}/arrow/{arrowId}", method = RequestMethod.PUT)
	public String setArrowId(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("outputId") int outputId,
			@PathVariable("arrowId") String arrowId) throws IOException {
		// set to real null
		if (arrowId.equals("null")) {
			arrowId = null;
		}
		Output result = this.outputDataService.setOutputArrowId(UUID.fromString(projectId), outputId, arrowId);
		return ser.serialize(result);
	}
}
