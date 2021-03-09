package de.xstampp.service.project.service.data.eclipse_project.mapping;

import de.xstampp.service.project.data.dto.eclipse_project.EclipseProjectDTO;
import de.xstampp.service.project.service.data.XStamppProject.XStamppProject;

public class EclipseProjectMapper {

    private EclipseProjectDTO eclipseProjectDTO;

    private XStamppProject xstamppProject;

    private EclipseProjectMapper() {
    }

    public EclipseProjectMapper(EclipseProjectDTO eclipseProjectDTO, XStamppProject xstamppProject) {
        this.xstamppProject = xstamppProject;
        this.eclipseProjectDTO = eclipseProjectDTO;
    }

    public XStamppProject getXStamppProject() {
        return xstamppProject;
    }

    public EclipseProjectMapper mapSystemDescription() {
        SystemDescriptionMapper.mapToSystemDescription(eclipseProjectDTO, xstamppProject);
        return this;
    }

    public EclipseProjectMapper mapLosses() {
        LossMapper.mapToLosses(eclipseProjectDTO, xstamppProject);
        return this;
    }

    public EclipseProjectMapper mapHazards() {
        HazardMapper.mapToHazards(eclipseProjectDTO, xstamppProject);
        return this;
    }

    public EclipseProjectMapper mapSystemConstraints() {
        SystemConstraintMapper.mapToSystemConstraints(eclipseProjectDTO, xstamppProject);
        return this;
    }

    public EclipseProjectMapper mapControlStructure() {
        ControlStructureMapper.mapToControlStructure(eclipseProjectDTO, xstamppProject);
        return this;
    }

    public EclipseProjectMapper mapControlActions() {
        ControlActionMapper.mapToControlActions(eclipseProjectDTO, xstamppProject);
        return this;
    }

    public EclipseProjectMapper mapUnsafeControlActions() {
        UnsafeControlActionMapper.mapToUnsafeControlActions(eclipseProjectDTO, xstamppProject);
        return this;
    }
}
