package de.xstampp.service.project.service.data.eclipse_project.mapping;

import de.xstampp.service.project.data.dto.eclipse_project.EclipseProjectDTO;
import de.xstampp.service.project.data.dto.eclipse_project.abstracts.AbstractDataModel;
import de.xstampp.service.project.data.dto.eclipse_project.systemDescription.DesignRequirement;
import de.xstampp.service.project.data.dto.eclipse_project.systemDescription.DesignRequirementStep1;
import de.xstampp.service.project.data.dto.eclipse_project.systemDescription.DesignRequirementStep2;
import de.xstampp.service.project.data.entity.SystemDescription;
import de.xstampp.service.project.service.data.XStamppProject.XStamppProject;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

class SystemDescriptionMapper {

    static void mapToSystemDescription(EclipseProjectDTO eclipseProjectDTO, XStamppProject xstamppProject) {

        String eclipseSystemDescription = Optional.ofNullable(eclipseProjectDTO.getProjectData().getProjectDescription())
                .orElse("");

        if (Objects.isNull(eclipseProjectDTO.getSystemDescriptionData())) {
            xstamppProject.setSystemDescription(new SystemDescription(xstamppProject.getProjectUUID()));
            return;
        }

        List<DesignRequirement> designRequirements
                = Optional.ofNullable(eclipseProjectDTO.getSystemDescriptionData().getDesignRequirements()).
                orElse(new ArrayList<>());
        List<DesignRequirementStep1> designRequirementsStep1
                = Optional.ofNullable(eclipseProjectDTO.getSystemDescriptionData().getDesignRequirementsStep1()).
                orElse(new ArrayList<>());
        List<DesignRequirementStep2> designRequirementsStep2
                = Optional.ofNullable(eclipseProjectDTO.getSystemDescriptionData().getDesignRequirementsStep2()).
                orElse(new ArrayList<>());

        String description = new StringBuilder()
                .append(htmlBold(htmlUnderline("System Description:")))
                .append("<br><br>")
                .append(eclipseSystemDescription)
                .append("<br><br>")
                .append(formatDesignRequirements(designRequirements, 0))
                .append("<br>")
                .append(formatDesignRequirements(designRequirementsStep1, 1))
                .append("<br>")
                .append(formatDesignRequirements(designRequirementsStep2, 2))
                .toString();

        xstamppProject.setSystemDescription(SystemDescription.SystemDescriptionBuilder
                .aSystemDescription()
                .withProjectId(xstamppProject.getProjectUUID())
                .withDescription(description)
                .withLastEdited(Timestamp.from(Instant.now()))
                .build());
    }

    private static <T extends AbstractDataModel> String formatDesignRequirements(List<T> designRequirements, int step) {

        if (Objects.isNull(designRequirements))
            return "";

        StringBuilder stringBuilder = new StringBuilder("<p>");

        String stepNumber = step == 0 ? "" : step + "";
        stringBuilder.append(htmlBold(htmlUnderline("Design Requirements Step" + stepNumber +":")));

        for (T t : designRequirements) {
            String title = getHtmlDesignRequirementTitle(t.getTitle());
            stringBuilder
                    .append("<br>")
                    .append(title)
                    .append("<br>")
                    .append(t.getDescription())
                    .append("<br>");
        }

        return stringBuilder.toString();
    }

    private static String getHtmlDesignRequirementTitle(String str) {
        return String.format("%s:", htmlUnderline(str));
    }

    private static String htmlBold(String str) {
        return String.format("<b>%s</b>", str);
    }

    private static String htmlUnderline(String str) {
        return String.format("<u>%s</u>", str);
    }
}
