package de.xstampp.service.project.service.data.eclipse_project.mapping;

import de.xstampp.service.project.data.dto.eclipse_project.EclipseProjectDTO;
import de.xstampp.service.project.data.dto.eclipse_project.systemDescription.SafetyConstraint;
import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.SystemConstraint;
import de.xstampp.service.project.data.entity.lastId.ProjectEntityLastId;
import de.xstampp.service.project.service.data.XStamppProject.XStamppProject;

import java.util.*;

class SystemConstraintMapper {

    static void mapToSystemConstraints(EclipseProjectDTO eclipseProjectDTO, XStamppProject xstamppProject) {
        UUID projectId = xstamppProject.getProjectUUID();
        List<SystemConstraint> systemConstraints = new LinkedList<>();

        if (Objects.isNull(eclipseProjectDTO.getSystemDescriptionData())) {
            return;
        }

        int systemConstraintId = 0;

        List<SafetyConstraint> safetyConstraints = Optional
                .ofNullable(eclipseProjectDTO.getSystemDescriptionData().getSafetyConstraints())
                .orElse(new ArrayList<>());
        for (SafetyConstraint safetyConstraint : safetyConstraints) {
            systemConstraints.add(SystemConstraint.SystemConstraintBuilder
                    .aSystemConstraint()
                    .withId(new ProjectDependentKey(projectId, ++systemConstraintId))
                    .withName(safetyConstraint.getTitle())
                    .withDescription(safetyConstraint.getDescription())
                    .build()
            );
        }


        xstamppProject.setSystemConstraints(systemConstraints);
        if (systemConstraintId > 0)
            xstamppProject.getProjectEntityLastIds()
                    .add(new ProjectEntityLastId(projectId, "system_constraint", systemConstraintId));
    }
}
