package de.xstampp.service.project.service.dao.control_structure.iface;

import java.util.List;
import java.util.UUID;

import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.ControlAlgorithm;
import de.xstampp.service.project.service.dao.iface.IProjectDependentGenericDAO;

public interface IControlAlgorithmDAO extends IProjectDependentGenericDAO<ControlAlgorithm, ProjectDependentKey>{

	public ControlAlgorithm getByBoxId (UUID projectId, String boxId);
	public List<ControlAlgorithm> getAllUnlinkedControlAlgorithm (UUID projectId);
	public ControlAlgorithm setBoxId (UUID projectID, int controlAlgorithmId, String boxId);
}
