package de.xstampp.service.project.service.dao.test.systemDescription;

import static de.xstampp.service.project.service.dao.test.project.ProjectEntityTestConstants.TEST_DESCRIPTION;
import static de.xstampp.service.project.service.dao.test.project.ProjectEntityTestConstants.TEST_NON_EXISTENT_PROJECT_ID;
import static de.xstampp.service.project.service.dao.test.project.ProjectEntityTestConstants.TEST_PROJECT_ID_1;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.transaction.TestTransaction;

import de.xstampp.common.service.ConfigurationService;
import de.xstampp.service.project.configuration.PersistenceHibernateConfiguration;
import de.xstampp.service.project.data.entity.Project;
import de.xstampp.service.project.data.entity.SystemDescription;
import de.xstampp.service.project.service.dao.AbstractDAOTest;
import de.xstampp.service.project.service.dao.ProjectHibernateDAO;
import de.xstampp.service.project.service.dao.ProjectServiceTestDatabaseHibernateCleaner;
import de.xstampp.service.project.service.dao.SystemDescriptionHibernateDAO;
import de.xstampp.service.project.service.dao.XStamppEntityTestHelper;
import de.xstampp.service.project.service.dao.iface.IProjectDAO;
import de.xstampp.service.project.service.dao.iface.ISystemDescriptionDAO;

/**
 *	Temporarily ignore tests, because the automated pipeline currently does not support database access.
 */
@Ignore
@SpringBootTest(classes = {ProjectServiceTestDatabaseHibernateCleaner.class, PersistenceHibernateConfiguration.class, ConfigurationService.class, SystemDescriptionHibernateDAO.class, ProjectHibernateDAO.class})
public class SystemDescriptionDAOTest extends AbstractDAOTest {
	
	@Autowired
	private ISystemDescriptionDAO systemDescriptionDAO;
	
	@Autowired
	private IProjectDAO projectDAO;
	
	private XStamppEntityTestHelper xstamppHelper;

	@Override
	protected void initTestCase() {
		this.xstamppHelper = new XStamppEntityTestHelper(now(), afterFewMinutes());
		projectDAO.makePersistent(new Project(TEST_PROJECT_ID_1));
		SystemDescription systemDescription = new SystemDescription(TEST_PROJECT_ID_1);
		systemDescription.setDescription(TEST_DESCRIPTION);
		xstamppHelper.init(systemDescription);
		systemDescriptionDAO.makePersistent(systemDescription);
	}
	
	@Test
	public void loadEntityForRead() {
		TestTransaction.start();
		SystemDescription systemDescription = systemDescriptionDAO.findById(TEST_PROJECT_ID_1, false);
		checkSystemDescription(systemDescription);
		TestTransaction.end();
	}
	
	@Test
	public void entityExist() {
		TestTransaction.start();
		boolean exist = systemDescriptionDAO.exist(TEST_PROJECT_ID_1);
		assertTrue(exist);
		TestTransaction.end();
	}
	
	@Test
	public void entityNotExist() {
		TestTransaction.start();
		boolean exist = systemDescriptionDAO.exist(TEST_NON_EXISTENT_PROJECT_ID);
		assertFalse(exist);
		TestTransaction.end();
	}

	private void checkSystemDescription(SystemDescription systemDescription) {
		assertNotNull(systemDescription);
		assertEquals(TEST_PROJECT_ID_1, systemDescription.getProjectId());
		assertEquals(TEST_DESCRIPTION, systemDescription.getDescription());
		xstamppHelper.check(systemDescription);
	}

}
