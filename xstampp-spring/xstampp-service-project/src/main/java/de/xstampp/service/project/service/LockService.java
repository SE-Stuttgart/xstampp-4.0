package de.xstampp.service.project.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import de.xstampp.service.project.service.dao.iface.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import de.xstampp.common.auth.SecurityContext;
import de.xstampp.common.service.SecurityService;
import de.xstampp.common.utils.EntityNameConstants;
import de.xstampp.service.project.data.XStamppEntity;
import de.xstampp.service.project.data.dto.LockRequestDTO;
import de.xstampp.service.project.data.entity.EntityDependentKey;
import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.service.dao.control_structure.iface.IActuatorDAO;
import de.xstampp.service.project.service.dao.control_structure.iface.IControlActionDAO;
import de.xstampp.service.project.service.dao.control_structure.iface.IControlAlgorithmDAO;
import de.xstampp.service.project.service.dao.control_structure.iface.IControlledProcessDAO;
import de.xstampp.service.project.service.dao.control_structure.iface.IControllerDAO;
import de.xstampp.service.project.service.dao.control_structure.iface.IFeedbackDAO;
import de.xstampp.service.project.service.dao.control_structure.iface.IInputDAO;
import de.xstampp.service.project.service.dao.control_structure.iface.IOutputDAO;
import de.xstampp.service.project.service.dao.control_structure.iface.IProcessModelDAO;
import de.xstampp.service.project.service.dao.control_structure.iface.IProcessVariableDAO;
import de.xstampp.service.project.service.dao.control_structure.iface.ISensorDAO;
import de.xstampp.service.project.service.dao.iface.IControlStructureDAO;
import de.xstampp.service.project.service.dao.iface.IControllerConstraintDAO;
import de.xstampp.service.project.service.dao.iface.IEntityDependentGenericDAO;
import de.xstampp.service.project.service.dao.iface.IHazardDAO;
import de.xstampp.service.project.service.dao.iface.ILossDAO;
import de.xstampp.service.project.service.dao.iface.IProjectDAO;
import de.xstampp.service.project.service.dao.iface.IProjectDependentGenericDAO;
import de.xstampp.service.project.service.dao.iface.IResponsibilityDAO;
import de.xstampp.service.project.service.dao.iface.ISubHazardDAO;
import de.xstampp.service.project.service.dao.iface.ISubSystemConstraintDAO;
import de.xstampp.service.project.service.dao.iface.ISystemConstraintDAO;
import de.xstampp.service.project.service.dao.iface.ISystemDescriptionDAO;
import de.xstampp.service.project.service.dao.iface.IUnsafeControlActionDAO;
import de.xstampp.service.project.service.dao.iface.IRuleDAO;
import de.xstampp.service.project.service.dao.iface.IConversionDAO;
import de.xstampp.service.project.util.LockCheckAspect;

@Service
@Transactional
public class LockService {

	@Autowired
	IProjectDAO projectDAO;

	@Autowired
	IActuatorDAO actuatorDAO;

	@Autowired
	IControlStructureDAO controlStructureDAO;

	@Autowired
	IControlActionDAO controlActionDAO;

	@Autowired
	IControlAlgorithmDAO controlAlgorithmDAO;

	@Autowired
	IControllerConstraintDAO controllerConstrtaintDAO;

	@Autowired
	IControlledProcessDAO controlledProcessDAO;

	@Autowired
	IControllerDAO controllerDAO;

	@Autowired
	IFeedbackDAO feedbackDAO;

	@Autowired
	IHazardDAO hazardDAO;

	@Autowired
	IInputDAO inputDAO;

	@Autowired
	ILossDAO lossDAO;

	@Autowired
	ILossScenarioDAO lossScenarioDAO;

	@Autowired
	IOutputDAO outputDAO;

	@Autowired
	IProcessModelDAO processModelDAO;
	@Autowired
	IProcessVariableDAO processVariableDAO;

	@Autowired
	IResponsibilityDAO responsibilityDAO;

	@Autowired
	ISensorDAO sensorDAO;

	@Autowired
	ISubHazardDAO subHazardDAO;

	@Autowired
	ISubSystemConstraintDAO subSystemConstraintDAO;

	@Autowired
	ISystemConstraintDAO systemConstraintDAO;

	@Autowired
	ISystemDescriptionDAO systemDescriptionDAO;

	@Autowired
	IUnsafeControlActionDAO unsafeControlActionDAO;

	@Autowired
	IImplementationConstraintDAO implementationConstraintDAO;
	
	@Autowired
	IRuleDAO ruleDAO;
	
	@Autowired
	IConversionDAO conversionDAO;

	@Autowired
	SecurityService securityService;

	private final long MAX_LOCK_DURATION = 15 * 60 * 1000; // in ms
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private enum Mode {
		LOCK, UNLOCK
	}

	public boolean lockEntity(LockRequestDTO lockRequest, UUID projectID) {
		if (validateSecuityContext(securityService.getContext())) {
			return execute(lockRequest, projectID, securityService.getContext().getUserId(),
					securityService.getContext().getDisplayName(), Mode.LOCK);
		}
		return false;
	}

	public boolean unlockEntity(LockRequestDTO unlockRequest, UUID projectID) {
		if (validateSecuityContext(securityService.getContext())) {
			return execute(unlockRequest, projectID, securityService.getContext().getUserId(),
					securityService.getContext().getDisplayName(), Mode.UNLOCK);
		}
		return false;
	}

	private boolean validateSecuityContext(SecurityContext context) {
		if (context != null) {
			if (context.getUserId() != null && !StringUtils.isEmpty(context.getDisplayName())) {
				return true;
			}
		}
		return false;
	}

	private boolean execute(LockRequestDTO lockRequest, UUID projectID, UUID userId, String userName, Mode mode) {

		if (!validateInputs(lockRequest, mode)) {
			return false;
		}
		
		if (mode.equals(Mode.LOCK) && !checkExpTimeLength(lockRequest.getExpirationTime())) {
			logger.error("expiration time {} exeeds maximum of {} milliseconds", lockRequest.getExpirationTime().toString(), MAX_LOCK_DURATION);
			return false;
		}

		switch (lockRequest.getEntityName()) {
		case EntityNameConstants.ACTUATOR:
			if (mode == Mode.LOCK) {
				ProjectDependentKey id = new ProjectDependentKey(projectID, Integer.parseInt(lockRequest.getId()));
				return actuatorDAO.lockEntity(id, userId, userName, lockRequest.getExpirationTime());
			} else if (mode == Mode.UNLOCK) {
				ProjectDependentKey id = new ProjectDependentKey(projectID, Integer.parseInt(lockRequest.getId()));
				return actuatorDAO.unlockEntity(id, userId, userName);
			} else {
				throw new IllegalStateException("unable to lock or unlock actuator");
			}
		case EntityNameConstants.CONTROL_STRUCTURE:
			if (mode == Mode.LOCK) {
				return projectDAO.lockEntity(projectID, userId, userName, lockRequest.getExpirationTime());
			} else if (mode == Mode.UNLOCK) {
				return projectDAO.unlockEntity(projectID, userId, userName);
			} else {
				throw new IllegalStateException("unable to lock or unlock actuator");
			}
		case EntityNameConstants.CONTROL_ACTION:
			if (mode == Mode.LOCK) {
				ProjectDependentKey id = new ProjectDependentKey(projectID, Integer.parseInt(lockRequest.getId()));
				return controlActionDAO.lockEntity(id, userId, userName, lockRequest.getExpirationTime());
			} else if (mode == Mode.UNLOCK) {
				ProjectDependentKey id = new ProjectDependentKey(projectID, Integer.parseInt(lockRequest.getId()));
				return controlActionDAO.unlockEntity(id, userId, userName);
			} else {
				throw new IllegalStateException("unable to lock or unlock control action");
			}
		case EntityNameConstants.CONTROL_ALGORITHM:
			if (mode == Mode.LOCK) {
				ProjectDependentKey id = new ProjectDependentKey(projectID, Integer.parseInt(lockRequest.getId()));
				return controlAlgorithmDAO.lockEntity(id, userId, userName, lockRequest.getExpirationTime());
			} else if (mode == Mode.UNLOCK) {
				ProjectDependentKey id = new ProjectDependentKey(projectID, Integer.parseInt(lockRequest.getId()));
				return controlAlgorithmDAO.unlockEntity(id, userId, userName);
			} else {
				throw new IllegalStateException("unable to lock or unlock control algorithm");
			}
		case EntityNameConstants.CONTROLLER_CONSTRAINT:
			if (mode == Mode.LOCK) {
				EntityDependentKey id = new EntityDependentKey(projectID, Integer.parseInt(lockRequest.getParentId()),
						Integer.parseInt(lockRequest.getId()));
				return controllerConstrtaintDAO.lockEntity(id, userId, userName, lockRequest.getExpirationTime());
			} else if (mode == Mode.UNLOCK) {
				EntityDependentKey id = new EntityDependentKey(projectID, Integer.parseInt(lockRequest.getParentId()),
						Integer.parseInt(lockRequest.getId()));
				return controllerConstrtaintDAO.unlockEntity(id, userId, userName);
			} else {
				throw new IllegalStateException("unable to lock or unlock actuator");
			}
		case EntityNameConstants.CONTROLLED_PROCESS:
			if (mode == Mode.LOCK) {
				ProjectDependentKey id = new ProjectDependentKey(projectID, Integer.parseInt(lockRequest.getId()));
				return controlledProcessDAO.lockEntity(id, userId, userName, lockRequest.getExpirationTime());
			} else if (mode == Mode.UNLOCK) {
				ProjectDependentKey id = new ProjectDependentKey(projectID, Integer.parseInt(lockRequest.getId()));
				return controlledProcessDAO.unlockEntity(id, userId, userName);
			} else {
				throw new IllegalStateException("unable to lock or unlock controlled process");
			}
		case EntityNameConstants.CONTROLLER:
			if (mode == Mode.LOCK) {
				ProjectDependentKey id = new ProjectDependentKey(projectID, Integer.parseInt(lockRequest.getId()));
				return controllerDAO.lockEntity(id, userId, userName, lockRequest.getExpirationTime());
			} else if (mode == Mode.UNLOCK) {
				ProjectDependentKey id = new ProjectDependentKey(projectID, Integer.parseInt(lockRequest.getId()));
				return controllerDAO.unlockEntity(id, userId, userName);
			} else {
				throw new IllegalStateException("unable to lock or unlock controller");
			}
		case EntityNameConstants.FEEDBACK:
			if (mode == Mode.LOCK) {
				ProjectDependentKey id = new ProjectDependentKey(projectID, Integer.parseInt(lockRequest.getId()));
				return feedbackDAO.lockEntity(id, userId, userName, lockRequest.getExpirationTime());
			} else if (mode == Mode.UNLOCK) {
				ProjectDependentKey id = new ProjectDependentKey(projectID, Integer.parseInt(lockRequest.getId()));
				return feedbackDAO.unlockEntity(id, userId, userName);
			} else {
				throw new IllegalStateException("unable to lock or unlock feedback");
			}
		case EntityNameConstants.HAZARD:
			if (mode == Mode.LOCK) {
				ProjectDependentKey id = new ProjectDependentKey(projectID, Integer.parseInt(lockRequest.getId()));
				return hazardDAO.lockEntity(id, userId, userName, lockRequest.getExpirationTime());
			} else if (mode == Mode.UNLOCK) {
				ProjectDependentKey id = new ProjectDependentKey(projectID, Integer.parseInt(lockRequest.getId()));
				return hazardDAO.unlockEntity(id, userId, userName);
			} else {
				throw new IllegalStateException("unable to lock or unlock hazard");
			}
		case EntityNameConstants.INPUT:
			if (mode == Mode.LOCK) {
				ProjectDependentKey id = new ProjectDependentKey(projectID, Integer.parseInt(lockRequest.getId()));
				return inputDAO.lockEntity(id, userId, userName, lockRequest.getExpirationTime());
			} else if (mode == Mode.UNLOCK) {
				ProjectDependentKey id = new ProjectDependentKey(projectID, Integer.parseInt(lockRequest.getId()));
				return inputDAO.unlockEntity(id, userId, userName);
			} else {
				throw new IllegalStateException("unable to lock or unlock input");
			}
		case EntityNameConstants.LOSS:
			if (mode == Mode.LOCK) {
				ProjectDependentKey id = new ProjectDependentKey(projectID, Integer.parseInt(lockRequest.getId()));
				return lossDAO.lockEntity(id, userId, userName, lockRequest.getExpirationTime());
			} else if (mode == Mode.UNLOCK) {
				ProjectDependentKey id = new ProjectDependentKey(projectID, Integer.parseInt(lockRequest.getId()));
				return lossDAO.unlockEntity(id, userId, userName);
			} else {
				throw new IllegalStateException("unable to lock or unlock loss");
			}
		case EntityNameConstants.LOSS_SCENARIO:
			if (mode == Mode.LOCK) {
				ProjectDependentKey id = new ProjectDependentKey(projectID, Integer.parseInt(lockRequest.getId()));
				return lossScenarioDAO.lockEntity(id, userId, userName, lockRequest.getExpirationTime());
			} else if (mode == Mode.UNLOCK) {
				ProjectDependentKey id = new ProjectDependentKey(projectID, Integer.parseInt(lockRequest.getId()));
				return lossScenarioDAO.unlockEntity(id, userId, userName);
			} else {
				throw new IllegalStateException("unable to lock or unlock loss scenario");
			}
		case EntityNameConstants.OUTPUT:
			if (mode == Mode.LOCK) {
				ProjectDependentKey id = new ProjectDependentKey(projectID, Integer.parseInt(lockRequest.getId()));
				return outputDAO.lockEntity(id, userId, userName, lockRequest.getExpirationTime());
			} else if (mode == Mode.UNLOCK) {
				ProjectDependentKey id = new ProjectDependentKey(projectID, Integer.parseInt(lockRequest.getId()));
				return outputDAO.unlockEntity(id, userId, userName);
			} else {
				throw new IllegalStateException("unable to lock or unlock output");
			}
			case EntityNameConstants.PROCESS_MODEL:
			if (mode == Mode.LOCK) {
				ProjectDependentKey id = new ProjectDependentKey(projectID, Integer.parseInt(lockRequest.getId()));
				return processModelDAO.lockEntity(id, userId, userName, lockRequest.getExpirationTime());
			} else if (mode == Mode.UNLOCK) {
				ProjectDependentKey id = new ProjectDependentKey(projectID, Integer.parseInt(lockRequest.getId()));
				return processModelDAO.unlockEntity(id, userId, userName);
			} else {
				throw new IllegalStateException("unable to lock or unlock process model");
			}
		case EntityNameConstants.PROCESS_VARIABLE:
			if (mode == Mode.LOCK) {
				ProjectDependentKey id = new ProjectDependentKey(projectID, Integer.parseInt(lockRequest.getId()));
				return processVariableDAO.lockEntity(id, userId, userName, lockRequest.getExpirationTime());
			} else if (mode == Mode.UNLOCK) {
				ProjectDependentKey id = new ProjectDependentKey(projectID, Integer.parseInt(lockRequest.getId()));
				return processVariableDAO.unlockEntity(id, userId, userName);
			} else {
				throw new IllegalStateException("unable to lock or unlock process variable");
			}
		case EntityNameConstants.RESPONSIBILITY:
			if (mode == Mode.LOCK) {
				ProjectDependentKey id = new ProjectDependentKey(projectID, Integer.parseInt(lockRequest.getId()));
				return responsibilityDAO.lockEntity(id, userId, userName, lockRequest.getExpirationTime());
			} else if (mode == Mode.UNLOCK) {
				ProjectDependentKey id = new ProjectDependentKey(projectID, Integer.parseInt(lockRequest.getId()));
				return responsibilityDAO.unlockEntity(id, userId, userName);
			} else {
				throw new IllegalStateException("unable to lock or unlock responsibility");
			}
		case EntityNameConstants.SENSOR:
			if (mode == Mode.LOCK) {
				ProjectDependentKey id = new ProjectDependentKey(projectID, Integer.parseInt(lockRequest.getId()));
				return sensorDAO.lockEntity(id, userId, userName, lockRequest.getExpirationTime());
			} else if (mode == Mode.UNLOCK) {
				ProjectDependentKey id = new ProjectDependentKey(projectID, Integer.parseInt(lockRequest.getId()));
				return sensorDAO.unlockEntity(id, userId, userName);
			} else {
				throw new IllegalStateException("unable to lock or unlock sensor");
			}
		case EntityNameConstants.SUB_HAZARD:
			if (mode == Mode.LOCK) {
				EntityDependentKey id = new EntityDependentKey(projectID, Integer.parseInt(lockRequest.getParentId()),
						Integer.parseInt(lockRequest.getId()));
				return subHazardDAO.lockEntity(id, userId, userName, lockRequest.getExpirationTime());
			} else if (mode == Mode.UNLOCK) {
				EntityDependentKey id = new EntityDependentKey(projectID, Integer.parseInt(lockRequest.getParentId()),
						Integer.parseInt(lockRequest.getId()));
				return subHazardDAO.unlockEntity(id, userId, userName);
			} else {
				throw new IllegalStateException("unable to lock or unlock sub hazard");
			}
		case EntityNameConstants.SUB_SYSTEM_CONSTRAINT:
			if (mode == Mode.LOCK) {
				EntityDependentKey id = new EntityDependentKey(projectID, Integer.parseInt(lockRequest.getParentId()),
						Integer.parseInt(lockRequest.getId()));
				return subSystemConstraintDAO.lockEntity(id, userId, userName, lockRequest.getExpirationTime());
			} else if (mode == Mode.UNLOCK) {
				EntityDependentKey id = new EntityDependentKey(projectID, Integer.parseInt(lockRequest.getParentId()),
						Integer.parseInt(lockRequest.getId()));
				return subSystemConstraintDAO.unlockEntity(id, userId, userName);
			} else {
				throw new IllegalStateException("unable to lock or unlock sub system constraint");
			}
		case EntityNameConstants.SYSTEM_CONSTRAINT:
			if (mode == Mode.LOCK) {
				ProjectDependentKey id = new ProjectDependentKey(projectID, Integer.parseInt(lockRequest.getId()));
				return systemConstraintDAO.lockEntity(id, userId, userName, lockRequest.getExpirationTime());
			} else if (mode == Mode.UNLOCK) {
				ProjectDependentKey id = new ProjectDependentKey(projectID, Integer.parseInt(lockRequest.getId()));
				return systemConstraintDAO.unlockEntity(id, userId, userName);
			} else {
				throw new IllegalStateException("unable to lock or unlock system constraint");
			}
		case EntityNameConstants.SYSTEM_DESCRIPTION:
			if (mode == Mode.LOCK) {
				return systemDescriptionDAO.lockEntity(projectID, userId, userName, lockRequest.getExpirationTime());
			} else if (mode == Mode.UNLOCK) {
				return systemDescriptionDAO.unlockEntity(projectID, userId, userName);
			} else {
				throw new IllegalStateException("unable to lock or unlock actuator");
			}
		case EntityNameConstants.UNSAFE_CONTROL_ACTION:
			if (mode == Mode.LOCK) {
				EntityDependentKey id = new EntityDependentKey(projectID, Integer.parseInt(lockRequest.getParentId()),
						Integer.parseInt(lockRequest.getId()));
				return unsafeControlActionDAO.lockEntity(id, userId, userName, lockRequest.getExpirationTime());
			} else if (mode == Mode.UNLOCK) {
				EntityDependentKey id = new EntityDependentKey(projectID, Integer.parseInt(lockRequest.getParentId()),
						Integer.parseInt(lockRequest.getId()));
				return unsafeControlActionDAO.unlockEntity(id, userId, userName);
			} else {
				throw new IllegalStateException("unable to lock or unlock actuator");
			}
		case EntityNameConstants.RULE:
			if (mode == Mode.LOCK) {
				EntityDependentKey id = new EntityDependentKey(projectID, Integer.parseInt(lockRequest.getParentId()),
						Integer.parseInt(lockRequest.getId()));
				return ruleDAO.lockEntity(id, userId, userName, lockRequest.getExpirationTime());
			} else if (mode == Mode.UNLOCK) {
				EntityDependentKey id = new EntityDependentKey(projectID, Integer.parseInt(lockRequest.getParentId()),
						Integer.parseInt(lockRequest.getId()));
				return ruleDAO.unlockEntity(id, userId, userName);
			} else {
				throw new IllegalStateException("unable to lock or unlock rule");
			}
		case EntityNameConstants.IMPLEMENTATION_CONSTRAINT:
			if (mode == Mode.LOCK) {
				ProjectDependentKey id = new ProjectDependentKey(projectID, Integer.parseInt(lockRequest.getId()));
				return implementationConstraintDAO.lockEntity(id, userId, userName, lockRequest.getExpirationTime());
			} else if (mode == Mode.UNLOCK) {
				ProjectDependentKey id = new ProjectDependentKey(projectID, Integer.parseInt(lockRequest.getId()));
				return implementationConstraintDAO.unlockEntity(id, userId, userName);
			} else {
				throw new IllegalStateException("unable to lock or unlock implementation constraint");
			}
		case EntityNameConstants.CONVERSION:
			if (mode == Mode.LOCK) {
				EntityDependentKey id = new EntityDependentKey(projectID, Integer.parseInt(lockRequest.getParentId()),
						Integer.parseInt(lockRequest.getId()));
				return conversionDAO.lockEntity(id, userId, userName, lockRequest.getExpirationTime());
			} else if (mode == Mode.UNLOCK) {
				EntityDependentKey id = new EntityDependentKey(projectID, Integer.parseInt(lockRequest.getParentId()),
						Integer.parseInt(lockRequest.getId()));
				return conversionDAO.unlockEntity(id, userId, userName);
			} else {
				throw new IllegalStateException("unable to lock or unlock conversion");
			}
		default:
			throw new IllegalArgumentException("no matching entity found");
		}
	}

	private boolean validateInputs(LockRequestDTO lock, Mode mode) {
		if (StringUtils.isEmpty(lock.getEntityName())) {
			return false;
		}
		if (mode == Mode.LOCK && lock.getExpirationTime().before(Timestamp.from(Instant.now()))) {
			return false;
		}
		if (StringUtils.isEmpty(lock.getId())) {
			return false;
		}

		return true;
	}

	private boolean checkExpTimeLength (Timestamp expTime) {
		return expTime.before(Timestamp.from(Instant.now().plusMillis(MAX_LOCK_DURATION)));
	}

	// TODO: Complete Documentation @Rico
	/**
	 * 
	 * @param entity the entity defined in {@link EntityNameConstants}
	 * @return
	 */
	public IProjectDependentGenericDAO<? extends XStamppEntity, ProjectDependentKey> getDAOForProjectEntitiy(
			String entity) {
		switch (entity) {
		case EntityNameConstants.ACTUATOR:
			return actuatorDAO;
		case EntityNameConstants.CONTROL_ACTION:
			return controlActionDAO;
		case EntityNameConstants.CONTROL_ALGORITHM:
			return controlAlgorithmDAO;
		case EntityNameConstants.CONTROLLED_PROCESS:
			return controlledProcessDAO;
		case EntityNameConstants.CONTROLLER:
			return controllerDAO;
		case EntityNameConstants.FEEDBACK:
			return feedbackDAO;
		case EntityNameConstants.HAZARD:
			return hazardDAO;
		case EntityNameConstants.INPUT:
			return inputDAO;
		case EntityNameConstants.LOSS:
			return lossDAO;
		case EntityNameConstants.LOSS_SCENARIO:
			return lossScenarioDAO;
		case EntityNameConstants.OUTPUT:
			return outputDAO;
		case EntityNameConstants.PROCESS_MODEL:
			return processModelDAO;
		case EntityNameConstants.PROCESS_VARIABLE:
			return processVariableDAO;
		case EntityNameConstants.RESPONSIBILITY:
			return responsibilityDAO;
		case EntityNameConstants.SENSOR:
			return sensorDAO;
		case EntityNameConstants.SYSTEM_CONSTRAINT:
			return systemConstraintDAO;
		case EntityNameConstants.IMPLEMENTATION_CONSTRAINT:
			return implementationConstraintDAO;
		default:
			throw new IllegalArgumentException();
		}
	}

	public IEntityDependentGenericDAO<? extends XStamppEntity, EntityDependentKey> getDAOForEntitiyDependent(
			String entityName) {
		switch (entityName) {
		case EntityNameConstants.SUB_HAZARD:
			return subHazardDAO;
		case EntityNameConstants.SUB_SYSTEM_CONSTRAINT:
			return subSystemConstraintDAO;
		case EntityNameConstants.UNSAFE_CONTROL_ACTION:
			return unsafeControlActionDAO;
		case EntityNameConstants.CONTROLLER_CONSTRAINT:
			return controllerConstrtaintDAO;
		case EntityNameConstants.RULE:
			return ruleDAO;
		case EntityNameConstants.CONVERSION:
			return conversionDAO;
		default:
			throw new IllegalArgumentException();
		}
	}

	// TODO: Complete Documentation @Rico
	/**
	 * These methods exist for the {@link LockCheckAspect}, because we need a
	 * transactional context
	 */
	public XStamppEntity fetchEntity(IProjectDependentGenericDAO<? extends XStamppEntity, ProjectDependentKey> dao,
			ProjectDependentKey key) {
		return dao.findById(key, false);
	}

	public XStamppEntity fetchEntity(IEntityDependentGenericDAO<? extends XStamppEntity, EntityDependentKey> dao,
			EntityDependentKey key) {
		return dao.findById(key, false);
	}
}
