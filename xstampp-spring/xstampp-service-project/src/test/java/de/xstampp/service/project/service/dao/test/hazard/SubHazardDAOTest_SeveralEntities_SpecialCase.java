package de.xstampp.service.project.service.dao.test.hazard;

import static de.xstampp.service.project.service.dao.test.hazard.HazardEntityTestConstants.TEST_HAZARD_ID_1;
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
import static de.xstampp.service.project.service.dao.test.project.ProjectEntityTestConstants.TEST_PROJECT_ID_1;
import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.transaction.TestTransaction;

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
public class SubHazardDAOTest_SeveralEntities_SpecialCase extends AbstractDAOTest {
	
	@Autowired
	private IProjectDAO projectDAO;
	
	@Autowired
	private IHazardDAO hazardDAO;
	
	@Autowired
	private ISubHazardDAO subHazardDAO;
	
	private SubHazardEntityTestHelper subHazardHelper;
	
	private static final int AMOUNT_OF_ENTITIES = 7;

	@Override
	protected void initTestCase() {
		subHazardHelper = new SubHazardEntityTestHelper(now(), afterFewMinutes());
		
		projectDAO.makePersistent(new Project(TEST_PROJECT_ID_1));
		hazardDAO.makePersistent(subHazardHelper.getHazardFor(TEST_PROJECT_ID_1, TEST_HAZARD_ID_1));
		
		saveSubHazard(TEST_SUBHAZARD_ID_1, TEST_SUBHAZARD_NAME_3, TEST_SUBHAZARD_DESCRIPTION_1);
		saveSubHazard(TEST_SUBHAZARD_ID_2, TEST_SUBHAZARD_NAME_2, TEST_SUBHAZARD_DESCRIPTION_2);
		saveSubHazard(TEST_SUBHAZARD_ID_3, TEST_SUBHAZARD_NAME_2, TEST_SUBHAZARD_DESCRIPTION_1);
		saveSubHazard(TEST_SUBHAZARD_ID_4, TEST_SUBHAZARD_NAME_2, TEST_SUBHAZARD_DESCRIPTION_2);
		saveSubHazard(TEST_SUBHAZARD_ID_5, TEST_SUBHAZARD_NAME_1, TEST_SUBHAZARD_DESCRIPTION_1);
		saveSubHazard(TEST_SUBHAZARD_ID_6, TEST_SUBHAZARD_NAME_1, TEST_SUBHAZARD_DESCRIPTION_2);
		saveSubHazard(TEST_SUBHAZARD_ID_7, TEST_SUBHAZARD_NAME_1, TEST_SUBHAZARD_DESCRIPTION_1);
	}
	
	private void saveSubHazard(int id, String name, String description) {
		SubHazard subHazard = getFor(id,name,description);
		subHazardDAO.makePersistent(subHazard);
	}

	private SubHazard getFor(int id, String name, String description) {
		return subHazardHelper.getSubHazardFor(TEST_PROJECT_ID_1, TEST_HAZARD_ID_1, id, name, description);
	}
	
	/**
	 * Resulting sort order:
	 * id	name	description
	 * 5	1		1
	 * 7	1		1
	 * 6	1		2
	 * 3	2		1
	 * 2	2		2
	 * 4	2		2
	 * 1	3		1
	 * 
	 */
	@Test
	public void findSortedSpecial() {
		TestTransaction.start();
		LinkedHashMap<String, SortOrder> order = new LinkedHashMap<>();
		order.put(SubHazard.EntityAttributes.NAME, SortOrder.ASC);
		order.put(SubHazard.EntityAttributes.DESCRIPTION, SortOrder.ASC);
		order.put(SubHazard.EntityAttributes.NAME, SortOrder.ASC);
		order.put(SubHazard.EntityAttributes.ID, SortOrder.ASC);
		List<SubHazard> subHazards = getSortedBy(order);
		checkSorted(subHazards);
		TestTransaction.end();
	}
	
	private List<SubHazard> getSortedBy(LinkedHashMap<String, SortOrder> order) {
		return subHazardDAO.findFromTo(0, AMOUNT_OF_ENTITIES, order, TEST_PROJECT_ID_1, TEST_HAZARD_ID_1);
	}

	private void checkSorted(List<SubHazard> result) {
		assertEquals(AMOUNT_OF_ENTITIES, result.size());
		
		assertEquals(TEST_SUBHAZARD_ID_5, result.get(0).getId().getId());
		assertEquals(TEST_SUBHAZARD_ID_7, result.get(1).getId().getId());
		assertEquals(TEST_SUBHAZARD_ID_6, result.get(2).getId().getId());
		assertEquals(TEST_SUBHAZARD_ID_3, result.get(3).getId().getId());
		assertEquals(TEST_SUBHAZARD_ID_2, result.get(4).getId().getId());
		assertEquals(TEST_SUBHAZARD_ID_4, result.get(5).getId().getId());
		assertEquals(TEST_SUBHAZARD_ID_1, result.get(6).getId().getId());
	}

}
