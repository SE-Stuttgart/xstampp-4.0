package de.xstampp.service.project.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.xstampp.common.filter.AuthenticationFilter;
import de.xstampp.common.filter.ClearAuthFilter;
import de.xstampp.common.service.SecurityService;
import de.xstampp.service.project.controller.ExceptionController;

@Configuration
public class AuthFilterConfiguration {
	@Autowired
	SecurityService securitySerice;
	@Autowired
	ExceptionController handler;

	@Bean
	/**
	 * This configuration enables the authentication clear filter. This clears the
	 * thread local security storage before each new context creation.
	 * 
	 * @return
	 */
	public FilterRegistrationBean<ClearAuthFilter> clearAuthFilter() {
		FilterRegistrationBean<ClearAuthFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(new ClearAuthFilter(securitySerice));
		registration.setName("ClearAuthFilter");
		registration.setOrder(1);
		return registration;
	}

	@Bean
	/**
	 * This configuration enables the authentication filter that is located in the
	 * commons
	 * 
	 * @return the filter instance
	 */
	public FilterRegistrationBean<AuthenticationFilter> authenticationFilter() {
		FilterRegistrationBean<AuthenticationFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(new AuthenticationFilter(securitySerice, handler));
		registration.addUrlPatterns("/api/project/*");
		registration.setName("AuthenticationFilter");
		registration.setOrder(2);
		return registration;
	}
}
