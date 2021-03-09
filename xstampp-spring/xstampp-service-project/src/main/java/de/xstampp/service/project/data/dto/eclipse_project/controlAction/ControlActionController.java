package de.xstampp.service.project.data.dto.eclipse_project.controlAction;

import de.xstampp.service.project.data.dto.eclipse_project.extendedData.RefinedSafetyRule;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.NONE)
public class ControlActionController  {

    @XmlElementWrapper(name = "controlactions")
    @XmlElement(name = "controlaction")
    private List<ControlAction> controlActions;

    @XmlElementWrapper(name = "links")
    @XmlElement(name = "link")
    private List<UCAHazLink> links;

    @XmlElementWrapper(name = "rules")
    @XmlElement(name = "rule")
    private List<RefinedSafetyRule> rules;

    @XmlAttribute(name = "nextUcaIndex")
    private Integer nextUcaIndex;

    @XmlAttribute(name = "nextCAIndex")
    private Integer nextCAIndex;

    @XmlElementWrapper(name = "ucaCustomHeaders")
    @XmlElement(name = "ucaHeader")
    private List<String> ucaCustomHeaders;

    public List<ControlAction> getControlActions() {
        return controlActions;
    }

    public void setControlActions(List<ControlAction> controlActions) {
        this.controlActions = controlActions;
    }

    public List<UCAHazLink> getLinks() {
        return links;
    }

    public void setLinks(List<UCAHazLink> links) {
        this.links = links;
    }

    public List<RefinedSafetyRule> getRules() {
        return rules;
    }

    public void setRules(List<RefinedSafetyRule> rules) {
        this.rules = rules;
    }

    public Integer getNextUcaIndex() {
        return nextUcaIndex;
    }

    public void setNextUcaIndex(Integer nextUcaIndex) {
        this.nextUcaIndex = nextUcaIndex;
    }

    public Integer getNextCAIndex() {
        return nextCAIndex;
    }

    public void setNextCAIndex(Integer nextCAIndex) {
        this.nextCAIndex = nextCAIndex;
    }

    public List<String> getUcaCustomHeaders() {
        return ucaCustomHeaders;
    }

    public void setUcaCustomHeaders(List<String> ucaCustomHeaders) {
        this.ucaCustomHeaders = ucaCustomHeaders;
    }
}
