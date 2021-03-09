package de.xstampp.service.project.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A DTO used in creation and update of implementation constraints.
 * A data class with no interesting behaviour.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImplementationConstraintRequestDTO {

	private Integer id;
	private String projectId;
	private Integer lossScenarioId;
	private String name;
	private String description;

	private String state;

	public ImplementationConstraintRequestDTO() {
		/* default constructor for Jackson */
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public Integer getLossScenarioId() {
		return lossScenarioId;
	}

	public void setLossScenarioId(Integer lossScenarioId) {
		this.lossScenarioId = lossScenarioId;
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

	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}
}
