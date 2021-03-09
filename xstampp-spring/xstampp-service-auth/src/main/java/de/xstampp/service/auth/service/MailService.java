package de.xstampp.service.auth.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import de.xstampp.common.dto.email.SendTemplateEmailDTO;
import de.xstampp.common.service.ConfigurationService;

@Service
public class MailService {

	@Value("classpath:xstampp-mail-templates/password-reset")
	Resource permaLockNotification;

	@Value("classpath:xstampp-mail-templates/lock-notification")
	Resource attemptLockNotification;

	@Autowired
	private RequestNotifyService requestService;

	@Autowired
	private ConfigurationService configService;

	private static final String SUBJECT_PERMA_LOCK = "XSTAMPP 4.0 account locked due to multiple failed password attempts";
	private static final String SUBJECT_ATTEMPT_LOCK = "Failed password attempts to XTAMPP 4.0 Account";

	private static final String BASE_URL_CONFIG_KEY = "constants.system.baseurl";
	private static final String API_PATH = "api/auth/unlock/";

	private String permaLockString;
	private String attemptLockString;
	

	Logger logger = LoggerFactory.getLogger(MailService.class);

	@PostConstruct
	public void init() throws IOException {

		this.permaLockString = new String(permaLockNotification.getInputStream().readAllBytes(),
				StandardCharsets.UTF_8);
		this.attemptLockString = new String(attemptLockNotification.getInputStream().readAllBytes(),
				StandardCharsets.UTF_8);
	}

	public void sendPermaLockMail(String displayName, String mail, UUID unlockToken, UUID uid) {
		logger.debug("Sending email to user informing them of a permanent lock.");
		
		String xstampURL = configService.getStringProperty(BASE_URL_CONFIG_KEY);
		String unlockURI = xstampURL + API_PATH + uid + "/" + unlockToken;
		
		HashMap<String, String> params = new HashMap<>();
		params.put("displayName", displayName);
		params.put("mail", mail);
		params.put("unlockURI", unlockURI);

		SendTemplateEmailDTO sendMail = new SendTemplateEmailDTO(mail, displayName, SUBJECT_PERMA_LOCK, permaLockString,
				params);
		try {
			requestService.sendTemplateMail(sendMail);
		} catch (IOException | URISyntaxException e) {
			logger.error("sending mail failed", e);
		} catch (InterruptedException e) {
			logger.error("sending mailing was interuppted");
		}
	}

	public void sendAttemptLockMail(String displayName, String mail) {
		logger.debug("Sending email to user informing them of failed login attempts.");
		HashMap<String, String> params = new HashMap<>();
		params.put("displayName", displayName);
		params.put("mail", mail);

		SendTemplateEmailDTO sendMail = new SendTemplateEmailDTO(mail, displayName, SUBJECT_ATTEMPT_LOCK,
				attemptLockString, params);
		try {
			requestService.sendTemplateMail(sendMail);
		} catch (IOException | URISyntaxException e) {
			logger.error("sending mail failed", e);
		} catch (InterruptedException e) {
			logger.error("sending mailing was interuppted");
		}
	}

}
