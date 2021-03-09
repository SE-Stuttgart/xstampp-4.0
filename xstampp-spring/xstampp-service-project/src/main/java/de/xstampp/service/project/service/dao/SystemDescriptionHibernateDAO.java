package de.xstampp.service.project.service.dao;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import de.xstampp.service.project.data.entity.SystemDescription;
import de.xstampp.service.project.exceptions.ProjectException;
import de.xstampp.service.project.service.dao.generic.AbstractGenericHibernateDAO;
import de.xstampp.service.project.service.dao.iface.ISystemDescriptionDAO;

@Repository
public class SystemDescriptionHibernateDAO extends AbstractGenericHibernateDAO<SystemDescription, UUID> implements ISystemDescriptionDAO {

	@Override
	protected String getIdAttributeName() {
		return SystemDescription.EntityAttributes.PROJECT_ID;
	}

	@Override
	public boolean lockEntity(UUID id, UUID userId, String userName, Timestamp expirationTime) {
		SystemDescription loadedEntity = findById(id, false);

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
			}
			return false;
		} else {
			return false;
		}
	}

	@Override
	public boolean unlockEntity(UUID id, UUID userId, String userName) {
		SystemDescription loadedEntity = findById(id, false);

		// check if entity is null
		if (loadedEntity != null) {

			if (loadedEntity.getLockExpirationTime() == null) {
				throw new ProjectException("No such lock exists.");
			}

			// check if entity is still locked by the same user
			if (loadedEntity.getLockExpirationTime().after(Timestamp.from(Instant.now()))
					&& loadedEntity.getLockHolderId().equals(userId)) {

				loadedEntity.setLockExpirationTime(Timestamp.from(Instant.now()));

				makePersistent(loadedEntity);
				return true;
			}
			return false;
		} else {
			return false;
		}
	}

}
