package de.xstampp.service.auth.service.dao.remote;

import de.xstampp.service.auth.data.remote.RemoteUser;
import de.xstampp.service.auth.service.dao.AbstractGenericHibernateDAO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class RemoteUserHibernateDAO  extends AbstractGenericHibernateDAO<RemoteUser, UUID> implements IRemoteUserDAO  {
    @Override
    public List<RemoteUser> findByExample(RemoteUser exampleInstance) {
        throw new  UnsupportedOperationException();
    }
}
