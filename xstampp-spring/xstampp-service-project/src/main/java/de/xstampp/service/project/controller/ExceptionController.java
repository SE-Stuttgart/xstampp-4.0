package de.xstampp.service.project.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.xstampp.common.errors.ErrorsComm;
import de.xstampp.common.errors.GenericXSTAMPPException;
import de.xstampp.common.errors.GenericXSTAMPPExceptionHandler;
import de.xstampp.common.utils.IgnorePrivilegeCheck;
import de.xstampp.common.utils.SerializationUtil;
import de.xstampp.service.project.exceptions.ProjectException;

/**
 * 
 * Exception controller that handles all exceptions thrown in the application.
 *
 */
@RestController
@IgnorePrivilegeCheck
@ControllerAdvice
public class ExceptionController implements GenericXSTAMPPExceptionHandler {
	private Logger logger = LoggerFactory.getLogger(ExceptionController.class);
	private SerializationUtil ser = new SerializationUtil();

	@ExceptionHandler(ProjectException.class)
	public void projectException(HttpServletResponse resp, ProjectException e) throws IOException {
		logger.warn("ExceptionController received uncaught ProjectException", e);
		resp.sendError(400, e.getErrorMessage());
	}

	@ExceptionHandler(JsonProcessingException.class)
	public ResponseEntity<String> handleJsonParseException(HttpServletRequest req, JsonProcessingException e)
			throws IOException {
		return xstamppException(req, ErrorsComm.JSON_DECODING_FAILED.exc(e));
	}

	@ExceptionHandler(DataAccessException.class)
	public ResponseEntity<String> dataAccessException(HttpServletRequest req, DataAccessException e)
			throws IOException {
		return xstamppException(req, ErrorsComm.DB_ACCESS_FAILED.exc(e));
	}

	@ExceptionHandler(GenericXSTAMPPException.class)
	public ResponseEntity<String> xstamppException(HttpServletRequest req, GenericXSTAMPPException e)
			throws IOException {
		logger.warn("ExceptionController received uncaught GenericXSTAMPPException: {}", e.getMessage(), e);
		int statusCode = e.getHttpStatusCode();
		String body = ser.serialize(e.getReply());
		return ResponseEntity.status(statusCode).contentType(MediaType.APPLICATION_JSON_UTF8).body(body);
	}
}
