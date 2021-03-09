package de.xstampp.service.notify.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.xstampp.common.service.ConfigurationService;

@Configuration
public class CommonBeanConfiguration {
	@Bean
	public ConfigurationService confService() {
		return new ConfigurationService();
	}
}
