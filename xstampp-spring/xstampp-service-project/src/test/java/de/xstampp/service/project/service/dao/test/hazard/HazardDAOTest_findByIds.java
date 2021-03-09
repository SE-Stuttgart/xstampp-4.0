package de.xstampp.service.project.service.dao.test.hazard;

import static de.xstampp.service.project.service.dao.test.hazard.HazardEntityTestConstants.TEST_HAZARD_ID_1;
import static de.xstampp.service.project.service.dao.test.hazard.HazardEntityTestConstants.TEST_HAZARD_ID_2;
import static de.xstampp.service.project.service.dao.test.hazard.HazardEntityTestConstants.TEST_HAZARD_ID_3;
import static de.xstampp.service.project.service.dao.test.hazard.HazardEntityTestConstants.TEST_HAZARD_NAME_1;
import static de.xstampp.service.project.service.dao.test.project.ProjectEntityTestConstants.TEST_DESCRIPTION;
import static de.xstampp.service.project.service.dao.test.project.ProjectEntityTestConstants.TEST_PROJECT_ID_1;
import static de.xstampp.service.project.service.dao.test.project.ProjectEntityTestConstants.TEST_PROJECT_ID_2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
 * Temporarily ignore tests, because the automated pipeline currently does not
 * support database access.
 */
@Ignore
@SpringBootTest(classes = { ProjectServiceTestDatabaseHibernateCleaner.class, PersistenceHibernateConfiguration.class,
		ConfigurationService.class, HazardHibernateDAO.class, ProjectHibernateDAO.class })
public class HazardDAOTest_findByIds extends AbstractDAOTest {
	
	private static final String ASSERT_CONTAINS = "expected: key is in the result set.";
	
	@Autowired
	private IProjectDAO projectDAO;
	
	@Autowired
	private IHazardDAO hazardDAO;
	
	private XStamppEntityTestHelper xstamppHelper;

	@Override
	protected void initTestCase() {
		xstamppHelper = new XStamppEntityTestHelper(now(), afterFewMinutes());
		
		projectDAO.makePersistent(new Project(TEST_PROJECT_ID_1));
		projectDAO.makePersistent(new Project(TEST_PROJECT_ID_2));
		
		hazardDAO.makePersistent(initHazard(TEST_PROJECT_ID_1, TEST_HAZARD_ID_1, TEST_HAZARD_NAME_1));
		hazardDAO.makePersistent(initHazard(TEST_PROJECT_ID_1, TEST_HAZARD_ID_2, TEST_HAZARD_NAME_1));
		hazardDAO.makePersistent(initHazard(TEST_PROJECT_ID_1, TEST_HAZARD_ID_3, TEST_HAZARD_NAME_1));

		hazardDAO.makePersistent(initHazard(TEST_PROJECT_ID_2, TEST_HAZARD_ID_1, TEST_HAZARD_NAME_1));
	}
	
	private Hazard initHazard(UUID projectId, int hazardId, String name) {
		Hazard result = new Hazard(new ProjectDependentKey(projectId, hazardId));
		result.setName(name);
		result.setDescription(TEST_DESCRIPTION);
		xstamppHelper.init(result);
		return result;
	}
	
	/**
	 * 
	 * P1-H2
	 */
	@Test
	public void find1() {
		TestTransaction.start();
		List<Integer> hazardIds = new ArrayList<>();
		hazardIds.add(TEST_HAZARD_ID_2);
		List<Hazard> hazards = hazardDAO.findHazardsByIds(TEST_PROJECT_ID_1, hazardIds);
		checkFind1(hazards);
		TestTransaction.end();
	}
	
	/**
	 * 
	 * P1-H2,P1-H2
	 */
	@Test
	public void find2() {
		TestTransaction.start();
		List<Integer> hazardIds = new ArrayList<>();
		hazardIds.add(TEST_HAZARD_ID_2);
		hazardIds.add(TEST_HAZARD_ID_3);
		List<Hazard> hazards = hazardDAO.findHazardsByIds(TEST_PROJECT_ID_1, hazardIds);
		checkFind2(hazards);
		TestTransaction.end();
	}
	
	/**
	 * 
	 * P2-H1
	 */
	@Test
	public void find3() {
		TestTransaction.start();
		List<Integer> hazardIds = new ArrayList<>();
		hazardIds.add(TEST_HAZARD_ID_1);
		List<Hazard> hazards = hazardDAO.findHazardsByIds(TEST_PROJECT_ID_2, hazardIds);
		checkFind3(hazards);
		TestTransaction.end();
	}
	
	/**
	 * 
	 * P2-H2, P2-H3 -> empty result set.
	 */
	@Test
	public void find4() {
		TestTransaction.start();
		List<Integer> hazardIds = new ArrayList<>();
		hazardIds.add(TEST_HAZARD_ID_2);
		hazardIds.add(TEST_HAZARD_ID_3);
		List<Hazard> hazards = hazardDAO.findHazardsByIds(TEST_PROJECT_ID_2, hazardIds);
		assertEquals(0, hazards.size());
		TestTransaction.end();
	}
	
	@Test
	public void findProjectNull() {
		TestTransaction.start();
		List<Integer> hazardIds = new ArrayList<>();
		hazardIds.add(TEST_HAZARD_ID_1);
		hazardIds.add(TEST_HAZARD_ID_2);
		List<Hazard> hazards = hazardDAO.findHazardsByIds(null, hazardIds);
		assertEquals(0, hazards.size());
		TestTransaction.end();
	}
	
	@Test
	public void findNullAsIds() {
		TestTransaction.start();
		List<Hazard> hazards = hazardDAO.findHazardsByIds(TEST_PROJECT_ID_1, null);
		assertEquals(0, hazards.size());
		TestTransaction.end();
	}
	
	@Test
	public void findEmptyListAsIds() {
		TestTransaction.start();
		List<Integer> hazardIds = new ArrayList<>();
		List<Hazard> hazards = hazardDAO.findHazardsByIds(TEST_PROJECT_ID_1, hazardIds);
		assertEquals(0, hazards.size());
		TestTransaction.end();
	}
	
	private void checkFind1(List<Hazard> hazards) {
		assertEquals(1, hazards.size());
		checkGeneral(hazards);
		List<ProjectDependentKey> keys = getKeys(hazards);
		assertTrue(ASSERT_CONTAINS, keys.contains(new ProjectDependentKey(TEST_PROJECT_ID_1, TEST_HAZARD_ID_2)));
	}
	
	private void checkFind2(List<Hazard> hazards) {
		assertEquals(2, hazards.size());
		checkGeneral(hazards);
		List<ProjectDependentKey> keys = getKeys(hazards);
		assertTrue(ASSERT_CONTAINS, keys.contains(new ProjectDependentKey(TEST_PROJECT_ID_1, TEST_HAZARD_ID_2)));
		assertTrue(ASSERT_CONTAINS, keys.contains(new ProjectDependentKey(TEST_PROJECT_ID_1, TEST_HAZARD_ID_3)));
	}
	
	private void checkFind3(List<Hazard> hazards) {
		assertEquals(1, hazards.size());
		checkGeneral(hazards);
		List<ProjectDependentKey> keys = getKeys(hazards);
		assertTrue(ASSERT_CONTAINS, keys.contains(new ProjectDependentKey(TEST_PROJECT_ID_2, TEST_HAZARD_ID_1)));
	}

	private void checkGeneral(List<Hazard> hazards) {
		for (Hazard hazard: hazards) {
			assertEquals(TEST_HAZARD_NAME_1, hazard.getName());
			assertEquals(TEST_DESCRIPTION, hazard.getDescription());
			xstamppHelper.check(hazard);
		}
	}
	
	private static List<ProjectDependentKey> getKeys(List<Hazard> hazards) {
		List<ProjectDependentKey> result = new ArrayList<ProjectDependentKey>();
		for (Hazard hazard: hazards) {
			result.add(hazard.getId());
		}
		return result;
	}
	
}
