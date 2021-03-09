package de.xstampp.service.project.service.dao.test.hazard;

import static de.xstampp.service.project.service.dao.test.hazard.HazardEntityTestConstants.TEST_HAZARD_ID_1;
import static de.xstampp.service.project.service.dao.test.hazard.HazardEntityTestConstants.TEST_HAZARD_ID_2;
import static de.xstampp.service.project.service.dao.test.hazard.HazardEntityTestConstants.TEST_NON_EXISTENT_HAZARD_ID;
import static de.xstampp.service.project.service.dao.test.hazard.SubHazardEntityTestConstants.TEST_SUBHAZARD_DESCRIPTION_1;
import static de.xstampp.service.project.service.dao.test.hazard.SubHazardEntityTestConstants.TEST_SUBHAZARD_DESCRIPTION_2;
import static de.xstampp.service.project.service.dao.test.hazard.SubHazardEntityTestConstants.TEST_SUBHAZARD_ID_1;
import static de.xstampp.service.project.service.dao.test.hazard.SubHazardEntityTestConstants.TEST_SUBHAZARD_ID_2;
import static de.xstampp.service.project.service.dao.test.hazard.SubHazardEntityTestConstants.TEST_SUBHAZARD_ID_3;
import static de.xstampp.service.project.service.dao.test.hazard.SubHazardEntityTestConstants.TEST_SUBHAZARD_ID_4;
import static de.xstampp.service.project.service.dao.test.hazard.SubHazardEntityTestConstants.TEST_SUBHAZARD_ID_5;
import static de.xstampp.service.project.service.dao.test.hazard.SubHazardEntityTestConstants.TEST_SUBHAZARD_ID_6;
import static de.xstampp.service.project.service.dao.test.hazard.SubHazardEntityTestConstants.TEST_SUBHAZARD_ID_7;
import static de.xstampp.service.project.service.dao.test.hazard.SubHazardEntityTestConstants.TEST_SUBHAZARD_NAME_1;
import static de.xstampp.service.project.service.dao.test.hazard.SubHazardEntityTestConstants.TEST_SUBHAZARD_NAME_2;
import static de.xstampp.service.project.service.dao.test.hazard.SubHazardEntityTestConstants.TEST_SUBHAZARD_NAME_3;
import static de.xstampp.service.project.service.dao.test.hazard.SubHazardEntityTestConstants.TEST_SUBHAZARD_NAME_4;
import static de.xstampp.service.project.service.dao.test.hazard.SubHazardEntityTestConstants.TEST_SUBHAZARD_NAME_5;
import static de.xstampp.service.project.service.dao.test.hazard.SubHazardEntityTestConstants.TEST_SUBHAZARD_NAME_6;
import static de.xstampp.service.project.service.dao.test.hazard.SubHazardEntityTestConstants.TEST_SUBHAZARD_NAME_7;
import static de.xstampp.service.project.service.dao.test.project.ProjectEntityTestConstants.TEST_NON_EXISTENT_PROJECT_ID;
import static de.xstampp.service.project.service.dao.test.project.ProjectEntityTestConstants.TEST_PROJECT_ID_1;
import static de.xstampp.service.project.service.dao.test.project.ProjectEntityTestConstants.TEST_PROJECT_ID_2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.transaction.TestTransaction;

import de.xstampp.common.errors.GenericXSTAMPPException;
import de.xstampp.common.service.ConfigurationService;
import de.xstampp.service.project.configuration.PersistenceHibernateConfiguration;
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
import de.xstampp.service.project.service.dao.iface.SortOrder;

/**
 *	Temporarily ignore tests, because the automated pipeline currently does not support database access.
 */
@Ignore
@SpringBootTest(classes = {ProjectServiceTestDatabaseHibernateCleaner.class, PersistenceHibernateConfiguration.class, ConfigurationService.class, SubHazardHibernateDAO.class, HazardHibernateDAO.class, ProjectHibernateDAO.class})
public class SubHazardDAOTest_SeveralEntities extends AbstractDAOTest {
	
	private static final int AMOUNT_OF_ENTITIES = 7;
	
	private static final String ASSERT_CONTAINS = "entity with the given id was expected.";
	private static final String ASSERT_ILLEGAL_ARGUMENT_EXCEPTION = "An IllegalArgumentException was expected.";
	
	private static final String ERROR_MSG_NO_SORT_ORDER = "At least one ordering is required";
	
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

		hazardDAO.makePersistent(subHazardHelper.getHazardFor(TEST_PROJECT_ID_2, TEST_HAZARD_ID_1));
		hazardDAO.makePersistent(subHazardHelper.getHazardFor(TEST_PROJECT_ID_1, TEST_HAZARD_ID_2));
		hazardDAO.makePersistent(subHazardHelper.getHazardFor(TEST_PROJECT_ID_1, TEST_HAZARD_ID_1));

		SubHazard subHazard = subHazardHelper.getSubHazardFor(TEST_PROJECT_ID_2, TEST_HAZARD_ID_1, TEST_SUBHAZARD_ID_1,
				TEST_SUBHAZARD_NAME_1, TEST_SUBHAZARD_DESCRIPTION_1);
		subHazardDAO.makePersistent(subHazard);

		subHazard = subHazardHelper.getSubHazardFor(TEST_PROJECT_ID_1, TEST_HAZARD_ID_2, TEST_SUBHAZARD_ID_1,
				TEST_SUBHAZARD_NAME_1, TEST_SUBHAZARD_DESCRIPTION_1);
		subHazardDAO.makePersistent(subHazard);

		initSubHazardsToSort();
	}
	
	private void initSubHazardsToSort() {
		saveSubHazard(TEST_SUBHAZARD_ID_1, TEST_SUBHAZARD_NAME_7, TEST_SUBHAZARD_DESCRIPTION_1);
		saveSubHazard(TEST_SUBHAZARD_ID_2, TEST_SUBHAZARD_NAME_6, TEST_SUBHAZARD_DESCRIPTION_2);
		saveSubHazard(TEST_SUBHAZARD_ID_3, TEST_SUBHAZARD_NAME_5, TEST_SUBHAZARD_DESCRIPTION_1);
		saveSubHazard(TEST_SUBHAZARD_ID_4, TEST_SUBHAZARD_NAME_4, TEST_SUBHAZARD_DESCRIPTION_2);
		saveSubHazard(TEST_SUBHAZARD_ID_5, TEST_SUBHAZARD_NAME_3, TEST_SUBHAZARD_DESCRIPTION_1);
		saveSubHazard(TEST_SUBHAZARD_ID_6, TEST_SUBHAZARD_NAME_2, TEST_SUBHAZARD_DESCRIPTION_2);
		saveSubHazard(TEST_SUBHAZARD_ID_7, TEST_SUBHAZARD_NAME_1, TEST_SUBHAZARD_DESCRIPTION_1);
	}
	
	private void saveSubHazard(int id, String name, String description) {
		SubHazard subHazard = getFor(id, name, description);
		subHazardDAO.makePersistent(subHazard);
	}
	
	private SubHazard getFor(int id, String name, String description) {
		return subHazardHelper.getSubHazardFor(TEST_PROJECT_ID_1, TEST_HAZARD_ID_1, id, name, description);
	}

	@Test
	public void findSortedAscById() {
		TestTransaction.start();
		List<SubHazard> subHazards = getSortedBy(SubHazard.EntityAttributes.ID, SortOrder.ASC);
		checkSortedAscById(subHazards);
		TestTransaction.end();
	}

	@Test
	public void findSortedDescById() {
		TestTransaction.start();
		List<SubHazard> subHazards = getSortedBy(SubHazard.EntityAttributes.ID, SortOrder.DESC);
		checkSortedDescById(subHazards);
		TestTransaction.end();
	}
	
	@Test
	public void findSortedAscByName() {
		TestTransaction.start();
		List<SubHazard> subHazards = getSortedBy(SubHazard.EntityAttributes.NAME, SortOrder.ASC);
		checkSortedDescById(subHazards);
		TestTransaction.end();
	}
	
	@Test
	public void findSortedDescByName() {
		TestTransaction.start();
		List<SubHazard> subHazards = getSortedBy(SubHazard.EntityAttributes.NAME, SortOrder.DESC);
		checkSortedAscById(subHazards);
		TestTransaction.end();
	}
	
	@Test
	public void findSortedAscByDescription() {
		TestTransaction.start();
		List<SubHazard> subHazards = getSortedBy(SubHazard.EntityAttributes.DESCRIPTION, SortOrder.ASC);
		checkSortedAscByDescription(subHazards);
		TestTransaction.end();
	}
	
	@Test
	public void findSortedDescByDescription() {
		TestTransaction.start();
		List<SubHazard> subHazards = getSortedBy(SubHazard.EntityAttributes.DESCRIPTION, SortOrder.DESC);
		checkSortedDescByDescription(subHazards);
		TestTransaction.end();
	}

	/**
	 * Resulting sort order:
	 * id	description
	 * 1	1
	 * 3	1
	 * 5	1
	 * 7	1
	 * 2	2
	 * 4	2
	 * 6	2
	 */
	@Test
	public void findSortedAscByIdAndThenAscByDescription() {
		TestTransaction.start();
		LinkedHashMap<String, SortOrder> order = new LinkedHashMap<>();
		order.put(SubHazard.EntityAttributes.DESCRIPTION, SortOrder.ASC);
		order.put(SubHazard.EntityAttributes.ID, SortOrder.ASC);
		List<SubHazard> subHazards = getSortedBy(order);
		checkSortedAscByIdAndThenAscByDescription(subHazards);
		TestTransaction.end();
	}
	
	/**
	 * Resulting sort order:
	 * id	name	description
	 * 7	1		1
	 * 5	3		1
	 * 3	5		1
	 * 1	7		1
	 * 6	2		2
	 * 4	4		2
	 * 2	6		2
	 */
	@Test
	public void findSortedAscByIdAndThenAscByNameAndThenAscByDescription() {
		TestTransaction.start();
		LinkedHashMap<String, SortOrder> order = new LinkedHashMap<>();
		order.put(SubHazard.EntityAttributes.DESCRIPTION, SortOrder.ASC);
		order.put(SubHazard.EntityAttributes.NAME, SortOrder.ASC);
		order.put(SubHazard.EntityAttributes.ID, SortOrder.ASC);
		List<SubHazard> subHazards = getSortedBy(order);
		checkSortedAscByIdAscByNameAndThenByDescription(subHazards);
		TestTransaction.end();
	}
	
	@Test
	public void findSortedAscByNameNoSortOrder() {
		TestTransaction.start();
		List<SubHazard> subHazards = getSortedBy(SubHazard.EntityAttributes.NAME, null);
		checkSortedDescById(subHazards);
		TestTransaction.end();
	}
	
	@Test
	public void findSortParameterNull() {
		TestTransaction.start();
		boolean exceptionThrown = false;
		try {
			subHazardDAO.findFromTo(0, AMOUNT_OF_ENTITIES, null, TEST_PROJECT_ID_1, TEST_SUBHAZARD_ID_1);
		} catch (GenericXSTAMPPException ex) {
			exceptionThrown = true;
			assertEquals(ERROR_MSG_NO_SORT_ORDER, ex.getLocalizedMessage());
		} 
		assertTrue(ASSERT_ILLEGAL_ARGUMENT_EXCEPTION, exceptionThrown);
		TestTransaction.end();
	}
	
	@Test
	public void findSortParameterEmpty() {
		TestTransaction.start();
		boolean exceptionThrown = false;
		try {
			subHazardDAO.findFromTo(0, AMOUNT_OF_ENTITIES, new LinkedHashMap<String, SortOrder>(), TEST_PROJECT_ID_1, TEST_HAZARD_ID_1);
		} catch (GenericXSTAMPPException ex) {
			exceptionThrown = true;
			assertEquals(ERROR_MSG_NO_SORT_ORDER, ex.getLocalizedMessage());
		}
		assertTrue(ASSERT_ILLEGAL_ARGUMENT_EXCEPTION,exceptionThrown);
		TestTransaction.end();
	}
	
	@Test
	public void findOneSortedAscById() {
		TestTransaction.start();
		LinkedHashMap<String, SortOrder> order = new LinkedHashMap<>();
		order.put(SubHazard.EntityAttributes.ID, SortOrder.ASC);
		List<SubHazard> subHazards = subHazardDAO.findFromTo(0, 1, order, TEST_PROJECT_ID_1, TEST_HAZARD_ID_2);
		assertEquals(1, subHazards.size());
		SubHazard subHazard = subHazards.get(0);
		checkOne(subHazard);
		TestTransaction.end();
	}
	
	@Test
	public void findFromSecondToFourthSortedAscById() {
		TestTransaction.start();
		LinkedHashMap<String, SortOrder> order = new LinkedHashMap<>();
		order.put(SubHazard.EntityAttributes.ID, SortOrder.ASC);
		List<SubHazard> subHazards = subHazardDAO.findFromTo(1, 3, order, TEST_PROJECT_ID_1, TEST_HAZARD_ID_1);
		checkSecondToFourth(subHazards);
		TestTransaction.end();
	}
	
	private void checkSecondToFourth(List<SubHazard> result) {
		checkParents(result);
		assertEquals(TEST_SUBHAZARD_ID_2, result.get(0).getId().getId());
		assertEquals(TEST_SUBHAZARD_ID_3, result.get(1).getId().getId());
		assertEquals(TEST_SUBHAZARD_ID_4, result.get(2).getId().getId());
	}
	
	@Test
	public void findAll() {
		TestTransaction.start();
		List<SubHazard> subHazards = subHazardDAO.findAll(TEST_PROJECT_ID_1, TEST_HAZARD_ID_1);
		checkAll(subHazards);
		TestTransaction.end();
	}
	
	@Test
	public void findOne() {
		TestTransaction.start();
		List<SubHazard> subHazards = subHazardDAO.findAll(TEST_PROJECT_ID_1, TEST_HAZARD_ID_2);
		assertEquals(1, subHazards.size());
		SubHazard subHazard = subHazards.get(0);
		checkOne(subHazard);
		TestTransaction.end();
	}

	@Test
	public void findNone() {
		TestTransaction.start();
		List<SubHazard> result = subHazardDAO.findAll(TEST_PROJECT_ID_2, TEST_HAZARD_ID_2);
		assertEquals(0, result.size());
		TestTransaction.end();
	}
	
	@Test
	public void findNonExistent() {
		TestTransaction.start();
		List<SubHazard> result = subHazardDAO.findAll(TEST_NON_EXISTENT_PROJECT_ID, TEST_NON_EXISTENT_HAZARD_ID);
		assertEquals(0, result.size());
		TestTransaction.end();
	}
	
	@Test
	public void countAll() {
		TestTransaction.start();
		long result = subHazardDAO.count(TEST_PROJECT_ID_1, TEST_HAZARD_ID_1);
		assertEquals(AMOUNT_OF_ENTITIES, result);
		TestTransaction.end();
	}
	
	@Test
	public void countOne() {
		TestTransaction.start();
		long result = subHazardDAO.count(TEST_PROJECT_ID_1, TEST_HAZARD_ID_2);
		assertEquals(1, result);
		TestTransaction.end();
	}
	
	@Test
	public void countNone() {
		TestTransaction.start();
		long result = subHazardDAO.count(TEST_PROJECT_ID_2, TEST_HAZARD_ID_2);
		assertEquals(0, result);
		TestTransaction.end();
	}
	
	@Test
	public void countNonExistent() {
		TestTransaction.start();
		long result = subHazardDAO.count(TEST_NON_EXISTENT_PROJECT_ID, TEST_NON_EXISTENT_HAZARD_ID);
		assertEquals(0, result);
		TestTransaction.end();
	}
	
	private void checkAll(List<SubHazard> result) {
		checkGeneral(result);
		List<Integer> relevantResults = new ArrayList<>();
		for (SubHazard subHazard: result) {
			relevantResults.add(subHazard.getId().getId());
		}
		
		assertTrue(ASSERT_CONTAINS, relevantResults.contains(TEST_SUBHAZARD_ID_1));
		assertTrue(ASSERT_CONTAINS, relevantResults.contains(TEST_SUBHAZARD_ID_2));
		assertTrue(ASSERT_CONTAINS, relevantResults.contains(TEST_SUBHAZARD_ID_3));
		assertTrue(ASSERT_CONTAINS, relevantResults.contains(TEST_SUBHAZARD_ID_4));
		assertTrue(ASSERT_CONTAINS, relevantResults.contains(TEST_SUBHAZARD_ID_5));
		assertTrue(ASSERT_CONTAINS, relevantResults.contains(TEST_SUBHAZARD_ID_6));
		assertTrue(ASSERT_CONTAINS, relevantResults.contains(TEST_SUBHAZARD_ID_7));
	}
	
	private void checkOne(SubHazard subHazard) {
		assertEquals(TEST_PROJECT_ID_1, subHazard.getId().getProjectId());
		assertEquals(TEST_HAZARD_ID_2, subHazard.getId().getParentId());
		assertEquals(TEST_SUBHAZARD_ID_1, subHazard.getId().getId());
		assertEquals(TEST_SUBHAZARD_NAME_1, subHazard.getName());
		assertEquals(TEST_SUBHAZARD_DESCRIPTION_1, subHazard.getDescription());
		subHazardHelper.checkXStamppAttributes(subHazard);
	}
	
	private void checkSortedDescById(List<SubHazard> result) {
		checkGeneral(result);
		
		assertEquals(TEST_SUBHAZARD_ID_7, result.get(0).getId().getId());
		assertEquals(TEST_SUBHAZARD_ID_6, result.get(1).getId().getId());
		assertEquals(TEST_SUBHAZARD_ID_5, result.get(2).getId().getId());
		assertEquals(TEST_SUBHAZARD_ID_4, result.get(3).getId().getId());
		assertEquals(TEST_SUBHAZARD_ID_3, result.get(4).getId().getId());
		assertEquals(TEST_SUBHAZARD_ID_2, result.get(5).getId().getId());
		assertEquals(TEST_SUBHAZARD_ID_1, result.get(6).getId().getId());
		
	}
	
	private void checkSortedAscById(List<SubHazard> result) {
		
		checkGeneral(result);
		assertEquals(TEST_SUBHAZARD_ID_1, result.get(0).getId().getId());
		assertEquals(TEST_SUBHAZARD_ID_2, result.get(1).getId().getId());
		assertEquals(TEST_SUBHAZARD_ID_3, result.get(2).getId().getId());
		assertEquals(TEST_SUBHAZARD_ID_4, result.get(3).getId().getId());
		assertEquals(TEST_SUBHAZARD_ID_5, result.get(4).getId().getId());
		assertEquals(TEST_SUBHAZARD_ID_6, result.get(5).getId().getId());
		assertEquals(TEST_SUBHAZARD_ID_7, result.get(6).getId().getId());
	}
	
	private void checkSortedDescByDescription(List<SubHazard> result) {
		checkGeneral(result);
		
		assertEquals(TEST_SUBHAZARD_DESCRIPTION_2, result.get(0).getDescription());
		assertEquals(TEST_SUBHAZARD_DESCRIPTION_2, result.get(1).getDescription());
		assertEquals(TEST_SUBHAZARD_DESCRIPTION_2, result.get(2).getDescription());
		assertEquals(TEST_SUBHAZARD_DESCRIPTION_1, result.get(3).getDescription());
		assertEquals(TEST_SUBHAZARD_DESCRIPTION_1, result.get(4).getDescription());
		assertEquals(TEST_SUBHAZARD_DESCRIPTION_1, result.get(5).getDescription());
		assertEquals(TEST_SUBHAZARD_DESCRIPTION_1, result.get(6).getDescription());
	}

	private void checkSortedAscByDescription(List<SubHazard> result) {
		checkGeneral(result);
		
		assertEquals(TEST_SUBHAZARD_DESCRIPTION_1, result.get(0).getDescription());
		assertEquals(TEST_SUBHAZARD_DESCRIPTION_1, result.get(1).getDescription());
		assertEquals(TEST_SUBHAZARD_DESCRIPTION_1, result.get(2).getDescription());
		assertEquals(TEST_SUBHAZARD_DESCRIPTION_1, result.get(3).getDescription());
		assertEquals(TEST_SUBHAZARD_DESCRIPTION_2, result.get(4).getDescription());
		assertEquals(TEST_SUBHAZARD_DESCRIPTION_2, result.get(5).getDescription());
		assertEquals(TEST_SUBHAZARD_DESCRIPTION_2, result.get(6).getDescription());
	}
	
	private void checkSortedAscByIdAndThenAscByDescription(List<SubHazard> result) {
		checkGeneral(result);
		
		assertEquals(TEST_SUBHAZARD_ID_1, result.get(0).getId().getId());
		assertEquals(TEST_SUBHAZARD_ID_3, result.get(1).getId().getId());
		assertEquals(TEST_SUBHAZARD_ID_5, result.get(2).getId().getId());
		assertEquals(TEST_SUBHAZARD_ID_7, result.get(3).getId().getId());
		assertEquals(TEST_SUBHAZARD_ID_2, result.get(4).getId().getId());
		assertEquals(TEST_SUBHAZARD_ID_4, result.get(5).getId().getId());
		assertEquals(TEST_SUBHAZARD_ID_6, result.get(6).getId().getId());
		
	}
	
	private void checkSortedAscByIdAscByNameAndThenByDescription(List<SubHazard> result) {
		checkGeneral(result);
		
		assertEquals(TEST_SUBHAZARD_ID_7, result.get(0).getId().getId());
		assertEquals(TEST_SUBHAZARD_ID_5, result.get(1).getId().getId());
		assertEquals(TEST_SUBHAZARD_ID_3, result.get(2).getId().getId());
		assertEquals(TEST_SUBHAZARD_ID_1, result.get(3).getId().getId());
		assertEquals(TEST_SUBHAZARD_ID_6, result.get(4).getId().getId());
		assertEquals(TEST_SUBHAZARD_ID_4, result.get(5).getId().getId());
		assertEquals(TEST_SUBHAZARD_ID_2, result.get(6).getId().getId());
	}
	
	private void checkGeneral(List<SubHazard> result) {
		assertEquals(AMOUNT_OF_ENTITIES, result.size());

		checkParents(result);
	}
	
	private void checkParents(List<SubHazard> result) {
		for (SubHazard subHazard : result) {
			assertEquals(TEST_PROJECT_ID_1, subHazard.getId().getProjectId());
			assertEquals(TEST_HAZARD_ID_1, subHazard.getId().getParentId());
		}
	}
	
	private List<SubHazard> getSortedBy(String property, SortOrder sortOrder) {
		LinkedHashMap<String, SortOrder> order = new LinkedHashMap<>();
		order.put(property, sortOrder);
		return getSortedBy(order);
	}
	
	private List<SubHazard> getSortedBy(LinkedHashMap<String, SortOrder> order) {
		List<SubHazard> result = subHazardDAO.findFromTo(0, AMOUNT_OF_ENTITIES, order, TEST_PROJECT_ID_1, TEST_HAZARD_ID_1);
		return result;
	}
	
}
