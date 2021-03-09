package de.xstampp.service.project.data.dto.report;

public class ReportConfigDTO {

    String reportName;
    boolean titlePage;
    boolean tableOfContents;

    boolean systemDescription;
    boolean losses;
    boolean hazards;
    boolean subHazards;
    boolean systemConstraints;
    boolean subSystemConstraints;

    boolean controlStructure;
    boolean controlStructureHasColour;
    boolean controllers;
    boolean actuators;
    boolean sensors;
    boolean controlledProcesses;
    boolean controlActions;
    boolean feedback;
    boolean inputs;
    boolean outputs;
    boolean responsibilities;

    boolean ucas;
    boolean controllerConstraints;

    boolean processModels;
    boolean processVariables;
    boolean controlAlgorithms;
    boolean conversions;
    boolean lossScenarios;
    boolean implementationConstraints;

    public ReportConfigDTO() {
        //empty constructor for jackson
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public boolean isTitlePage() {
        return titlePage;
    }

    public void setTitlePage(boolean titlePage) {
        this.titlePage = titlePage;
    }

    public boolean isTableOfContents() {
        return tableOfContents;
    }

    public void setTableOfContents(boolean tableOfContents) {
        this.tableOfContents = tableOfContents;
    }

    public boolean isSystemDescription() {
        return systemDescription;
    }

    public void setSystemDescription(boolean systemDescription) {
        this.systemDescription = systemDescription;
    }

    public boolean isLosses() {
        return losses;
    }

    public void setLosses(boolean losses) {
        this.losses = losses;
    }

    public boolean isHazards() {
        return hazards;
    }

    public void setHazards(boolean hazards) {
        this.hazards = hazards;
    }

    public boolean isSubHazards() {
        return subHazards;
    }

    public void setSubHazards(boolean subHazards) {
        this.subHazards = subHazards;
    }

    public boolean isSystemConstraints() {
        return systemConstraints;
    }

    public void setSystemConstraints(boolean systemConstraints) {
        this.systemConstraints = systemConstraints;
    }

    public boolean isSubSystemConstraints() {
        return subSystemConstraints;
    }

    public void setSubSystemConstraints(boolean subSystemConstraints) {
        this.subSystemConstraints = subSystemConstraints;
    }

    public boolean isControlStructure() {
        return controlStructure;
    }

    public void setControlStructure(boolean controlStructure) {
        this.controlStructure = controlStructure;
    }

    public boolean isControlStructureHasColour() {
        return controlStructureHasColour;
    }

    public void setControlStructureHasColour(boolean controlStructureHasColour) {
        this.controlStructureHasColour = controlStructureHasColour;
    }

    public boolean isControllers() {
        return controllers;
    }

    public void setControllers(boolean controllers) {
        this.controllers = controllers;
    }

    public boolean isActuators() {
        return actuators;
    }

    public void setActuators(boolean actuators) {
        this.actuators = actuators;
    }

    public boolean isSensors() {
        return sensors;
    }

    public void setSensors(boolean sensors) {
        this.sensors = sensors;
    }

    public boolean isControlledProcesses() {
        return controlledProcesses;
    }

    public void setControlledProcesses(boolean controlledProcesses) {
        this.controlledProcesses = controlledProcesses;
    }

    public boolean isControlActions() {
        return controlActions;
    }

    public void setControlActions(boolean controlActions) {
        this.controlActions = controlActions;
    }

    public boolean isFeedback() {
        return feedback;
    }

    public void setFeedback(boolean feedback) {
        this.feedback = feedback;
    }

    public boolean isInputs() {
        return inputs;
    }

    public void setInputs(boolean inputs) {
        this.inputs = inputs;
    }

    public boolean isOutputs() {
        return outputs;
    }

    public void setOutputs(boolean outputs) {
        this.outputs = outputs;
    }

    public boolean isResponsibilities() {
        return responsibilities;
    }

    public void setResponsibilities(boolean responsibilities) {
        this.responsibilities = responsibilities;
    }

    public boolean isUcas() {
        return ucas;
    }

    public void setUcas(boolean ucas) {
        this.ucas = ucas;
    }

    public boolean isControllerConstraints() {
        return controllerConstraints;
    }

    public void setControllerConstraints(boolean controllerConstraints) {
        this.controllerConstraints = controllerConstraints;
    }

    public boolean isProcessModels() {
        return processModels;
    }

    public void setProcessModels(boolean processModels) {
        this.processModels = processModels;
    }

    public boolean isProcessVariables() {
        return processVariables;
    }

    public void setProcessVariables(boolean processVariables) {
        this.processVariables = processVariables;
    }

    public boolean isControlAlgorithms() {
        return controlAlgorithms;
    }

    public void setControlAlgorithms(boolean controlAlgorithms) {
        this.controlAlgorithms = controlAlgorithms;
    }

    public boolean isConversions() {
        return conversions;
    }

    public void setConversions(boolean conversions) {
        this.conversions = conversions;
    }

    public boolean isLossScenarios() {
        return lossScenarios;
    }

    public void setLossScenarios(boolean lossScenarios) {
        this.lossScenarios = lossScenarios;
    }

    public boolean isImplementationConstraints() {
        return implementationConstraints;
    }

    public void setImplementationConstraints(boolean implementationConstraints) {
        this.implementationConstraints = implementationConstraints;
    }
}
