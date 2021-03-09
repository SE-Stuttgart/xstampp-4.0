package de.xstampp.service.project.service.dao.control_structure.iface;

import java.util.List;
import java.util.UUID;

import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.control_structure.Input;
import de.xstampp.service.project.service.dao.iface.IProjectDependentGenericDAO;

public interface IInputDAO extends IProjectDependentGenericDAO<Input, ProjectDependentKey>{

	public List<Input> getByArrowId (UUID projectId, String arrowId);
	public List<Input> getAllUnlinkedInputs (UUID projectId);
	public Input setArrowId (UUID projectID, int inputId, String arrowId);
}
