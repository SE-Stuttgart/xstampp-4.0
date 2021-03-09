package de.xstampp.service.auth.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.xstampp.common.filter.AuthenticationFilter;
import de.xstampp.common.filter.ClearAuthFilter;
import de.xstampp.common.service.SecurityService;
import de.xstampp.service.auth.controller.ExceptionController;

@Configuration
public class AuthFilterConfiguration {
	@Autowired
	SecurityService securitySerice;
	@Autowired
	ExceptionController handler;

	@Bean
	public FilterRegistrationBean<ClearAuthFilter> clearAuthFilter() {
		FilterRegistrationBean<ClearAuthFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(new ClearAuthFilter(securitySerice));
		registration.setName("ClearAuthFilter");
		registration.setOrder(1);
		return registration;
	}
	
	@Bean 
	public FilterRegistrationBean<AuthenticationFilter> authenticationFilter() {
		FilterRegistrationBean<AuthenticationFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(new AuthenticationFilter(securitySerice, handler));
		registration.addUrlPatterns("/api/auth/project-token/*");
		registration.addUrlPatterns("/api/auth/project/*");
		registration.addUrlPatterns("/api/auth/group/*");
		registration.addUrlPatterns("/api/auth/user/*");
		registration.setName("AuthenticationFilter");
		registration.setOrder(2);
		return registration;
	}
}
