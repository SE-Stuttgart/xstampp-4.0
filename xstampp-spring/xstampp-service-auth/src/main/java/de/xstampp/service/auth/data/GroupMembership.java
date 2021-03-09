package de.xstampp.service.auth.data;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "group_membership")
public class GroupMembership implements Serializable {

	private static final long serialVersionUID = -6971893327199724354L;
	
	@Id
	@Column(name="user_id")
	private UUID userId;
	
	@Id
	@Column(name="group_id")
	private UUID groupId;
	
	@Column(name="access_level")
	private String accessLevel;

	public GroupMembership() {
		/* default constructor for hibernate */
	}

	public GroupMembership(UUID userId, UUID groupId, String accessLevel) {
		this.userId = userId;
		this.groupId = groupId;
		this.accessLevel = accessLevel;
	}
	
	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public UUID getGroupId() {
		return groupId;
	}

	public void setGroupId(UUID groupId) {
		this.groupId = groupId;
	}

	public String getAccessLevel() {
		return accessLevel;
	}

	public void setAccessLevel(String accessLevel) {
		this.accessLevel = accessLevel;
	}
	
	@Override
	public String toString() {
		return this.userId.toString() + this.groupId.toString() + this.accessLevel;
		
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GroupMembership other = (GroupMembership) obj;
		if (groupId == null) {
			if (other.groupId != null)
				return false;
		} else if (!groupId.equals(other.groupId))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	public static final class EntityAttributes {
		public static final String GROUP_ID ="groupId";
		public static final String USER_ID ="userId";
		public static final String ACCESS_LEVEL ="accessLevel";
	}

}
