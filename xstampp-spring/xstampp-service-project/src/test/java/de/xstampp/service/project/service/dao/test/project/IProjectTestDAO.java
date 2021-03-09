package de.xstampp.service.project.service.dao.test.project;

import java.util.UUID;

import de.xstampp.service.project.data.entity.Project;
import de.xstampp.service.project.service.dao.iface.IStandAloneGenericDAO;

/**
 * Temporary solution. Move the class {@link ProjectDAOTest_SeveralEntities} to
 * the auth project and remove this interface and its implementations.
 */
public interface IProjectTestDAO extends IStandAloneGenericDAO<Project, UUID> {

}
