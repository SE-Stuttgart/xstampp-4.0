package de.xstampp.common.auth;

import java.util.HashMap;
import java.util.UUID;

public class SecurityContext {
	private UUID userId;
	private String displayName;
	private String tokenType;
	private String projectId;
	private String projectRole;
	private String role;
	private String token;
	
	HashMap<String, String> map = new HashMap<>();

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getProjectRole() {
		return projectRole;
	}

	public void setProjectRole(String projectRole) {
		this.projectRole = projectRole;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
	public void setValue (String key, String value) {
		this.map.put(key, value);
	}
	
	public String getValue (String key) {
		return map.get(key);
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
