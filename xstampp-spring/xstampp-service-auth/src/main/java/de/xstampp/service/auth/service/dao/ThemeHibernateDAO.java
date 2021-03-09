package de.xstampp.service.auth.service.dao;

import de.xstampp.service.auth.data.Theme;

import de.xstampp.service.auth.service.dao.AbstractGenericHibernateDAO;
import de.xstampp.service.auth.service.dao.IThemeDAO;

import org.hibernate.LockMode;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public class ThemeHibernateDAO extends AbstractGenericHibernateDAO<Theme, Integer> implements IThemeDAO {

    /*
    @Override
    protected String getId() {
        return "id";
    }
*/
    @Override
    public List<Theme> getAllThemes() {
        CriteriaBuilder builder = getSession().getCriteriaBuilder();
        CriteriaQuery<Theme> query = builder.createQuery(Theme.class);
        Root<Theme> root = query.from(Theme.class);
        query.select(root);
        return getSession().createQuery(query).getResultList();
    }

    @Override
    public List<Theme> findByExample(Theme exampleInstance) {
		throw new UnsupportedOperationException();
    }
    
    @Override
    public Theme findById(Integer id, boolean lock){
        Theme entity;
        if(lock){
            entity = (Theme) getSession().get(getPersistentClass(), id, LockMode.PESSIMISTIC_WRITE);
        }else{
            entity = (Theme) getSession().get(getPersistentClass(), id);
        }

        return entity;
    }
    
}
