package de.xstampp.service.push.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.xstampp.common.dto.Response;
import de.xstampp.common.dto.push.PushDTO;
import de.xstampp.common.utils.DeserializationUtil;
import de.xstampp.common.utils.SerializationUtil;
import de.xstampp.service.push.service.PushService;

@RestController
@RequestMapping("/internal/push")
public class InternalProjectRestController {
	
	SerializationUtil ser = new SerializationUtil();
	DeserializationUtil deSer = new DeserializationUtil();
	Logger logger = LoggerFactory.getLogger(InternalProjectRestController.class);
	
	@Autowired
	PushService pushService;

	@RequestMapping(value = "/project/{projectId}/topic/{topic}/id/{entityId}", method = RequestMethod.POST)
	public String dataChanged(@PathVariable("topic") String topic, @PathVariable("projectId") String projectId,
			@PathVariable("entityId") String entityId, @RequestBody String body) throws IOException {
		PushDTO dto = deSer.deserialize(body, PushDTO.class);
		
		//send async push messages for topic
		pushService.sendPush(topic, projectId, entityId, dto.getDisplayName());
		return ser.serialize(new Response(true));
	}
}
