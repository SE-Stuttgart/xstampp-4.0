package de.xstampp.service.project.service.data.eclipse_project.mapping;

import de.xstampp.service.project.data.dto.eclipse_project.EclipseProjectDTO;
import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.control_structure.ControlAction;
import de.xstampp.service.project.data.entity.lastId.ProjectEntityLastId;
import de.xstampp.service.project.service.data.XStamppProject.XStamppProject;

import java.util.*;

class ControlActionMapper {

    static void mapToControlActions(EclipseProjectDTO eclipseProjectDTO, XStamppProject xstamppProject) {
        UUID projectId = xstamppProject.getProjectUUID();
        List<ControlAction> controlActions = new LinkedList<>();

        if (Objects.isNull(eclipseProjectDTO.getControlActionController())) {
            return;
        }

        int controlActionId = 0;

        List<de.xstampp.service.project.data.dto.eclipse_project.controlAction.ControlAction> eclipseControlActions =
                Optional.ofNullable(eclipseProjectDTO.getControlActionController().getControlActions())
                        .orElse(new ArrayList<>());
        for (de.xstampp.service.project.data.dto.eclipse_project.controlAction.ControlAction eclipseControlAction
                : eclipseControlActions) {
            controlActions.add(ControlAction.ControlActionBuilder
                    .aControlAction()
                    .withId(new ProjectDependentKey(projectId, ++controlActionId))
                    .withName(eclipseControlAction.getTitle())
                    .withDescription(eclipseControlAction.getDescription())
                    .build()
            );
        }


        xstamppProject.setControlActions(controlActions);
        if (controlActionId > 0)
            xstamppProject.getProjectEntityLastIds()
                    .add(new ProjectEntityLastId(projectId, "control_action", controlActionId));
    }
}
