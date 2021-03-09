package de.xstampp.service.project.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.xstampp.common.service.ConfigurationService;
import de.xstampp.common.service.SecurityService;

@Configuration
public class CommonBeanConfiguration {
	@Bean
	/**
	 * Configuration for the configuration service that loads the system configuration
	 * @return returns the configuration service instance
	 */
	public ConfigurationService confService() {
		return new ConfigurationService();
	}

	@Bean
	/**
	 * Confguration for the security service that holds all security relevant information.
	 * @return returns the security service instance
	 */
	public SecurityService securityService() {
		return new SecurityService();
	}
}
