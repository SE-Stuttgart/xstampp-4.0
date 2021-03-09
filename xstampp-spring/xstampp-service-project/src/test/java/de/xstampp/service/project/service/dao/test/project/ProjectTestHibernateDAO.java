package de.xstampp.service.project.service.dao.test.project;

import java.sql.Timestamp;
import java.util.UUID;

import de.xstampp.service.project.data.entity.Project;
import de.xstampp.service.project.service.dao.generic.pagination.AbstractStandAloneHibernateDAO;

public class ProjectTestHibernateDAO extends AbstractStandAloneHibernateDAO<Project, UUID> implements IProjectTestDAO {

	@Override
	protected String getIdAttributeName() {
		return Project.EntityAttributes.ID;
	}

	@Override
	public boolean lockEntity(UUID id, UUID userId, String userName, Timestamp expirationTime) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean unlockEntity(UUID id, UUID userId, String userName) {
		throw new UnsupportedOperationException("not implemented");
	}

}
