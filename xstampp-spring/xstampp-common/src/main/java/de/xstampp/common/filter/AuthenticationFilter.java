package de.xstampp.common.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;

import de.xstampp.common.auth.SecurityContext;
import de.xstampp.common.errors.ErrorsPerm;
import de.xstampp.common.errors.GenericXSTAMPPException;
import de.xstampp.common.errors.GenericXSTAMPPExceptionHandler;
import de.xstampp.common.service.SecurityService; 

public class AuthenticationFilter implements Filter{
	
	private SecurityService securityService;
	private GenericXSTAMPPExceptionHandler handler;

	public AuthenticationFilter(SecurityService service, GenericXSTAMPPExceptionHandler handler) {
		this.securityService = service;
		this.handler = handler;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		try {
			String authHeader = httpRequest.getHeader("Authorization");
			if (authHeader == null) {
				throw ErrorsPerm.TOKEN_MISSING.exc();
			}
			SecurityContext context = securityService.createSecurityContext(authHeader);
			securityService.setContext(context);

			// if token could not be extracted
			if (context == null) {
				throw ErrorsPerm.TOKEN_INVALID.exc();
			}

			chain.doFilter(request, response);
		} catch (GenericXSTAMPPException e) {
			ResponseEntity<String> errorResponse = handler.xstamppException(httpRequest, e);
			errorResponse.getHeaders().forEach((h, v) -> v.forEach(iv -> httpResponse.addHeader(h, iv)));
			httpResponse.setStatus(errorResponse.getStatusCodeValue());
			httpResponse.getWriter().write(errorResponse.getBody());
		}
	}
}

