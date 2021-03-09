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
import de.xstampp.service.project.data.dto.control_structure.InputRequestDTO;
import de.xstampp.service.project.data.entity.control_structure.Input;
import de.xstampp.service.project.service.RequestPushService;
import de.xstampp.service.project.service.RequestPushService.Method;
import de.xstampp.service.project.service.data.control_structure.InputDataService;
import de.xstampp.service.project.util.annotation.CheckLock;
import de.xstampp.service.project.util.annotation.XStamppEntityId;
import de.xstampp.service.project.util.annotation.XStamppProjectId;

@RestController
@RequestMapping("/api/project")
public class InputRestController {
	
	private SerializationUtil ser = new SerializationUtil();
	private DeserializationUtil deSer = new DeserializationUtil();
	
	@Autowired
	InputDataService inputDataService;
	
	@Autowired
	RequestPushService push;

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@RequestMapping(value = "/{id}/input", method = RequestMethod.POST)
	public String create (@PathVariable("id") @XStamppProjectId String projectId, @RequestBody String body) throws IOException {
		InputRequestDTO inputDTO = deSer.deserialize(body, InputRequestDTO.class);
		Input result = inputDataService.createInput(UUID.fromString(projectId), inputDTO);
		
		if (result != null) {
			push.notify(String.valueOf(result.getId().getId()), projectId, EntityNameConstants.INPUT, Method.CREATE);
		}
		
		return ser.serialize(result);
	}
	
	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.INPUT)
	@RequestMapping(value = "/{id}/input/{inputId}", method = RequestMethod.PUT)
	public String alter (@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("inputId") @XStamppEntityId int id, @RequestBody String body) throws IOException {
		InputRequestDTO inputDTO = deSer.deserialize(body, InputRequestDTO.class);
		Input result = inputDataService.alterInput(UUID.fromString(projectId), id, inputDTO);
		
		if (result != null) {
			push.notify(String.valueOf(result.getId().getId()), projectId, EntityNameConstants.INPUT, Method.ALTER);
		}
		
		return ser.serialize(result);
	}
	
	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.INPUT)
	@RequestMapping(value = "/{id}/input/{inputId}", method = RequestMethod.DELETE)
	public String delete (@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("inputId") @XStamppEntityId int id) throws IOException {
		boolean result = inputDataService.deleteInput(UUID.fromString(projectId), id);
		
		if (result) {
			push.notify(String.valueOf(id), projectId, EntityNameConstants.INPUT, Method.DELETE);
		}
				
		return ser.serialize(new Response(result));
	}
	
	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/input/{inputId}", method = RequestMethod.GET)
	public String getById (@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("inputId") int id) throws IOException {
		return ser.serialize(inputDataService.getInputById(UUID.fromString(projectId), id));
	}
	
	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/input/search", method = RequestMethod.POST)
	public String getAll(@PathVariable("id") @XStamppProjectId String projectId, @RequestBody String body) throws IOException {
		SearchRequestDTO search = deSer.deserialize(body, SearchRequestDTO.class);
		return ser.serialize(inputDataService.getAllInputs(UUID.fromString(projectId), search.getFilter(), search.getAmount(), search.getFrom()));
	}
	
	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/input/arrow/{arrowId}", method = RequestMethod.GET)
	public String getByArrowId (@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("arrowId") String arrowId) throws IOException {
		return ser.serialize(inputDataService.getInputByArrowId(UUID.fromString(projectId), arrowId));
	}
	
	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.CONTROL_STRUCTURE)
	@RequestMapping(value = "/{id}/input/{inputId}/arrow/{arrowId}", method = RequestMethod.PUT)
	public String setArrowId(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("inputId") int inputId,
			@PathVariable("arrowId") String arrowId) throws IOException {
		// set to real null
		if (arrowId.equals("null")) {
			arrowId = null;
		}
		Input result = this.inputDataService.setInputArrowId(UUID.fromString(projectId), inputId, arrowId);
		return ser.serialize(result);
	}
}
