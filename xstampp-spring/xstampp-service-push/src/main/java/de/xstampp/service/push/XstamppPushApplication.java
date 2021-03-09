package de.xstampp.service.push;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class XstamppPushApplication {

	public static void main(String[] args) {
		SpringApplication.run(XstamppPushApplication.class, args);
	}
}
