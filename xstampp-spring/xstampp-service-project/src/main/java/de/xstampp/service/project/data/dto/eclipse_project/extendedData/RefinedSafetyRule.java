package de.xstampp.service.project.data.dto.eclipse_project.extendedData;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.UUID;

@XmlRootElement(name = "rule")
public class RefinedSafetyRule {

    @XmlElement(name = "ruleID")
    private UUID ruleId;

    @XmlElement(name = "ruleNR")
    private int ruleNumber;

    @XmlElement(name = "criticalCombies")
    private String criticalCombies;

    @XmlElement(name = "RefinedSafetyRule")
    private String RefinedSafetyRule;

    @XmlElement(name = "refinedUCA")
    private String refinedUCA;

    @XmlElement(name = "refinedSC")
    private String refinedSC;

    @XmlElement(name = "ltlProp")
    private String ltlProp;

    @XmlElement(name = "type")
    private String type;

    @XmlElement(name = "links")
    private String links;

    @XmlElementWrapper(name = "relatedUCAIDs")
    @XmlElement(name = "ucaID")
    private List<UUID> ucaID;

    @XmlElement(name = "relatedCaID")
    private UUID relatedCaID;

    public UUID getRuleId() {
        return ruleId;
    }

    public void setRuleId(UUID ruleId) {
        this.ruleId = ruleId;
    }

    public int getRuleNumber() {
        return ruleNumber;
    }

    public void setRuleNumber(int ruleNumber) {
        this.ruleNumber = ruleNumber;
    }

    public String getCriticalCombies() {
        return criticalCombies;
    }

    public void setCriticalCombies(String criticalCombies) {
        this.criticalCombies = criticalCombies;
    }

    public String getRefinedSafetyRule() {
        return RefinedSafetyRule;
    }

    public void setRefinedSafetyRule(String refinedSafetyRule) {
        RefinedSafetyRule = refinedSafetyRule;
    }

    public String getRefinedUCA() {
        return refinedUCA;
    }

    public void setRefinedUCA(String refinedUCA) {
        this.refinedUCA = refinedUCA;
    }

    public String getRefinedSC() {
        return refinedSC;
    }

    public void setRefinedSC(String refinedSC) {
        this.refinedSC = refinedSC;
    }

    public String getLtlProp() {
        return ltlProp;
    }

    public void setLtlProp(String ltlProp) {
        this.ltlProp = ltlProp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLinks() {
        return links;
    }

    public void setLinks(String links) {
        this.links = links;
    }

    public List<UUID> getUcaID() {
        return ucaID;
    }

    public void setUcaID(List<UUID> ucaID) {
        this.ucaID = ucaID;
    }

    public UUID getRelatedCaID() {
        return relatedCaID;
    }

    public void setRelatedCaID(UUID relatedCaID) {
        this.relatedCaID = relatedCaID;
    }
}
