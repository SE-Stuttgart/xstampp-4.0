package de.xstampp.service.auth.service.dao;

import java.util.List;
import java.util.UUID;

import de.xstampp.service.auth.data.Group;

public interface IGroupDAO extends IGenericDAO<Group, UUID> {
	
	@Override
	List<Group> findByExample(Group exampleInstance);

	List<Group> findGroupsByIds(UUID projectId, List<UUID> groupIds);
}
