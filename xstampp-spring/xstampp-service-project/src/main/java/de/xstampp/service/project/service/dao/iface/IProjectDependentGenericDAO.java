package de.xstampp.service.project.service.dao.iface;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.xstampp.service.project.data.entity.ProjectDependentKey;

public interface IProjectDependentGenericDAO<T extends Serializable, ID extends ProjectDependentKey> extends IGenericDAO<T, ID> {

    /*/**
    /* * @see IStandAloneGenericDAO#findAll(). The result set is restricted by the
    /* *      given parameter projectId.
    /* */
    List<T> findAll(UUID projectId);

    /*/**
    /* * @see IStandAloneGenericDAO#count(). The result set is restricted by the given
    /* *      parameter projectId.
    /* */
    long count(UUID projectId);

    /*/**
    /* * @see IStandAloneGenericDAO#findFromTo(int, int, Map). The result set is
    /* *      restricted by the given parameter projectId.
    /* */
    List<T> findFromTo(int from, int amount, Map<String, SortOrder> order, UUID projectId);
}
