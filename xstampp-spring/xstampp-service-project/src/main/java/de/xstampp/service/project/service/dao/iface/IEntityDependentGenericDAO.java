package de.xstampp.service.project.service.dao.iface;

import de.xstampp.service.project.data.entity.EntityDependentKey;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IEntityDependentGenericDAO<T extends Serializable, ID extends EntityDependentKey> extends IGenericDAO<T, ID> {

    /**
     * Finds all dependent entities matching the given projectId
     *
     * @param projectId
     * @return a list of the matching entities
     */
    List<T> findAll(UUID projectId);

    /*/**
    /* * @see IStandAloneGenericDAO#findAll(). The result set is restricted by the
    /* *      given parameters projectId and parentId.
    /* */
    List<T> findAll(UUID projectId, int parentId);

    /*/**
    /* * @see IStandAloneGenericDAO#count(). The result set is restricted by the given
    /* *      parameters projectId and parentId.
    /* */
    long count(UUID projectId, int parentId);

    /*/**
    /* * @see IStandAloneGenericDAO#findFromTo(int, int, Map). The result set is
    /* *      restricted by the given parameters projectId and parentId.
    /* */
    List<T> findFromTo(int from, int amount, Map<String, SortOrder> order, UUID projectId, int parentId);

    /*/**
	/* * @see IStandAloneGenericDAO#findFromTo(int, int, Map). The result set is
	/* *      restricted by the given parameter projectId.
	/* */
    List<T> findFromTo(int from, int amount, Map<String, SortOrder> order, UUID projectId);
}
