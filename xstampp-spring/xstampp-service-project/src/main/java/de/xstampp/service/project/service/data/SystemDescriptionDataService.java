package de.xstampp.service.project.service.data;

import de.xstampp.common.errors.ErrorsProj;
import de.xstampp.common.service.SecurityService;
import de.xstampp.service.project.data.dto.SystemDescriptionRequestDTO;
import de.xstampp.service.project.data.entity.SystemDescription;
import de.xstampp.service.project.data.entity.User;
import de.xstampp.service.project.exceptions.ProjectException;
import de.xstampp.service.project.service.dao.iface.ISystemDescriptionDAO;
import de.xstampp.service.project.service.dao.iface.IUserDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class SystemDescriptionDataService {

	/* NOTE: this error message may be relied upon by API clients */
	private static final String GET_SYSTEMDESC_NOTFOUND = "Cannot retrieve system description. There is no system description with the id: ";

	private Logger logger = LoggerFactory.getLogger(SystemDescriptionDataService.class);

	@Autowired
	SecurityService security;

	@Autowired
	private ISystemDescriptionDAO systemDescriptionDAO;

	@Autowired
	IUserDAO userDAO;

	/**
	 * Creates persistent system description entity for the given parameters.
	 * 
	 * @param request system description to persist.
	 * @param projectId            project id of the corresponding project.
	 * @return the updated SystemDescription object
	 */
	public SystemDescription create(SystemDescriptionRequestDTO request, UUID projectId) {
		if (systemDescriptionDAO.exist(projectId)) {
			throw ErrorsProj.ALREADY_EXISTS.exc();
		}
		SystemDescription systemDescription = init(request, projectId);

		SystemDescription result = systemDescriptionDAO.makePersistent(systemDescription);
		return result;
	}

	/**
	 * Creates persistent system description entity for the given project id.
	 *
	 * @param projectId  project id of the corresponding project.
	 * @return the updated SystemDescription object
	 */
	public SystemDescription create(UUID projectId) {
		if (systemDescriptionDAO.exist(projectId))
			throw ErrorsProj.ALREADY_EXISTS.exc();

		return systemDescriptionDAO.makePersistent(new SystemDescription(projectId));
	}

	private SystemDescription init(SystemDescriptionRequestDTO request, UUID projectId) {
		SystemDescription result = new SystemDescription(projectId);
		result.setDescription(request.getDescription());
		result.setLastEditNow(security.getContext().getUserId());
		userDAO.makePersistent(new User(security.getContext().getUserId(), security.getContext().getDisplayName()));
		return result;
	}

	/**
	 * Alters system description entity.
	 * 
	 * @param request contains data for update.
	 * @param projectId            corresponding project id.
	 * @return the updated SystemDescription object
	 */
	public SystemDescription alter(SystemDescriptionRequestDTO request, UUID projectId) {
		if (!systemDescriptionDAO.exist(projectId)) {
			throw ErrorsProj.DOES_NOT_EXIST.exc();
		}
		SystemDescription systemDescription = init(request, projectId);
		systemDescription.setLockExpirationTime(Timestamp.from(Instant.now()));

		SystemDescription result = systemDescriptionDAO.makePersistent(systemDescription);
		return result;
	}

	/**
	 * Deletes system description entity for the given id. The entity has to exist.
	 * 
	 * @param projectId id of the system description entity.
	 * @return true if the operation succeeds.
	 */
	public boolean deleteFor(UUID projectId) {
		if (!systemDescriptionDAO.exist(projectId)) {
			throw ErrorsProj.DOES_NOT_EXIST.exc();
		}
		SystemDescription systemDescription = new SystemDescription(projectId);
		systemDescriptionDAO.makeTransient(systemDescription);
		return true;
	}

	/**
	 * Retrieves entity for the given id from the database.
	 * 
	 * @param id id of the entity to retrieve.
	 * @return entity for the given id if one exists.
	 */
	public SystemDescription getById(UUID id) {
		UUID systemDescriptionId = id;
		logger.info("loading system description with the id: {}", systemDescriptionId.toString());
		SystemDescription systemDescription = systemDescriptionDAO.findById(systemDescriptionId, false);
		if (systemDescription == null) {
			/*
			 * TODO replace with an error condition, however this requires coordination with
			 * frontend.
			 */
			String errorMsg = GET_SYSTEMDESC_NOTFOUND + systemDescriptionId.toString();
			logger.error(errorMsg);
			throw new ProjectException(errorMsg);
		}
		return systemDescription;
	}
}
