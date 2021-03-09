package de.xstampp.service.auth.service.dao;

import java.util.List;
import java.util.UUID;

import de.xstampp.service.auth.data.Project;

public interface IProjectDAO extends IGenericDAO<Project, UUID> {
	
	@Override
	List<Project> findByExample(Project exampleInstance);
	
}
