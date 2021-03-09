package de.xstampp.service.project.service.data.eclipse_project.mapping;

import de.xstampp.service.project.data.dto.eclipse_project.EclipseProjectDTO;
import de.xstampp.service.project.data.dto.eclipse_project.controlStructure.ControlStructureData;
import de.xstampp.service.project.data.dto.eclipse_project.controlStructure.components.CSConnection;
import de.xstampp.service.project.data.dto.eclipse_project.controlStructure.components.Component;
import de.xstampp.service.project.data.entity.*;
import de.xstampp.service.project.data.entity.control_structure.*;
import de.xstampp.service.project.data.entity.lastId.ProjectEntityLastId;
import de.xstampp.service.project.service.data.XStamppProject.XStamppProject;

import java.util.*;

class ControlStructureMapper {

    static void mapToControlStructure(EclipseProjectDTO eclipseProjectDTO, XStamppProject xstamppProject) {
        UUID projectId = xstamppProject.getProjectUUID();

        List<UUID> validConnections = new LinkedList<>();

        int controllerId = 0;
        int actuatorId = 0;
        int sensorId = 0;
        int controlledProcessId = 0;
        int processModelId = 0;
        int processVariableId = 0;
        int feedbackId = 0;

        ControlStructureData controlStructureData = eclipseProjectDTO.getControlStructureData();
        if (Objects.isNull(controlStructureData)) {
            return;
        }

        Component rootComponent = controlStructureData.getRootComponents().get(0);

        List<Component> rootChildren = Optional
                .ofNullable(rootComponent.getChildren())
                .orElse(new ArrayList<>());

        for (Component rootChild : rootChildren) {
            switch (rootChild.getComponentType()) {
                case "CONTROLLER":
                    xstamppProject.getControllers().add(Controller.ControllerBuilder
                            .aController()
                            .withId(new ProjectDependentKey(projectId, ++controllerId))
                            .withBoxId(rootChild.getId().toString())
                            .withName(rootChild.getText())
                            .withDescription(rootChild.getComment())
                            .build());

                    xstamppProject.getBoxes().add(Box.BoxBuilder
                            .aBox()
                            .withProjectId(projectId)
                            .withId(rootChild.getId().toString())
                            .withX(rootChild.getLayout().getX())
                            .withY(rootChild.getLayout().getY())
                            .withHeight(rootChild.getLayout().getHeight())
                            .withWidth(rootChild.getLayout().getWidth())
                            .withBoxType("Controller")
                            .withName(rootChild.getText())
                            .build());

                    validConnections.add(rootChild.getId());

                    List<Component> controllerChildren = Optional
                            .ofNullable(rootChild.getChildren())
                            .orElse(Collections.emptyList());
                    for (Component controllerChild : controllerChildren) {
                        if ("PROCESS_MODEL".equals(controllerChild.getComponentType())) {
                            xstamppProject.getProcessModels().add(ProcessModel.ProcessModelBuilder
                                    .aProcessModel()
                                    .withId(new ProjectDependentKey(projectId, ++processModelId))
                                    .withControllerId(controllerId)
                                    .withName(controllerChild.getText())
                                    .withDescription(controllerChild.getComment())
                                    .build());

                            List<Component> processModelChildren = Optional
                                    .ofNullable(controllerChild.getChildren())
                                    .orElse(Collections.emptyList());
                            for (Component processModelChild : processModelChildren) {
                                if ("PROCESS_VARIABLE".equals(processModelChild.getComponentType())) {
                                    xstamppProject.getProcessVariables().add(ProcessVariable.ProcessVariableBuilder
                                            .aProcessVariable()
                                            .withId(new ProjectDependentKey(projectId, ++processVariableId))
                                            .withVariable_type("DISCREET")
                                            .withName(processModelChild.getText())
                                            .withDescription(processModelChild.getComment())
                                            .build());

                                    List<Component> processVariableChildren = Optional
                                            .ofNullable(processModelChild.getChildren())
                                            .orElse(Collections.emptyList());
                                    for (Component processVariableChild : processVariableChildren) {
                                        if ("PROCESS_VALUE".equals(processVariableChild.getComponentType())) {
                                            xstamppProject.getDiscreteProcessVariableValues()
                                                    .add(DiscreteProcessVariableValue.DiscreteProcessVariableValueBuilder
                                                            .aDiscreteProcessVariableValue()
                                                            .withProjectId(projectId)
                                                            .withProcessVariableId(processVariableId)
                                                            .withVariableValue(processVariableChild.getText())
                                                            .build());
                                        }
                                    }

                                    xstamppProject.getProcessModelProcessVariableLinks()
                                            .add(new ProcessModelProcessVariableLink(projectId, processModelId,
                                                    processVariableId, ""));
                                }
                            }
                        }
                    }
                    break;
                case "ACTUATOR":
                    xstamppProject.getActuators().add(Actuator.ActuatorBuilder
                            .anActuator()
                            .withId(new ProjectDependentKey(projectId, ++actuatorId))
                            .withBoxId(rootChild.getId().toString())
                            .withName(rootChild.getText())
                            .withDescription(rootChild.getComment())
                            .build());

                    xstamppProject.getBoxes().add(Box.BoxBuilder
                            .aBox()
                            .withProjectId(projectId)
                            .withId(rootChild.getId().toString())
                            .withX(rootChild.getLayout().getX())
                            .withY(rootChild.getLayout().getY())
                            .withHeight(rootChild.getLayout().getHeight())
                            .withWidth(rootChild.getLayout().getWidth())
                            .withBoxType("Actuator")
                            .withName(rootChild.getText())
                            .build());

                    validConnections.add(rootChild.getId());

                    break;
                case "SENSOR":
                    xstamppProject.getSensors().add(Sensor.SensorBuilder
                            .aSensor()
                            .withId(new ProjectDependentKey(projectId, ++sensorId))
                            .withBoxId(rootChild.getId().toString())
                            .withName(rootChild.getText())
                            .withDescription(rootChild.getComment())
                            .build());

                    xstamppProject.getBoxes().add(Box.BoxBuilder
                            .aBox()
                            .withProjectId(projectId)
                            .withId(rootChild.getId().toString())
                            .withX(rootChild.getLayout().getX())
                            .withY(rootChild.getLayout().getY())
                            .withHeight(rootChild.getLayout().getHeight())
                            .withWidth(rootChild.getLayout().getWidth())
                            .withBoxType("Sensor")
                            .withName(rootChild.getText())
                            .build());

                    validConnections.add(rootChild.getId());

                    break;
                case "CONTROLLED_PROCESS":
                    xstamppProject.getControlledProcesses().add(ControlledProcess.ControlledProcessBuilder
                            .aControlledProcess()
                            .withId(new ProjectDependentKey(projectId, ++controlledProcessId))
                            .withBoxId(rootChild.getId().toString())
                            .withName(rootChild.getText())
                            .withDescription(rootChild.getComment())
                            .build());

                    xstamppProject.getBoxes().add(Box.BoxBuilder
                            .aBox()
                            .withProjectId(projectId)
                            .withId(rootChild.getId().toString())
                            .withX(rootChild.getLayout().getX())
                            .withY(rootChild.getLayout().getY())
                            .withHeight(rootChild.getLayout().getHeight())
                            .withWidth(rootChild.getLayout().getWidth())
                            .withBoxType("ControlledProcess")
                            .withName(rootChild.getText())
                            .build());

                    validConnections.add(rootChild.getId());

                    break;
                case "FEEDBACK":
                    xstamppProject.getFeedback().add(Feedback.FeedbackBuilder
                            .aFeedback()
                            .withId(new ProjectDependentKey(projectId, ++feedbackId))
                            .withName(rootChild.getText())
                            .withDescription(rootChild.getComment())
                            .build());

                    xstamppProject.getBoxes().add(Box.BoxBuilder
                            .aBox()
                            .withProjectId(projectId)
                            .withId(rootChild.getId().toString())
                            .withX(rootChild.getLayout().getX())
                            .withY(rootChild.getLayout().getY())
                            .withHeight(rootChild.getLayout().getHeight())
                            .withWidth(rootChild.getLayout().getWidth())
                            .withBoxType("TextBox")
                            .withName(rootChild.getText())
                            .build());
                    break;
                case "DASHEDBOX":
                    xstamppProject.getBoxes().add(Box.BoxBuilder
                            .aBox()
                            .withProjectId(projectId)
                            .withId(rootChild.getId().toString())
                            .withX(rootChild.getLayout().getX())
                            .withY(rootChild.getLayout().getY())
                            .withHeight(rootChild.getLayout().getHeight())
                            .withWidth(rootChild.getLayout().getWidth())
                            .withBoxType("DashedBox")
                            .withName(rootChild.getText())
                            .build());
                    break;
                case "CONTROLACTION":
                case "TEXTFIELD":
                default:
                    xstamppProject.getBoxes().add(Box.BoxBuilder
                            .aBox()
                            .withProjectId(projectId)
                            .withId(rootChild.getId().toString())
                            .withX(rootChild.getLayout().getX())
                            .withY(rootChild.getLayout().getY())
                            .withHeight(rootChild.getLayout().getHeight())
                            .withWidth(rootChild.getLayout().getWidth())
                            .withBoxType("TextBox")
                            .withName(rootChild.getText())
                            .build());
                    break;
            }
        }

        List<CSConnection> csConnections = Optional
                .ofNullable(controlStructureData.getConnections())
                .orElse(new ArrayList<>());
        for (CSConnection csConnection : csConnections) {
            if (hasValidSourceAndTarget(csConnection, validConnections)) {
                xstamppProject.getArrows().add(Arrow.ArrowBuilder
                        .anArrow()
                        .withProjectId(projectId)
                        .withId(csConnection.getId().toString())
                        .withSource(csConnection.getSourceAnchor().getOwnerId().toString())
                        .withDestination(csConnection.getTargetAnchor().getOwnerId().toString())
                        .withLabel("")
                        .withType("Output")
                        .build());
            }
        }

        xstamppProject.getProjectEntityLastIds()
                .add(new ProjectEntityLastId(projectId, "controller", controllerId));
        xstamppProject.getProjectEntityLastIds()
                .add(new ProjectEntityLastId(projectId, "actuator", actuatorId));
        xstamppProject.getProjectEntityLastIds()
                .add(new ProjectEntityLastId(projectId, "sensor", sensorId));
        xstamppProject.getProjectEntityLastIds()
                .add(new ProjectEntityLastId(projectId, "controlled_process", controlledProcessId));
        xstamppProject.getProjectEntityLastIds()
                .add(new ProjectEntityLastId(projectId, "process_model", processModelId));
        xstamppProject.getProjectEntityLastIds()
                .add(new ProjectEntityLastId(projectId, "process_variable", processVariableId));
        xstamppProject.getProjectEntityLastIds()
                .add(new ProjectEntityLastId(projectId, "feedback", feedbackId));
    }

    private static boolean hasValidSourceAndTarget(CSConnection csConnection, List<UUID> validConnections) {
        boolean targetExists = validConnections.contains(csConnection.getTargetAnchor().getOwnerId());
        boolean sourceExists = validConnections.contains(csConnection.getSourceAnchor().getOwnerId());
        return targetExists && sourceExists;
    }
}
