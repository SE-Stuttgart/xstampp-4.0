package de.xstampp.service.project.service.dao.control_structure.iface;

import java.util.List;
import java.util.UUID;

import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.control_structure.ControlledProcess;
import de.xstampp.service.project.service.dao.iface.IProjectDependentGenericDAO;

public interface IControlledProcessDAO extends IProjectDependentGenericDAO<ControlledProcess, ProjectDependentKey>{

	public ControlledProcess getByBoxId (UUID projectId, String boxId);
	public List<ControlledProcess> getAllUnlinkedControlledProcesses (UUID projectId);
	public ControlledProcess setBoxId (UUID projectID, int controlledProcessId, String boxId);
}
