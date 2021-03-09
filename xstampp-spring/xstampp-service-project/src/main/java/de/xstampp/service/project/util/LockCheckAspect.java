package de.xstampp.service.project.util;

import de.xstampp.common.auth.SecurityContext;
import de.xstampp.common.service.ConfigurationService;
import de.xstampp.common.service.SecurityService;
import de.xstampp.common.utils.EntityNameConstants;
import de.xstampp.service.project.data.XStamppEntity;
import de.xstampp.service.project.data.entity.EntityDependentKey;
import de.xstampp.service.project.data.entity.Project;
import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.exceptions.ProjectException;
import de.xstampp.service.project.service.LockService;
import de.xstampp.service.project.service.dao.iface.IEntityDependentGenericDAO;
import de.xstampp.service.project.service.dao.iface.IProjectDependentGenericDAO;
import de.xstampp.service.project.service.data.ProjectDataService;
import de.xstampp.service.project.service.data.SystemDescriptionDataService;
import de.xstampp.service.project.util.annotation.CheckLock;
import de.xstampp.service.project.util.annotation.XStamppEntityId;
import de.xstampp.service.project.util.annotation.XStamppEntityParentId;
import de.xstampp.service.project.util.annotation.XStamppProjectId;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Part of Aspect-oriented-approach. Executed every time RestController method has @CheckLock Annotation
 */
@Aspect
@Component
public class LockCheckAspect {

	@Autowired
	SecurityService securityService;

	@Autowired
	LockService lockService;

	@Autowired
	ConfigurationService confService;

	@Autowired
	ProjectDataService projectService;

	@Autowired
	SystemDescriptionDataService systemDescriptionService;

	@Before("@annotation(de.xstampp.service.project.util.annotation.CheckLock)")
	public void checkLock(JoinPoint jointPoint) {
		String enforceLock = confService.getStringProperty("constants.system.enforceLock");
		if (enforceLock != null && !Boolean.parseBoolean(enforceLock)) {
			return;
		}

		SecurityContext context = securityService.getContext();

		if (context == null) {
			throw new IllegalStateException("No valid token available");
		}

		// extract set privileges from method annotation
		MethodSignature methodSignature = (MethodSignature) jointPoint.getSignature();
		Method method = methodSignature.getMethod();
		CheckLock checkAnnotation = method.getAnnotation(CheckLock.class);
		String entityName;
		// check if annotation is present. If not use highest group privilege as default
		if (checkAnnotation != null) {
			entityName = checkAnnotation.entity();
		} else {
			throw new RuntimeException("no annotation found");
		}

		// get all parameter annotations
		Annotation[][] allAnnotations = method.getParameterAnnotations();
		int entityIdIndex = -1;
		int projectIdIndex = -1;
		int entityParentIdIndex = -1;

		for (int i = 0; i < allAnnotations.length; i++) {
			// determine index of desired parameters from method signature
			for (Annotation anno : allAnnotations[i]) {
				if (anno instanceof XStamppProjectId) {
					projectIdIndex = i;
				}
				if (anno instanceof XStamppEntityId) {
					entityIdIndex = i;
				}
				if (anno instanceof XStamppEntityParentId) {
					entityParentIdIndex = i;
				}
			}
		}

		// check for sub entities and entity dependent
		if (entityName.equals(EntityNameConstants.SUB_HAZARD)
				|| entityName.equals(EntityNameConstants.SUB_SYSTEM_CONSTRAINT)
				|| entityName.equals(EntityNameConstants.UNSAFE_CONTROL_ACTION)
				|| entityName.equals(EntityNameConstants.CONTROLLER_CONSTRAINT)
				|| entityName.equals(EntityNameConstants.RULE)
				|| entityName.equals(EntityNameConstants.CONVERSION)) {
			if (projectIdIndex == -1 || entityIdIndex == -1 || entityParentIdIndex == -1) {
				throw new ProjectException("invalid parameters");
			}
			UUID projectId = UUID.fromString(String.valueOf(jointPoint.getArgs()[projectIdIndex]));
			int entityId = Integer.valueOf(String.valueOf(jointPoint.getArgs()[entityIdIndex]));
			int entityParentId = Integer.valueOf(String.valueOf(jointPoint.getArgs()[entityParentIdIndex]));

			IEntityDependentGenericDAO<? extends XStamppEntity, EntityDependentKey> dao = lockService
					.getDAOForEntitiyDependent(entityName);
			EntityDependentKey key = new EntityDependentKey(projectId, entityParentId, entityId);

			if (!checkLock(context.getUserId(), lockService.fetchEntity(dao, key))) {
				throw new ProjectException("No valid lock");
			}

			// check for control structure
		} else if (entityName.equals(EntityNameConstants.CONTROL_STRUCTURE)) {
			if (projectIdIndex == -1) {
				throw new ProjectException("no provided projectid annotation");
			}
			UUID projectId = UUID.fromString(String.valueOf(jointPoint.getArgs()[projectIdIndex]));

			if (!checkLockForProject(context.getUserId(), projectService.getProjectById(projectId))) {
				throw new ProjectException("No valid lock");
			}

			// check for system description
		} else if (entityName.equals(EntityNameConstants.SYSTEM_DESCRIPTION)) {
			if (projectIdIndex == -1) {
				throw new ProjectException("no provided projectid annotation");
			}
			UUID projectId = UUID.fromString(String.valueOf(jointPoint.getArgs()[projectIdIndex]));

			if (!checkLock(context.getUserId(), systemDescriptionService.getById(projectId))) {
				throw new ProjectException("No valid lock");
			}
			// other entities are project dependent
		} else {
			if (projectIdIndex == -1 || entityIdIndex == -1) {
				throw new RuntimeException("invalid parameter annotations");
			}
			IProjectDependentGenericDAO<? extends XStamppEntity, ProjectDependentKey> dao = lockService
					.getDAOForProjectEntitiy(entityName);

			UUID projectId = UUID.fromString(String.valueOf(jointPoint.getArgs()[projectIdIndex]));
			int entityId = Integer.valueOf(String.valueOf(jointPoint.getArgs()[entityIdIndex]));

			ProjectDependentKey key = new ProjectDependentKey(projectId, entityId);

			XStamppEntity entity = lockService.fetchEntity(dao, key);
			if (!checkLock(context.getUserId(), entity)) {
				throw new ProjectException("No valid lock");
			}
		}
	}

	// checks if the lock is valid
	private boolean checkLock(UUID userId, XStamppEntity entity) {
		return userId.equals(entity.getLockHolderId());
	}

	private boolean checkLockForProject(UUID userId, Project project) {
		return userId.equals(project.getLockHolderId());
	}

}
