package de.xstampp.service.project.service.dao;

import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.time.Instant;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@TestPropertySource(properties = { "config.path=./config/" })
@Transactional
@Commit
public abstract class AbstractDAOTest {
	
	private Timestamp now;
	
	private Timestamp afterFewMinutes;
	
	@Autowired
	private TestDatabaseHibernateCleaner dbCleaner;
	
	protected AbstractDAOTest() {
		super();
	}
	
	protected Timestamp now() {
		return this.now;
	}
	
	protected Timestamp afterFewMinutes() {
		return this.afterFewMinutes;
	}

	@Before
	public void init() {
		assertTrue(TestTransaction.isActive());
		dbCleaner.clean();
		TestTransaction.end();
		initTimestamps();
		TestTransaction.start();
		initTestCase();
		TestTransaction.end();
	}

	protected abstract void initTestCase();
	
	private void initTimestamps() {
		Instant timestamp = Instant.now();
		now = Timestamp.from(timestamp);
		afterFewMinutes = Timestamp.from(timestamp.plusMillis(100000));
		now = cutOffNano(now);
		afterFewMinutes= cutOffNano(afterFewMinutes);
	}
	
	private Timestamp cutOffNano (Timestamp time) {
		return new Timestamp(time.toInstant().toEpochMilli());
	}

}
