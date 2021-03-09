package de.xstampp.service.project.controller;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.xstampp.common.dto.Response;
import de.xstampp.common.utils.DeserializationUtil;
import de.xstampp.common.utils.PrivilegeCheck;
import de.xstampp.common.utils.Privileges;
import de.xstampp.common.utils.SerializationUtil;
import de.xstampp.service.project.data.dto.LockRequestDTO;
import de.xstampp.service.project.service.LockService;
import de.xstampp.service.project.util.annotation.XStamppProjectId;

/**
 * 
 * Rest controller for handling all incoming requests for the lock. This allows users to lock specified entities
 *
 */
@RestController
@RequestMapping("/api/project")
public class LockRestController {

	@Autowired
	LockService lockService;

	SerializationUtil ser = new SerializationUtil();
	DeserializationUtil deSer = new DeserializationUtil();
	Logger logger = LoggerFactory.getLogger(LockRestController.class);

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@RequestMapping(value = "/{projectId}/lock", method = RequestMethod.POST)
	public String lockEntity(@PathVariable("projectId") @XStamppProjectId String projectId, @RequestBody String body) throws IOException {
		LockRequestDTO request = deSer.deserialize(body, LockRequestDTO.class);
		return ser.serialize(new Response(lockService.lockEntity(request, UUID.fromString(projectId))));
	}

	@PrivilegeCheck(privilege = Privileges.DEVELOPER)
	@RequestMapping(value = "/{projectId}/unlock", method = RequestMethod.POST)
	public String unlockEntinty(@PathVariable("projectId") @XStamppProjectId String projectId, @RequestBody String body)
			throws IOException {
		LockRequestDTO request = deSer.deserialize(body, LockRequestDTO.class);
		return ser.serialize(new Response(lockService.unlockEntity(request, UUID.fromString(projectId))));
	}
}
