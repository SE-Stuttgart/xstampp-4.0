package de.xstampp.service.auth.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import de.xstampp.service.auth.data.Group;

public class GroupAndAccessLevelResponseDTO {

	@JsonUnwrapped
	private final Group group;

	private final String accessLevel;

	public GroupAndAccessLevelResponseDTO(Group group, String accessLevel) {
		this.group = group;
		this.accessLevel = accessLevel;
	}

	public Group getGroup() {
		return group;
	}

	public String getAccessLevel() {
		return accessLevel;
	}
}
