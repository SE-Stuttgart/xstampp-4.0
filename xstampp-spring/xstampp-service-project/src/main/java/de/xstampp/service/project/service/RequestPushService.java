package de.xstampp.service.project.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import de.xstampp.common.dto.push.PushDTO;
import de.xstampp.common.service.ConfigurationService;
import de.xstampp.common.service.SecurityService;
import de.xstampp.common.utils.SerializationUtil;

/* TODO consider moving to common (MasterStudent)*/
@Service
public class RequestPushService {

	private static final String PUSH_SERVICE_CONFIG_KEY = "services.push";

	@Autowired
	private ConfigurationService configService;
	
	@Autowired
	private SecurityService security;
	
	private SerializationUtil ser = new SerializationUtil();
	
	public enum Method {CREATE, ALTER, DELETE}
	
	private String pushAddress;
	private Logger logger = LoggerFactory.getLogger(RequestPushService.class);
	
	private HttpClient client;
	
	@PostConstruct
	public void init() {
		pushAddress = configService.getStringProperty(PUSH_SERVICE_CONFIG_KEY);
		client = HttpClient.newBuilder().build();
	}
	
	@Async
	public void notify(String entityId, String projectId, String topic, Method method) {
		PushDTO dto;
		if (security.getContext() != null && security.getContext().getDisplayName() != null) {
			dto = new PushDTO(security.getContext().getDisplayName());
		} else {
			dto = new PushDTO("(System)");
		}

		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(endpointFor(entityId, projectId, topic))
					.timeout(Duration.ofMinutes(1))
					.header("Content-Type", "application/json")
					.POST(BodyPublishers.ofString(ser.serialize(dto)))
					.build();

			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

			if (response.statusCode() >= 400) {
				logger.error("push service returned error {}: {}", response.statusCode(), response.body());
			}
		} catch (IOException | InterruptedException e) {
			logger.error("push call to push service failed with cause:{}", e.getMessage(), e);
		}
	}

	private URI endpointFor(String entityId, String projectId, String topic) {
		URI result;
		if (projectId != null) {
			result = URI.create("http://" + pushAddress + "/internal/push/project/" + projectId + "/topic/" + topic + "/id/"
					+ entityId);
		} else {
			result = URI.create("http://" + pushAddress + "/internal/push/topic/" + topic + "/id/"
					+ entityId);
		}
		return result;
	}
}

