package de.xstampp.service.project.service.dao.test.loss;

import static de.xstampp.service.project.service.dao.test.loss.LossEntityTestConstants.TEST_LOSS_ID_1;
import static de.xstampp.service.project.service.dao.test.loss.LossEntityTestConstants.TEST_LOSS_ID_2;
import static de.xstampp.service.project.service.dao.test.loss.LossEntityTestConstants.TEST_LOSS_ID_3;
import static de.xstampp.service.project.service.dao.test.loss.LossEntityTestConstants.TEST_LOSS_NAME_1;
import static de.xstampp.service.project.service.dao.test.loss.LossEntityTestConstants.TEST_LOSS_NAME_2;
import static de.xstampp.service.project.service.dao.test.loss.LossEntityTestConstants.TEST_LOSS_NAME_3;
import static de.xstampp.service.project.service.dao.test.loss.LossEntityTestConstants.TEST_NON_EXISTENT_LOSS_ID;
import static de.xstampp.service.project.service.dao.test.project.ProjectEntityTestConstants.TEST_DESCRIPTION;
import static de.xstampp.service.project.service.dao.test.project.ProjectEntityTestConstants.TEST_NON_EXISTENT_PROJECT_ID;
import static de.xstampp.service.project.service.dao.test.project.ProjectEntityTestConstants.TEST_PROJECT_ID_1;
import static de.xstampp.service.project.service.dao.test.project.ProjectEntityTestConstants.TEST_PROJECT_ID_2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.transaction.TestTransaction;

import de.xstampp.common.service.ConfigurationService;
import de.xstampp.service.project.configuration.PersistenceHibernateConfiguration;
import de.xstampp.service.project.data.XStamppDependentEntity;
import de.xstampp.service.project.data.entity.Loss;
import de.xstampp.service.project.data.entity.Project;
import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.service.dao.AbstractDAOTest;
import de.xstampp.service.project.service.dao.LossHibernateDAO;
import de.xstampp.service.project.service.dao.ProjectHibernateDAO;
import de.xstampp.service.project.service.dao.ProjectServiceTestDatabaseHibernateCleaner;
import de.xstampp.service.project.service.dao.XStamppEntityTestHelper;
import de.xstampp.service.project.service.dao.iface.ILossDAO;
import de.xstampp.service.project.service.dao.iface.IProjectDAO;
import de.xstampp.service.project.service.dao.iface.SortOrder;

/**
 *	Temporarily ignore tests, because the automated pipeline currently does not support database access.
 */
@Ignore
@SpringBootTest(classes = {ProjectServiceTestDatabaseHibernateCleaner.class, PersistenceHibernateConfiguration.class, ConfigurationService.class, LossHibernateDAO.class, ProjectHibernateDAO.class})
public class LossDAOTest_SeveralEntities extends AbstractDAOTest {
	
	private static final int AMOUNT_OF_ENTITIES = 3;
	
	@Autowired
	private IProjectDAO projectDAO;
	
	@Autowired
	private ILossDAO lossDAO;
	
	private XStamppEntityTestHelper xstamppHelper;

	@Override
	protected void initTestCase() {
		
		xstamppHelper = new XStamppEntityTestHelper(now(), afterFewMinutes());
		
		Project project = new Project(TEST_PROJECT_ID_1);
		projectDAO.makePersistent(project);
		
		project = new Project(TEST_PROJECT_ID_2);
		projectDAO.makePersistent(project);
		
		Loss loss = getLossFor(TEST_PROJECT_ID_2, TEST_LOSS_ID_1, TEST_LOSS_NAME_1);
		lossDAO.makePersistent(loss);
		
		loss = getLossFor(TEST_PROJECT_ID_1, TEST_LOSS_ID_1, TEST_LOSS_NAME_3);
		lossDAO.makePersistent(loss);
		
		loss = getLossFor(TEST_PROJECT_ID_1, TEST_LOSS_ID_2, TEST_LOSS_NAME_2);
		lossDAO.makePersistent(loss);
		
		loss = getLossFor(TEST_PROJECT_ID_1,TEST_LOSS_ID_3, TEST_LOSS_NAME_1);
		lossDAO.makePersistent(loss);
	}
	
	private Loss getLossFor(UUID projectId, int lossId, String name) {
		ProjectDependentKey id = new ProjectDependentKey(projectId, lossId);
		Loss result = new Loss(id);
		result.setName(name);
		result.setDescription(TEST_DESCRIPTION);
		xstamppHelper.init(result);
		return result;
	}
	
	@Test
	public void findSortedAscById() {
		TestTransaction.start();
		List<Loss> losses = getSortedById(SortOrder.ASC);
		checkSortedAscById(losses);
		TestTransaction.end();
	}
	
	@Test
	public void findSortedDescById() {
		TestTransaction.start();
		List<Loss> losses = getSortedById(SortOrder.DESC);
		checkSortedDescById(losses);
		TestTransaction.end();
	}
	
	@Test
	public void findSortedAscByName() {
		TestTransaction.start();
		List<Loss> result = getSortedByName(SortOrder.ASC);
		checkSortedDescById(result);
		TestTransaction.end();
	}
	
	@Test
	public void findSortedDescByName() {
		TestTransaction.start();
		List<Loss> result = getSortedByName(SortOrder.DESC);
		checkSortedAscById(result);
		TestTransaction.end();
	}
	
	@Test
	public void findFirstAscById() {
		TestTransaction.start();
		List<Loss> result = getSortedById(0, 1, SortOrder.ASC);
		assertEquals(1, result.size());
		checkSingle(result.get(0), TEST_LOSS_ID_1);
		TestTransaction.end();
	}

	@Test
	public void findFirstDescById() {
		TestTransaction.start();
		List<Loss> result = getSortedById(0, 1, SortOrder.DESC);
		assertEquals(1, result.size());
		checkSingle(result.get(0), TEST_LOSS_ID_3);
		TestTransaction.end();
	}

	@Test
	public void findSecondDescById() {
		TestTransaction.start();
		List<Loss> result = getSortedById(1, 1, SortOrder.DESC);
		assertEquals(1, result.size());
		checkSingle(result.get(0), TEST_LOSS_ID_2);
		TestTransaction.end();
	}

	@Test
	public void findThirdDescById() {
		TestTransaction.start();
		List<Loss> result = getSortedById(2, 1, SortOrder.DESC);
		assertEquals(1, result.size());
		checkSingle(result.get(0), TEST_LOSS_ID_1);
		TestTransaction.end();
	}

	@Test
	public void findFromSecondToThirdDescById() {
		TestTransaction.start();
		List<Loss> result = getSortedById(1, 2, SortOrder.DESC);
		assertEquals(2, result.size());
		checkSingle(result.get(0), TEST_LOSS_ID_2);
		checkSingle(result.get(1), TEST_LOSS_ID_1);
		TestTransaction.end();
	}

	@Test
	public void findFromSecondToThirdDescByName() {
		TestTransaction.start();
		List<Loss> result = getSortedByName(1, 2, SortOrder.DESC);
		assertEquals(2, result.size());
		checkSingle(result.get(0), TEST_LOSS_ID_2);
		checkSingle(result.get(1), TEST_LOSS_ID_3);
		TestTransaction.end();
	}

	@Test
	public void findSeveralByIds() {
		TestTransaction.start();
		List<Integer> lossIds = new ArrayList<Integer>();
		lossIds.add(TEST_LOSS_ID_3);
		lossIds.add(TEST_LOSS_ID_2);
		List<Loss> result = lossDAO.findLossesByIds(TEST_PROJECT_ID_1, lossIds);
		assertEquals(2, result.size());
		assertEquals(TEST_PROJECT_ID_1, result.get(0).getId().getProjectId());
		assertEquals(TEST_PROJECT_ID_1, result.get(1).getId().getProjectId());
		List<Integer> relevantResults = new ArrayList<>();
		for (int i = 0; i < result.size(); i++) {
			relevantResults.add(result.get(i).getId().getId());
		}
		assertTrue("expected loss with the id: " + TEST_LOSS_ID_2, relevantResults.contains(TEST_LOSS_ID_2));
		assertTrue("expected loss with the id: " + TEST_LOSS_ID_3, relevantResults.contains(TEST_LOSS_ID_3));
		TestTransaction.end();
	}

	@Test
	public void findByIdsWrongProject() {
		TestTransaction.start();
		List<Integer> lossIds = new ArrayList<>();
		lossIds.add(TEST_LOSS_ID_3);
		lossIds.add(TEST_LOSS_ID_2);
		List<Loss> result = lossDAO.findLossesByIds(TEST_PROJECT_ID_2, lossIds);
		assertEquals(0, result.size());
		TestTransaction.end();
	}
	
	@Test
	public void findNoneByIds() {
		TestTransaction.start();
		List<Integer> lossIds = new ArrayList<>();
		lossIds.add(TEST_NON_EXISTENT_LOSS_ID);
		List<Loss> result = lossDAO.findLossesByIds(TEST_PROJECT_ID_1, lossIds);
		assertEquals(0, result.size());
		TestTransaction.end();
	}
	
	@Test
	public void findMixedByIds() {
		TestTransaction.start();
		List<Integer> lossIds = new ArrayList<>();
		lossIds.add(TEST_NON_EXISTENT_LOSS_ID);
		lossIds.add(TEST_LOSS_ID_2);
		List<Loss> result = lossDAO.findLossesByIds(TEST_PROJECT_ID_1, lossIds);
		assertEquals(1, result.size());
		checkSingle(result.get(0), TEST_LOSS_ID_2);
		TestTransaction.end();
	}
	
	@Test
	public void findByIdsNonExistentProject() {
		TestTransaction.start();
		List<Integer> lossIds = new ArrayList<>();
		lossIds.add(TEST_LOSS_ID_3);
		lossIds.add(TEST_LOSS_ID_2);
		List<Loss> result = lossDAO.findLossesByIds(TEST_NON_EXISTENT_PROJECT_ID, lossIds);
		assertEquals(0, result.size());
		TestTransaction.end();
	}
	
	@Test
	public void findAll() {
		TestTransaction.start();
		List<Loss> losses = lossDAO.findAll(TEST_PROJECT_ID_1);
		checkAll(losses);
		TestTransaction.end();
	}
	
	@Test
	public void countAll() {
		TestTransaction.start();
		long count = lossDAO.count(TEST_PROJECT_ID_1);
		assertEquals(AMOUNT_OF_ENTITIES, count);
		TestTransaction.end();
	}
	
	private void checkSingle(Loss loss, int lossId) {
		assertEquals(TEST_PROJECT_ID_1, loss.getId().getProjectId());
		assertEquals(lossId, loss.getId().getId());
	}
	
	private List<Loss> getSortedByName(SortOrder sortOrder) {
		return getSortedByName(0, AMOUNT_OF_ENTITIES, sortOrder);
	}
	
	private List<Loss> getSortedByName(int from, int amount, SortOrder sortOrder) {
		LinkedHashMap<String, SortOrder> order = new LinkedHashMap<String, SortOrder>();
		order.put(Loss.EntityAttributes.NAME, sortOrder);
		return lossDAO.findFromTo(from, amount, order, TEST_PROJECT_ID_1);
	}

	private void checkAll(List<Loss> result) {
		assertEquals(AMOUNT_OF_ENTITIES, result.size());
		
		for (int i = 0; i<result.size();i++) {
			Loss loss = result.get(i);
			assertEquals(TEST_PROJECT_ID_1, loss.getId().getProjectId());
		}
		
		Map<Integer, String> relevantResults = new HashMap<>();
		for (int i =0; i<result.size(); i++) {
			Loss loss = result.get(i);
			relevantResults.put(loss.getId().getId(), loss.getName());
		}
		assertNotNull(relevantResults.get(TEST_LOSS_ID_1));
		assertEquals(TEST_LOSS_NAME_3, relevantResults.get(TEST_LOSS_ID_1));
		assertNotNull(relevantResults.get(TEST_LOSS_ID_2));
		assertEquals(TEST_LOSS_NAME_2, relevantResults.get(TEST_LOSS_ID_2));
		assertNotNull(relevantResults.get(TEST_LOSS_ID_3));
		assertEquals(TEST_LOSS_NAME_1, relevantResults.get(TEST_LOSS_ID_3));
	}

	private void checkSortedDescById(List<Loss> result) {
		assertEquals(AMOUNT_OF_ENTITIES, result.size());
		
		assertEquals(TEST_PROJECT_ID_1, result.get(0).getId().getProjectId());
		assertEquals(TEST_LOSS_ID_3, result.get(0).getId().getId());
		
		assertEquals(TEST_PROJECT_ID_1, result.get(1).getId().getProjectId());
		assertEquals(TEST_LOSS_ID_2, result.get(1).getId().getId());
		
		assertEquals(TEST_PROJECT_ID_1, result.get(2).getId().getProjectId());
		assertEquals(TEST_LOSS_ID_1, result.get(2).getId().getId());
	}

	private void checkSortedAscById(List<Loss> result) {
		assertEquals(AMOUNT_OF_ENTITIES, result.size());
		
		assertEquals(TEST_PROJECT_ID_1, result.get(0).getId().getProjectId());
		assertEquals(TEST_LOSS_ID_1, result.get(0).getId().getId());
		
		assertEquals(TEST_PROJECT_ID_1, result.get(1).getId().getProjectId());
		assertEquals(TEST_LOSS_ID_2, result.get(1).getId().getId());
		
		assertEquals(TEST_PROJECT_ID_1, result.get(2).getId().getProjectId());
		assertEquals(TEST_LOSS_ID_3, result.get(2).getId().getId());
	}
	
	private List<Loss> getSortedById(SortOrder sortOrder) {
		return getSortedById(0, AMOUNT_OF_ENTITIES, sortOrder);
	}
	
	private List<Loss> getSortedById(int from, int amount, SortOrder sortOrder) {
		LinkedHashMap<String, SortOrder> order = new LinkedHashMap<>();
		order.put(XStamppDependentEntity.DEPENDENT_KEY_ATTRIBUTE, sortOrder);
		return lossDAO.findFromTo(from, amount, order, TEST_PROJECT_ID_1);
	}		

}
