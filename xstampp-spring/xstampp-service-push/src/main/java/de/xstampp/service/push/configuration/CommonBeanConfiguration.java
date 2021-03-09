package de.xstampp.service.push.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import de.xstampp.common.service.ConfigurationService;
import de.xstampp.common.service.SecurityService;

@Configuration
@EnableAsync
@EnableScheduling
public class CommonBeanConfiguration {
	@Bean
	/**
	 * Configures the configuration service bean located in the commons
	 * @return returns the configuration service instance
	 */
	public ConfigurationService confService() {
		return new ConfigurationService();
	}

	@Bean
	/**
	 * Configures the security service bean located in the commons
	 * @return returns the security service instance
	 */
	public SecurityService securityService() {
		return new SecurityService();
	}
	
	@Bean
	/**
	 * Configures the task scheduler bean needed for the scheduled keep alive task
	 * @return returns the task scheduler instance
	 */
	public TaskScheduler taskScheduler () {
		return new ThreadPoolTaskScheduler();
	}
	
	@Bean
	/**
	 * Configures the task executor bean needed for the scheduled keep alive task
	 * @return returns the task executer instance
	 */
	public TaskExecutor taskExecuter () {
		return new SimpleAsyncTaskExecutor();
	}
}
