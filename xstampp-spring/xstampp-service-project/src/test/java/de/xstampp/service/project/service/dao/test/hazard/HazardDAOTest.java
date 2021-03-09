package de.xstampp.service.project.service.dao.test.hazard;

import static de.xstampp.service.project.service.dao.test.hazard.HazardEntityTestConstants.TEST_HAZARD_ID_1;
import static de.xstampp.service.project.service.dao.test.hazard.HazardEntityTestConstants.TEST_HAZARD_NAME_1;
import static de.xstampp.service.project.service.dao.test.hazard.HazardEntityTestConstants.TEST_NON_EXISTENT_HAZARD_ID;
import static de.xstampp.service.project.service.dao.test.project.ProjectEntityTestConstants.TEST_DESCRIPTION;
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
import de.xstampp.service.project.data.entity.Hazard;
import de.xstampp.service.project.data.entity.Project;
import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.service.dao.AbstractDAOTest;
import de.xstampp.service.project.service.dao.HazardHibernateDAO;
import de.xstampp.service.project.service.dao.ProjectHibernateDAO;
import de.xstampp.service.project.service.dao.ProjectServiceTestDatabaseHibernateCleaner;
import de.xstampp.service.project.service.dao.XStamppEntityTestHelper;
import de.xstampp.service.project.service.dao.iface.IHazardDAO;
import de.xstampp.service.project.service.dao.iface.IProjectDAO;

/**
 *	Temporarily ignore tests, because the automated pipeline currently does not support database access.
 */
@Ignore
@SpringBootTest(classes = {ProjectServiceTestDatabaseHibernateCleaner.class, PersistenceHibernateConfiguration.class, ConfigurationService.class, HazardHibernateDAO.class, ProjectHibernateDAO.class})
public class HazardDAOTest extends AbstractDAOTest {
	
	@Autowired
	private IProjectDAO projectDAO;
	
	@Autowired
	private IHazardDAO hazardDAO;
	
	private XStamppEntityTestHelper xstamppHelper;

	@Override
	protected void initTestCase() {
		xstamppHelper = new XStamppEntityTestHelper(now(), afterFewMinutes());
		projectDAO.makePersistent(new Project(TEST_PROJECT_ID_1));
		ProjectDependentKey id = new ProjectDependentKey(TEST_PROJECT_ID_1, TEST_HAZARD_ID_1);
		Hazard hazard = new Hazard(id);
		hazard.setName(TEST_HAZARD_NAME_1);
		hazard.setDescription(TEST_DESCRIPTION);
		xstamppHelper.init(hazard);
		hazardDAO.makePersistent(hazard);
	}
	
	@Test
	public void loadByIdForRead() {
		TestTransaction.start();
		ProjectDependentKey id = new ProjectDependentKey(TEST_PROJECT_ID_1, TEST_HAZARD_ID_1);
		Hazard hazard = hazardDAO.findById(id, false);
		checkHazard(hazard);
		TestTransaction.end();
	}
	
	@Test
	public void loadByIdForWrite() {
		TestTransaction.start();
		ProjectDependentKey id = new ProjectDependentKey(TEST_PROJECT_ID_1, TEST_HAZARD_ID_1);
		Hazard hazard = hazardDAO.findById(id, true);
		checkHazard(hazard);
		TestTransaction.end();
	}
	
	@Test
	public void entityExist() {
		TestTransaction.start();
		ProjectDependentKey id = new ProjectDependentKey(TEST_PROJECT_ID_1, TEST_HAZARD_ID_1);
		boolean exist = hazardDAO.exist(id);
		assertTrue("expected: entity exists.", exist);
		TestTransaction.end();
	}
	
	@Test
	public void entityNotExist() {
		TestTransaction.start();
		ProjectDependentKey id = new ProjectDependentKey(TEST_PROJECT_ID_1, TEST_NON_EXISTENT_HAZARD_ID);
		boolean exist = hazardDAO.exist(id);
		assertFalse("expected: entity does not exist.", exist);
		TestTransaction.end();
	}

	private void checkHazard(Hazard hazard) {
		assertNotNull(hazard);
		assertEquals(TEST_PROJECT_ID_1, hazard.getId().getProjectId());
		assertEquals(TEST_HAZARD_ID_1, hazard.getId().getId());
		assertEquals(TEST_HAZARD_NAME_1, hazard.getName());
		assertEquals(TEST_DESCRIPTION, hazard.getDescription());
		xstamppHelper.check(hazard);
	}

}
