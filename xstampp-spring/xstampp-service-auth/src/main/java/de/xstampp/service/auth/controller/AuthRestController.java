package de.xstampp.service.auth.controller;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.xstampp.common.utils.DeserializationUtil;
import de.xstampp.common.utils.SerializationUtil;
import de.xstampp.service.auth.dto.LoginRequestDTO;
import de.xstampp.service.auth.dto.ProjectTokenRequestDTO;
import de.xstampp.service.auth.dto.RegisterRequestDTO;
import de.xstampp.service.auth.service.AuthenticationService;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

	SerializationUtil ser = new SerializationUtil();
	DeserializationUtil deSer = new DeserializationUtil();

	@Autowired
	AuthenticationService service;

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String register(HttpServletRequest request, HttpServletResponse response, @RequestBody String body)
			throws IOException {
		RegisterRequestDTO register = deSer.deserialize(body, RegisterRequestDTO.class);

		return ser.serialize(service.register(register));
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(@RequestBody String body, @RequestHeader HttpHeaders headers) throws IOException {
		LoginRequestDTO login = deSer.deserialize(body, LoginRequestDTO.class);

		return ser.serialize(service.login(login));
	}


	@RequestMapping(value = "/project-token", method = RequestMethod.POST)
	public String projectToken(@RequestBody String body, @RequestHeader HttpHeaders headers) throws IOException {
		ProjectTokenRequestDTO projectToken = deSer.deserialize(body, ProjectTokenRequestDTO.class);

		return ser.serialize(service.projectToken(projectToken));
	}

	@RequestMapping(value = "/unlock/{userid}/{unlockToken}", method = RequestMethod.POST)
	public void unlockToken(@PathVariable("userid") String userid, @PathVariable UUID unlockToken) {
		service.unlockPermalockedUser(unlockToken, UUID.fromString(userid));
	}

}
