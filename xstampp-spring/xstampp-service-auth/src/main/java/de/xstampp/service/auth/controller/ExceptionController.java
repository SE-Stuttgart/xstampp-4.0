package de.xstampp.service.auth.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

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
import de.xstampp.common.utils.SerializationUtil;

@RestController
@ControllerAdvice
public class ExceptionController implements GenericXSTAMPPExceptionHandler {
	private Logger logger = LoggerFactory.getLogger(ExceptionController.class);
	private SerializationUtil ser = new SerializationUtil();

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

	@Override
	@ExceptionHandler(GenericXSTAMPPException.class)
	public ResponseEntity<String> xstamppException(HttpServletRequest req, GenericXSTAMPPException e)
			throws IOException {
		logger.warn("ExceptionController received uncaught GenericXSTAMPPException: {}", e.getMessage(), e);
		int statusCode = e.getHttpStatusCode();
		String body = ser.serialize(e.getReply());
		return ResponseEntity.status(statusCode).contentType(MediaType.APPLICATION_JSON_UTF8).body(body);
	}
}
