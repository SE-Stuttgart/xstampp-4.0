package de.xstampp.service.project.service.dao.test.hazard;

import static de.xstampp.service.project.service.dao.test.hazard.HazardEntityTestConstants.TEST_HAZARD_ID_1;
import static de.xstampp.service.project.service.dao.test.hazard.HazardEntityTestConstants.TEST_HAZARD_NAME_1;
import static de.xstampp.service.project.service.dao.test.hazard.SubHazardEntityTestConstants.TEST_NON_EXISTENT_SUBHAZARD_ID;
import static de.xstampp.service.project.service.dao.test.hazard.SubHazardEntityTestConstants.TEST_SUBHAZARD_ID_1;
import static de.xstampp.service.project.service.dao.test.hazard.SubHazardEntityTestConstants.TEST_SUBHAZARD_NAME_1;
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
import de.xstampp.service.project.data.entity.EntityDependentKey;
import de.xstampp.service.project.data.entity.Hazard;
import de.xstampp.service.project.data.entity.Project;
import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.SubHazard;
import de.xstampp.service.project.service.dao.AbstractDAOTest;
import de.xstampp.service.project.service.dao.HazardHibernateDAO;
import de.xstampp.service.project.service.dao.ProjectHibernateDAO;
import de.xstampp.service.project.service.dao.ProjectServiceTestDatabaseHibernateCleaner;
import de.xstampp.service.project.service.dao.SubHazardHibernateDAO;
import de.xstampp.service.project.service.dao.XStamppEntityTestHelper;
import de.xstampp.service.project.service.dao.iface.IHazardDAO;
import de.xstampp.service.project.service.dao.iface.IProjectDAO;
import de.xstampp.service.project.service.dao.iface.ISubHazardDAO;

/**
 *	Temporarily ignore tests, because the automated pipeline currently does not support database access.
 */
@Ignore
@SpringBootTest(classes = {ProjectServiceTestDatabaseHibernateCleaner.class, PersistenceHibernateConfiguration.class, ConfigurationService.class, SubHazardHibernateDAO.class, HazardHibernateDAO.class, ProjectHibernateDAO.class})
public class SubHazardDAOTest extends AbstractDAOTest {
	
	@Autowired
	private IProjectDAO projectDAO;
	
	@Autowired
	private IHazardDAO hazardDAO;
	
	@Autowired
	private ISubHazardDAO subHazardDAO;
	
	private XStamppEntityTestHelper xstamppHelper;

	@Override
	protected void initTestCase() {
		xstamppHelper = new XStamppEntityTestHelper(now(), afterFewMinutes());
		
		projectDAO.makePersistent(new Project(TEST_PROJECT_ID_1));
		
		ProjectDependentKey hazardKey = new ProjectDependentKey(TEST_PROJECT_ID_1, TEST_HAZARD_ID_1);
		Hazard hazard = new Hazard(hazardKey);
		hazard.setName(TEST_HAZARD_NAME_1);
		hazard.setDescription(TEST_DESCRIPTION);
		xstamppHelper.init(hazard);
		hazardDAO.makePersistent(hazard);
		
		EntityDependentKey subHazardKey = new EntityDependentKey(TEST_PROJECT_ID_1, TEST_HAZARD_ID_1, TEST_SUBHAZARD_ID_1);
		SubHazard subHazard = new SubHazard(subHazardKey);
		subHazard.setName(TEST_SUBHAZARD_NAME_1);
		subHazard.setDescription(TEST_DESCRIPTION);
		xstamppHelper.init(subHazard);
		subHazardDAO.makePersistent(subHazard);
	}
	
	@Test
	public void findByIdForRead() {
		TestTransaction.start();
		EntityDependentKey id = new EntityDependentKey(TEST_PROJECT_ID_1, TEST_HAZARD_ID_1, TEST_SUBHAZARD_ID_1);
		SubHazard subHazard = subHazardDAO.findById(id, false);
		checkSubHazard(subHazard);
		TestTransaction.end();
	}
	
	@Test
	public void findByIdForWrite() {
		TestTransaction.start();
		EntityDependentKey id = new EntityDependentKey(TEST_PROJECT_ID_1, TEST_HAZARD_ID_1, TEST_SUBHAZARD_ID_1);
		SubHazard subHazard = subHazardDAO.findById(id, true);
		checkSubHazard(subHazard);
		TestTransaction.end();
	}
	
	@Test
	public void entityExist() {
		TestTransaction.start();
		EntityDependentKey id = new EntityDependentKey(TEST_PROJECT_ID_1, TEST_HAZARD_ID_1, TEST_SUBHAZARD_ID_1);
		boolean exist = subHazardDAO.exist(id);
		assertTrue("expected: entity exists.", exist);
		TestTransaction.end();
	}
	
	@Test
	public void entityNotExist() {
		TestTransaction.start();
		EntityDependentKey id = new EntityDependentKey(TEST_PROJECT_ID_1, TEST_HAZARD_ID_1, TEST_NON_EXISTENT_SUBHAZARD_ID);
		boolean exist = subHazardDAO.exist(id);
		assertFalse("expected: entity does not exist.", exist);
		TestTransaction.end();
	}

	private void checkSubHazard(SubHazard subHazard) {
		assertNotNull(subHazard);
		assertEquals(TEST_PROJECT_ID_1, subHazard.getId().getProjectId());
		assertEquals(TEST_HAZARD_ID_1, subHazard.getId().getParentId());
		assertEquals(TEST_SUBHAZARD_ID_1, subHazard.getId().getId());
		assertEquals(TEST_SUBHAZARD_NAME_1, subHazard.getName());
		assertEquals(TEST_DESCRIPTION, subHazard.getDescription());
		xstamppHelper.check(subHazard);
	}

}
