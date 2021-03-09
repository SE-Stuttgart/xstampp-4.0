package de.xstampp.service.auth.dto;

import java.sql.Timestamp;
import java.util.UUID;

import de.xstampp.service.auth.data.User;

/** Holds all fields of a {@link User} that are public for system admins. */
public class UserResponseAdminDTO {
	private UUID uid;
	private String email;
	private String displayName;
	private boolean isSystemAdmin;
	private Timestamp lockedUntil;

	public UserResponseAdminDTO(User user) {
		this.uid = user.getId();
		this.email = user.getEmail();
		this.displayName = user.getDisplayName();
		this.isSystemAdmin = user.isSystemAdmin();
		this.lockedUntil = user.getLockedUntil();
	}

	public UUID getUid() {
		return uid;
	}

	public void setUid(UUID uid) {
		this.uid = uid;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public boolean isSystemAdmin() {
		return isSystemAdmin;
	}

	public void setSystemAdmin(boolean isSystemAdmin) {
		this.isSystemAdmin = isSystemAdmin;
	}

	public Timestamp getLockedUntil() {
		return lockedUntil;
	}

	public void setLockedUntil(Timestamp lockedUntil) {
		this.lockedUntil = lockedUntil;
	}
}
