package de.xstampp.service.project.service.dao.control_structure.iface;

import java.util.List;
import java.util.UUID;

import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.control_structure.Sensor;
import de.xstampp.service.project.service.dao.iface.IProjectDependentGenericDAO;

public interface ISensorDAO extends IProjectDependentGenericDAO<Sensor, ProjectDependentKey> {

	public Sensor getByBoxId(UUID projectId, String boxId);

	public List<Sensor> getAllUnlinkedSensor(UUID projectId);

	public Sensor setBoxId(UUID projectID, int sensorId, String boxId);
}
