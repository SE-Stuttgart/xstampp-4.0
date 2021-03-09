package de.xstampp.service.auth.dto;

import java.util.UUID;

import de.xstampp.service.auth.data.User;

/** Holds all fields of a {@link User} that are public for group admins. */
public class UserInGroupResponseDTO {
	private UUID uid;
	private String email;
	private String displayName;
	private String accessLevel;

	public UserInGroupResponseDTO(UUID uid, String email, String displayName, String accessLevel) {
		this.uid = uid;
		this.email = email;
		this.displayName = displayName;
		this.accessLevel = accessLevel;
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

	public String getAccessLevel() {
		return accessLevel;
	}

	public void setAccessLevel(String accessLevel) {
		this.accessLevel = accessLevel;
	}
}
