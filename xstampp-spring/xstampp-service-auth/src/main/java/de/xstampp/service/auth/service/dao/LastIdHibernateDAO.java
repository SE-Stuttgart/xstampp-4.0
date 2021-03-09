package de.xstampp.service.auth.service.dao;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import javax.persistence.Entity;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;



@Repository
public class LastIdHibernateDAO implements ILastIdDAO {

	/** initial ID of all entities that are managed with this class */
	private final static int INIT_ID = 1;

	private static final String GENERIC_UPDATE_QUERY = "UPDATE project_entity_last_id "
			+ "SET last_id = last_id + 1 WHERE project_id = ?1 AND entity = ?2 RETURNING last_id";
	private static final String HAZARD_UPDATE_QUERY = "UPDATE sub_hazard_last_id "
			+ "SET last_id = last_id + 1 WHERE project_id = ?1 AND hazard_id = ?2 RETURNING last_id";
	private static final String SYSCONST_UPDATE_QUERY = "UPDATE sub_system_constraint_last_id "
			+ "SET last_id = last_id + 1 WHERE project_id = ?1 AND system_constraint_id = ?2 RETURNING last_id";
	private static final String CONTRACT_UPDATE_QUERY = "UPDATE unsafe_control_action_last_id "
			+ "SET last_id = last_id + 1 WHERE project_id = ?1 AND control_action_id = ?2 RETURNING last_id";
	private static final String RULE_UPDATE_QUERY = "UPDATE rule_last_id "
			+ "SET last_id = last_id + 1 WHERE project_id = ?1 AND controller_id = ?2 RETURNING last_id";
	private static final String WITHOUT_PID_UPDATE_QUERY = "UPDATE without_pid_last_id "
			+ "SET last_id = last_id + 1 WHERE entity = ?1 RETURNING last_id";

	private static final String GENERIC_INSERT_QUERY = "INSERT INTO project_entity_last_id "
			+ "(project_id, entity, last_id) VALUES (?1, ?2, " + INIT_ID + ")";
	private static final String HAZARD_INSERT_QUERY = "INSERT INTO sub_hazard_last_id "
			+ "(project_id, hazard_id, last_id) VALUES (?1, ?2, " + INIT_ID + ")";
	private static final String SYSCONST_INSERT_QUERY = "INSERT INTO sub_system_constraint_last_id "
			+ "(project_id, system_constraint_id, last_id) VALUES (?1, ?2, " + INIT_ID + ")";
	private static final String CONTRACT_INSERT_QUERY ="INSERT INTO unsafe_control_action_last_id "
			+ "(project_id, control_action_id, last_id) VALUES (?1, ?2, " + INIT_ID + ")";
	private static final String RULE_INSERT_QUERY ="INSERT INTO rule_last_id "
			+ "(project_id, controller_id, last_id) VALUES (?1, ?2, " + INIT_ID + ")";
	private static final String WITHOUT_PID_INSERT_QUERY ="INSERT INTO without_pid_last_id "
			+ "(entity, last_id) VALUES (?1, " + INIT_ID + ")";

	private static final String GENERIC_LOCK_QUERY = "LOCK TABLE project_entity_last_id IN SHARE ROW EXCLUSIVE MODE";
	private static final String HAZARD_LOCK_QUERY = "LOCK TABLE sub_hazard_last_id IN SHARE ROW EXCLUSIVE MODE";
	private static final String SYSCONST_LOCK_QUERY = "LOCK TABLE sub_system_constraint_last_id IN SHARE ROW EXCLUSIVE MODE";
	private static final String CONTRACT_LOCK_QUERY = "LOCK TABLE unsafe_control_action_last_id IN SHARE ROW EXCLUSIVE MODE";
	private static final String RULE_LOCK_QUERY = "LOCK TABLE rule_last_id IN SHARE ROW EXCLUSIVE MODE";
	private static final String WITHOUT_PID_LOCK_QUERY = "LOCK TABLE without_pid_last_id IN SHARE ROW EXCLUSIVE MODE";

	private static Map< Object, String> entityNameCache =
			Collections.synchronizedMap(new WeakHashMap<>());

	@Autowired
	SessionFactory sessionFactory;

	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}



	@Override /* javadoc inherited */
	public<T> int getNewIdforEntityWithoutPid(Class<T> entity) {
		

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
			return ((Integer) o).intValue();
		}
		throw new IllegalStateException("Got result of wrong type for last ID: " + resultList);
	}

	private static <T>String tableNameFromClass(Class<T> entity) {
		String cachedName = entityNameCache.get(entity);
		if (cachedName != null) {
			return cachedName;
		}

		String computedName = tableNameFromClassInner(entity);
		entityNameCache.put(entity, computedName);
		return computedName;
	}

	private static <T>String tableNameFromClassInner(Class<T> entity) {
		org.hibernate.annotations.Table hiberTable = entity.getAnnotation(org.hibernate.annotations.Table.class);
		if (hiberTable != null && hiberTable.appliesTo() != null && !hiberTable.appliesTo().isBlank()) {
			return hiberTable.appliesTo();
		}
		javax.persistence.Table jpaTable = entity.getAnnotation(javax.persistence.Table.class);
		if (jpaTable != null && jpaTable.name() != null && !jpaTable.name().isBlank()) {
			return jpaTable.name();
		}
		Entity entityAnn = entity.getAnnotation(Entity.class);
		if (entityAnn != null && entityAnn.name() != null && !entityAnn.name().isBlank()) {
			return entityAnn.name();
		}
		return entity.getSimpleName().toLowerCase();
	}

}
