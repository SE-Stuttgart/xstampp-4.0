package de.xstampp.service.project.service.data.eclipse_project;

import de.xstampp.service.project.data.dto.eclipse_project.EclipseProjectDTO;
import de.xstampp.service.project.service.data.XStamppProject.XStamppProject;
import de.xstampp.service.project.service.data.eclipse_project.mapping.EclipseProjectMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EclipseProjectService {

    public XStamppProject mapToXStamppProject(UUID projectId, EclipseProjectDTO eclipseProjectDTO) {

        var eclipseProjectMapper = new EclipseProjectMapper(eclipseProjectDTO,
                XStamppProject.XStamppProjectBuilder
                        .aXStamppProject()
                        .withProjectUUID(projectId)
                        .build());

        return eclipseProjectMapper
                .mapSystemDescription()
                .mapLosses()
                .mapHazards()
                .mapSystemConstraints()
                .mapControlStructure()
                .mapControlActions()
                .mapUnsafeControlActions()
                .getXStamppProject();
    }
}
