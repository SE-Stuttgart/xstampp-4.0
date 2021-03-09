package de.xstampp.service.project.service.data.eclipse_project.mapping;

import de.xstampp.service.project.data.dto.eclipse_project.EclipseProjectDTO;
import de.xstampp.service.project.data.dto.eclipse_project.controlAction.ControlAction;
import de.xstampp.service.project.data.dto.eclipse_project.controlAction.CorrespondingSafetyConstraint;
import de.xstampp.service.project.data.dto.eclipse_project.controlAction.UnsafeControlActionType;
import de.xstampp.service.project.data.entity.ControllerConstraint;
import de.xstampp.service.project.data.entity.EntityDependentKey;
import de.xstampp.service.project.data.entity.UnsafeControlAction;
import de.xstampp.service.project.data.entity.lastId.UnsafeControlActionLastId;
import de.xstampp.service.project.service.data.XStamppProject.XStamppProject;

import java.util.*;

class UnsafeControlActionMapper {

    static void mapToUnsafeControlActions(EclipseProjectDTO eclipseProjectDTO, XStamppProject xstamppProject) {
        UUID projectId = xstamppProject.getProjectUUID();
        List<UnsafeControlAction> unsafeControlActions = new LinkedList<>();
        List<ControllerConstraint> controllerConstraints = new LinkedList<>();

        int controlActionId = 1;

        if (Objects.isNull(eclipseProjectDTO.getControlActionController())) {
            return;
        }

        List<ControlAction> eclipseControlActions = Optional
                .ofNullable(eclipseProjectDTO.getControlActionController().getControlActions())
                .orElse(new ArrayList<>());
        for (ControlAction eclipseControlAction: eclipseControlActions) {

            int unsafeControlActionId = 0;

            for (de.xstampp.service.project.data.dto.eclipse_project.controlAction.UnsafeControlAction eclipseUnsafeControlAction
                    : Optional.ofNullable(eclipseControlAction.getUnsafeControlActions()).orElse(new ArrayList<>())) {

                unsafeControlActions.add(UnsafeControlAction.UnsafeControlActionBuilder
                        .anUnsafeControlAction()
                        .withId(new EntityDependentKey(projectId, controlActionId, ++unsafeControlActionId))
                        .withName(getOrElse(eclipseUnsafeControlAction.getTitle()))
                        .withDescription(getOrElse(eclipseUnsafeControlAction.getDescription()))
                        .withCategory(getUnsafeControlActionType(eclipseUnsafeControlAction.getType()))
                        .build()
                );

                CorrespondingSafetyConstraint correspondingSafetyConstraint =
                        eclipseUnsafeControlAction.getCorrespondingSafetyConstraint();
                controllerConstraints.add(ControllerConstraint.ControllerConstraintBuilder
                        .aControllerConstraint()
                        .withId(new EntityDependentKey(projectId, controlActionId, unsafeControlActionId))
                        .withName(getOrElse(correspondingSafetyConstraint.getTitle()))
                        .withDescription(getOrElse(correspondingSafetyConstraint.getDescription()))
                        .build());
            }

            if (unsafeControlActionId > 0)
                xstamppProject.getUnsafeControlActionLastIds()
                        .add(new UnsafeControlActionLastId(projectId, controlActionId++, unsafeControlActionId));

        }

        xstamppProject.setUnsafeControlActions(unsafeControlActions);
        xstamppProject.setControllerConstraints(controllerConstraints);
    }

    private static String getUnsafeControlActionType(UnsafeControlActionType type) {
        switch (type) {
            case NOT_GIVEN: return "not provided";
            case GIVEN_INCORRECTLY: return "provided";
            case WRONG_TIMING: return " too early or too late"; // FIXME: leading space is necessary
            case STOPPED_TOO_SOON: return "stopped too soon or applied too long";
            default: return "unknown type";
        }
    }

    private static String getOrElse(String str) {
        if (Objects.isNull(str) || str.isBlank() || str.trim().equals("N/A"))
            return "n/a";
        return str;
    }
}
