package de.xstampp.service.auth.service.dao;

import java.util.List;
import java.util.UUID;

import de.xstampp.service.auth.data.User;

public interface IUserDAO extends IGenericDAO<User, UUID> {

	@Override
	List<User> findByExample(User exampleInstance);

	User getByEmail(String email);

	List<User> findGUsersByIds(List<UUID> userIds);
	}