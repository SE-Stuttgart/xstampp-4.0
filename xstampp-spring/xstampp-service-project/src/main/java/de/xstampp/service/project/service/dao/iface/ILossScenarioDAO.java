package de.xstampp.service.project.service.dao.iface;

import de.xstampp.service.project.data.entity.LossScenario;
import de.xstampp.service.project.data.entity.ProjectDependentKey;

import java.util.List;
import java.util.UUID;

/**
 * Loss scenario DAO for service injection
 * @author Timo
 */
public interface ILossScenarioDAO extends IProjectDependentGenericDAO<LossScenario, ProjectDependentKey> {

    List<LossScenario> findLossScenariosByIds(UUID projectId, List<Integer> lossScenarioIds);

    List<LossScenario> getLossScenariosByUcaId(UUID projectId, Integer ucaId, Integer lossScenarioId);
}
