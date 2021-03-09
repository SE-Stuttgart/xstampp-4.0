package de.xstampp.service.project.util;

import de.xstampp.common.auth.SecurityContext;
import de.xstampp.common.errors.ErrorsPerm;
import de.xstampp.common.service.SecurityService;
import de.xstampp.common.utils.PrivilegeCheck;
import de.xstampp.common.utils.Privileges;
import de.xstampp.service.project.util.annotation.XStamppProjectId;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.security.GeneralSecurityException;

/**
 * This class follows the aspect oriented programming principle and the method
 * will be invoked before the method execution for the privileges check. The
 * execution conditions are defined on the methods
 * 
 * @author Tobias Weiß
 *
 */
@Aspect
@Component
public class PrivilegeCheckAspect {

	@Autowired
	SecurityService securityService;

	Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * This method checks if a user is allowed to perform a certian action.
	 * Therefore the method in the rest controllers has to be annotated with the
	 * {@link PrivilegeCheck} annotation. With this annotation the access level can
	 * be defined
	 * 
	 * @param jointPoint the invoke joinPoint with access to information of the
	 *                   invoked method
	 * @throws RuntimeException wrapped {@link GeneralSecurityException} in order to
	 *                          be handled by the AOP Spring framework
	 */
	@Before("within(@org.springframework.web.bind.annotation.RestController *) &&  !within(@de.xstampp.common.utils.IgnorePrivilegeCheck *)")
	public void checkPrivileges(JoinPoint jointPoint) throws RuntimeException {

		SecurityContext context = securityService.getContext();

		// check if token information is available
		if (context == null || context.getRole() == null) {
			throw ErrorsPerm.TOKEN_MISSING.exc();
		}
		
		// compare token project id and requested project
		if (!compareProjectIdWithToken(jointPoint)) {
			throw ErrorsPerm.TOKEN_MISMATCHING.exc();
		}

		Privileges userPrivilege;
		try {
			userPrivilege = Privileges.valueOf(context.getRole().toUpperCase());
		} catch (IllegalArgumentException iae) {
			// in case of matching errors the user has a valid token and the roles can not
			// matched. Therefore no error but continue with minimal rights.
			userPrivilege = Privileges.GUEST;
		}

		// extract set privileges from method annotation
		MethodSignature methodSignature = (MethodSignature) jointPoint.getSignature();
		Method method = methodSignature.getMethod();
		PrivilegeCheck checkAnnotation = method.getAnnotation(PrivilegeCheck.class);
		Privileges neededPrivilege;
		// check if annotation is present. If not use highest group privilege as default
		if (checkAnnotation != null) {
			neededPrivilege = checkAnnotation.privilege();
		} else {
			neededPrivilege = Privileges.GROUP_ADMIN;
		}

		// determine if user is allowed to use this function
		if (userPrivilege.ordinal() >= neededPrivilege.ordinal()) {
			logger.debug("access granted. Privilege sufficient.");
		} else {
			throw ErrorsPerm.NEED_PROJ_GENERIC.exc();
		}
	}
	
	private boolean compareProjectIdWithToken (JoinPoint jointPoint) {
		String tokenProjectId = securityService.getContext().getProjectId();
		
		MethodSignature methodSignature = (MethodSignature) jointPoint.getSignature();
		Method method = methodSignature.getMethod();
		Annotation[][] allAnnotations = method.getParameterAnnotations();
		int projectIdIndex = -1;

		for (int i = 0; i < allAnnotations.length; i++) {
			// determine index of desired parameters from method signature
			for (Annotation anno : allAnnotations[i]) {
				if (anno instanceof XStamppProjectId) {
					projectIdIndex = i;
				}
			}
		}
		
		// check if annotated project id was defined
		if (projectIdIndex == -1) {
			return false;
		}
		
		Object parameterResult  = jointPoint.getArgs()[projectIdIndex];
		
		if (parameterResult instanceof String) {
			return tokenProjectId.equals((String) parameterResult);
		} else {
			return false;
		}
	}
}
