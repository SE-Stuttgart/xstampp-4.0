package de.xstampp.service.project.data.dto.eclipse_project.causalFactor;

import de.xstampp.service.project.data.dto.eclipse_project.link.LinkController;

import javax.xml.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@XmlAccessorType(XmlAccessType.NONE)
public class CausalFactorController {

    @XmlElementWrapper(name = "causalComponents")
    @XmlElement(name = "causalComponent")
    private Map<UUID, CausalCSComponent> causalComponents;

    @XmlAttribute(name = "useScenarios")
    private boolean useScenarios;

    @XmlAttribute(name = "switchUCAsPerFactorToFactorsPerUCA")
    private boolean switchUCAsPerFactorToFactorsPerUCA;

    @XmlElementWrapper(name = "causalSafetyConstraints")
    @XmlElement(name = "causalSafetyConstraint")
    private List<CausalSafetyConstraint> causalSafetyConstraints;

    @XmlElementWrapper(name = "causalFactors")
    @XmlElement(name = "causalFactor")
    private List<CausalFactor> causalFactors;

    private LinkController linkController;

    @XmlElementWrapper(name = "componentsList")
    @XmlElement(name = "component")
    private List<CausalCSComponent> componentsList;

    public Map<UUID, CausalCSComponent> getCausalComponents() {
        return causalComponents;
    }

    public void setCausalComponents(Map<UUID, CausalCSComponent> causalComponents) {
        this.causalComponents = causalComponents;
    }

    public boolean isUseScenarios() {
        return useScenarios;
    }

    public void setUseScenarios(boolean useScenarios) {
        this.useScenarios = useScenarios;
    }

    public boolean isSwitchUCAsPerFactorToFactorsPerUCA() {
        return switchUCAsPerFactorToFactorsPerUCA;
    }

    public void setSwitchUCAsPerFactorToFactorsPerUCA(boolean switchUCAsPerFactorToFactorsPerUCA) {
        this.switchUCAsPerFactorToFactorsPerUCA = switchUCAsPerFactorToFactorsPerUCA;
    }

    public List<CausalSafetyConstraint> getCausalSafetyConstraints() {
        return causalSafetyConstraints;
    }

    public void setCausalSafetyConstraints(List<CausalSafetyConstraint> causalSafetyConstraints) {
        this.causalSafetyConstraints = causalSafetyConstraints;
    }

    public List<CausalFactor> getCausalFactors() {
        return causalFactors;
    }

    public void setCausalFactors(List<CausalFactor> causalFactors) {
        this.causalFactors = causalFactors;
    }

    public LinkController getLinkController() {
        return linkController;
    }

    public void setLinkController(LinkController linkController) {
        this.linkController = linkController;
    }

    public List<CausalCSComponent> getComponentsList() {
        return componentsList;
    }

    public void setComponentsList(List<CausalCSComponent> componentsList) {
        this.componentsList = componentsList;
    }
}

