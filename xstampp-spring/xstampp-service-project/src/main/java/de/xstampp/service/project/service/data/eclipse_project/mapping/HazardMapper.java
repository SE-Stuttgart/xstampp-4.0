package de.xstampp.service.project.service.data.eclipse_project.mapping;

import de.xstampp.service.project.data.dto.eclipse_project.EclipseProjectDTO;
import de.xstampp.service.project.data.entity.Hazard;
import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.lastId.ProjectEntityLastId;
import de.xstampp.service.project.service.data.XStamppProject.XStamppProject;

import java.util.*;

class HazardMapper {

    static void mapToHazards(EclipseProjectDTO eclipseProjectDTO, XStamppProject xstamppProject) {
        UUID projectId = xstamppProject.getProjectUUID();
        List<Hazard> hazards = new LinkedList<>();

        if (Objects.isNull(eclipseProjectDTO.getSystemDescriptionData())) {
            return;
        }

        int hazardId = 0;

        List<de.xstampp.service.project.data.dto.eclipse_project.hazardAccident.Hazard> eclipseHazards =
                Optional.ofNullable(eclipseProjectDTO.getHazardAccidentData().getHazards())
                        .orElse(new ArrayList<>());
        for (de.xstampp.service.project.data.dto.eclipse_project.hazardAccident.Hazard eclipseHazard : eclipseHazards) {
            hazards.add(Hazard.HazardBuilder
                    .aHazard()
                    .withId(new ProjectDependentKey(projectId, ++hazardId))
                    .withName(eclipseHazard.getTitle())
                    .withDescription(eclipseHazard.getDescription())
                    .build()
            );
        }

        xstamppProject.setHazards(hazards);
        if (hazardId > 0)
            xstamppProject.getProjectEntityLastIds()
                    .add(new ProjectEntityLastId(projectId, "hazard", hazardId));
    }
}
