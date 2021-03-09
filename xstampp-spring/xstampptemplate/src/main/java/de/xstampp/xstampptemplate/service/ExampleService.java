package de.xstampp.xstampptemplate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ExampleService {

	private Logger logger = LoggerFactory.getLogger(ExampleService.class);

	public String hello() {
		logger.info("hello requested");
		return "Hello World!";
	}
}
