package de.xstampp.service.project.service.dao.test.project;

import static de.xstampp.service.project.service.dao.test.project.ProjectEntityTestConstants.TEST_PROJECT_ID_1;
import static de.xstampp.service.project.service.dao.test.project.ProjectEntityTestConstants.TEST_PROJECT_ID_2;
import static de.xstampp.service.project.service.dao.test.project.ProjectEntityTestConstants.TEST_PROJECT_ID_3;
import static de.xstampp.service.project.service.dao.test.project.ProjectEntityTestConstants.TEST_PROJECT_ID_4;
import static de.xstampp.service.project.service.dao.test.project.ProjectEntityTestConstants.TEST_PROJECT_ID_5;
import static de.xstampp.service.project.service.dao.test.project.ProjectEntityTestConstants.TEST_PROJECT_ID_6;
import static de.xstampp.service.project.service.dao.test.project.ProjectEntityTestConstants.TEST_PROJECT_ID_7;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.transaction.TestTransaction;

import de.xstampp.common.service.ConfigurationService;
import de.xstampp.service.project.configuration.PersistenceHibernateConfiguration;
import de.xstampp.service.project.data.entity.Project;
import de.xstampp.service.project.service.dao.AbstractDAOTest;
import de.xstampp.service.project.service.dao.ProjectServiceTestDatabaseHibernateCleaner;
import de.xstampp.service.project.service.dao.iface.SortOrder;

/**
 *	Temporarily ignore tests, because the automated pipeline currently does not support database access.
 *	Move these tests to the xstampp-service-auth.
 */
@Ignore
@SpringBootTest(classes = {ProjectServiceTestDatabaseHibernateCleaner.class, PersistenceHibernateConfiguration.class, ConfigurationService.class, ProjectTestHibernateDAO.class})
public class ProjectDAOTest_SeveralEntities extends AbstractDAOTest {
	
	private static final int AMOUNT_OF_ENTITIES = 7;
	
	@Autowired
	private IProjectTestDAO projectDAO;
	
	@Override
	protected void initTestCase() {
		projectDAO.makePersistent(new Project(TEST_PROJECT_ID_1));
		projectDAO.makePersistent(new Project(TEST_PROJECT_ID_2));
		projectDAO.makePersistent(new Project(TEST_PROJECT_ID_3));
		projectDAO.makePersistent(new Project(TEST_PROJECT_ID_4));
		projectDAO.makePersistent(new Project(TEST_PROJECT_ID_5));
		projectDAO.makePersistent(new Project(TEST_PROJECT_ID_6));
		projectDAO.makePersistent(new Project(TEST_PROJECT_ID_7));
	}
	
	@Test
	public void findSortedAscById() {
		TestTransaction.start();
		List<Project> result = getAscById(0, AMOUNT_OF_ENTITIES);
		checkSortedAscById(result);
		TestTransaction.end();
	}
	
	private List<Project> getAscById(int from, int amount) {
		LinkedHashMap<String, SortOrder> order = new LinkedHashMap<>();
		order.put(Project.EntityAttributes.ID, SortOrder.ASC);
		return projectDAO.findFromTo(from, amount, order);
	}
	
	@Test
	public void findSortedDescById() {
		TestTransaction.start();
		LinkedHashMap<String, SortOrder> order = new LinkedHashMap<>();
		order.put(Project.EntityAttributes.ID, SortOrder.DESC);
		List<Project> result = projectDAO.findFromTo(0, AMOUNT_OF_ENTITIES, order);
		checkSortedDescById(result);
		TestTransaction.end();
	}
	
	@Test
	public void findFourthToSixthAscById() {
		TestTransaction.start();
		List<Project> result = getAscById(3,3);
		assertEquals(3, result.size());
		assertEquals(TEST_PROJECT_ID_4, result.get(0).getId());
		assertEquals(TEST_PROJECT_ID_5, result.get(1).getId());
		assertEquals(TEST_PROJECT_ID_6, result.get(2).getId());
		TestTransaction.end();
	}
	
	@Test
	public void findOneProject() {
		TestTransaction.start();
		LinkedHashMap<String, SortOrder> order = new LinkedHashMap<>();
		order.put(Project.EntityAttributes.ID, SortOrder.ASC);
		List<Project> result = projectDAO.findFromTo(0, 1, order);
		assertEquals(1, result.size());
		TestTransaction.end();
	}
	
	@Test
	public void findTwoProjects() {
		TestTransaction.start();
		LinkedHashMap<String, SortOrder> order = new LinkedHashMap<>();
		order.put(Project.EntityAttributes.ID, SortOrder.ASC);
		List<Project> result = projectDAO.findFromTo(0, 2, order);
		assertEquals(2, result.size());
		TestTransaction.end();
	}
	
	@Test
	public void findThreeProjects() {
		TestTransaction.start();
		LinkedHashMap<String, SortOrder> order = new LinkedHashMap<>();
		order.put(Project.EntityAttributes.ID, SortOrder.ASC);
		List<Project> result = projectDAO.findFromTo(0, 3, order);
		assertEquals(3, result.size());
		TestTransaction.end();
	}
	
	@Test
	public void findAll() {
		TestTransaction.start();
		List<Project> result = projectDAO.findAll();
		checkAll(result);
		TestTransaction.end();
	}
	
	@Test
	public void countAll() {
		TestTransaction.start();
		long count = projectDAO.count();
		assertEquals(AMOUNT_OF_ENTITIES, count);
		TestTransaction.end();
	}

	private void checkAll(List<Project> result) {
		assertEquals(AMOUNT_OF_ENTITIES, result.size());
		List<UUID> relevantResults = new ArrayList<>();
		for(int i=0;i<result.size();i++) {
			Project next = result.get(i);
			relevantResults.add(next.getId());
		}
		assertTrue(relevantResults.contains(TEST_PROJECT_ID_1));
		assertTrue(relevantResults.contains(TEST_PROJECT_ID_2));
		assertTrue(relevantResults.contains(TEST_PROJECT_ID_3));
		assertTrue(relevantResults.contains(TEST_PROJECT_ID_4));
		assertTrue(relevantResults.contains(TEST_PROJECT_ID_5));
		assertTrue(relevantResults.contains(TEST_PROJECT_ID_6));
		assertTrue(relevantResults.contains(TEST_PROJECT_ID_7));
	}
	
	private void checkSortedDescById(List<Project> result) {
		assertEquals(AMOUNT_OF_ENTITIES, result.size());
		assertEquals(TEST_PROJECT_ID_7, result.get(0).getId());
		assertEquals(TEST_PROJECT_ID_6, result.get(1).getId());
		assertEquals(TEST_PROJECT_ID_5, result.get(2).getId());
		assertEquals(TEST_PROJECT_ID_4, result.get(3).getId());
		assertEquals(TEST_PROJECT_ID_3, result.get(4).getId());
		assertEquals(TEST_PROJECT_ID_2, result.get(5).getId());
		assertEquals(TEST_PROJECT_ID_1, result.get(6).getId());
		
	}

	private void checkSortedAscById(List<Project> result) {
		assertEquals(AMOUNT_OF_ENTITIES, result.size());
		assertEquals(TEST_PROJECT_ID_1, result.get(0).getId());
		assertEquals(TEST_PROJECT_ID_2, result.get(1).getId());
		assertEquals(TEST_PROJECT_ID_3, result.get(2).getId());
		assertEquals(TEST_PROJECT_ID_4, result.get(3).getId());
		assertEquals(TEST_PROJECT_ID_5, result.get(4).getId());
		assertEquals(TEST_PROJECT_ID_6, result.get(5).getId());
		assertEquals(TEST_PROJECT_ID_7, result.get(6).getId());
	}

}
