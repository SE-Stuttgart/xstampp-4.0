package de.xstampp.service.project.service.dao;

import de.xstampp.service.project.data.entity.User;
import de.xstampp.service.project.service.dao.generic.AbstractGenericHibernateDAO;
import de.xstampp.service.project.service.dao.iface.IUserDAO;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.UUID;

@Repository
public class UserHibernateDAO extends AbstractGenericHibernateDAO<User, UUID> implements IUserDAO {

    @Override
    protected String getIdAttributeName() {
        return User.EntityAttributes.ID;
    }

    @Override
    public boolean lockEntity(UUID uuid, UUID userId, String userName, Timestamp expirationTime) {
        return false;
    }

    @Override
    public boolean unlockEntity(UUID uuid, UUID userId, String userName) {
        return false;
    }
}
