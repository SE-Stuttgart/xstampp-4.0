package de.xstampp.service.auth.service;

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

import de.xstampp.common.service.ConfigurationService;

/* TODO consider moving to common */
@Service
public class RequestPushService {

	private static final String PUSH_SERVICE_CONFIG_KEY = "services.push";

	@Autowired
	private ConfigurationService configService;
	
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
		HttpRequest request = HttpRequest.newBuilder()
				.uri(endpointFor(entityId, projectId, topic))
				.timeout(Duration.ofMinutes(1))
				.header("Content-Type", "application/json")
				.POST(BodyPublishers.ofString("{}"))
				.build();
		
		try {
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

			if (response.statusCode() >= 400) {
				logger.error("push service returned error {}: {}", response.statusCode(), response.body());
			}
		} catch (IOException | InterruptedException e) {
			logger.error("push call to push service failed with cause:{}", e.getMessage(), e);
		}
	}

	private URI endpointFor(String entityId, String projectId, String topic) {
		return URI.create("http://" + pushAddress + "/internal/push/project/" + projectId + "/topic/" + topic + "/id/"
				+ entityId);
	}
}
