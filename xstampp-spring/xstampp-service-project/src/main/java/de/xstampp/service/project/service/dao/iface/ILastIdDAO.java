package de.xstampp.service.project.service.dao.iface;

import de.xstampp.service.project.data.XStamppEntity;
import de.xstampp.service.project.data.entity.Hazard;
import de.xstampp.service.project.data.entity.SubHazard;
import de.xstampp.service.project.data.entity.SubSystemConstraint;
import de.xstampp.service.project.data.entity.SystemConstraint;
import de.xstampp.service.project.data.entity.lastId.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Table access by this DAO retrieves last ID value for a classEntity.  Usually used to determine next ID increment
 */
public interface ILastIdDAO {

    /**
     * Returns the next entity ID for a given project and type of entity
     * <p>
     * <strong>NOTE:</strong> Always use this in a transactional context.
     *
     * @param projectId ID of the project in question
     * @param entity    the entity class obtained with e.g. {@code Loss.class}
     * @return new ID
     * @see Transactional
     */
    int getNewIdForEntity(UUID projectId, Class<? extends XStamppEntity> entity);

	/**
	 * Returns the next entity ID for a given type of entity without a link to a project
	 * <p>
	 * <strong>NOTE:</strong> Always use this in a transactional context.
	 *
	 * @see Transactional
	 *
	 * @param entity    the entity class without link to a project obtained with e.g. {@code Theme.class}
	 * @return new ID
	 */
	int getNewIdForEntityWithoutPid(Class<? extends XStamppEntity> entity);

	/**
	 * Returns the {@link SubHazard} ID for a given project and hazard
	 * <p>
	 * <strong>NOTE:</strong> Always use this in a transactional context.
	 * 
	 * @see Transactional
	 * 
	 * @param projectId ID of the project in question
	 * @param hazardId  the {@link Hazard} ID
	 * @return new ID
	 */
	int getNewIdForSubHazard(UUID projectId, int hazardId);


	/**
	 * Returns the next {@link SubSystemConstraint} ID for a given project and
	 * system constraint
	 * <p>
	 * <strong>NOTE:</strong> Always use this in a transactional context.
	 * 
	 * @see Transactional
	 * 
	 * @param projectId          ID of the project in question
	 * @param systemConstraintId the {@link SystemConstraint} ID
	 * @return new ID
	 */
	int getNewIdForSubSystemConstraint(UUID projectId, int systemConstraintId);

	
	/**
	 * Returns the next {@link de.xstampp.service.project.data.entity.Conversion} ID for a given project ID and
	 * actuator ID
	 * <p>
	 * <strong>NOTE:</strong> Always use this in a transactional context.
	 * 
	 * @see Transactional
	 * 
	 * @param projectId          ID of the project in question
	 * @param actuatorId the {@link de.xstampp.service.project.data.entity.control_structure.Actuator} ID
	 * @return new ID
	 */
	int getNewIdForConversion(UUID projectId, int actuatorId);

    /**
     * Returns the next Rule ID for a given project ID and
     * controller ID
     * <p>
     * <strong>NOTE:</strong> Always use this in a transactional context.
     *
     * @param projectId    ID of the project in question
     * @param controllerId the link Controller ID
     * @return new ID
     * @see Transactional
     */
    public int getNewIdForRule(UUID projectId, int controllerId);

    int getNewIdForUnsafeControlAction(UUID projectId, int controlActionId);

	List<ProjectEntityLastId> findAllProjectEntityLastIds(UUID projectId);

	List<ProjectEntityLastId> saveAllProjectEntityLastIds(List<ProjectEntityLastId> projectEntityLastIds);

	List<SubHazardLastId> findAllSubHazardLastIds(UUID projectId);

	List<SubHazardLastId> saveAllSubHazardLastIds(List<SubHazardLastId> subHazardLastIds);

	List<SubSystemConstraintLastId> findAllSubSystemConstraintLastIds(UUID projectId);

	List<SubSystemConstraintLastId> saveAllSubSystemConstraintLastIds(List<SubSystemConstraintLastId> subSystemConstraintLastIds);

	List<UnsafeControlActionLastId> findAllUnsafeControlActionLastId(UUID projectId);

	List<UnsafeControlActionLastId> saveAllUnsafeControlActionLastIds(List<UnsafeControlActionLastId> unsafeControlActionLastIds);

	List<RuleLastId> findAllRuleLastIds(UUID projectId);

	List<RuleLastId> saveAllRuleLastIds(List<RuleLastId> ruleLastIds);

	List<ConversionLastId> findAllConversionLastIds(UUID projectId);

	List<ConversionLastId> saveAllConversionLastIds(List<ConversionLastId> conversionLastIds);
}
