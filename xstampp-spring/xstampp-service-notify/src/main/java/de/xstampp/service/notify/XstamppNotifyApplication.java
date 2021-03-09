package de.xstampp.service.notify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class XstamppNotifyApplication {

	public static void main(String[] args) {
		SpringApplication.run(XstamppNotifyApplication.class, args);
	}
}
