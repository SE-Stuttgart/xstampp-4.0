package de.xstampp.xstampptemplate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/test")
public class RestController {

	@GetMapping
	public String hallo () {
		return "hallo";
	}
}
