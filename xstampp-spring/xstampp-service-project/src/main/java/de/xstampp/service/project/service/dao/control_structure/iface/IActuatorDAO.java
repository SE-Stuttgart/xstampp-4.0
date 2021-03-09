package de.xstampp.service.project.service.dao.control_structure.iface;

import java.util.List;
import java.util.UUID;

import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.control_structure.Actuator;
import de.xstampp.service.project.service.dao.iface.IProjectDependentGenericDAO;

public interface IActuatorDAO extends IProjectDependentGenericDAO<Actuator, ProjectDependentKey>{
	
	public Actuator getByBoxId (UUID projectId, String boxId);
	public List<Actuator> getAllUnlinkedActuators (UUID projectId);
	public Actuator setBoxId (UUID projectID, int actuatorId, String boxId);

}
