package de.xstampp.service.project.data.dto.control_structure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessModelRequestDTO {

    private Integer id;
    private String projectId;
    private String name;
    private String description;
    private int controllerId;

    private String state;

    public ProcessModelRequestDTO() {
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

    public int getControllerId() {
        return controllerId;
    }

    public void setControllerId(int controllerID) {
        this.controllerId = controllerID;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }
}