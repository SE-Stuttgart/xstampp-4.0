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
import de.xstampp.service.project.data.dto.control_structure.ControlAlgorithmRequestDTO;
import de.xstampp.service.project.data.entity.ControlAlgorithm;
import de.xstampp.service.project.service.RequestPushService;
import de.xstampp.service.project.service.RequestPushService.Method;
import de.xstampp.service.project.service.data.control_structure.ControlAlgorithmDataService;
import de.xstampp.service.project.util.annotation.CheckLock;
import de.xstampp.service.project.util.annotation.XStamppEntityId;
import de.xstampp.service.project.util.annotation.XStamppProjectId;

@RestController
@RequestMapping("/api/project")
public class ControlAlgorithmRestController {
	
	private SerializationUtil ser = new SerializationUtil();
	private DeserializationUtil deSer = new DeserializationUtil();
	
	@Autowired
	ControlAlgorithmDataService controlAlgorithmDataService;
	
	@Autowired
	RequestPushService push;

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@RequestMapping(value = "/{id}/control-algorithm", method = RequestMethod.POST)
	public String create (@PathVariable("id") @XStamppProjectId String projectId, @RequestBody String body) throws IOException {
		ControlAlgorithmRequestDTO controlAlgorithmDTO = deSer.deserialize(body, ControlAlgorithmRequestDTO.class);
		ControlAlgorithm result = controlAlgorithmDataService.createControlAlgorithm(UUID.fromString(projectId), controlAlgorithmDTO);
		
		if (result != null) {
			push.notify(String.valueOf(result.getId().getId()), projectId, EntityNameConstants.CONTROL_ALGORITHM, Method.CREATE);
		}
		
		return ser.serialize(result);
	}
	
	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.CONTROL_ALGORITHM)
	@RequestMapping(value = "/{id}/control-algorithm/{controlAlgorithmId}", method = RequestMethod.PUT)
	public String alter (@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("controlAlgorithmId") @XStamppEntityId int id, @RequestBody String body) throws IOException {
		ControlAlgorithmRequestDTO controlAlgorithmDTO = deSer.deserialize(body, ControlAlgorithmRequestDTO.class);
		ControlAlgorithm result = controlAlgorithmDataService.alterControlAlgorithm(UUID.fromString(projectId), id, controlAlgorithmDTO);
		
		if (result != null) {
			push.notify(String.valueOf(result.getId().getId()), projectId, EntityNameConstants.CONTROL_ALGORITHM, Method.ALTER);
		}
		
		return ser.serialize(result);
	}
	
	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.CONTROL_ALGORITHM)
	@RequestMapping(value = "/{id}/control-algorithm/{controlAlgorithmId}", method = RequestMethod.DELETE)
	public String delete (@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("controlAlgorithmId") @XStamppEntityId int id) throws IOException {
		boolean result = controlAlgorithmDataService.deleteControlAlgorithm(UUID.fromString(projectId), id);
		
		if (result) {
			push.notify(String.valueOf(id), projectId, EntityNameConstants.CONTROL_ALGORITHM, Method.DELETE);
		}
		
		return ser.serialize(new Response(result));
	}
	
	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/control-algorithm/{controlAlgorithmId}", method = RequestMethod.GET)
	public String getById (@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("controlAlgorithmId") int id) throws IOException {
		return ser.serialize(controlAlgorithmDataService.getControlAlgorithmById(UUID.fromString(projectId), id));
	}
	
	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/control-algorithm/search", method = RequestMethod.POST)
	public String getAll(@PathVariable("id") @XStamppProjectId String projectId, @RequestBody String body) throws IOException {
		SearchRequestDTO search = deSer.deserialize(body, SearchRequestDTO.class);
		return ser.serialize(controlAlgorithmDataService.getAllControlAlgorithm(UUID.fromString(projectId), search.getFilter(), search.getAmount(), search.getFrom()));
	}
	
	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/control-algorithm/box/{boxId}", method = RequestMethod.GET)
	public String getByBoxId (@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("boxId") String boxId) throws IOException {
		return ser.serialize(controlAlgorithmDataService.getControlAlgorithmByBoxId(UUID.fromString(projectId), boxId));
	}
	
	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.CONTROL_STRUCTURE)
	@RequestMapping(value = "/{id}/control-algorithm/{controlAlgorithmId}/box/{boxId}", method = RequestMethod.PUT)
	public String setBoxId(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("controlAlgorithmId") int controlAlgorithmId,
			@PathVariable("boxId") String boxId) throws IOException {
		// set to real null
		if (boxId.equals("null")) {
			boxId = null;
		}
		ControlAlgorithm result = this.controlAlgorithmDataService.setControlAlgorithmBoxId(UUID.fromString(projectId), controlAlgorithmId, boxId);
		return ser.serialize(result);
	}
}
