package de.xstampp.service.auth.dto;

public class GroupMembershipRequestDTO {

	String accessLevel;

	public GroupMembershipRequestDTO() {
		
	}
	
	public GroupMembershipRequestDTO(String accessLevel) {
		this.accessLevel = accessLevel;
	}

	public String getAccessLevel() {
		return accessLevel;
	}

	public void setAccessLevel(String accessLevel) {
		this.accessLevel = accessLevel;
	}

	@Override
	public String toString() {
		return "GroupMembershipRequestDTO [accessLevel=" + accessLevel + "]";
	}
}
