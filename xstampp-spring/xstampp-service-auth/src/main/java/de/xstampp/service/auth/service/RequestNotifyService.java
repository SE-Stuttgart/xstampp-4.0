package de.xstampp.service.auth.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.xstampp.common.dto.email.SendRawEmailDTO;
import de.xstampp.common.dto.email.SendTemplateEmailDTO;
import de.xstampp.common.service.ConfigurationService;
import de.xstampp.common.utils.SerializationUtil;

@Service
public class RequestNotifyService {
	
	private static final String NOTIFY_SERVICE_CONFIG_KEY = "services.notify";

	HttpClient client = HttpClient.newHttpClient();
	
	SerializationUtil ser = new SerializationUtil();
	
	private static final String PATH_MAIL_RAW = "/internal/notify/send";
	private static final String PATH_MAIL_TEMPLATE = "/internal/notify/template";

	@Autowired
	ConfigurationService config;
	
	Logger logger = LoggerFactory.getLogger(RequestNotifyService.class);

	public void sendRawMail (SendRawEmailDTO mail) throws IOException, URISyntaxException, InterruptedException{
		String endpoint = "http://" + config.getStringProperty(NOTIFY_SERVICE_CONFIG_KEY) + PATH_MAIL_RAW;
		logger.debug("Contacting {} to send a raw mail.", endpoint);
		HttpRequest request = HttpRequest.newBuilder()
				.POST(BodyPublishers.ofString(ser.serialize(mail)))
				.uri(new URI(endpoint))
				.build();
		
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		logger.info("Got response: {}", response);
	}
	
	public void sendTemplateMail (SendTemplateEmailDTO template) throws IOException, InterruptedException, URISyntaxException {
		String endpoint = "http://" + config.getStringProperty(NOTIFY_SERVICE_CONFIG_KEY) + PATH_MAIL_TEMPLATE;
		logger.debug("Contacting {} to send a templated mail.", endpoint);
		HttpRequest request = HttpRequest.newBuilder()
				.POST(BodyPublishers.ofString(ser.serialize(template)))
				.uri(new URI(endpoint))
				.build();
		
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		logger.info("Got response: {}", response);
	}
}
