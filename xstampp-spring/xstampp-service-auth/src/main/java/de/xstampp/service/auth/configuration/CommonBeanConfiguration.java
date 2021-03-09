package de.xstampp.service.auth.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.xstampp.common.service.ConfigurationService;
import de.xstampp.common.service.SecurityService;

@Configuration
public class CommonBeanConfiguration {
	@Bean
	public ConfigurationService confService() {
		return new ConfigurationService();
	}

	@Bean
	public SecurityService securityService() {
		return new SecurityService();
	}
}
