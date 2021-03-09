package de.xstampp.service.project.service.dao.iface;

import java.util.UUID;

import de.xstampp.service.project.data.entity.Project;

/**
 * Accesses and modifies project data in the database.
 */
public interface IProjectDAO extends IGenericDAO<Project, UUID> {

}
