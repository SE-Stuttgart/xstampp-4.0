package de.xstampp.service.auth.service.dao;

import de.xstampp.service.auth.data.Theme;

import java.util.List;

public interface IThemeDAO extends IGenericDAO<Theme, Integer> {

    public List<Theme> getAllThemes();
}
