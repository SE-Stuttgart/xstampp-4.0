package de.xstampp.service.auth.service.dao;

import de.xstampp.service.auth.data.Icon;
import de.xstampp.service.auth.service.dao.AbstractGenericHibernateDAO;
import de.xstampp.service.auth.service.dao.IIconDAO;

import org.hibernate.LockMode;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public class IconHibernateDAO extends AbstractGenericHibernateDAO<Icon, Integer> implements IIconDAO {

    /*
    @Override
    protected String getIdAttributeName() {
        return "id";
    }
*/
    @Override
    public List<Icon> getAllIcons() {
        CriteriaBuilder builder = getSession().getCriteriaBuilder();
        CriteriaQuery<Icon> query = builder.createQuery(Icon.class);
        Root<Icon> root = query.from(Icon.class);
        query.select(root);
        return getSession().createQuery(query).getResultList();
    }



    @Override
    public List<Icon> findByExample(Icon exampleInstance) {
		throw new UnsupportedOperationException();
    }
    
    @Override
    public Icon findById(Integer id, boolean lock){
        Icon entity;
        if(lock){
            entity = (Icon) getSession().get(getPersistentClass(), id, LockMode.PESSIMISTIC_WRITE);
        }else{
            entity = (Icon) getSession().get(getPersistentClass(), id);
        }

        return entity;
    }
}
