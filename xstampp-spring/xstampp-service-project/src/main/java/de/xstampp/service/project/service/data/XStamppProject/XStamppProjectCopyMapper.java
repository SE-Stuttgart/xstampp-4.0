package de.xstampp.service.project.service.data.XStamppProject;

import de.xstampp.service.project.data.entity.*;
import de.xstampp.service.project.data.entity.control_structure.*;
import de.xstampp.service.project.data.entity.lastId.*;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This class provides a copy function for every XStamppProject entity.
 * The copy functions for a specific entity takes a List of instances and
 * returns a new List of the copied instances, but with the given project UUID.
 */
@Service
public class XStamppProjectCopyMapper {

    List<Loss> copyLosses(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getLosses()
                .stream()
                .map(loss -> Loss.LossBuilder
                        .aLoss()
                        .from(loss)
                        .withId(new ProjectDependentKey(projectUUID, loss.getId().getId()))
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<Hazard> copyHazards(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getHazards()
                .stream()
                .map(hazard -> Hazard.HazardBuilder
                        .aHazard()
                        .from(hazard)
                        .withId(new ProjectDependentKey(projectUUID, hazard.getId().getId()))
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<SystemConstraint> copySystemConstraints(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getSystemConstraints()
                .stream()
                .map(systemConstraint -> SystemConstraint.SystemConstraintBuilder
                        .aSystemConstraint()
                        .from(systemConstraint)
                        .withId(new ProjectDependentKey(projectUUID, systemConstraint.getId().getId()))
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<SubHazard> copySubHazards(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getSubHazards()
                .stream()
                .map(subHazard -> SubHazard.SubHazardBuilder
                        .aSubHazard()
                        .from(subHazard)
                        .withId(new EntityDependentKey(projectUUID,
                                subHazard.getId().getParentId(), subHazard.getId().getId()))
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<SubSystemConstraint> copySubSystemConstraints(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getSubSystemConstraints()
                .stream()
                .map(subSystemConstraint -> SubSystemConstraint.SubSystemConstraintBuilder
                        .aSubSystemConstraint()
                        .from(subSystemConstraint)
                        .withId(new EntityDependentKey(projectUUID,
                                subSystemConstraint.getId().getParentId(), subSystemConstraint.getId().getId()))
                        .withSubHazardProjectId(projectUUID)
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<Controller> copyControllers(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getControllers()
                .stream()
                .map(controller -> Controller.ControllerBuilder
                        .aController()
                        .from(controller)
                        .withId(new ProjectDependentKey(projectUUID, controller.getId().getId()))
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<Actuator> copyActuators(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getActuators()
                .stream()
                .map(actuator -> Actuator.ActuatorBuilder
                        .anActuator()
                        .from(actuator)
                        .withId(new ProjectDependentKey(projectUUID, actuator.getId().getId()))
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<Sensor> copySensors(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getSensors()
                .stream()
                .map(sensor -> Sensor.SensorBuilder
                        .aSensor()
                        .from(sensor)
                        .withId(new ProjectDependentKey(projectUUID, sensor.getId().getId()))
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<ControlledProcess> copyControlledProcesses(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getControlledProcesses()
                .stream()
                .map(controlledProcess -> ControlledProcess.ControlledProcessBuilder
                        .aControlledProcess()
                        .from(controlledProcess)
                        .withId(new ProjectDependentKey(projectUUID, controlledProcess.getId().getId()))
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<ControlAction> copyControlActions(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getControlActions()
                .stream()
                .map(controlAction -> ControlAction.ControlActionBuilder
                        .aControlAction()
                        .from(controlAction)
                        .withId(new ProjectDependentKey(projectUUID, controlAction.getId().getId()))
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<Feedback> copyFeedback(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getFeedback()
                .stream().map(feedback -> Feedback.FeedbackBuilder
                        .aFeedback()
                        .from(feedback)
                        .withId(new ProjectDependentKey(projectUUID, feedback.getId().getId()))
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<Input> copyInputs(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getInputs()
                .stream()
                .map(input -> Input.InputBuilder
                        .anInput()
                        .from(input)
                        .withId(new ProjectDependentKey(projectUUID, input.getId().getId()))
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<Output> copyOutputs(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getOutputs()
                .stream()
                .map(output -> Output.OutputBuilder
                        .anOutput()
                        .from(output)
                        .withId(new ProjectDependentKey(projectUUID, output.getId().getId()))
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<Arrow> copyArrows(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getArrows()
                .stream()
                .map(arrow -> Arrow.ArrowBuilder
                        .anArrow()
                        .from(arrow)
                        .withProjectId(projectUUID)
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<Box> copyBoxes(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getBoxes()
                .stream()
                .map(box -> Box.BoxBuilder
                        .aBox()
                        .from(box)
                        .withProjectId(projectUUID)
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<Responsibility> copyResponsibilities(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getResponsibilities()
                .stream()
                .map(responsibility -> Responsibility.ResponsibilityBuilder
                        .aResponsibility()
                        .from(responsibility)
                        .withId(new ProjectDependentKey(projectUUID, responsibility.getId().getId()))
                        .withControllerProjectId(projectUUID)
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<UnsafeControlAction> copyUnsafeControlActions(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getUnsafeControlActions()
                .stream()
                .map(unsafeControlAction -> UnsafeControlAction.UnsafeControlActionBuilder
                        .anUnsafeControlAction()
                        .from(unsafeControlAction)
                        .withId(new EntityDependentKey(projectUUID,
                                unsafeControlAction.getId().getParentId(), unsafeControlAction.getId().getId()))
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<ControllerConstraint> copyControllerConstrains(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getControllerConstraints()
                .stream()
                .map(controllerConstraint -> ControllerConstraint.ControllerConstraintBuilder
                        .aControllerConstraint()
                        .from(controllerConstraint)
                        .withId(new EntityDependentKey(projectUUID,
                                controllerConstraint.getId().getParentId(), controllerConstraint.getId().getId()))
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<ProcessModel> copyProcessModels(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getProcessModels()
                .stream()
                .map(processModel -> ProcessModel.ProcessModelBuilder
                        .aProcessModel()
                        .from(processModel)
                        .withId(new ProjectDependentKey(projectUUID, processModel.getId().getId()))
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<ProcessVariable> copyProcessVariables(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getProcessVariables()
                .stream()
                .map(processVariable -> ProcessVariable.ProcessVariableBuilder
                        .aProcessVariable()
                        .from(processVariable)
                        .withId(new ProjectDependentKey(projectUUID, processVariable.getId().getId()))
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<DiscreteProcessVariableValue> copyDiscreteProcessVariableValues(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getDiscreteProcessVariableValues()
                .stream()
                .map(discreteValue  -> DiscreteProcessVariableValue.DiscreteProcessVariableValueBuilder
                        .aDiscreteProcessVariableValue()
                        .from(discreteValue)
                        .withProjectId(projectUUID)
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<Rule> copyRules(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getRules()
                .stream()
                .map(rule -> Rule.RuleBuilder
                        .aRule()
                        .from(rule)
                        .withId(new EntityDependentKey(projectUUID,
                                rule.getId().getParentId(), rule.getId().getId()))
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<Conversion> copyConversions(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getConversions()
                .stream()
                .map(conversion -> Conversion.ConversionBuilder
                        .aConversion()
                        .from(conversion)
                        .withId(new EntityDependentKey(projectUUID,
                                conversion.getId().getParentId(), conversion.getId().getId()))
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<LossScenario> copyLossScenarios(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getLossScenarios()
                .stream()
                .map(lossScenario -> LossScenario.LossScenarioBuilder
                        .aLossScenario()
                        .from(lossScenario)
                        .withId(new ProjectDependentKey(projectUUID, lossScenario.getId().getId()))
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<ImplementationConstraint> copyImplementationConstraints(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getImplementationConstraints()
                .stream()
                .map(implementationConstraint -> ImplementationConstraint.ImplementationConstraintBuilder
                        .anImplementationConstraint()
                        .from(implementationConstraint)
                        .withId(new ProjectDependentKey(projectUUID, implementationConstraint.getId().getId()))
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<HazardLossLink> copyHazardLossLinks(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getHazardLossLinks()
                .stream()
                .map(link -> HazardLossLink.HazardLossLinkBuilder
                        .aHazardLossLink()
                        .from(link)
                        .withProjectId(projectUUID)
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<HazardSystemConstraintLink> copyHazardSystemConstraintLinks(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getHazardSystemConstraintLinks()
                .stream()
                .map(link -> HazardSystemConstraintLink.HazardSystemConstraintLinkBuilder
                        .aHazardSystemConstraintLink()
                        .from(link)
                        .withProjectId(projectUUID)
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<UnsafeControlActionHazardLink> copyUnsafeControlActionHazardLinks(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getUnsafeControlActionHazardLinks()
                .stream()
                .map(link -> UnsafeControlActionHazardLink.UnsafeControlActionHazardLinkBuilder
                        .anUnsafeControlActionHazardLink()
                        .from(link)
                        .withProjectId(projectUUID)
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<ProcessModelProcessVariableLink> copyProcessModelProcessVariableLinks(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getProcessModelProcessVariableLinks()
                .stream()
                .map(link -> ProcessModelProcessVariableLink.ProcessModelProcessVariableLinkBuilder
                        .aProcessModelProcessVariableLink()
                        .from(link)
                        .withProjectId(projectUUID)
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<UnsafeControlActionSubHazardLink> copyUnsafeControlActionSubHazardLinks(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getUnsafeControlActionSubHazardLinks()
                .stream()
                .map(link -> UnsafeControlActionSubHazardLink.UnsafeControlActionSubHazardLinkBuilder
                        .anUnsafeControlActionSubHazardLink()
                        .from(link)
                        .withProjectId(projectUUID)
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<ProcessVariableResponsibilityLink> copyProcessVariableResponsibilityLinks(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getProcessVariableResponsibilityLinks()
                .stream()
                .map(link -> ProcessVariableResponsibilityLink.ProcessVariableResponsibilityLinkBuilder
                        .aProcessVariableResponsibilityLink()
                        .from(link)
                        .withProjectId(projectUUID)
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<ResponsibilitySystemConstraintLink> copyResponsibilitySystemConstraintLinks(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getResponsibilitySystemConstraintLinks()
                .stream()
                .map(link -> ResponsibilitySystemConstraintLink.ResponsibilitySystemConstraintLinkBuilder
                        .aResponsibilitySystemConstraintLink()
                        .from(link)
                        .withProjectId(projectUUID)
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<ProjectEntityLastId> copyProjectEntityLastIds(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getProjectEntityLastIds()
                .stream()
                .map(lastId -> ProjectEntityLastId.ProjectEntityLastIdBuilder
                        .aProjectEntityLastId()
                        .from(lastId)
                        .withProjectId(projectUUID)
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<SubHazardLastId> copySubHazardLastIds(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getSubHazardLastIds()
                .stream()
                .map(lastId -> SubHazardLastId.SubHazardLastIdBuilder
                        .aSubHazardLastId()
                        .from(lastId)
                        .withProjectId(projectUUID)
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<SubSystemConstraintLastId> copySubSystemConstraintLastIds(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getSubSystemConstraintLastIds()
                .stream()
                .map(lastId -> SubSystemConstraintLastId.SubSystemConstraintLastIdBuilder
                        .aSubSystemConstraintLastId()
                        .from(lastId)
                        .withProjectId(projectUUID)
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<UnsafeControlActionLastId> copyUnsafeControlActionLastIds(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getUnsafeControlActionLastIds()
                .stream()
                .map(lastId -> UnsafeControlActionLastId.UnsafeControlActionLastIdBuilder
                        .anUnsafeControlActionLastId()
                        .from(lastId)
                        .withProjectId(projectUUID)
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<RuleLastId> copyRuleLastIds(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getRuleLastIds()
                .stream()
                .map(lastId -> RuleLastId.RuleLastIdBuilder
                        .aRuleLastId()
                        .from(lastId)
                        .withProjectId(projectUUID)
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<ConversionLastId> copyConversionLastIds(
            UUID projectUUID, XStamppProject xstamppProject) {
        return xstamppProject.getConversionLastIds()
                .stream()
                .map(lastId -> ConversionLastId.ConversionLastIdBuilder
                        .aConversionLastId()
                        .from(lastId)
                        .withProjectId(projectUUID)
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
