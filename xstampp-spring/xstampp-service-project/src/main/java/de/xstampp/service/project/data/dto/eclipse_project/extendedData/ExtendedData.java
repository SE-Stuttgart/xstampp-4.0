package de.xstampp.service.project.data.dto.eclipse_project.extendedData;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.NONE)
public class ExtendedData {

    public static final String CONSTRAINT_ID = "SSC2.";

    @XmlElementWrapper(name = "rules")
    @XmlElement(name = "rule")
    private List<RefinedSafetyRule> rules;

    @XmlElementWrapper(name = "scenarios")
    @XmlElement(name = "scenario")
    private List<RefinedSafetyRule> scenarios;

    @XmlElementWrapper(name = "customLTLs")
    @XmlElement(name = "customLTL")
    private List<RefinedSafetyRule> customLTLs;

    @XmlAttribute(name = "nextScenarioIndex")
    private int nextScenarioIndex;

    public static String getConstraintId() {
        return CONSTRAINT_ID;
    }

    public List<RefinedSafetyRule> getRules() {
        return rules;
    }

    public void setRules(List<RefinedSafetyRule> rules) {
        this.rules = rules;
    }

    public List<RefinedSafetyRule> getScenarios() {
        return scenarios;
    }

    public void setScenarios(List<RefinedSafetyRule> scenarios) {
        this.scenarios = scenarios;
    }

    public List<RefinedSafetyRule> getCustomLTLs() {
        return customLTLs;
    }

    public void setCustomLTLs(List<RefinedSafetyRule> customLTLs) {
        this.customLTLs = customLTLs;
    }

    public int getNextScenarioIndex() {
        return nextScenarioIndex;
    }

    public void setNextScenarioIndex(int nextScenarioIndex) {
        this.nextScenarioIndex = nextScenarioIndex;
    }
}
