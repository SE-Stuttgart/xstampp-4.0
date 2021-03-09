package de.xstampp.service.project.service.dao;

import de.xstampp.service.project.data.entity.DiscreteProcessVariableValue;
import de.xstampp.service.project.service.dao.iface.IDiscreteProcessVariableValueDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Repository
public class DiscreteProcessVariableValueHibernateDAO implements IDiscreteProcessVariableValueDAO {

    @Autowired
    SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<DiscreteProcessVariableValue> getAllValuesById(UUID projectId, int processVariableId) {

        //Initialize Database Access
        CriteriaBuilder builder = getSession().getCriteriaBuilder();
        CriteriaQuery<DiscreteProcessVariableValue> query = builder.createQuery(DiscreteProcessVariableValue.class);
        Root<DiscreteProcessVariableValue> root = query.from(DiscreteProcessVariableValue.class);
        query.select(root);


        //searchQuery
        query.where(builder.and(
                builder.equal(root.get(DiscreteProcessVariableValue.EntityAttributes.PROJECT_ID), projectId),
                builder.equal(root.get(DiscreteProcessVariableValue.EntityAttributes.PROCESS_VARIABLE_ID), processVariableId)
        ));


        if (getSession().createQuery(query).getResultList() == null) {
            return Collections.emptyList();
        }

        return getSession().createQuery(query).getResultList();
    }

    @Override
    public List<DiscreteProcessVariableValue> getAllValuesById(UUID projectId) {
        //Initialize Database Access
        CriteriaBuilder builder = getSession().getCriteriaBuilder();
        CriteriaQuery<DiscreteProcessVariableValue> query = builder.createQuery(DiscreteProcessVariableValue.class);
        Root<DiscreteProcessVariableValue> root = query.from(DiscreteProcessVariableValue.class);
        query.select(root);

        //searchQuery
        query.where(builder.and(
                builder.equal(root.get(DiscreteProcessVariableValue.EntityAttributes.PROJECT_ID), projectId)));

        return getSession().createQuery(query).getResultList();
    }

    @Override
    public DiscreteProcessVariableValue createNewValue(UUID projectId, int processVariableId, String value) {

        DiscreteProcessVariableValue discreteProcessVariableValue = new DiscreteProcessVariableValue(projectId, processVariableId, value);
        getSession().persist(discreteProcessVariableValue);
        return discreteProcessVariableValue;
    }


    @Override
    public boolean deleteValue(UUID projectId, int processVariableId, String value) {

        DiscreteProcessVariableValue variableValue = getValue(projectId, processVariableId, value);
        if (variableValue != null) {
            getSession().delete(variableValue);
            return true;
        }

        return false;
    }

    @Override
    public boolean deleteAllValues(UUID projectId, int processVariableId) {

        List<DiscreteProcessVariableValue> savedValues = getAllValuesById(projectId, processVariableId);

        if (savedValues != null || savedValues.isEmpty()) {
            for (DiscreteProcessVariableValue value : savedValues) {
                getSession().delete(value);
            }
            return true;
        }
        return false;
    }

    @Override
    public DiscreteProcessVariableValue getValue(UUID projectId, int processVariableId, String value) {
        //Initialize Database Access
        CriteriaBuilder builder = getSession().getCriteriaBuilder();
        CriteriaQuery<DiscreteProcessVariableValue> query = builder.createQuery(DiscreteProcessVariableValue.class);
        Root<DiscreteProcessVariableValue> root = query.from(DiscreteProcessVariableValue.class);
        query.select(root);

        //searchQuery
        query.where(builder.and(
                builder.equal(root.get(DiscreteProcessVariableValue.EntityAttributes.PROJECT_ID), projectId),
                builder.equal(root.get(DiscreteProcessVariableValue.EntityAttributes.PROCESS_VARIABLE_ID), processVariableId),
                builder.equal(root.get(DiscreteProcessVariableValue.EntityAttributes.PROCESS_VARIABLE_VALUE), value)
        ));

        return getSession().createQuery(query).getSingleResult();
    }

    @Override
    public List<DiscreteProcessVariableValue> saveAll(List<DiscreteProcessVariableValue> discreteProcessVariableValues) {
        Session session = getSession();
        List<DiscreteProcessVariableValue> savedDiscreteProcessVariableValues = new LinkedList<>();

        for (DiscreteProcessVariableValue discreteProcessVariableValue: discreteProcessVariableValues) {
            DiscreteProcessVariableValue savedDiscreteProcessVariableValue = DiscreteProcessVariableValue.class
                    .cast(session.save(discreteProcessVariableValue));
            savedDiscreteProcessVariableValues.add(savedDiscreteProcessVariableValue);
        }

        session.flush();

        return savedDiscreteProcessVariableValues;
    }
}
