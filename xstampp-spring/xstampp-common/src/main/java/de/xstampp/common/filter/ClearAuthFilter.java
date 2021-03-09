package de.xstampp.common.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import de.xstampp.common.service.SecurityService; 

public class ClearAuthFilter implements Filter{
	
	SecurityService securityService;

	public ClearAuthFilter(SecurityService service) {
		this.securityService = service;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		securityService.clear();
		
		chain.doFilter(request, response);
	}
}
