package de.xstampp.service.auth.service.dao;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import de.xstampp.service.auth.data.Project;

@Repository
public class ProjectHibernateDAO extends AbstractGenericHibernateDAO<Project,UUID> implements IProjectDAO {

	@Override
	public List<Project> findByExample(Project exampleInstance) {
		throw new  UnsupportedOperationException();
	}
	

}
