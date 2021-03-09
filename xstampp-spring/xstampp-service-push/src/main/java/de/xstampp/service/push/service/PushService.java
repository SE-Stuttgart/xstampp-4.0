package de.xstampp.service.push.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Multimap;

import de.xstampp.service.push.data.SessionKey;
import de.xstampp.service.push.util.LookUpGenerator;

@Service
public class PushService {
	
	@Autowired
	WebsocketService wsService;
	
	ObjectMapper mapper = new ObjectMapper();
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	Multimap<String, String> lookUpTable;
	
	public PushService() {
		this.lookUpTable = LookUpGenerator.getLockUpMap();
	}

	@Async
	public void sendPush(String topic, String projectId, String entityId, String displayName) throws IOException {
//		logger.info(projectId != null);
		List<String> topics = new ArrayList<>(lookUpTable.get(topic));
		
		List<WebSocketSession> list = new ArrayList<>();
		for(String t : topics) {
			list.addAll(wsService.getSessionsForSessionKey(new SessionKey(projectId, t)));
		}
		
		for (WebSocketSession wss :list) {
			wss.sendMessage(new TextMessage(getChangeMessage(topic, projectId, entityId, displayName)));
		}
	}

	// TODO: Complete Documentation (throws) @Rico
	/**
	 * runs as scheduled jon and sends keep alive messages to all clients in a defined interval
	 * @throws IOException
	 */
	@Scheduled(fixedRate = 30000)
	public void sendKeepAlive () throws IOException {
		logger.trace("started keep alive task");
		TextMessage message = new TextMessage(getKeepAliveMessage());
		for (Entry<SessionKey, Collection<WebSocketSession>> entry: wsService.currentSessions.asMap().entrySet()) {
			for (WebSocketSession s : entry.getValue()) {
				s.sendMessage(message);
			}
		}
	}
	
	public String getChangeMessage(String topic, String projectId, String entityId, String displayName) {
		ObjectNode node = mapper.createObjectNode();
		node.put("topic", topic);
		node.put("projectId", projectId);
		node.put("id", entityId);
		node.put("type", "update");
		node.put("displayName", displayName);
		return node.toString();
	}
	
	public String getKeepAliveMessage () {
		ObjectNode node = mapper.createObjectNode();
		node.put("type", "keep-alive");
		return node.toString();
	}
	
	

}
