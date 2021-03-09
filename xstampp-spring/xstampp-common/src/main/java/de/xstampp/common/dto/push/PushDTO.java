package de.xstampp.common.dto.push;

public class PushDTO {
	private String displayName;

	public PushDTO() {
		// default constructor for Jackson
	}

	public PushDTO(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}
