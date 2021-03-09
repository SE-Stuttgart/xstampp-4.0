package de.xstampp.service.project.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LossScenarioRequestDTO {

    Integer id;
    String projectId;
    Integer ucaId;
    String name;
    String description;

    String headCategory;
    String subCategory;
    Integer controller1Id;
    Integer controller2Id;
    Integer controlAlgorithm;
    String description1;
    String description2;
    String description3;
    Integer controlActionId;
    Integer inputArrowId;
    Integer feedbackArrowId;
    String inputBoxId;
    Integer sensorId;
    String reason;

    String state;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProjectId() {
        return this.projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public Integer getUcaId() {
        return this.ucaId;
    }

    public void setUcaId(Integer ucaId) {
        this.ucaId = ucaId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getController1Id() {
        return this.controller1Id;
    }

    public void setController1Id(Integer controller1Id) {
        this.controller1Id = controller1Id;
    }

    public Integer getController2Id() {
        return this.controller2Id;
    }

    public void setController2Id(Integer controller2Id) {
        this.controller2Id = controller2Id;
    }

    public Integer getControlAlgorithm() {
        return this.controlAlgorithm;
    }

    public void setControlAlgorithm(Integer controlAlgorithm) {
        this.controlAlgorithm = controlAlgorithm;
    }

    public String getDescription1() {
        return this.description1;
    }

    public void setDescription1(String description1) {
        this.description1 = description1;
    }

    public String getDescription2() {
        return this.description2;
    }

    public void setDescription2(String description2) {
        this.description2 = description2;
    }

    public String getDescription3() {
        return this.description3;
    }

    public void setDescription3(String description3) {
        this.description3 = description3;
    }

    public Integer getControlActionId() {
        return this.controlActionId;
    }

    public void setControlActionId(Integer controlActionId) {
        this.controlActionId = controlActionId;
    }

    public Integer getInputArrowId() {
        return this.inputArrowId;
    }

    public void setInputArrowId(Integer inputArrowId) {
        this.inputArrowId = inputArrowId;
    }

    public Integer getFeedbackArrowId() {
        return this.feedbackArrowId;
    }

    public void setFeedbackArrowId(Integer feedbackArrowId) {
        this.feedbackArrowId = feedbackArrowId;
    }

    public String getInputBoxId() {
        return this.inputBoxId;
    }

    public void setInputBoxId(String inputBoxId) {
        this.inputBoxId = inputBoxId;
    }

    public Integer getSensorId() {
        return this.sensorId;
    }

    public void setSensorId(Integer sensorId) {
        this.sensorId = sensorId;
    }

    public String getHeadCategory() {
        return headCategory;
    }

    public void setHeadCategory(String headCategory) {
        this.headCategory = headCategory;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
