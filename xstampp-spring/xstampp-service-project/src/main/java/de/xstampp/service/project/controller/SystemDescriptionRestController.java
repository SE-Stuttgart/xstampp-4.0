package de.xstampp.service.project.controller;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.xstampp.common.dto.Response;
import de.xstampp.common.utils.DeserializationUtil;
import de.xstampp.common.utils.EntityNameConstants;
import de.xstampp.common.utils.PrivilegeCheck;
import de.xstampp.common.utils.Privileges;
import de.xstampp.common.utils.SerializationUtil;
import de.xstampp.service.project.data.dto.SystemDescriptionRequestDTO;
import de.xstampp.service.project.data.entity.SystemDescription;
import de.xstampp.service.project.service.RequestPushService;
import de.xstampp.service.project.service.RequestPushService.Method;
import de.xstampp.service.project.service.data.SystemDescriptionDataService;
import de.xstampp.service.project.util.annotation.CheckLock;
import de.xstampp.service.project.util.annotation.XStamppProjectId;

/**
 * 
 * Rest controller for handling all incoming requests for the system description
 *
 */
@RestController
@RequestMapping(value = "/api/project")
public class SystemDescriptionRestController {
	@Autowired
	private SystemDescriptionDataService systemDescriptionData;
	
	@Autowired
	RequestPushService push;
	
	private SerializationUtil ser = new SerializationUtil();
	private DeserializationUtil deSer = new DeserializationUtil();
	
	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@RequestMapping(value = "/{id}/system-description", method = RequestMethod.POST)
	public String createSystemDescription (@PathVariable("id") @XStamppProjectId String projectId, @RequestBody String body) throws IOException {
		SystemDescriptionRequestDTO descr = deSer.deserialize(body, SystemDescriptionRequestDTO.class);
		SystemDescription result = systemDescriptionData.create(descr, UUID.fromString(projectId));
		
		if (result != null) {
			push.notify(projectId, projectId, EntityNameConstants.SYSTEM_DESCRIPTION, Method.CREATE);
		}
		
		return ser.serialize(result);
	}
	
	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.SYSTEM_DESCRIPTION)
	@RequestMapping(value = "/{id}/system-description", method = RequestMethod.PUT)
	public String alterSystemDescription (@PathVariable("id") @XStamppProjectId String projectId, @RequestBody String body) throws IOException {
		SystemDescriptionRequestDTO descr = deSer.deserialize(body, SystemDescriptionRequestDTO.class);
		SystemDescription result = systemDescriptionData.alter(descr, UUID.fromString(projectId));
		
		if (result != null) {
			push.notify(projectId, projectId, EntityNameConstants.SYSTEM_DESCRIPTION, Method.ALTER);
		}
		
		return ser.serialize(result);
	}
	
	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@CheckLock(entity = EntityNameConstants.SYSTEM_DESCRIPTION)
	@RequestMapping(value = "/{id}/system-description", method = RequestMethod.DELETE)
	public String deleteSystemDescription (@PathVariable("id") @XStamppProjectId String projectId) throws IOException{
		boolean result = systemDescriptionData.deleteFor(UUID.fromString(projectId));
		
		if (result) {
			push.notify(projectId, projectId, EntityNameConstants.SYSTEM_DESCRIPTION, Method.DELETE);
		}
		return ser.serialize(new Response(result));
	}
	
	@PrivilegeCheck(privilege = Privileges.GUEST)
	@RequestMapping(value = "/{id}/system-description", method = RequestMethod.GET)
	public String getSystemDescription (@PathVariable("id") @XStamppProjectId String projectId) throws IOException {
		return ser.serialize(systemDescriptionData.getById(UUID.fromString(projectId)));
	}

}
