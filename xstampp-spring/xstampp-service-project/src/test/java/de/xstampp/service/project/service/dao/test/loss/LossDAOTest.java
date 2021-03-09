package de.xstampp.service.project.service.dao.test.loss;

import static de.xstampp.service.project.service.dao.test.loss.LossEntityTestConstants.TEST_LOSS_ID_1;
import static de.xstampp.service.project.service.dao.test.loss.LossEntityTestConstants.TEST_LOSS_NAME_1;
import static de.xstampp.service.project.service.dao.test.loss.LossEntityTestConstants.TEST_NON_EXISTENT_LOSS_ID;
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

/**
 *	Temporarily ignore tests, because the automated pipeline currently does not support database access.
 */
@Ignore
@SpringBootTest(classes = {ProjectServiceTestDatabaseHibernateCleaner.class, PersistenceHibernateConfiguration.class, ConfigurationService.class, LossHibernateDAO.class, ProjectHibernateDAO.class})
public class LossDAOTest extends AbstractDAOTest {
	
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
		ProjectDependentKey id = new ProjectDependentKey(TEST_PROJECT_ID_1, TEST_LOSS_ID_1);
		Loss loss = new Loss(id);
		loss.setName(TEST_LOSS_NAME_1);
		loss.setDescription(TEST_DESCRIPTION);
		xstamppHelper.init(loss);
		lossDAO.makePersistent(loss);
	}
	
	@Test
	public void loadByIdForRead() {
		TestTransaction.start();
		ProjectDependentKey id = new ProjectDependentKey(TEST_PROJECT_ID_1, TEST_LOSS_ID_1);
		Loss loss = lossDAO.findById(id, false);
		checkLoss(loss);
		TestTransaction.end();
	}
	
	@Test
	public void loadByIdForWrite() {
		TestTransaction.start();
		ProjectDependentKey id = new ProjectDependentKey(TEST_PROJECT_ID_1, TEST_LOSS_ID_1);
		Loss loss = lossDAO.findById(id, true);
		checkLoss(loss);
		TestTransaction.end();
	}
	
	@Test
	public void entityExist() {
		TestTransaction.start();
		ProjectDependentKey id = new ProjectDependentKey(TEST_PROJECT_ID_1, TEST_LOSS_ID_1);
		boolean exist = lossDAO.exist(id);
		assertTrue("expected: entity exists.",exist);
		TestTransaction.end();
	}
	
	@Test
	public void entityNotExist() {
		TestTransaction.start();
		ProjectDependentKey id = new ProjectDependentKey(TEST_PROJECT_ID_1, TEST_NON_EXISTENT_LOSS_ID);
		boolean exist = lossDAO.exist(id);
		assertFalse("expected: entity does not exist.",exist);
		TestTransaction.end();
	}

	private void checkLoss(Loss loss) {
		assertNotNull(loss);
		assertEquals(TEST_PROJECT_ID_1, loss.getId().getProjectId());
		assertEquals(TEST_LOSS_ID_1, loss.getId().getId());
		assertEquals(TEST_LOSS_NAME_1, loss.getName());
		assertEquals(TEST_DESCRIPTION, loss.getDescription());
		xstamppHelper.check(loss);
	}

}
