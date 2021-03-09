package de.xstampp.service.project.data.dto.eclipse_project.controlAction;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;
import java.util.UUID;

@XmlAccessorType(XmlAccessType.NONE)
public class NotProvidedValuesCombi {

    @XmlElementWrapper(name = "processModelValueIDs")
    @XmlElement(name = "value")
    private List<UUID> values;

    @XmlElementWrapper(name = "processModelVariableIDs")
    @XmlElement(name = "variable")
    private List<UUID> variables;

    @XmlElementWrapper(name = "relatedUnsafeCOntrolActionIDs")
    @XmlElement(name = "ucaID")
    private List<UUID> relatedUCAs;

    @XmlElementWrapper(name = "refinedSafetyConstraint")
    @XmlElement(name = "value")
    private List<UUID> refinedSC;

    @XmlElement(name = "hazardous")
    private boolean hazardous;

    @XmlElement(name = "safetyConstraint")
    private String constraint;

    @XmlElement(name = "id")
    private UUID id;

    @XmlElement(name = "refinedRuleId")
    private UUID ruleId;

    @XmlElement(name = "archived")
    private boolean archived;

    @XmlElementWrapper(name = "valueNames")
    @XmlElement(name = "name")
    private List<String> valueNames;

    public List<UUID> getValues() {
        return values;
    }

    public void setValues(List<UUID> values) {
        this.values = values;
    }

    public List<UUID> getVariables() {
        return variables;
    }

    public void setVariables(List<UUID> variables) {
        this.variables = variables;
    }

    public List<UUID> getRelatedUCAs() {
        return relatedUCAs;
    }

    public void setRelatedUCAs(List<UUID> relatedUCAs) {
        this.relatedUCAs = relatedUCAs;
    }

    public List<UUID> getRefinedSC() {
        return refinedSC;
    }

    public void setRefinedSC(List<UUID> refinedSC) {
        this.refinedSC = refinedSC;
    }

    public boolean isHazardous() {
        return hazardous;
    }

    public void setHazardous(boolean hazardous) {
        this.hazardous = hazardous;
    }

    public String getConstraint() {
        return constraint;
    }

    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getRuleId() {
        return ruleId;
    }

    public void setRuleId(UUID ruleId) {
        this.ruleId = ruleId;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public List<String> getValueNames() {
        return valueNames;
    }

    public void setValueNames(List<String> valueNames) {
        this.valueNames = valueNames;
    }
}
