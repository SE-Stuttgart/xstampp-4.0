package de.xstampp.service.project.data.dto.eclipse_project.controlAction;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;
import java.util.UUID;

@XmlAccessorType(XmlAccessType.NONE)
public class ProvidedValuesCombi {

    @XmlElementWrapper(name = "processModelValueIDs")
    @XmlElement(name = "value")
    private List<UUID> values;

    @XmlElementWrapper(name = "processModelVariableIDs")
    @XmlElement(name = "variable")
    private List<UUID> variables;

    @XmlElementWrapper(name = "relatedUCAsAnytime")
    @XmlElement(name = "ucaID")
    private List<UUID> relatedUCAsAnytime;

    @XmlElementWrapper(name = "relatedUCAsTooLate")
    @XmlElement(name = "ucaID")
    private List<UUID> relatedUCAsTooLate;

    @XmlElementWrapper(name = "relatedUCAsTooEarly")
    @XmlElement(name = "ucaID")
    private List<UUID> relatedUCAsTooEarly;

    @XmlElementWrapper(name = "refinedSafetyConstraint")
    @XmlElement(name = "value")
    private List<UUID> refinedSC;

    @XmlElement(name = "hazardousAnyTime")
    private boolean hazardousAnyTime;

    @XmlElement(name = "hazardousToLate")
    private boolean hazardousToLate;

    @XmlElement(name = "hazardousifProvidedToEarly")
    private boolean hazardousToEarly;

    @XmlElement(name = "safetyConstraint")
    private String constraint;

    @XmlElement(name = "combieId")
    private UUID id;

    @XmlElement(name = "anytimeRuleId")
    private UUID anytimeRule;

    @XmlElement(name = "tooEarlyRuleId")
    private UUID tooEarlyRule;

    @XmlElement(name = "tooLateRuleId")
    private UUID tooLateRule;

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

    public List<UUID> getRelatedUCAsAnytime() {
        return relatedUCAsAnytime;
    }

    public void setRelatedUCAsAnytime(List<UUID> relatedUCAsAnytime) {
        this.relatedUCAsAnytime = relatedUCAsAnytime;
    }

    public List<UUID> getRelatedUCAsTooLate() {
        return relatedUCAsTooLate;
    }

    public void setRelatedUCAsTooLate(List<UUID> relatedUCAsTooLate) {
        this.relatedUCAsTooLate = relatedUCAsTooLate;
    }

    public List<UUID> getRelatedUCAsTooEarly() {
        return relatedUCAsTooEarly;
    }

    public void setRelatedUCAsTooEarly(List<UUID> relatedUCAsTooEarly) {
        this.relatedUCAsTooEarly = relatedUCAsTooEarly;
    }

    public List<UUID> getRefinedSC() {
        return refinedSC;
    }

    public void setRefinedSC(List<UUID> refinedSC) {
        this.refinedSC = refinedSC;
    }

    public boolean isHazardousAnyTime() {
        return hazardousAnyTime;
    }

    public void setHazardousAnyTime(boolean hazardousAnyTime) {
        this.hazardousAnyTime = hazardousAnyTime;
    }

    public boolean isHazardousToLate() {
        return hazardousToLate;
    }

    public void setHazardousToLate(boolean hazardousToLate) {
        this.hazardousToLate = hazardousToLate;
    }

    public boolean isHazardousToEarly() {
        return hazardousToEarly;
    }

    public void setHazardousToEarly(boolean hazardousToEarly) {
        this.hazardousToEarly = hazardousToEarly;
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

    public UUID getAnytimeRule() {
        return anytimeRule;
    }

    public void setAnytimeRule(UUID anytimeRule) {
        this.anytimeRule = anytimeRule;
    }

    public UUID getTooEarlyRule() {
        return tooEarlyRule;
    }

    public void setTooEarlyRule(UUID tooEarlyRule) {
        this.tooEarlyRule = tooEarlyRule;
    }

    public UUID getTooLateRule() {
        return tooLateRule;
    }

    public void setTooLateRule(UUID tooLateRule) {
        this.tooLateRule = tooLateRule;
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
