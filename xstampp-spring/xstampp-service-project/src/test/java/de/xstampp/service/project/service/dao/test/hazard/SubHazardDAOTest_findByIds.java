package de.xstampp.service.project.service.dao.test.hazard;

import static de.xstampp.service.project.service.dao.test.hazard.HazardEntityTestConstants.TEST_HAZARD_ID_1;
import static de.xstampp.service.project.service.dao.test.hazard.HazardEntityTestConstants.TEST_HAZARD_ID_2;
import static de.xstampp.service.project.service.dao.test.hazard.SubHazardEntityTestConstants.TEST_SUBHAZARD_ID_1;
import static de.xstampp.service.project.service.dao.test.hazard.SubHazardEntityTestConstants.TEST_SUBHAZARD_ID_2;
import static de.xstampp.service.project.service.dao.test.hazard.SubHazardEntityTestConstants.TEST_SUBHAZARD_ID_3;
import static de.xstampp.service.project.service.dao.test.hazard.SubHazardEntityTestConstants.TEST_SUBHAZARD_NAME_1;
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
import de.xstampp.service.project.data.entity.EntityDependentKey;
import de.xstampp.service.project.data.entity.Project;
import de.xstampp.service.project.data.entity.SubHazard;
import de.xstampp.service.project.service.dao.AbstractDAOTest;
import de.xstampp.service.project.service.dao.HazardHibernateDAO;
import de.xstampp.service.project.service.dao.ProjectHibernateDAO;
import de.xstampp.service.project.service.dao.ProjectServiceTestDatabaseHibernateCleaner;
import de.xstampp.service.project.service.dao.SubHazardHibernateDAO;
import de.xstampp.service.project.service.dao.iface.IHazardDAO;
import de.xstampp.service.project.service.dao.iface.IProjectDAO;
import de.xstampp.service.project.service.dao.iface.ISubHazardDAO;

/**
 * Temporarily ignore tests, because the automated pipeline currently does not
 * support database access.
 */
@Ignore
@SpringBootTest(classes = { ProjectServiceTestDatabaseHibernateCleaner.class, PersistenceHibernateConfiguration.class,
		ConfigurationService.class, SubHazardHibernateDAO.class, HazardHibernateDAO.class, ProjectHibernateDAO.class })
public class SubHazardDAOTest_findByIds extends AbstractDAOTest {
	
	private static final String ASSERT_CONTAINS_MESSAGE = "expected: key is in the result set.";

	@Autowired
	private IProjectDAO projectDAO;

	@Autowired
	private IHazardDAO hazardDAO;

	@Autowired
	private ISubHazardDAO subHazardDAO;

	private SubHazardEntityTestHelper subHazardHelper;

	@Override
	protected void initTestCase() {
		subHazardHelper = new SubHazardEntityTestHelper(now(), afterFewMinutes());

		projectDAO.makePersistent(new Project(TEST_PROJECT_ID_1));
		projectDAO.makePersistent(new Project(TEST_PROJECT_ID_2));

		hazardDAO.makePersistent(subHazardHelper.getHazardFor(TEST_PROJECT_ID_1, TEST_HAZARD_ID_1));
		hazardDAO.makePersistent(subHazardHelper.getHazardFor(TEST_PROJECT_ID_1, TEST_HAZARD_ID_2));

		saveSubHazard(TEST_PROJECT_ID_1, TEST_HAZARD_ID_1, TEST_SUBHAZARD_ID_1);
		saveSubHazard(TEST_PROJECT_ID_1, TEST_HAZARD_ID_1, TEST_SUBHAZARD_ID_2);
		saveSubHazard(TEST_PROJECT_ID_1, TEST_HAZARD_ID_1, TEST_SUBHAZARD_ID_3);

		saveSubHazard(TEST_PROJECT_ID_1, TEST_HAZARD_ID_2, TEST_SUBHAZARD_ID_1);
	}

	private void saveSubHazard(UUID projectId, int hazardId, int id) {
		subHazardDAO.makePersistent(
				subHazardHelper.getSubHazardFor(projectId, hazardId, id, TEST_SUBHAZARD_NAME_1, TEST_DESCRIPTION));
	}

	@Test
	public void find1() {
		TestTransaction.start();
		List<EntityDependentKey> keys = new ArrayList<>();
		keys.add(new EntityDependentKey(TEST_PROJECT_ID_1, TEST_HAZARD_ID_1, TEST_SUBHAZARD_ID_1));
		keys.add(new EntityDependentKey(TEST_PROJECT_ID_1, TEST_HAZARD_ID_1, TEST_SUBHAZARD_ID_2));
		List<SubHazard> result = subHazardDAO.findSubHazardsByIds(keys, 0, 0);
		checkFind1(result);
		TestTransaction.end();
	}

	@Test
	public void find2() {
		TestTransaction.start();
		List<EntityDependentKey> keys = new ArrayList<>();
		keys.add(new EntityDependentKey(TEST_PROJECT_ID_1, TEST_HAZARD_ID_1, TEST_SUBHAZARD_ID_2));
		keys.add(new EntityDependentKey(TEST_PROJECT_ID_1, TEST_HAZARD_ID_1, TEST_SUBHAZARD_ID_3));
		keys.add(new EntityDependentKey(TEST_PROJECT_ID_1, TEST_HAZARD_ID_2, TEST_SUBHAZARD_ID_1));
		List<SubHazard> result = subHazardDAO.findSubHazardsByIds(keys, 0, 0);
		checkFind2(result);
		TestTransaction.end();
	}
	
	private void checkFind1(List<SubHazard> result) {
		assertEquals(2, result.size());
		checkGeneral(result);
		List<EntityDependentKey> keys = getKeys(result);

		assertTrue(ASSERT_CONTAINS_MESSAGE,
				keys.contains(new EntityDependentKey(TEST_PROJECT_ID_1, TEST_HAZARD_ID_1, TEST_SUBHAZARD_ID_1)));
		assertTrue(ASSERT_CONTAINS_MESSAGE,
				keys.contains(new EntityDependentKey(TEST_PROJECT_ID_1, TEST_HAZARD_ID_1, TEST_SUBHAZARD_ID_2)));
	}

	private void checkFind2(List<SubHazard> result) {
		assertEquals(3, result.size());
		checkGeneral(result);
		List<EntityDependentKey> keys = getKeys(result);
		assertTrue(ASSERT_CONTAINS_MESSAGE,
				keys.contains(new EntityDependentKey(TEST_PROJECT_ID_1, TEST_HAZARD_ID_1, TEST_SUBHAZARD_ID_2)));
		assertTrue(ASSERT_CONTAINS_MESSAGE,
				keys.contains(new EntityDependentKey(TEST_PROJECT_ID_1, TEST_HAZARD_ID_1, TEST_SUBHAZARD_ID_3)));
		assertTrue(ASSERT_CONTAINS_MESSAGE,
				keys.contains(new EntityDependentKey(TEST_PROJECT_ID_1, TEST_HAZARD_ID_2, TEST_SUBHAZARD_ID_1)));
	}
	
	private void checkGeneral(List<SubHazard> result) {
		for (SubHazard subHazard: result) {
			assertEquals(TEST_SUBHAZARD_NAME_1, subHazard.getName());
			assertEquals(TEST_DESCRIPTION, subHazard.getDescription());
			subHazardHelper.checkXStamppAttributes(subHazard);
		}
	}
	
	private static List<EntityDependentKey> getKeys(List<SubHazard> subHazards) {
		List<EntityDependentKey> keys = new ArrayList<>();
		for (SubHazard subHazard : subHazards) {
			keys.add(subHazard.getId());
		}
		return keys;
	}

}
