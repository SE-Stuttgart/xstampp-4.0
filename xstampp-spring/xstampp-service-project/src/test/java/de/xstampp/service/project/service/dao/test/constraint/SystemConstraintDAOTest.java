package de.xstampp.service.project.service.dao.test.constraint;

import static de.xstampp.service.project.service.dao.test.constraint.SystemConstraintEntityTestConstants.*;
import static de.xstampp.service.project.service.dao.test.project.ProjectEntityTestConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.transaction.TestTransaction;

import de.xstampp.common.service.ConfigurationService;
import de.xstampp.service.project.configuration.PersistenceHibernateConfiguration;
import de.xstampp.service.project.data.entity.Project;
import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.SystemConstraint;
import de.xstampp.service.project.service.dao.AbstractDAOTest;
import de.xstampp.service.project.service.dao.ProjectHibernateDAO;
import de.xstampp.service.project.service.dao.ProjectServiceTestDatabaseHibernateCleaner;
import de.xstampp.service.project.service.dao.SystemConstraintHibernateDAO;
import de.xstampp.service.project.service.dao.XStamppEntityTestHelper;
import de.xstampp.service.project.service.dao.iface.IProjectDAO;
import de.xstampp.service.project.service.dao.iface.ISystemConstraintDAO;

/**
 *	Temporarily ignore tests, because the automated pipeline currently does not support database access.
 */
@Ignore
@SpringBootTest(classes = {ProjectServiceTestDatabaseHibernateCleaner.class, PersistenceHibernateConfiguration.class, ConfigurationService.class, SystemConstraintHibernateDAO.class, ProjectHibernateDAO.class})
public class SystemConstraintDAOTest extends AbstractDAOTest {
	
	@Autowired
	private IProjectDAO projectDAO;
	
	@Autowired
	private ISystemConstraintDAO systemConstraintDAO;
	
	private XStamppEntityTestHelper xstamppHelper;

	@Override
	protected void initTestCase() {
		xstamppHelper = new XStamppEntityTestHelper(now(), afterFewMinutes());
		
		projectDAO.makePersistent(new Project(TEST_PROJECT_ID_1));
		
		ProjectDependentKey key = new ProjectDependentKey(TEST_PROJECT_ID_1, TEST_SYSTEM_CONSTRAINT_ID);
		SystemConstraint entity = new SystemConstraint(key);
		entity.setName(TEST_SYSTEM_CONSTRAINT_NAME);
		entity.setDescription(TEST_DESCRIPTION);
		xstamppHelper.init(entity);
		systemConstraintDAO.makePersistent(entity);
	}
	
	@Test
	public void loadByIdForRead() {
		TestTransaction.start();
		ProjectDependentKey key = new ProjectDependentKey(TEST_PROJECT_ID_1, TEST_SYSTEM_CONSTRAINT_ID);
		SystemConstraint result = systemConstraintDAO.findById(key, false);
		checkSystemConstraint(result);
		TestTransaction.end();
	}
	
	@Test
	public void loadByIdForReadNonExistent() {
		TestTransaction.start();
		ProjectDependentKey key = new ProjectDependentKey(TEST_PROJECT_ID_1, TEST_NON_EXISTENT_SYSTEM_CONSTRAINT_ID);
		SystemConstraint result = systemConstraintDAO.findById(key, false);
		assertNull(result);
		TestTransaction.end();
	}
	
	private void checkSystemConstraint(SystemConstraint result) {
		assertEquals(TEST_PROJECT_ID_1, result.getId().getProjectId());
		assertEquals(TEST_SYSTEM_CONSTRAINT_ID, result.getId().getId());
		assertEquals(TEST_SYSTEM_CONSTRAINT_NAME, result.getName());
		assertEquals(TEST_DESCRIPTION, result.getDescription());
		xstamppHelper.check(result);
	}

}
