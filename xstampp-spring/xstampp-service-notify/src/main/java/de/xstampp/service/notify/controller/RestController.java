package de.xstampp.service.notify.controller;

import java.io.IOException;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.xstampp.common.dto.Response;
import de.xstampp.common.dto.email.SendRawEmailDTO;
import de.xstampp.common.dto.email.SendTemplateEmailDTO;
import de.xstampp.common.utils.DeserializationUtil;
import de.xstampp.common.utils.SerializationUtil;
import de.xstampp.service.notify.service.EmailService;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/internal/notify")
public class RestController {

	SerializationUtil ser = new SerializationUtil();
	DeserializationUtil deSer = new DeserializationUtil();

	@Autowired
	EmailService service;

	@RequestMapping(path = "/send", method = RequestMethod.POST)
	public String sendRawEmail(@RequestBody String body) throws IOException, MessagingException {
		SendRawEmailDTO request = deSer.deserialize(body, SendRawEmailDTO.class);

		service.sendRawEmail(request);

		return ser.serialize(new Response(true));
	}
	
	@RequestMapping(path = "/template", method = RequestMethod.POST)
	public String sendTemplateEmail(@RequestBody String body) throws IOException, MessagingException {
		SendTemplateEmailDTO request = deSer.deserialize(body, SendTemplateEmailDTO.class);

		service.sendTemplateEmail(request);

		return ser.serialize(new Response(true));
	}
}
