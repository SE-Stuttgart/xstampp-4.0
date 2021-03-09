package de.xstampp.service.project.controller;

import de.xstampp.common.dto.Response;
import de.xstampp.common.utils.*;
import de.xstampp.service.project.data.dto.ControlStructureDTO;
import de.xstampp.service.project.service.RequestPushService;
import de.xstampp.service.project.service.RequestPushService.Method;
import de.xstampp.service.project.service.data.ControlStructureDataService;
import de.xstampp.service.project.util.annotation.CheckLock;
import de.xstampp.service.project.util.annotation.XStamppProjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * 
 * Rest controller for handling all incoming requests for the Control Structure
 *
 */
@RestController
@RequestMapping("/api/project")
public class ControlStructureController {
	
	private SerializationUtil ser = new SerializationUtil();
	private DeserializationUtil deSer = new DeserializationUtil();
	
	@Autowired
	ControlStructureDataService dataService;
	
	@Autowired
	RequestPushService push;
	
	@PrivilegeCheck(privilege = Privileges.GUEST)
	@GetMapping("/{id}/control-structure")
	public String getRootControlStructure(@PathVariable("id") @XStamppProjectId String projectId) throws IOException {

		ControlStructureDTO controlStructure = dataService.getRootControlStructure(projectId);
		return ser.serialize(controlStructure);
	}
	
	@PrivilegeCheck(privilege = Privileges.GUEST)
	@GetMapping("/{id}/control-structure/{parent}")
	public String getDetailedControlStructure(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("parent") int parent) throws IOException {
		return ser.serialize(dataService.getDetailedControlStructure(projectId, parent));
	}
	
	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.CONTROL_STRUCTURE)
	@PostMapping("/{id}/control-structure")
	public String alterRootControlStructure(@PathVariable("id") @XStamppProjectId String projectId, @RequestBody String body) throws IOException {
		ControlStructureDTO controlStructure = deSer.deserialize(body, ControlStructureDTO.class);

		dataService.alterRootControlStructure(projectId, controlStructure);
		if (controlStructure != null) {
			push.notify(String.valueOf(projectId), projectId, "control-structure", Method.CREATE);
		}
		return ser.serialize(new Response(true));
	}
	
	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.CONTROL_STRUCTURE)
	@PostMapping("/{id}/control-structure/{parent}")
	public String alterDetailedControlStructure(@PathVariable("id") @XStamppProjectId String projectId, @PathVariable("parent") int parent, @RequestBody String body) throws IOException {
		ControlStructureDTO controlStructure = deSer.deserialize(body, ControlStructureDTO.class);
		dataService.alterDetailedCotrolStructure(projectId, parent, controlStructure);
		return ser.serialize(new Response(true));
	}

}
