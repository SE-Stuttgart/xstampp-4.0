package de.xstampp.service.push.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.xstampp.common.utils.SerializationUtil;

@RestController
@RequestMapping("/api/push")
public class PublicRestController {
	
	SerializationUtil ser = new SerializationUtil();

}
