package de.xstampp.service.project.service.data.eclipse_project.mapping;

import de.xstampp.service.project.data.dto.eclipse_project.EclipseProjectDTO;
import de.xstampp.service.project.data.dto.eclipse_project.abstracts.AbstractDataModel;
import de.xstampp.service.project.data.dto.eclipse_project.hazardAccident.Accident;
import de.xstampp.service.project.data.dto.eclipse_project.systemDescription.SystemGoal;
import de.xstampp.service.project.data.entity.Loss;
import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.lastId.ProjectEntityLastId;
import de.xstampp.service.project.service.data.XStamppProject.XStamppProject;

import java.util.*;

class LossMapper {

    static void mapToLosses(EclipseProjectDTO eclipseProjectDTO, XStamppProject xstamppProject) {
        UUID projectId = xstamppProject.getProjectUUID();
        List<Loss> losses = new LinkedList<>();

        int lossId = 0;

        if (Objects.isNull(eclipseProjectDTO.getSystemDescriptionData())) {
            return;
        }

        List<SystemGoal> systemGoals = Optional.ofNullable(eclipseProjectDTO.getSystemDescriptionData().getSystemGoals())
                .orElse(new ArrayList<>());
        for (SystemGoal systemGoal : systemGoals) {
            losses.add(Loss.LossBuilder
                    .aLoss()
                    .withId(new ProjectDependentKey(projectId, ++lossId))
                    .withName(formatTitle("System Goal", systemGoal))
                    .withDescription(formatDescription(systemGoal))
                    .build()
            );
        }

        List<Accident> accidents = Optional.ofNullable(eclipseProjectDTO.getHazardAccidentData().getAccidents())
                .orElse(new ArrayList<>());
        for (Accident accident : accidents) {
            losses.add(Loss.LossBuilder
                    .aLoss()
                    .withId(new ProjectDependentKey(projectId, ++lossId))
                    .withName(formatTitle("Accident", accident))
                    .withDescription(accident.getDescription())
                    .build()
            );
        }

        xstamppProject.setLosses(losses);
        xstamppProject.getProjectEntityLastIds()
                .add(new ProjectEntityLastId(projectId, "loss", lossId));
    }


    private static String formatTitle(String title, AbstractDataModel abstractDataModel) {
        return String.format("%s:  %s", title, abstractDataModel.getTitle());
    }

    private static String formatDescription(SystemGoal systemGoal) {
        return String.format("%s\n\nSystem Goal Description: %s",
                "(Please negate the System Goals description to convert it into a Loss.)", systemGoal.getDescription());
    }
}
