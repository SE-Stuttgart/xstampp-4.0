package de.xstampp.service.project.service.dao;

import de.xstampp.service.project.data.entity.Project;
import de.xstampp.service.project.exceptions.ProjectException;
import de.xstampp.service.project.service.dao.generic.AbstractGenericHibernateDAO;
import de.xstampp.service.project.service.dao.iface.IProjectDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Repository
public class ProjectHibernateDAO extends AbstractGenericHibernateDAO<Project, UUID> implements IProjectDAO {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	protected String getIdAttributeName() {
		return Project.EntityAttributes.ID;
	}

	@Override
	public boolean lockEntity(UUID id, UUID userId, String userName, Timestamp expirationTime) {
		Project loadedEntity = findById(id, false);

		// check if entity is null
		if (loadedEntity != null) {

			// check if entity is not locked by another user 
			if (loadedEntity.getLockExpirationTime() == null
					|| loadedEntity.getLockExpirationTime().before(Timestamp.from(Instant.now()))
					|| loadedEntity.getLockHolderId().equals(userId)) {

				loadedEntity.setLockExpirationTime(expirationTime);
				loadedEntity.setLockHolderId(userId);
				loadedEntity.setLockHolderDisplayName(userName);
				
				makePersistent(loadedEntity);
				return true;
			} else {
				logger.error("entity currently locked by another user");
			}
			return false;
		} else {
			logger.error("entity with the given id not found");
			return false;
		}
	}

	@Override
	public boolean unlockEntity(UUID id, UUID userId, String userName) {
		Project loadedEntity = findById(id, false);

		// check if entity is null
		if (loadedEntity != null) {

			if (loadedEntity.getLockExpirationTime() == null) {
				throw new ProjectException("No such lock exists.");
			}

			// check if entity is not locked by another user 
			if (loadedEntity.getLockExpirationTime().after(Timestamp.from(Instant.now()))
					&& loadedEntity.getLockHolderId().equals(userId)) {

				loadedEntity.setLockExpirationTime(Timestamp.from(Instant.now()));
				
				makePersistent(loadedEntity);
				return true;
			} else {
				logger.error("entity currently locked by another user");
			}
			return false;
		} else {
			logger.error("entity with the given id not found");
			return false;
		}
	}
}
