package de.xstampp.common.service;

import java.util.UUID;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import de.xstampp.common.auth.SecurityContext;
import de.xstampp.common.auth.TokenConstants;

@Service
public class SecurityService {

	private ThreadLocal<Boolean> filterRan = ThreadLocal.withInitial(() -> Boolean.FALSE);
	private ThreadLocal<SecurityContext> storage = new ThreadLocal<>();
	private JWTVerifier verifier;
	
	@Autowired
	ConfigurationService config;
	
	Logger logger;

	public SecurityService() {
		logger = LoggerFactory.getLogger(this.getClass());
	}
	
	@PostConstruct
	@Order(2)
	public void init() {
		String secret = config.getStringProperty("constants.system.secret");
		verifier = JWT.require(Algorithm.HMAC512(secret)).build();
	}

	public void setContext(SecurityContext context) {
		this.filterRan.set(Boolean.TRUE);
		this.storage.set(context);
	}

	public SecurityContext getContext() {
		if (!this.filterRan.get()) {
			throw new IllegalStateException(
					"trying to access the security context in a call that wasn't security-filtered");
		}
		return storage.get();
	}

	// TODO: Complete documentation @Rico
	/**
	 *
	 * @param token
	 * @return the context. Null if token is invalid
	 */
	public SecurityContext createSecurityContext(String token) {
		SecurityContext context = new SecurityContext();
		
		try {
			if (token == null) {
				return null;
			}
			context.setToken(token);

			DecodedJWT decodedJwt = verifier.verify(token);
			
			for (Entry<String, Claim> entry : decodedJwt.getClaims().entrySet()) {
				context.setValue(entry.getKey(), entry.getValue().asString());
			}
			
			if (context.getValue(TokenConstants.CLAIM_UID) != null) {
				context.setUserId(UUID.fromString(context.getValue(TokenConstants.CLAIM_UID)));
			}
			
			if (context.getValue(TokenConstants.CLAIM_DISPLAY_NAME) != null) {
				context.setDisplayName(context.getValue(TokenConstants.CLAIM_DISPLAY_NAME));
			}

			if (context.getValue(TokenConstants.CLAIM_TOKEN_TYPE) != null) {
				context.setTokenType(context.getValue(TokenConstants.CLAIM_TOKEN_TYPE));
			}

			if (context.getValue(TokenConstants.CLAIM_PROJECT_ID) != null) {
				context.setProjectId(context.getValue(TokenConstants.CLAIM_PROJECT_ID));
			}
			
//			if (context.getValue(TokenConstants.CLAIM_PROJECT_NAME) != null) {
//				context.setProjectId(context.getValue(TokenConstants.CLAIM_PROJECT_NAMED));
//			}  // outdated, but maybe we add this constant again in the future?

			if (context.getValue(TokenConstants.CLAIM_PROJECT_ROLE) != null) {
				context.setProjectRole(context.getValue(TokenConstants.CLAIM_PROJECT_ROLE));
			}

			if (context.getValue("role") != null) {
				context.setRole(context.getValue("role"));
			}

		} catch (SignatureVerificationException sve) {
			logger.error("invalid token", sve);
			return null;
		} catch (JWTDecodeException jde) {
			logger.error("invalid json", jde);
			return null;
		}
		
		
		return context;
	}

	public void clear() {
		filterRan.set(Boolean.FALSE);
	}
}
