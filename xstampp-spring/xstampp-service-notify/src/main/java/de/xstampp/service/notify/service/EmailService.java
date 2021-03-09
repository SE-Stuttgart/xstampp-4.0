package de.xstampp.service.notify.service;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.stringtemplate.v4.ST;

import de.xstampp.common.dto.email.SendRawEmailDTO;
import de.xstampp.common.dto.email.SendTemplateEmailDTO;
import de.xstampp.common.service.ConfigurationService;

@Service
public class EmailService {
	private static final String EMAIL_CHARSET = "UTF-8";

	@Autowired
	private ConfigurationService config;

	private Logger logger = LoggerFactory.getLogger(EmailService.class);

	public void sendRawEmail(SendRawEmailDTO request) throws IOException, MessagingException {
		Properties prop = System.getProperties();
		String emailServer = config.getStringProperty("smtp.upstream");
		logger.info("Connecting to {} to send email", emailServer);
		prop.putIfAbsent("mail.smtp.host", emailServer);
		prop.putIfAbsent("mail.mime.charset", EMAIL_CHARSET);
		Session s = Session.getInstance(prop);

		Message msg = new MimeMessage(s);

		InternetAddress fromAddress = new InternetAddress(config.getStringProperty("smtp.fromaddress"));
		String fromDisplayName = config.getStringProperty("smtp.fromname");
		if (fromDisplayName != null) {
			fromAddress.setPersonal(fromDisplayName, EMAIL_CHARSET);
		}
		msg.setFrom(fromAddress);

		InternetAddress toAddress = new InternetAddress(request.getToAddress());
		if (request.getToDisplayName() != null) {
			toAddress.setPersonal(request.getToDisplayName(), EMAIL_CHARSET);
		}
		msg.setRecipient(Message.RecipientType.TO, toAddress);

		msg.setSubject(request.getSubject());

		msg.setText(request.getBody());

		Transport.send(msg);
	}
	
	public void sendTemplateEmail (SendTemplateEmailDTO request) throws IOException, MessagingException {
		ST template = new ST(request.getBody());
		
		for (Entry<String, String> entry: request.getFields().entrySet()) {
			template.add(entry.getKey(), entry.getValue());
		}
		
		String resultString = template.render();
		SendRawEmailDTO mail = new SendRawEmailDTO(request.getToAddress(), request.getToDisplayName(), request.getSubject(), resultString);
		sendRawEmail(mail);
	}
}