package de.xstampp.service.auth.service.dao.remote;

import de.xstampp.service.auth.data.Project;
import de.xstampp.service.auth.data.remote.RemoteProject;
import de.xstampp.service.auth.service.dao.AbstractGenericHibernateDAO;
import org.springframework.stereotype.Repository;

import java.rmi.Remote;
import java.util.List;
import java.util.UUID;

@Repository
public class RemoteProjectHibernateDAO extends AbstractGenericHibernateDAO<RemoteProject,UUID> implements IRemoteProjectDAO {

	@Override
	public List<RemoteProject> findByExample(RemoteProject exampleInstance) {
		throw new  UnsupportedOperationException();
	}
	

}
