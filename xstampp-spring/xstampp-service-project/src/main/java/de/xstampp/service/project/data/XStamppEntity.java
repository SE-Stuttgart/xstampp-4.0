package de.xstampp.service.project.data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * abstract Class needed for table-locks
 */
@MappedSuperclass
public abstract class XStamppEntity {

	@Column(name= "last_editor_id")
	private UUID lastEditorId;

	@Column(name = "last_edited")
	private Timestamp lastEdited;

	@Column(name = "lock_holder_id")
	private UUID lockHolderId;

	@Column(name = "lock_holder_displayname")
	private String lockHolderDisplayName;

	@Column(name = "lock_exp_time")
	private Timestamp lockExpirationTime;

	public UUID getLastEditorId() {
		return lastEditorId;
	}

	public void setLastEditorId(UUID lastEditorId) {
		this.lastEditorId = lastEditorId;
	}


	public Timestamp getLastEdited() {
		return lastEdited;
	}

	public void setLastEdited(Timestamp lastEdited) {
		this.lastEdited = lastEdited;
	}

	public Timestamp getLockExpirationTime() {
		return lockExpirationTime;
	}

	public void setLockExpirationTime(Timestamp lockExpirationTime) {
		this.lockExpirationTime = lockExpirationTime;
	}

	public UUID getLockHolderId() {
		return lockHolderId;
	}

	public void setLockHolderId(UUID lockHolderId) {
		this.lockHolderId = lockHolderId;
	}

	// FIXME: [hotfix] no user information in project service (DSGVO)
	@Deprecated
	public String getLockHolderDisplayName() {
		return "anonymous";
	}

	// FIXME: [hotfix] no user information in project service (DSGVO)
	@Deprecated
	public void setLockHolderDisplayName(String lockHolderDisplayName) {
		this.lockHolderDisplayName = "anonymous";
	}

	public void setLastEditNow(UUID lastEditorId) {
		this.setLastEdited(Timestamp.from(Instant.now()));
		this.setLastEditorId(lastEditorId);
	}

	@Override
	public String toString() {
		return "XStamppEntity [lastEditorId" + lastEditorId  +
				", lastEdited=" + lastEdited + ", lockHolderId=" + lockHolderId +
				", lockHolderDisplayName=" + lockHolderDisplayName + ", lockExpirationTime=" + lockExpirationTime + "]";
	}
	
	public static final class EntityAttributes {
		public static final String LAST_EDITED = "lastEdited";
		public static final String LAST_EDITOR_ID = "lastEditorId";
		public static final String LOCK_HOLDER_ID = "lockHolderId";
		public static final String LOCK_HOLDER_DISPLAYNAME = "lockHolderDisplayName";
		public static final String LOCK_EXPIRATION_TIME = "lockExpirationTime";

		public static List<String> getAllAttributes() {
			List<String> result = new ArrayList<>();
			result.add(LAST_EDITOR_ID);
			result.add(LAST_EDITED);
			result.add(LOCK_HOLDER_ID);
			result.add(LOCK_HOLDER_DISPLAYNAME);
			result.add(LOCK_EXPIRATION_TIME);
			return result;
		}
	}

}
