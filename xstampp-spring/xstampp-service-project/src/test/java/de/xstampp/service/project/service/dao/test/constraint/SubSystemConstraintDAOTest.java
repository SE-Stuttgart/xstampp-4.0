package de.xstampp.service.project.service.dao.test.constraint;

import static de.xstampp.service.project.service.dao.test.constraint.SubSystemConstraintEntityTestConstants.TEST_NON_EXISTENT_SUB_SYSTEM_CONSTRAINT_ID;
import static de.xstampp.service.project.service.dao.test.constraint.SubSystemConstraintEntityTestConstants.TEST_SUB_SYSTEM_CONSRAINT_ID;
import static de.xstampp.service.project.service.dao.test.constraint.SubSystemConstraintEntityTestConstants.TEST_SUB_SYSTEM_CONSTRAINT_NAME;
import static de.xstampp.service.project.service.dao.test.constraint.SystemConstraintEntityTestConstants.TEST_SYSTEM_CONSTRAINT_ID;
import static de.xstampp.service.project.service.dao.test.hazard.HazardEntityTestConstants.TEST_HAZARD_ID_1;
import static de.xstampp.service.project.service.dao.test.hazard.HazardEntityTestConstants.TEST_HAZARD_NAME_1;
import static de.xstampp.service.project.service.dao.test.hazard.SubHazardEntityTestConstants.TEST_SUBHAZARD_ID_1;
import static de.xstampp.service.project.service.dao.test.hazard.SubHazardEntityTestConstants.TEST_SUBHAZARD_NAME_1;
import static de.xstampp.service.project.service.dao.test.project.ProjectEntityTestConstants.TEST_DESCRIPTION;
import static de.xstampp.service.project.service.dao.test.project.ProjectEntityTestConstants.TEST_PROJECT_ID_1;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
import de.xstampp.service.project.data.entity.SubSystemConstraint;
import de.xstampp.service.project.data.entity.SystemConstraint;
import de.xstampp.service.project.service.dao.AbstractDAOTest;
import de.xstampp.service.project.service.dao.HazardHibernateDAO;
import de.xstampp.service.project.service.dao.ProjectHibernateDAO;
import de.xstampp.service.project.service.dao.ProjectServiceTestDatabaseHibernateCleaner;
import de.xstampp.service.project.service.dao.SubHazardHibernateDAO;
import de.xstampp.service.project.service.dao.SubSystemConstraintHibernateDAO;
import de.xstampp.service.project.service.dao.SystemConstraintHibernateDAO;
import de.xstampp.service.project.service.dao.XStamppEntityTestHelper;
import de.xstampp.service.project.service.dao.iface.IHazardDAO;
import de.xstampp.service.project.service.dao.iface.IProjectDAO;
import de.xstampp.service.project.service.dao.iface.ISubHazardDAO;
import de.xstampp.service.project.service.dao.iface.ISubSystemConstraintDAO;
import de.xstampp.service.project.service.dao.iface.ISystemConstraintDAO;

/**
 * Temporarily ignore tests, because the automated pipeline currently does not
 * support database access.
 */
@Ignore
@SpringBootTest(classes = { ProjectServiceTestDatabaseHibernateCleaner.class, PersistenceHibernateConfiguration.class,
		ConfigurationService.class, SubSystemConstraintHibernateDAO.class, SystemConstraintHibernateDAO.class,
		ProjectHibernateDAO.class, HazardHibernateDAO.class, SubHazardHibernateDAO.class})
public class SubSystemConstraintDAOTest extends AbstractDAOTest {

	@Autowired
	private IProjectDAO projectDAO;

	@Autowired
	private ISystemConstraintDAO systemConstraintDAO;

	@Autowired
	private ISubSystemConstraintDAO subSystemConstraintDAO;
	
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

		ProjectDependentKey sysConKey = new ProjectDependentKey(TEST_PROJECT_ID_1, TEST_SUB_SYSTEM_CONSRAINT_ID);
		SystemConstraint systemConstraint = new SystemConstraint(sysConKey);
		systemConstraint.setName(TEST_SUB_SYSTEM_CONSTRAINT_NAME);
		systemConstraint.setDescription(TEST_DESCRIPTION);
		xstamppHelper.init(systemConstraint);
		systemConstraintDAO.makePersistent(systemConstraint);
		
		EntityDependentKey subSysConKey = new EntityDependentKey(TEST_PROJECT_ID_1, TEST_SYSTEM_CONSTRAINT_ID, TEST_SUB_SYSTEM_CONSRAINT_ID);
		SubSystemConstraint subSystemConstraint = new SubSystemConstraint(subSysConKey);
		subSystemConstraint.setName(TEST_SUB_SYSTEM_CONSTRAINT_NAME);
		subSystemConstraint.setDescription(TEST_DESCRIPTION);
		subSystemConstraint.setSubHazardProjectId(TEST_PROJECT_ID_1);
		subSystemConstraint.setHazardId(TEST_HAZARD_ID_1);
		subSystemConstraint.setSubHazardId(TEST_SUBHAZARD_ID_1);
		xstamppHelper.init(subSystemConstraint);
		subSystemConstraintDAO.makePersistent(subSystemConstraint);
	}
	
	@Test
	public void loadByIdForRead() {
		TestTransaction.start();
		EntityDependentKey key = new EntityDependentKey(TEST_PROJECT_ID_1, TEST_SYSTEM_CONSTRAINT_ID, TEST_SUB_SYSTEM_CONSRAINT_ID);
		SubSystemConstraint result = subSystemConstraintDAO.findById(key, false);
		checkSubSystemConstraint(result);
		TestTransaction.end();
	}
	
	@Test
	public void loadByIdForReadNonExistent() {
		TestTransaction.start();
		EntityDependentKey key = new EntityDependentKey(TEST_PROJECT_ID_1, TEST_HAZARD_ID_1, TEST_NON_EXISTENT_SUB_SYSTEM_CONSTRAINT_ID);
		SubSystemConstraint result = subSystemConstraintDAO.findById(key, false);
		assertNull(result);
		TestTransaction.end();
	}
	
	private void checkSubSystemConstraint(SubSystemConstraint result) {
		
		assertEquals(TEST_PROJECT_ID_1, result.getId().getProjectId());
		assertEquals(TEST_SYSTEM_CONSTRAINT_ID, result.getId().getParentId());
		assertEquals(TEST_SUB_SYSTEM_CONSRAINT_ID, result.getId().getId());
		assertEquals(TEST_SUB_SYSTEM_CONSTRAINT_NAME, result.getName());
		assertEquals(TEST_DESCRIPTION, result.getDescription());
		assertEquals(TEST_PROJECT_ID_1, result.getSubHazardProjectId());
		assertEquals(TEST_HAZARD_ID_1, result.getHazardId().intValue());
		assertEquals(TEST_SUBHAZARD_ID_1, result.getSubHazardId().intValue());
		xstamppHelper.check(result);
	}

}
