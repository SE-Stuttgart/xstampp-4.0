package de.xstampp.service.project.service.dao.control_structure.iface;

import java.util.List;
import java.util.UUID;

import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.control_structure.Output;
import de.xstampp.service.project.service.dao.iface.IProjectDependentGenericDAO;

public interface IOutputDAO extends IProjectDependentGenericDAO<Output, ProjectDependentKey>{
	public List<Output> getByArrowId (UUID projectId, String arrowId);
	public List<Output> getAllUnlinkedOutputs (UUID projectId);
	public Output setArrowId (UUID projectID, int otputId, String arrowId);
}
