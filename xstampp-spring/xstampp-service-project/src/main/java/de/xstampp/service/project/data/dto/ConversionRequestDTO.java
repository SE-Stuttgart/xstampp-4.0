package de.xstampp.service.project.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConversionRequestDTO {

	private String projectId;

	private int controlActionId;

	private String conversion;

	private String state;
	
	public String getConversion() {
		return conversion;
	}
	public void setConversion(String conversion) {
		this.conversion = conversion;
	}
	public int getControlActionId() {
		return controlActionId;
	}
	public void setControlActionId(int controlActionId) {
		this.controlActionId = controlActionId;
	}
	public void setControlActionId(String controlActionId) {
		this.controlActionId = Integer.valueOf(controlActionId);
	}
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}
}
