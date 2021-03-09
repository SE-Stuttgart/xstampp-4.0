package de.xstampp.service.project.controller.control_structure;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.xstampp.common.dto.project.SearchRequestDTO;
import de.xstampp.common.utils.DeserializationUtil;
import de.xstampp.common.utils.EntityNameConstants;
import de.xstampp.common.utils.PrivilegeCheck;
import de.xstampp.common.utils.Privileges;
import de.xstampp.common.utils.SerializationUtil;
import de.xstampp.service.project.data.dto.control_structure.ControlledProcessRequestDTO;
import de.xstampp.service.project.data.entity.control_structure.ControlledProcess;
import de.xstampp.service.project.service.RequestPushService;
import de.xstampp.service.project.service.RequestPushService.Method;
import de.xstampp.service.project.service.data.control_structure.ControlledProcessDataService;
import de.xstampp.service.project.util.annotation.CheckLock;
import de.xstampp.service.project.util.annotation.XStamppEntityId;
import de.xstampp.service.project.util.annotation.XStamppProjectId;

@RestController
@RequestMapping(value = "/api/project")
public class ControlledProcessRestController {
	
	private SerializationUtil ser = new SerializationUtil();
	private DeserializationUtil deSer = new DeserializationUtil();
	
	@Autowired
	ControlledProcessDataService controlledProcessDataService;
	
	@Autowired
	RequestPushService push;
	
	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@RequestMapping(value = "/{id}/controlled-process", method = RequestMethod.POST)
	public String create (@PathVariable("id") @XStamppProjectId String projectId, @RequestBody String body) throws IOException {
		ControlledProcessRequestDTO cpDTO = deSer.deserialize(body, ControlledProcessRequestDTO.class);
		ControlledProcess result = controlledProcessDataService.createControlledProcess(UUID.fromString(projectId), cpDTO);
		
		if (result != null) {
			push.notify(String.valueOf(result.getId().getId()), projectId, EntityNameConstants.CONTROLLED_PROCESS, Method.CREATE);
		}
				
		return ser.serialize(result);
	}
	
	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.CONTROLLED_PROCESS)
	@RequestMapping(value = "/{id}/controlled-process/{controlledProcessId}", method = RequestMethod.PUT)
	public String alter (@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("controlledProcessId") @XStamppEntityId int id, @RequestBody String body) throws IOException {
		ControlledProcessRequestDTO cpDTO = deSer.deserialize(body, ControlledProcessRequestDTO.class);
		ControlledProcess result = controlledProcessDataService.alterControlledProcess(UUID.fromString(projectId), id, cpDTO);
		
		if (result != null) {
			push.notify(String.valueOf(result.getId().getId()), projectId, EntityNameConstants.CONTROLLED_PROCESS, Method.ALTER);
		}
		
		return ser.serialize(result);
	}
	
	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.CONTROLLED_PROCESS)
	@RequestMapping(value = "/{id}/controlled-process/{controlledProcessId}", method = RequestMethod.DELETE)
	public String delete (@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("controlledProcessId") @XStamppEntityId int id) throws IOException {
		boolean result = controlledProcessDataService.deleteControlledProcess(UUID.fromString(projectId), id);
		
		if (result) {
			push.notify(String.valueOf(id), projectId, EntityNameConstants.CONTROLLED_PROCESS, Method.DELETE);
		}
		
		return ser.serialize(result);
	}
	
	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/controlled-process/{controlledProcessId}", method = RequestMethod.GET)
	public String getById (@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("controlledProcessId") int id) throws IOException {
		return ser.serialize(controlledProcessDataService.getControlledProcessById(UUID.fromString(projectId), id));
	}
	
	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/controlled-process/search", method = RequestMethod.POST)
	public String getAll(@PathVariable("id") @XStamppProjectId String projectId, @RequestBody String body) throws IOException {
		SearchRequestDTO search = deSer.deserialize(body, SearchRequestDTO.class);
		return ser.serialize(controlledProcessDataService.getAllControlledProcess(UUID.fromString(projectId), search.getFilter(), search.getAmount(), search.getFrom()));
	}
	
	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/controlled-process/box/{boxId}", method = RequestMethod.GET)
	public String getByBoxId (@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("boxId") String boxId) throws IOException {
		return ser.serialize(controlledProcessDataService.getControlledProcessByBoxId(UUID.fromString(projectId), boxId));
	}
	
	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.CONTROL_STRUCTURE)
	@RequestMapping(value = "/{id}/controlled-process/{controlledProcessId}/box/{boxId}", method = RequestMethod.PUT)
	public String setBoxId(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("controlledProcessId") int controlledProcessId,
			@PathVariable("boxId") String boxId) throws IOException {
		// set to real null
		if (boxId.equals("null")) {
			boxId = null;
		}
		ControlledProcess result = this.controlledProcessDataService.setControlledProcessBoxId(UUID.fromString(projectId), controlledProcessId, boxId);
		return ser.serialize(result);
	}

}
