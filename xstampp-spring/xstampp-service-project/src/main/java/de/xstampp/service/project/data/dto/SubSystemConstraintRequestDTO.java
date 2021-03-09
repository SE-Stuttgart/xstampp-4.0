package de.xstampp.service.project.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SubSystemConstraintRequestDTO {

	private String projectId;

	private String subHazardProjectId;

	private String name;

	private String description;

	private Integer hazardId;

	private Integer subHazardId;

	private Integer systemConstraintId;

	private String state;

	public Integer getHazardId() {
		return hazardId;
	}

	public void setHazardId(Integer hazardId) {
		this.hazardId = hazardId;
	}

	public Integer getSubHazardId() {
		return subHazardId;
	}

	public void setSubHazardId(Integer subHazardId) {
		this.subHazardId = subHazardId;
	}

	public Integer getSystemConstraintId() {
		return systemConstraintId;
	}

	public void setSystemConstraintId(Integer systemConstraintId) {
		this.systemConstraintId = systemConstraintId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getSubHazardProjectId() {
		return subHazardProjectId;
	}

	public void setSubHazardProjectId(String subHazardProjectId) {
		this.subHazardProjectId = subHazardProjectId;
	}

	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}
}
