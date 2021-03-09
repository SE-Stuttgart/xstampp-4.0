package de.xstampp.service.project.service.dao.iface;

import de.xstampp.service.project.data.entity.EntityDependentKey;
import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.util.StateControl;

import java.util.UUID;

public interface IIncompleteEntitiesDAO {

    <T> T findEntityByClassAndId(Class<T> clazz, ProjectDependentKey id);

    <T> T findEntityByClassAndId(Class<T> clazz, EntityDependentKey id);

    <T> T findEntityByClassAndId(Class<T> clazz, UUID projectId, String id);

    <T> boolean updateStateForEntity(Class<T> clazz, ProjectDependentKey id, StateControl.STATE state);

    <T> boolean updateStateForEntity(Class<T> clazz, EntityDependentKey id, StateControl.STATE state);

    <T> boolean updateStateForEntity(Class<T> clazz, UUID projectId, String id, StateControl.STATE state);
}
