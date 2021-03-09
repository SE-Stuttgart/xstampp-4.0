package de.xstampp.service.project.service.dao.iface;

import de.xstampp.service.project.data.entity.ImplementationConstraint;
import de.xstampp.service.project.data.entity.ProjectDependentKey;

import java.util.List;
import java.util.UUID;

/**
 * Loss scenario DAO for service injection
 * @author Timo
 */
public interface IImplementationConstraintDAO extends IProjectDependentGenericDAO<ImplementationConstraint, ProjectDependentKey> {

    /**
     * Retrieve (possibly) multiple entities whose id is contained in implementationConstraintIds
     * @param projectId id of a project
     * @param implementationConstraintIds list of ids of existing implementation constraints
     * @return list of matching implementation constraints
     */
    List<ImplementationConstraint> findImplementationConstraintsByIds(UUID projectId, List<Integer> implementationConstraintIds);

    /**
     * There are 0..n implementation constraints associated with a loss scenario. This method retrieves them.
     * @param projectId id of a project
     * @param lossScenarioId id of a loss scenario
     * @return all implementation constraints associated with the loss scenario with id equal to lossScenarioId
     */
    List<ImplementationConstraint> getImplementationConstraintsByLossScenarioId(UUID projectId, Integer lossScenarioId);
}
