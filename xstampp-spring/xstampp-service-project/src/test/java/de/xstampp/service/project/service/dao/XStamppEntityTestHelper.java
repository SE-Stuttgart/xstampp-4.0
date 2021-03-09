package de.xstampp.service.project.service.dao;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.util.UUID;

import de.xstampp.service.project.data.XStamppEntity;

/**
 * Helper methods for tests with the
 * {@link de.xstampp.service.project.data.XStamppEntity}.
 */
public final class XStamppEntityTestHelper {
	
    private static final UUID TEST_USER_ID = UUID.fromString("152a35ca-589d-5cbb-a92c-010c4a444444");
	
	private static final String TEST_USER_NAME = "testUserJUnit";
	
	private final Timestamp now;
	
	private final Timestamp afterFewMinutes;

	public XStamppEntityTestHelper(Timestamp now, Timestamp afterFewMinutes) {
		super();
		this.now = now;
		this.afterFewMinutes = afterFewMinutes;
	}
	
	public void init(XStamppEntity entity) {
		entity.setLastEdited(now);
		//entity.setLastEditor(TEST_USER_NAME);
		entity.setLockExpirationTime(afterFewMinutes);
		entity.setLockHolderDisplayName(TEST_USER_NAME);
		entity.setLockHolderId(TEST_USER_ID);
	}
	
	
	public void check(XStamppEntity entity) {
		assertEquals(now, entity.getLastEdited());
		assertEquals(TEST_USER_NAME, entity.getLastEditorId());
		assertEquals(afterFewMinutes, entity.getLockExpirationTime());
		assertEquals(TEST_USER_NAME, entity.getLockHolderDisplayName());
		assertEquals(TEST_USER_ID, entity.getLockHolderId());
	}

}
