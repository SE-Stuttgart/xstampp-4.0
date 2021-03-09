package de.xstampp.service.project.data.dto.eclipse_project;

import de.xstampp.service.project.data.dto.eclipse_project.causalFactor.CausalFactorController;
import de.xstampp.service.project.data.dto.eclipse_project.controlAction.ControlActionController;
import de.xstampp.service.project.data.dto.eclipse_project.controlStructure.ControlStructureData;
import de.xstampp.service.project.data.dto.eclipse_project.controlStructure.components.Component;
import de.xstampp.service.project.data.dto.eclipse_project.exportInformation.ExportInformation;
import de.xstampp.service.project.data.dto.eclipse_project.extendedData.ExtendedData;
import de.xstampp.service.project.data.dto.eclipse_project.hazardAccident.HazardAccidentData;
import de.xstampp.service.project.data.dto.eclipse_project.link.LinkController;
import de.xstampp.service.project.data.dto.eclipse_project.project.ProjectData;
import de.xstampp.service.project.data.dto.eclipse_project.systemDescription.SystemDescriptionData;

import javax.xml.bind.annotation.*;
import java.util.UUID;

/**
 * This is a simplified project model of the XSTAMPP v3 Eclipse Plugin.
 */
@XmlRootElement(name = "dataModelController", namespace = "astpa.model")
@XmlAccessorType(XmlAccessType.NONE)
public class EclipseProjectDTO {

    private static final String HAZ = "haz";
    private static final String HAZX = "hazx";

    private ExportInformation exportInformation;

    @XmlAttribute(name = "version")
    private String astpaVersion;

    @XmlAttribute(name = "userSystemId")
    private UUID userSystemId;

    @XmlAttribute(name = "userSystemName")
    private String userSystemName;

    @XmlAttribute(name = "exclusiveUserId")
    private UUID exclusiveUserId;

    @XmlAttribute(name = "projectId")
    private UUID projectId;

    @XmlElement(name = "projectdata", required = true)
    private ProjectData projectData;

    @XmlElement(name = "hazacc")
    private HazardAccidentData hazardAccidentData;

    @XmlElement(name = "sds")
    private SystemDescriptionData systemDescriptionData;

    @XmlElement(name = "controlstructure")
    private ControlStructureData controlStructureData;

    @XmlElement(name = "ignoreLTLValue")
    private Component ignoreLtlValue;

    @XmlElement(name = "cac")
    private ControlActionController controlActionController;

    @XmlElement(name = "causalfactor")
    private CausalFactorController causalFactorController;

    @XmlElement(name = "extendedData")
    private ExtendedData extendedData;

    // FIXME: parse linkMap into linkController not possible via original XSTAMPP v3 implementation.
    @XmlElement
    private LinkController linkController;

    public static String getHAZ() {
        return HAZ;
    }

    public static String getHAZX() {
        return HAZX;
    }

    public ExportInformation getExportInformation() {
        return exportInformation;
    }

    public void setExportInformation(ExportInformation exportInformation) {
        this.exportInformation = exportInformation;
    }

    public String getAstpaVersion() {
        return astpaVersion;
    }

    public void setAstpaVersion(String astpaVersion) {
        this.astpaVersion = astpaVersion;
    }

    public UUID getUserSystemId() {
        return userSystemId;
    }

    public void setUserSystemId(UUID userSystemId) {
        this.userSystemId = userSystemId;
    }

    public String getUserSystemName() {
        return userSystemName;
    }

    public void setUserSystemName(String userSystemName) {
        this.userSystemName = userSystemName;
    }

    public UUID getExclusiveUserId() {
        return exclusiveUserId;
    }

    public void setExclusiveUserId(UUID exclusiveUserId) {
        this.exclusiveUserId = exclusiveUserId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public ProjectData getProjectData() {
        return projectData;
    }

    public void setProjectData(ProjectData projectData) {
        this.projectData = projectData;
    }

    public HazardAccidentData getHazardAccidentData() {
        return hazardAccidentData;
    }

    public void setHazardAccidentData(HazardAccidentData hazardAccidentData) {
        this.hazardAccidentData = hazardAccidentData;
    }

    public SystemDescriptionData getSystemDescriptionData() {
        return systemDescriptionData;
    }

    public void setSystemDescriptionData(SystemDescriptionData systemDescriptionData) {
        this.systemDescriptionData = systemDescriptionData;
    }

    public ControlStructureData getControlStructureData() {
        return controlStructureData;
    }

    public void setControlStructureData(ControlStructureData controlStructureData) {
        this.controlStructureData = controlStructureData;
    }

    public Component getIgnoreLtlValue() {
        return ignoreLtlValue;
    }

    public void setIgnoreLtlValue(Component ignoreLtlValue) {
        this.ignoreLtlValue = ignoreLtlValue;
    }

    public ControlActionController getControlActionController() {
        return controlActionController;
    }

    public void setControlActionController(ControlActionController controlActionController) {
        this.controlActionController = controlActionController;
    }

    public CausalFactorController getCausalFactorController() {
        return causalFactorController;
    }

    public void setCausalFactorController(CausalFactorController causalFactorController) {
        this.causalFactorController = causalFactorController;
    }

    public ExtendedData getExtendedData() {
        return extendedData;
    }

    public void setExtendedData(ExtendedData extendedData) {
        this.extendedData = extendedData;
    }

    public LinkController getLinkController() {
        return linkController;
    }

    public void setLinkController(LinkController linkController) {
        this.linkController = linkController;
    }
}
