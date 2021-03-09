package de.xstampp.service.project.service.data.XStamppProject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import de.xstampp.service.project.data.entity.*;
import de.xstampp.service.project.data.entity.control_structure.*;
import de.xstampp.service.project.data.entity.lastId.*;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName("XStamppProject")
public class XStamppProject {

    /*
     * Current Version: Flyway-Database-History 3.0
     */

    @JsonIgnore
    private UUID projectUUID;

    @JsonProperty
    private SystemDescription systemDescription;
    @JsonProperty
    private List<Loss> losses;
    @JsonProperty
    private List<Hazard> hazards;
    @JsonProperty
    private List<SystemConstraint> systemConstraints;
    @JsonProperty
    private List<SubHazard> subHazards;
    @JsonProperty
    private List<SubSystemConstraint> subSystemConstraints;

    @JsonProperty
    private List<Controller> controllers;
    @JsonProperty
    private List<Actuator> actuators;
    @JsonProperty
    private List<Sensor> sensors;
    @JsonProperty
    private List<ControlledProcess> controlledProcesses;

    @JsonProperty
    private List<ControlAction> controlActions;
    @JsonProperty
    private List<Feedback> feedback;
    @JsonProperty
    private List<Input> inputs;
    @JsonProperty
    private List<Output> outputs;
    @JsonProperty
    private List<Arrow> arrows;
    @JsonProperty
    private List<Box> boxes;
    @JsonProperty
    private List<Responsibility> responsibilities;

    @JsonProperty
    private List<UnsafeControlAction> unsafeControlActions;
    @JsonProperty
    private List<ControllerConstraint> controllerConstraints;

    @JsonProperty
    private List<ProcessModel> processModels;
    @JsonProperty
    private List<ProcessVariable> processVariables;
    @JsonProperty
    private List<DiscreteProcessVariableValue> discreteProcessVariableValues;
    @JsonProperty
    private List<Rule> rules;
    @JsonProperty
    private List<Conversion> conversions;
    @JsonProperty
    private List<LossScenario> lossScenarios;
    @JsonProperty
    private List<ImplementationConstraint> implementationConstraints;

    @JsonProperty
    private List<UnsafeControlActionHazardLink> unsafeControlActionHazardLinks;
    @JsonProperty
    private List<UnsafeControlActionSubHazardLink> unsafeControlActionSubHazardLinks;
    @JsonProperty
    private List<HazardLossLink> hazardLossLinks;
    @JsonProperty
    private List<HazardSystemConstraintLink> hazardSystemConstraintLinks;
    @JsonProperty
    private List<ResponsibilitySystemConstraintLink> responsibilitySystemConstraintLinks;
    @JsonProperty
    private List<ProcessModelProcessVariableLink> processModelProcessVariableLinks;
    @JsonProperty
    private List<ProcessVariableResponsibilityLink> processVariableResponsibilityLinks;

    @JsonProperty
    private List<ProjectEntityLastId> projectEntityLastIds;
    @JsonProperty
    private List<SubHazardLastId> subHazardLastIds;
    @JsonProperty
    private List<SubSystemConstraintLastId> subSystemConstraintLastIds;
    @JsonProperty
    private List<UnsafeControlActionLastId> unsafeControlActionLastIds;
    @JsonProperty
    private List<RuleLastId> ruleLastIds;
    @JsonProperty
    private List<ConversionLastId> conversionLastIds;

    public UUID getProjectUUID() {
        return projectUUID;
    }

    public void setProjectUUID(UUID projectUUID) {
        this.projectUUID = projectUUID;
    }

    public SystemDescription getSystemDescription() {
        return systemDescription;
    }

    public void setSystemDescription(SystemDescription systemDescription) {
        this.systemDescription = systemDescription;
    }

    public List<Loss> getLosses() {
        return losses;
    }

    public void setLosses(List<Loss> losses) {
        this.losses = losses;
    }

    public List<Hazard> getHazards() {
        return hazards;
    }

    public void setHazards(List<Hazard> hazards) {
        this.hazards = hazards;
    }

    public List<SystemConstraint> getSystemConstraints() {
        return systemConstraints;
    }

    public void setSystemConstraints(List<SystemConstraint> systemConstraints) {
        this.systemConstraints = systemConstraints;
    }

    public List<SubHazard> getSubHazards() {
        return subHazards;
    }

    public void setSubHazards(List<SubHazard> subHazards) {
        this.subHazards = subHazards;
    }

    public List<SubSystemConstraint> getSubSystemConstraints() {
        return subSystemConstraints;
    }

    public void setSubSystemConstraints(List<SubSystemConstraint> subSystemConstraints) {
        this.subSystemConstraints = subSystemConstraints;
    }

    public List<Controller> getControllers() {
        return controllers;
    }

    public void setControllers(List<Controller> controllers) {
        this.controllers = controllers;
    }

    public List<Actuator> getActuators() {
        return actuators;
    }

    public void setActuators(List<Actuator> actuators) {
        this.actuators = actuators;
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }

    public List<ControlledProcess> getControlledProcesses() {
        return controlledProcesses;
    }

    public void setControlledProcesses(List<ControlledProcess> controlledProcesses) {
        this.controlledProcesses = controlledProcesses;
    }

    public List<ControlAction> getControlActions() {
        return controlActions;
    }

    public void setControlActions(List<ControlAction> controlActions) {
        this.controlActions = controlActions;
    }

    public List<Feedback> getFeedback() {
        return feedback;
    }

    public void setFeedback(List<Feedback> feedback) {
        this.feedback = feedback;
    }

    public List<Input> getInputs() {
        return inputs;
    }

    public void setInputs(List<Input> inputs) {
        this.inputs = inputs;
    }

    public List<Output> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<Output> outputs) {
        this.outputs = outputs;
    }

    public List<Arrow> getArrows() {
        return arrows;
    }

    public void setArrows(List<Arrow> arrows) {
        this.arrows = arrows;
    }

    public List<Box> getBoxes() {
        return boxes;
    }

    public void setBoxes(List<Box> boxes) {
        this.boxes = boxes;
    }

    public List<Responsibility> getResponsibilities() {
        return responsibilities;
    }

    public void setResponsibilities(List<Responsibility> responsibilities) {
        this.responsibilities = responsibilities;
    }

    public List<UnsafeControlAction> getUnsafeControlActions() {
        return unsafeControlActions;
    }

    public void setUnsafeControlActions(List<UnsafeControlAction> unsafeControlActions) {
        this.unsafeControlActions = unsafeControlActions;
    }

    public List<ControllerConstraint> getControllerConstraints() {
        return controllerConstraints;
    }

    public void setControllerConstraints(List<ControllerConstraint> controllerConstraints) {
        this.controllerConstraints = controllerConstraints;
    }

    public List<ProcessModel> getProcessModels() {
        return processModels;
    }

    public void setProcessModels(List<ProcessModel> processModels) {
        this.processModels = processModels;
    }

    public List<ProcessVariable> getProcessVariables() {
        return processVariables;
    }

    public void setProcessVariables(List<ProcessVariable> processVariables) {
        this.processVariables = processVariables;
    }

    public List<DiscreteProcessVariableValue> getDiscreteProcessVariableValues() {
        return discreteProcessVariableValues;
    }

    public void setDiscreteProcessVariableValues(List<DiscreteProcessVariableValue> discreteProcessVariableValues) {
        this.discreteProcessVariableValues = discreteProcessVariableValues;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public List<Conversion> getConversions() {
        return conversions;
    }

    public void setConversions(List<Conversion> conversions) {
        this.conversions = conversions;
    }

    public List<LossScenario> getLossScenarios() {
        return lossScenarios;
    }

    public void setLossScenarios(List<LossScenario> lossScenarios) {
        this.lossScenarios = lossScenarios;
    }

    public List<ImplementationConstraint> getImplementationConstraints() {
        return implementationConstraints;
    }

    public void setImplementationConstraints(List<ImplementationConstraint> implementationConstraints) {
        this.implementationConstraints = implementationConstraints;
    }

    public List<UnsafeControlActionHazardLink> getUnsafeControlActionHazardLinks() {
        return unsafeControlActionHazardLinks;
    }

    public void setUnsafeControlActionHazardLinks(List<UnsafeControlActionHazardLink> unsafeControlActionHazardLinks) {
        this.unsafeControlActionHazardLinks = unsafeControlActionHazardLinks;
    }

    public List<UnsafeControlActionSubHazardLink> getUnsafeControlActionSubHazardLinks() {
        return unsafeControlActionSubHazardLinks;
    }

    public void setUnsafeControlActionSubHazardLinks(List<UnsafeControlActionSubHazardLink> unsafeControlActionSubHazardLinks) {
        this.unsafeControlActionSubHazardLinks = unsafeControlActionSubHazardLinks;
    }

    public List<HazardLossLink> getHazardLossLinks() {
        return hazardLossLinks;
    }

    public void setHazardLossLinks(List<HazardLossLink> hazardLossLinks) {
        this.hazardLossLinks = hazardLossLinks;
    }

    public List<HazardSystemConstraintLink> getHazardSystemConstraintLinks() {
        return hazardSystemConstraintLinks;
    }

    public void setHazardSystemConstraintLinks(List<HazardSystemConstraintLink> hazardSystemConstraintLinks) {
        this.hazardSystemConstraintLinks = hazardSystemConstraintLinks;
    }

    public List<ResponsibilitySystemConstraintLink> getResponsibilitySystemConstraintLinks() {
        return responsibilitySystemConstraintLinks;
    }

    public void setResponsibilitySystemConstraintLinks(List<ResponsibilitySystemConstraintLink> responsibilitySystemConstraintLinks) {
        this.responsibilitySystemConstraintLinks = responsibilitySystemConstraintLinks;
    }

    public List<ProcessModelProcessVariableLink> getProcessModelProcessVariableLinks() {
        return processModelProcessVariableLinks;
    }

    public void setProcessModelProcessVariableLinks(List<ProcessModelProcessVariableLink> processModelProcessVariableLinks) {
        this.processModelProcessVariableLinks = processModelProcessVariableLinks;
    }

    public List<ProcessVariableResponsibilityLink> getProcessVariableResponsibilityLinks() {
        return processVariableResponsibilityLinks;
    }

    public void setProcessVariableResponsibilityLinks(List<ProcessVariableResponsibilityLink> processVariableResponsibilityLinks) {
        this.processVariableResponsibilityLinks = processVariableResponsibilityLinks;
    }

    public List<ProjectEntityLastId> getProjectEntityLastIds() {
        return projectEntityLastIds;
    }

    public void setProjectEntityLastIds(List<ProjectEntityLastId> projectEntityLastIds) {
        this.projectEntityLastIds = projectEntityLastIds;
    }

    public List<SubHazardLastId> getSubHazardLastIds() {
        return subHazardLastIds;
    }

    public void setSubHazardLastIds(List<SubHazardLastId> subHazardLastIds) {
        this.subHazardLastIds = subHazardLastIds;
    }

    public List<SubSystemConstraintLastId> getSubSystemConstraintLastIds() {
        return subSystemConstraintLastIds;
    }

    public void setSubSystemConstraintLastIds(List<SubSystemConstraintLastId> subSystemConstraintLastIds) {
        this.subSystemConstraintLastIds = subSystemConstraintLastIds;
    }

    public List<UnsafeControlActionLastId> getUnsafeControlActionLastIds() {
        return unsafeControlActionLastIds;
    }

    public void setUnsafeControlActionLastIds(List<UnsafeControlActionLastId> unsafeControlActionLastIds) {
        this.unsafeControlActionLastIds = unsafeControlActionLastIds;
    }

    public List<RuleLastId> getRuleLastIds() {
        return ruleLastIds;
    }

    public void setRuleLastIds(List<RuleLastId> ruleLastIds) {
        this.ruleLastIds = ruleLastIds;
    }

    public List<ConversionLastId> getConversionLastIds() {
        return conversionLastIds;
    }

    public void setConversionLastIds(List<ConversionLastId> conversionLastIds) {
        this.conversionLastIds = conversionLastIds;
    }

    public static final class XStamppProjectBuilder {
        private UUID projectUUID;
        private SystemDescription systemDescription;
        private List<Loss> losses = new LinkedList<>();
        private List<Hazard> hazards = new LinkedList<>();
        private List<SystemConstraint> systemConstraints = new LinkedList<>();
        private List<SubHazard> subHazards = new LinkedList<>();
        private List<SubSystemConstraint> subSystemConstraints = new LinkedList<>();
        private List<Controller> controllers = new LinkedList<>();
        private List<Actuator> actuators = new LinkedList<>();
        private List<Sensor> sensors = new LinkedList<>();
        private List<ControlledProcess> controlledProcesses = new LinkedList<>();
        private List<ControlAction> controlActions = new LinkedList<>();
        private List<Feedback> feedback = new LinkedList<>();
        private List<Input> inputs = new LinkedList<>();
        private List<Output> outputs = new LinkedList<>();
        private List<Arrow> arrows = new LinkedList<>();
        private List<Box> boxes = new LinkedList<>();
        private List<Responsibility> responsibilities = new LinkedList<>();
        private List<UnsafeControlAction> unsafeControlActions = new LinkedList<>();
        private List<ControllerConstraint> controllerConstraints = new LinkedList<>();
        private List<ProcessModel> processModels = new LinkedList<>();
        private List<ProcessVariable> processVariables = new LinkedList<>();
        private List<DiscreteProcessVariableValue> discreteProcessVariableValues = new LinkedList<>();
        private List<Rule> rules = new LinkedList<>();
        private List<Conversion> conversions = new LinkedList<>();
        private List<LossScenario> lossScenarios = new LinkedList<>();
        private List<ImplementationConstraint> implementationConstraints = new LinkedList<>();
        private List<UnsafeControlActionHazardLink> unsafeControlActionHazardLinks = new LinkedList<>();
        private List<UnsafeControlActionSubHazardLink> unsafeControlActionSubHazardLinks = new LinkedList<>();
        private List<HazardLossLink> hazardLossLinks = new LinkedList<>();
        private List<HazardSystemConstraintLink> hazardSystemConstraintLinks = new LinkedList<>();
        private List<ResponsibilitySystemConstraintLink> responsibilitySystemConstraintLinks = new LinkedList<>();
        private List<ProcessModelProcessVariableLink> processModelProcessVariableLinks = new LinkedList<>();
        private List<ProcessVariableResponsibilityLink> processVariableResponsibilityLinks = new LinkedList<>();
        private List<ProjectEntityLastId> projectEntityLastIds = new LinkedList<>();
        private List<SubHazardLastId> subHazardLastIds = new LinkedList<>();
        private List<SubSystemConstraintLastId> subSystemConstraintLastIds = new LinkedList<>();
        private List<UnsafeControlActionLastId> unsafeControlActionLastIds = new LinkedList<>();
        private List<RuleLastId> ruleLastIds = new LinkedList<>();
        private List<ConversionLastId> conversionLastIds = new LinkedList<>();

        private XStamppProjectBuilder() {
        }

        public static XStamppProjectBuilder aXStamppProject() {
            return new XStamppProjectBuilder();
        }

        public XStamppProjectBuilder withProjectUUID(UUID projectUUID) {
            this.projectUUID = projectUUID;
            return this;
        }

        public XStamppProjectBuilder withSystemDescription(SystemDescription systemDescription) {
            this.systemDescription = systemDescription;
            return this;
        }

        public XStamppProjectBuilder withLosses(List<Loss> losses) {
            this.losses = losses;
            return this;
        }

        public XStamppProjectBuilder withHazards(List<Hazard> hazards) {
            this.hazards = hazards;
            return this;
        }

        public XStamppProjectBuilder withSystemConstraints(List<SystemConstraint> systemConstraints) {
            this.systemConstraints = systemConstraints;
            return this;
        }

        public XStamppProjectBuilder withSubHazards(List<SubHazard> subHazards) {
            this.subHazards = subHazards;
            return this;
        }

        public XStamppProjectBuilder withSubSystemConstraints(List<SubSystemConstraint> subSystemConstraints) {
            this.subSystemConstraints = subSystemConstraints;
            return this;
        }

        public XStamppProjectBuilder withControllers(List<Controller> controllers) {
            this.controllers = controllers;
            return this;
        }

        public XStamppProjectBuilder withActuators(List<Actuator> actuators) {
            this.actuators = actuators;
            return this;
        }

        public XStamppProjectBuilder withSensors(List<Sensor> sensors) {
            this.sensors = sensors;
            return this;
        }

        public XStamppProjectBuilder withControlledProcesses(List<ControlledProcess> controlledProcesses) {
            this.controlledProcesses = controlledProcesses;
            return this;
        }

        public XStamppProjectBuilder withControlActions(List<ControlAction> controlActions) {
            this.controlActions = controlActions;
            return this;
        }

        public XStamppProjectBuilder withFeedback(List<Feedback> feedback) {
            this.feedback = feedback;
            return this;
        }

        public XStamppProjectBuilder withInputs(List<Input> inputs) {
            this.inputs = inputs;
            return this;
        }

        public XStamppProjectBuilder withOutputs(List<Output> outputs) {
            this.outputs = outputs;
            return this;
        }

        public XStamppProjectBuilder withArrows(List<Arrow> arrows) {
            this.arrows = arrows;
            return this;
        }

        public XStamppProjectBuilder withBoxes(List<Box> boxes) {
            this.boxes = boxes;
            return this;
        }

        public XStamppProjectBuilder withResponsibilities(List<Responsibility> responsibilities) {
            this.responsibilities = responsibilities;
            return this;
        }

        public XStamppProjectBuilder withUnsafeControlActions(List<UnsafeControlAction> unsafeControlActions) {
            this.unsafeControlActions = unsafeControlActions;
            return this;
        }

        public XStamppProjectBuilder withControllerConstraints(List<ControllerConstraint> controllerConstraints) {
            this.controllerConstraints = controllerConstraints;
            return this;
        }

        public XStamppProjectBuilder withProcessModels(List<ProcessModel> processModels) {
            this.processModels = processModels;
            return this;
        }

        public XStamppProjectBuilder withProcessVariables(List<ProcessVariable> processVariables) {
            this.processVariables = processVariables;
            return this;
        }

        public XStamppProjectBuilder withDiscreteProcessVariableValues(List<DiscreteProcessVariableValue> discreteProcessVariableValues) {
            this.discreteProcessVariableValues = discreteProcessVariableValues;
            return this;
        }

        public XStamppProjectBuilder withRules(List<Rule> rules) {
            this.rules = rules;
            return this;
        }

        public XStamppProjectBuilder withConversions(List<Conversion> conversions) {
            this.conversions = conversions;
            return this;
        }

        public XStamppProjectBuilder withLossScenarios(List<LossScenario> lossScenarios) {
            this.lossScenarios = lossScenarios;
            return this;
        }

        public XStamppProjectBuilder withImplementationConstraints(List<ImplementationConstraint> implementationConstraints) {
            this.implementationConstraints = implementationConstraints;
            return this;
        }

        public XStamppProjectBuilder withUnsafeControlActionHazardLinks(List<UnsafeControlActionHazardLink> unsafeControlActionHazardLinks) {
            this.unsafeControlActionHazardLinks = unsafeControlActionHazardLinks;
            return this;
        }

        public XStamppProjectBuilder withUnsafeControlActionSubHazardLinks(List<UnsafeControlActionSubHazardLink> unsafeControlActionSubHazardLinks) {
            this.unsafeControlActionSubHazardLinks = unsafeControlActionSubHazardLinks;
            return this;
        }

        public XStamppProjectBuilder withHazardLossLinks(List<HazardLossLink> hazardLossLinks) {
            this.hazardLossLinks = hazardLossLinks;
            return this;
        }

        public XStamppProjectBuilder withHazardSystemConstraintLinks(List<HazardSystemConstraintLink> hazardSystemConstraintLinks) {
            this.hazardSystemConstraintLinks = hazardSystemConstraintLinks;
            return this;
        }

        public XStamppProjectBuilder withResponsibilitySystemConstraintLinks(List<ResponsibilitySystemConstraintLink> responsibilitySystemConstraintLinks) {
            this.responsibilitySystemConstraintLinks = responsibilitySystemConstraintLinks;
            return this;
        }

        public XStamppProjectBuilder withProcessModelProcessVariableLinks(List<ProcessModelProcessVariableLink> processModelProcessVariableLinks) {
            this.processModelProcessVariableLinks = processModelProcessVariableLinks;
            return this;
        }

        public XStamppProjectBuilder withProcessVariableResponsibilityLinks(List<ProcessVariableResponsibilityLink> processVariableResponsibilityLinks) {
            this.processVariableResponsibilityLinks = processVariableResponsibilityLinks;
            return this;
        }

        public XStamppProjectBuilder withProjectEntityLastIds(List<ProjectEntityLastId> projectEntityLastIds) {
            this.projectEntityLastIds = projectEntityLastIds;
            return this;
        }

        public XStamppProjectBuilder withSubHazardLastIds(List<SubHazardLastId> subHazardLastIds) {
            this.subHazardLastIds = subHazardLastIds;
            return this;
        }

        public XStamppProjectBuilder withSubSystemConstraintLastIds(List<SubSystemConstraintLastId> subSystemConstraintLastIds) {
            this.subSystemConstraintLastIds = subSystemConstraintLastIds;
            return this;
        }

        public XStamppProjectBuilder withUnsafeControlActionLastIds(List<UnsafeControlActionLastId> unsafeControlActionLastIds) {
            this.unsafeControlActionLastIds = unsafeControlActionLastIds;
            return this;
        }

        public XStamppProjectBuilder withRuleLastIds(List<RuleLastId> ruleLastIds) {
            this.ruleLastIds = ruleLastIds;
            return this;
        }

        public XStamppProjectBuilder withConversionLastIds(List<ConversionLastId> conversionLastIds) {
            this.conversionLastIds = conversionLastIds;
            return this;
        }

        public XStamppProject build() {
            XStamppProject xStamppProject = new XStamppProject();
            xStamppProject.setProjectEntityLastIds(projectEntityLastIds);
            xStamppProject.setSubHazardLastIds(subHazardLastIds);
            xStamppProject.setSubSystemConstraintLastIds(subSystemConstraintLastIds);
            xStamppProject.setUnsafeControlActionLastIds(unsafeControlActionLastIds);
            xStamppProject.setRuleLastIds(ruleLastIds);
            xStamppProject.setConversionLastIds(conversionLastIds);
            xStamppProject.boxes = this.boxes;
            xStamppProject.lossScenarios = this.lossScenarios;
            xStamppProject.hazardSystemConstraintLinks = this.hazardSystemConstraintLinks;
            xStamppProject.controllers = this.controllers;
            xStamppProject.projectUUID = this.projectUUID;
            xStamppProject.hazardLossLinks = this.hazardLossLinks;
            xStamppProject.processVariables = this.processVariables;
            xStamppProject.rules = this.rules;
            xStamppProject.responsibilities = this.responsibilities;
            xStamppProject.hazards = this.hazards;
            xStamppProject.systemConstraints = this.systemConstraints;
            xStamppProject.processModelProcessVariableLinks = this.processModelProcessVariableLinks;
            xStamppProject.unsafeControlActionHazardLinks = this.unsafeControlActionHazardLinks;
            xStamppProject.actuators = this.actuators;
            xStamppProject.implementationConstraints = this.implementationConstraints;
            xStamppProject.conversions = this.conversions;
            xStamppProject.feedback = this.feedback;
            xStamppProject.controlActions = this.controlActions;
            xStamppProject.responsibilitySystemConstraintLinks = this.responsibilitySystemConstraintLinks;
            xStamppProject.outputs = this.outputs;
            xStamppProject.subHazards = this.subHazards;
            xStamppProject.controlledProcesses = this.controlledProcesses;
            xStamppProject.processVariableResponsibilityLinks = this.processVariableResponsibilityLinks;
            xStamppProject.systemDescription = this.systemDescription;
            xStamppProject.sensors = this.sensors;
            xStamppProject.arrows = this.arrows;
            xStamppProject.unsafeControlActions = this.unsafeControlActions;
            xStamppProject.discreteProcessVariableValues = this.discreteProcessVariableValues;
            xStamppProject.losses = this.losses;
            xStamppProject.unsafeControlActionSubHazardLinks = this.unsafeControlActionSubHazardLinks;
            xStamppProject.controllerConstraints = this.controllerConstraints;
            xStamppProject.inputs = this.inputs;
            xStamppProject.subSystemConstraints = this.subSystemConstraints;
            xStamppProject.processModels = this.processModels;
            return xStamppProject;
        }
    }
}
