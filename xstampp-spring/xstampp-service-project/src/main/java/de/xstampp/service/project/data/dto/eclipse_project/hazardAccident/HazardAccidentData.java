package de.xstampp.service.project.data.dto.eclipse_project.hazardAccident;

import de.xstampp.service.project.data.dto.eclipse_project.link.LinkController;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.NONE)
public class HazardAccidentData {

    @XmlElementWrapper(name = "accidents")
    @XmlElement(name = "accident")
    private ArrayList<Accident> accidents;

    @XmlElementWrapper(name = "hazards")
    @XmlElement(name = "hazard")
    private ArrayList<Hazard> hazards;

    @XmlElementWrapper(name = "links")
    @XmlElement(name = "link")
    private ArrayList<HazAccLink> links;

    @XmlAttribute(name = "useSeverity")
    private Boolean useSeverity;

    @XmlAttribute(name = "useHazardConstraints")
    private Boolean useHazardConstraints;

    @XmlElement(name = "HAZ_ACC_LINK")
    private LinkController linkController;

    public List<Accident> getAccidents() {
        return accidents;
    }

    public void setAccidents(ArrayList<Accident> accidents) {
        this.accidents = accidents;
    }

    public List<Hazard> getHazards() {
        return hazards;
    }

    public void setHazards(ArrayList<Hazard> hazards) {
        this.hazards = hazards;
    }

    public List<HazAccLink> getLinks() {
        return links;
    }

    public void setLinks(ArrayList<HazAccLink> links) {
        this.links = links;
    }

    public Boolean getUseSeverity() {
        return useSeverity;
    }

    public void setUseSeverity(Boolean useSeverity) {
        this.useSeverity = useSeverity;
    }

    public Boolean getUseHazardConstraints() {
        return useHazardConstraints;
    }

    public void setUseHazardConstraints(Boolean useHazardConstraints) {
        this.useHazardConstraints = useHazardConstraints;
    }

    public LinkController getLinkController() {
        return linkController;
    }

    public void setLinkController(LinkController linkController) {
        this.linkController = linkController;
    }
}
