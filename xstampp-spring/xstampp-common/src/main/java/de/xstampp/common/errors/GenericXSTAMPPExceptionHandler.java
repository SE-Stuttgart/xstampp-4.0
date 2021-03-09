package de.xstampp.common.errors;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;

public interface GenericXSTAMPPExceptionHandler {

	ResponseEntity<String> xstamppException(HttpServletRequest req, GenericXSTAMPPException e) throws IOException;

}