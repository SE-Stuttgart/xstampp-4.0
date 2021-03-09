/**
 * 
 */
package de.xstampp.service.project.service.dao.iface;

import java.util.UUID;

import de.xstampp.service.project.data.entity.SystemDescription;

/**
 * Accesses and modifies system description data in the database.
 */
public interface ISystemDescriptionDAO extends IGenericDAO<SystemDescription, UUID> {

}
