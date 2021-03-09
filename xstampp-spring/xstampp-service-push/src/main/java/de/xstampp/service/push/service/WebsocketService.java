package de.xstampp.service.push.service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import de.xstampp.service.push.data.SessionKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.Map.Entry;

@Service
public class WebsocketService {

	// map for all sessions grouped by the subscribed topic
	SetMultimap<SessionKey, WebSocketSession> currentSessions;

	// all new created and yet uncategorized sessions
	Set<WebSocketSession> uncategorized;
	
	Logger logger = LoggerFactory.getLogger(WebsocketService.class);
	
	public WebsocketService() {
		currentSessions = Multimaps.synchronizedSetMultimap(HashMultimap.create());
		uncategorized = Collections.synchronizedSet(new HashSet<>());
	}

	/**
	 * registers a new session. This session can then assigned to a topic for the
	 * actual subscription
	 * 
	 * @param session the websocket session
	 * @return returns true if the session was registered successfully
	 */
	public boolean registerNewSession(WebSocketSession session) {
		if (uncategorized.contains(session)) {
			logger.debug("session already exist");
			return false;
		}

		this.uncategorized.add(session);
		logger.debug("registered new session");
		return true;
	}

	/*/**
	 * assigns a session with a specified topic. it is needed that this session was
	 * added as new session before with the
	 * {@link WebsocketService#registerNewSession(Session)} method
	 * 
	 * @param session
	 * @param topic
	 * @return
	 */
	public boolean assignSessionToTopic(WebSocketSession session, SessionKey key) {
		if (!uncategorized.contains(session)) {
			return false;
		}

		uncategorized.remove(session);
		currentSessions.put(key, session);
		logger.debug("assigned session {} to {} for project {}", session.getId(), key.getTopic(), key.getProjectId());

		return true;
	}

	/**
	 * removes a session from the set of registered sessions. The actual socket will
	 * not be closed
	 * 
	 * @param session the websocket session
	 * @return returns true if the session was removed successfully
	 */
	public boolean removeSession(WebSocketSession session) {
		if (uncategorized.contains(session)) {
			uncategorized.remove(session);
			logger.debug("removed uncategorized session");
			return true;
		}

		for (Entry<SessionKey, WebSocketSession> entry :currentSessions.entries()) {
			if (entry.getValue().equals(session)) {
				currentSessions.remove(entry.getKey(), entry.getValue());
				logger.debug("removed session for topic {} and project {}", entry.getKey().getTopic(), entry.getKey().getProjectId());
				return true;
			}
		}

		return false;
	}

	/*/**
	 * returns all websocket sessions which are subscribed to the given session key
	 * 
	 * @param topic the subscription topic
	 * @return returns a list of all sessions
	 */
	public List<WebSocketSession> getSessionsForSessionKey(SessionKey key) {
		List<WebSocketSession> result = new ArrayList<>();

		result.addAll(currentSessions.get(key));
		return result;
	}
	
//	public List<WebSocketSession> TEMPgetSessionsForSessionKey(SessionKey key) {
//		return getSessionsForSessionKey(key);
//	}
}
