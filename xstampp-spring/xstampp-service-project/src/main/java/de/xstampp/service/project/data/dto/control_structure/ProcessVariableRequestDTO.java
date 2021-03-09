package de.xstampp.service.project.data.dto.control_structure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.xstampp.service.project.data.entity.Box;

import java.sql.Timestamp;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessVariableRequestDTO {

	private Integer id;
	private String projectId;
	private String name;
	private String description;
	private Box source;
	//Variable can be ether discreet or non-discreet
	private String variable_type;
	private String variable_value;
	private Integer currentProcessModel;
	private Integer[] process_models;
	//If variable is discreet, it can have multiple different states
	private String[] valueStates;
	private Integer[] responsibilityIds;

	private Timestamp last_edited;
    private UUID last_editor_id;

	/**
	 * The state of the entity (either TODO, DOING or DONE)
	 */
	private String state;

	public String[] getValueStates() {
		return valueStates;
	}

	public void setValueStates(String[] valueStates) {
		this.valueStates = valueStates;
	}

	public ProcessVariableRequestDTO() {
		// default constructor for jackson
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

	public Box getSource() {
		return source;
	}

	public void setSource(Box source) {
		this.source = source;
	}

	public String getVariable_type() {
		return variable_type;
	}

	public void setVariable_type(String variable_type) {
		this.variable_type = variable_type;
	}

	public String getVariable_value() {
		return variable_value;
	}

	public void setVariable_value(String variable_value) {
		this.variable_value = variable_value;
	}

	public Integer[] getProcess_models() {
		return process_models;
	}

	public void setProcess_models(Integer[] process_models) {
		this.process_models = process_models;
	}

	public Integer[] getResponsibilityIds() { return responsibilityIds; }

	public void setResponsibilityIds(Integer[] responsibilityIds) { this.responsibilityIds = responsibilityIds; }

	public Integer getCurrentProcessModel() {
		return currentProcessModel;
	}

	public void setCurrentProcessModel(Integer currentProcessModel) {
		this.currentProcessModel = currentProcessModel;
	}

    public Timestamp getLast_edited() {
        return last_edited;
    }

    public void setLast_edited(Timestamp last_edited) {
        this.last_edited = last_edited;
    }

    public UUID getLast_editor_id() {
        return last_editor_id;
    }

    public void setLast_editor_id(UUID last_editor_id) {
        this.last_editor_id = last_editor_id;
    }


	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}
}
