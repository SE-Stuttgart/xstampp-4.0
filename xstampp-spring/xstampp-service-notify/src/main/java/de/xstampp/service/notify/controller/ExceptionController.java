package de.xstampp.service.notify.controller;

import java.io.IOException;

import javax.mail.MessagingException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@ControllerAdvice
public class ExceptionController {
	@ExceptionHandler(JsonProcessingException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Invalid JSON object")
	public void handleJsonParseException() {
		/* handled via annotations */
	}

	@ExceptionHandler(IOException.class)
	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Sending message failed.")
	public void handleIOException() {
		/* handled via annotations */
	}

	@ExceptionHandler(MessagingException.class)
	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Sending message failed.")
	public void handleMessagingException() {
		/* handled via annotations */
	}
}
