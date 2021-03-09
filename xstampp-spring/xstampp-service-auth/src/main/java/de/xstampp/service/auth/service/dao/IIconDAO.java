package de.xstampp.service.auth.service.dao;

import de.xstampp.service.auth.data.Icon;

import java.util.List;

public interface IIconDAO extends IGenericDAO<Icon, Integer> {

    public List<Icon> getAllIcons();
}
