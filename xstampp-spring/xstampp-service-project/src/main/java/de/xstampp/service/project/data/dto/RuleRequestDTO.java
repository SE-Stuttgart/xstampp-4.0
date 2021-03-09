package de.xstampp.service.project.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RuleRequestDTO {

	private String projectId;

	private int controlActionId;

	private String rule;

	private String state;
	
	public String getRule() {
		return rule;
	}
	public void setRule(String rule) {
		this.rule = rule;
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
