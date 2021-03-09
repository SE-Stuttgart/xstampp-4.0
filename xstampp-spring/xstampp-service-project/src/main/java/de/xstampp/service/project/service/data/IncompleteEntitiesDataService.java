package de.xstampp.service.project.service.data;

import de.xstampp.service.project.data.XStamppDependentEntity;
import de.xstampp.service.project.data.entity.EntityDependentKey;
import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.service.dao.iface.IIncompleteEntitiesDAO;
import de.xstampp.service.project.util.StateControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class IncompleteEntitiesDataService {

    @Autowired
    IIncompleteEntitiesDAO incompleteEntitiesDAO;

    public XStamppDependentEntity findEntityByNameAndId(Class<?> clazz, ProjectDependentKey id) {
        return (XStamppDependentEntity) incompleteEntitiesDAO.findEntityByClassAndId(clazz, id);
    }

    public XStamppDependentEntity findEntityByNameAndId(Class<?> clazz, EntityDependentKey id) {
        return (XStamppDependentEntity) incompleteEntitiesDAO.findEntityByClassAndId(clazz, id);
    }

    public XStamppDependentEntity findEntityByNameAndId(Class<?> clazz, UUID projectId, String id) {
        return (XStamppDependentEntity) incompleteEntitiesDAO.findEntityByClassAndId(clazz, projectId, id);
    }

    public boolean updateStateForEntity(Class<?> clazz, ProjectDependentKey id, StateControl.STATE state) {
        return incompleteEntitiesDAO.updateStateForEntity(clazz, id, state);
    }

    public boolean updateStateForEntity(Class<?> clazz, EntityDependentKey id, StateControl.STATE state) {
        return incompleteEntitiesDAO.updateStateForEntity(clazz, id, state);
    }

    public boolean updateStateForEntity(Class<?> clazz, UUID projectId, String id, StateControl.STATE state) {
        return incompleteEntitiesDAO.updateStateForEntity(clazz, projectId, id, state);
    }

    public boolean getStateForEntity(Class<?> clazz, ProjectDependentKey id, StateControl.STATE state) {
        return incompleteEntitiesDAO.updateStateForEntity(clazz, id, state);
    }

    public boolean getStateForEntity(Class<?> clazz, EntityDependentKey id, StateControl.STATE state) {
        return incompleteEntitiesDAO.updateStateForEntity(clazz, id, state);
    }

    public boolean getStateForEntity(Class<?> clazz, UUID projectId, String id, StateControl.STATE state) {
        return incompleteEntitiesDAO.updateStateForEntity(clazz, projectId, id, state);
    }
}
