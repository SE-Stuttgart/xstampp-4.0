package de.xstampp.service.project.service.dao.last_id;

import de.xstampp.service.project.data.XStamppEntity;
import de.xstampp.service.project.data.entity.*;
import de.xstampp.service.project.data.entity.lastId.*;
import de.xstampp.service.project.service.dao.iface.ILastIdDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import java.util.*;

@Repository
public class LastIdHibernateDAO implements ILastIdDAO {

    /* initial ID of all entities that are managed with this class */
    private final static int INIT_ID = 1;

    private static final String
            UPDATE_QUERY = "UPDATE %s SET last_id = last_id + 1 WHERE project_id = ?1 AND %s = ?2 RETURNING last_id";
    private static final String
            INSERT_QUERY = "INSERT INTO %s (project_id, %s, last_id) VALUES (?1, ?2, " + INIT_ID + ")";
    private static final String
            LOCK_QUERY = "LOCK TABLE %s IN SHARE ROW EXCLUSIVE MODE";

    // FIXME: ...
    private static final String WITHOUT_PID_UPDATE_QUERY = "UPDATE without_pid_last_id "
            + "SET last_id = last_id + 1 WHERE entity = ?1 RETURNING last_id";
    private static final String WITHOUT_PID_INSERT_QUERY ="INSERT INTO without_pid_last_id "
            + "(entity, last_id) VALUES (?1, " + INIT_ID + ")";
    private static final String WITHOUT_PID_LOCK_QUERY = "LOCK TABLE without_pid_last_id IN SHARE ROW EXCLUSIVE MODE";

    private static Map<Class<? extends XStamppEntity>, String> entityNameCache =
            Collections.synchronizedMap(new WeakHashMap<>());

    private final SessionFactory sessionFactory;

    private final ProjectEntityLastIdDAO projectEntityLastIdDAO;
    private final SubHazardLastIdDAO subHazardLastIdDAO;
    private final SubSystemConstraintLastIdDAO subSystemConstraintLastIdDAO;
    private final UnsafeControlActionLastIdDAO unsafeControlActionLastIdDAO;
    private final RuleLastIdDAO ruleLastIdDAO;
    private final ConversionLastIdDAO conversionLastIdDAO;


    @Autowired
    public LastIdHibernateDAO(
            SessionFactory sessionFactory,
            ProjectEntityLastIdDAO projectEntityLastIdDAO,
            SubHazardLastIdDAO subHazardLastIdDAO, SubSystemConstraintLastIdDAO subSystemConstraintLastIdDAO, UnsafeControlActionLastIdDAO unsafeControlActionLastIdDAO, RuleLastIdDAO ruleLastIdDAO, ConversionLastIdDAO conversionLastIdDAO) {
        this.sessionFactory = sessionFactory;
        this.projectEntityLastIdDAO = projectEntityLastIdDAO;
        this.subHazardLastIdDAO = subHazardLastIdDAO;
        this.subSystemConstraintLastIdDAO = subSystemConstraintLastIdDAO;
        this.unsafeControlActionLastIdDAO = unsafeControlActionLastIdDAO;
        this.ruleLastIdDAO = ruleLastIdDAO;
        this.conversionLastIdDAO = conversionLastIdDAO;
    }

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override /* javadoc inherited */
    public int getNewIdForEntity(UUID projectId, Class<? extends XStamppEntity> entity) {

        String template = "Use ILastIdDAO#getNewIdFor%s instead.";
        if (entity == SubHazard.class)
            throw new IllegalArgumentException(String.format(template, "SubHazard"));
        if (entity == SubSystemConstraint.class)
            throw new IllegalArgumentException(String.format(template, "SubSystemConstraint"));
        if (entity == UnsafeControlAction.class)
            throw new IllegalArgumentException(String.format(template, "UnsafeControlAction"));
        if (entity == Rule.class)
            throw new IllegalArgumentException(String.format(template, "Rule"));
        if (entity == Conversion.class)
            throw new IllegalArgumentException(String.format(template, "Conversion"));

        return getNewIdForEntity(projectId, tableNameFromClass(entity), LastIdType.PROJECT_ENTITY);
    }

    @Override
    public int getNewIdForEntityWithoutPid(Class<? extends XStamppEntity> entity) {
        getSession().createNativeQuery(WITHOUT_PID_LOCK_QUERY).executeUpdate();

        String entityname = tableNameFromClass(entity);
        NativeQuery<?> uQuery = getSession().createNativeQuery(WITHOUT_PID_UPDATE_QUERY);
        uQuery.setParameter(1, entityname);
        List<?> resultList = uQuery.list();
        if (containsOneResult(resultList)) {
            return intIdFromResult(resultList);
        }

        NativeQuery<?> iQuery = getSession().createNativeQuery(WITHOUT_PID_INSERT_QUERY);
        iQuery.setParameter(1, entityname);
        iQuery.executeUpdate();
        return INIT_ID;
    }

    @Override /* javadoc inherited */
    public int getNewIdForSubHazard(UUID projectId, int hazardId) {
        return getNewIdForEntity(projectId, hazardId, LastIdType.SUB_HAZARD);
    }

    @Override /* javadoc inherited */
    public int getNewIdForSubSystemConstraint(UUID projectId, int systemConstraintId) {
        return getNewIdForEntity(projectId, systemConstraintId, LastIdType.SUB_SYSTEM_CONSTRAINT);
    }

    @Override /* javadoc inherited */
    public int getNewIdForUnsafeControlAction(UUID projectId, int controlActionId) {
        return getNewIdForEntity(projectId, controlActionId, LastIdType.UNSAFE_CONTROL_ACTION);
    }

    @Override /* javadoc inherited */
    public int getNewIdForRule(UUID projectId, int controllerId) {
        return getNewIdForEntity(projectId, controllerId, LastIdType.RULE);
    }

    @Override /* javadoc inherited */
    public int getNewIdForConversion(UUID projectId, int actuatorId) {
        return getNewIdForEntity(projectId, actuatorId, LastIdType.CONVERSION);
    }

    private int getNewIdForEntity(UUID projectId, Object entityId, LastIdType entityType) {

        getLockQuery(entityType).executeUpdate();

        List<?> resultList = getUpdateQuery(entityType)
                .setParameter(1, projectId)
                .setParameter(2, entityId)
                .list();
        if (containsOneResult(resultList)) {
            return intIdFromResult(resultList);
        }

        getInsertQuery(entityType)
                .setParameter(1, projectId)
                .setParameter(2, entityId)
                .executeUpdate();
        return INIT_ID;
    }

    private NativeQuery<?> getLockQuery(LastIdType type) {
        String lockQuery =  String.format(LOCK_QUERY, type.lastIdTableName);
        return getSession().createNativeQuery(lockQuery);
    }

    private NativeQuery<?> getUpdateQuery(LastIdType type) {
        String updateQuery = String.format(UPDATE_QUERY, type.lastIdTableName, type.keyColumnName);
        return getSession().createNativeQuery(updateQuery);
    }

    private NativeQuery<?> getInsertQuery(LastIdType type) {
        String insertQuery =  String.format(INSERT_QUERY, type.lastIdTableName, type.keyColumnName);
        return getSession().createNativeQuery(insertQuery);
    }

    private static boolean containsOneResult(List<?> resultList) {
        if (resultList.size() == 0) {
            return false;
        }
        if (resultList.size() == 1) {
            return true;
        }
        throw new IllegalStateException("Got too many results for last ID: " + resultList);
    }

    private static int intIdFromResult(List<?> resultList) {
        Object o = resultList.get(0);
        if (o instanceof Integer) {
            return (Integer) o;
        }
        throw new IllegalStateException("Got result of wrong type for last ID: " + resultList);
    }

    private static String tableNameFromClass(Class<? extends XStamppEntity> entity) {
        String cachedName = entityNameCache.get(entity);
        if (cachedName != null) {
            return cachedName;
        }

        String computedName = tableNameFromClassInner(entity);
        entityNameCache.put(entity, computedName);
        return computedName;
    }

    private static String tableNameFromClassInner(Class<? extends XStamppEntity> entity) {
        org.hibernate.annotations.Table hiberTable = entity.getAnnotation(org.hibernate.annotations.Table.class);
        if (hiberTable != null && !hiberTable.appliesTo().isBlank()) {
            return hiberTable.appliesTo();
        }
        javax.persistence.Table jpaTable = entity.getAnnotation(javax.persistence.Table.class);
        if (jpaTable != null && !jpaTable.name().isBlank()) {
            return jpaTable.name();
        }
        Entity entityAnn = entity.getAnnotation(Entity.class);
        if (entityAnn != null && !entityAnn.name().isBlank()) {
            return entityAnn.name();
        }
        return entity.getSimpleName().toLowerCase();
    }

    @Override
    public List<ProjectEntityLastId> findAllProjectEntityLastIds(UUID projectId) {
        return projectEntityLastIdDAO.findAll(projectId);
    }

    @Override
    public List<ProjectEntityLastId> saveAllProjectEntityLastIds(List<ProjectEntityLastId> projectEntityLastIds) {
        return projectEntityLastIdDAO.saveAll(projectEntityLastIds);
    }

    @Override
    public List<SubHazardLastId> findAllSubHazardLastIds(UUID projectId) {
        return subHazardLastIdDAO.findAll(projectId);
    }

    @Override
    public List<SubHazardLastId> saveAllSubHazardLastIds(List<SubHazardLastId> subHazardLastIds) {
        return subHazardLastIdDAO.saveAll(subHazardLastIds);
    }

    @Override
    public List<SubSystemConstraintLastId> findAllSubSystemConstraintLastIds(UUID projectId) {
        return subSystemConstraintLastIdDAO.findAll(projectId);
    }

    @Override
    public List<SubSystemConstraintLastId> saveAllSubSystemConstraintLastIds(List<SubSystemConstraintLastId> subSystemConstraintLastIds) {
        return subSystemConstraintLastIdDAO.saveAll(subSystemConstraintLastIds);
    }

    @Override
    public List<UnsafeControlActionLastId> findAllUnsafeControlActionLastId(UUID projectId) {
        return unsafeControlActionLastIdDAO.findAll(projectId);
    }

    @Override
    public List<UnsafeControlActionLastId> saveAllUnsafeControlActionLastIds(List<UnsafeControlActionLastId> unsafeControlActionLastIds) {
        return unsafeControlActionLastIdDAO.saveAll(unsafeControlActionLastIds);
    }

    @Override
    public List<RuleLastId> findAllRuleLastIds(UUID projectId) {
        return ruleLastIdDAO.findAll(projectId);
    }

    @Override
    public List<RuleLastId> saveAllRuleLastIds(List<RuleLastId> ruleLastIds) {
        return ruleLastIdDAO.saveAll(ruleLastIds);
    }

    @Override
    public List<ConversionLastId> findAllConversionLastIds(UUID projectId) {
        return conversionLastIdDAO.findAll(projectId);
    }

    @Override
    public List<ConversionLastId> saveAllConversionLastIds(List<ConversionLastId> conversionLastIds) {
        return conversionLastIdDAO.saveAll(conversionLastIds);
    }

    private enum LastIdType {
        PROJECT_ENTITY("project_entity_last_id", "entity"),
        SUB_HAZARD("sub_hazard_last_id", "hazard_id"),
        SUB_SYSTEM_CONSTRAINT("sub_system_constraint_last_id", "system_constraint_id"),
        UNSAFE_CONTROL_ACTION("unsafe_control_action_last_id", "control_action_id"),
        RULE("rule_last_id", "controller_id"),
        CONVERSION("conversion_last_id", "actuator_id");

        final String lastIdTableName, keyColumnName;

        LastIdType(String lastIdTableName, String keyColumnName) {
            this.lastIdTableName = lastIdTableName;
            this.keyColumnName = keyColumnName;
        }
    }
}
