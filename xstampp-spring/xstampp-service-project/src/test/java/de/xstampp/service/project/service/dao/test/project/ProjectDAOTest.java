package de.xstampp.service.project.service.dao.test.project;

import static de.xstampp.service.project.service.dao.test.project.ProjectEntityTestConstants.TEST_NON_EXISTENT_PROJECT_ID;
import static de.xstampp.service.project.service.dao.test.project.ProjectEntityTestConstants.TEST_PROJECT_ID_1;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.transaction.TestTransaction;

import de.xstampp.common.service.ConfigurationService;
import de.xstampp.service.project.configuration.PersistenceHibernateConfiguration;
import de.xstampp.service.project.data.entity.Project;
import de.xstampp.service.project.service.dao.AbstractDAOTest;
import de.xstampp.service.project.service.dao.ProjectHibernateDAO;
import de.xstampp.service.project.service.dao.ProjectServiceTestDatabaseHibernateCleaner;
import de.xstampp.service.project.service.dao.iface.IProjectDAO;

/**
 *	Temporarily ignore tests, because the automated pipeline currently does not support database access.
 */
@Ignore
@SpringBootTest(classes = {ProjectServiceTestDatabaseHibernateCleaner.class, PersistenceHibernateConfiguration.class, ConfigurationService.class, ProjectHibernateDAO.class})
public class ProjectDAOTest extends AbstractDAOTest {
	
	@Autowired
	private IProjectDAO projectDAO;
	
	@Override
	protected void initTestCase() {
		Project project = new Project(TEST_PROJECT_ID_1);
		projectDAO.makePersistent(project);
	}
	
	@Test
	public void loadEntityForRead() {
		TestTransaction.start();
		Project project = projectDAO.findById(TEST_PROJECT_ID_1, false);
		checkProject(project);
		TestTransaction.end();
	}
	
	private void checkProject(Project project) {
		assertNotNull(project);
		assertEquals(TEST_PROJECT_ID_1, project.getId());
	}
	
	@Test
	public void loadEntityForWrite() {
		TestTransaction.start();
		Project project = projectDAO.findById(TEST_PROJECT_ID_1, true);
		checkProject(project);
		TestTransaction.end();
	}
	
	@Test
	public void loadNonExistentEntityForWrite() {
		TestTransaction.start();
		Project project = projectDAO.findById(TEST_NON_EXISTENT_PROJECT_ID, true);
		assertNull(project);
		TestTransaction.end();
	}
	
	@Test
	public void loadNonExistentEntityForRead() {
		TestTransaction.start();
		Project project = projectDAO.findById(TEST_NON_EXISTENT_PROJECT_ID, false);
		assertNull(project);
		TestTransaction.end();
	}
	
	@Test
	public void entityExist() {
		TestTransaction.start();
		boolean exist = projectDAO.exist(TEST_PROJECT_ID_1);
		assertTrue("expected: entity exists.", exist);
		TestTransaction.end();
	}
	
	@Test
	public void entityNotExist() {
		TestTransaction.start();
		boolean exist = projectDAO.exist(TEST_NON_EXISTENT_PROJECT_ID);
		assertFalse("expected: entity does not exist.", exist);
		TestTransaction.end();
	}

}
