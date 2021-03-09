package de.xstampp.service.auth.service.dao;

import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import de.xstampp.service.auth.data.Icon;
import de.xstampp.service.auth.data.Theme;


public interface ILastIdDAO {

	public<T>int getNewIdforEntityWithoutPid(Class<T> entity);
}
