package de.xstampp.service.push.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.JsonNode;

import de.xstampp.common.auth.SecurityContext;
import de.xstampp.common.service.SecurityService;
import de.xstampp.common.utils.DeserializationUtil;
import de.xstampp.service.push.data.SessionKey;
import de.xstampp.service.push.service.WebsocketService;

@Component
public class PushWebsocketHandler extends TextWebSocketHandler{
	
	@Autowired
	WebsocketService wsService;
	
	@Autowired
	SecurityService securityService; 
	
	DeserializationUtil deSer = new DeserializationUtil();
	Logger logger = LoggerFactory.getLogger(PushWebsocketHandler.class);

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) {
		JsonNode node;
		try {			
			node = deSer.deserialize(message.getPayload());
			
			String token = getTextNodeAttrebute(node, "token");
			SecurityContext context = null;
			if (token != null) {				
				 context = securityService.createSecurityContext(token);
			}
			
			//check if token is valid
			if (context == null) {
				logger.warn("ws token not valid");
				session.sendMessage(new TextMessage(getErrorJson("invalid token")));
				return;
			}
			
			String type = getTextNodeAttrebute(node, "type");
			
			if (type == null) {
				return;
			}
			
			switch (type) {
			case "subscribe":
				
				String topic = getTextNodeAttrebute(node, "topic");
				if (topic == null) {
					logger.warn("topic was not set");
					session.sendMessage(new TextMessage(getErrorJson("topic not set")));
					return;
				}
				
				wsService.assignSessionToTopic(session, new SessionKey(context.getProjectId(), topic));	
				break;

			default:
				logger.warn("invalid type");
				session.sendMessage(new TextMessage(getErrorJson("invalid type")));
				break;
			}
		} catch (IOException e) {
			logger.error("cant parse ws message", e);
		}
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		wsService.removeSession(session);
		session.close();
	}
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		wsService.registerNewSession(session);
	}
	
	private static String getTextNodeAttrebute(JsonNode node, String fieldName) {
		JsonNode node2 = node.get(fieldName);
		
		if (node2 != null) {
			return node2.asText();
		} else {
			return null;
		}
	}
	
	private static String getErrorJson(String error) {
		return "{\"error\": \"" + error + "\"}";
	}
}
