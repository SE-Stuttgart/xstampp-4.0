package de.xstampp.service.project.data.dto.eclipse_project.controlAction;

import de.xstampp.service.project.data.dto.eclipse_project.abstracts.AbstractDataModel;
import de.xstampp.service.project.data.dto.eclipse_project.extendedData.RefinedSafetyRule;

import javax.xml.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "controlaction")
public class ControlAction extends AbstractDataModel {

    @XmlElementWrapper(name = "unsafecontrolactions")
    @XmlElement(name = "unsafecontrolaction")
    private List<UnsafeControlAction> unsafeControlActions;

    @XmlElement(name = "componentLink")
    private UUID componentLink;

    @XmlElement(name = "isSafetyCritical")
    private boolean isSafetyCritical;

//    @XmlElementWrapper(name = "notProvidedPMVariables")
//    @XmlElement(name = "variableID")
//    private List<UUID> notProvidedVariables;
//
//    @XmlElementWrapper(name = "providedPMVariables")
//    @XmlElement(name = "variableID")
//    private List<UUID> providedVariables;
//
//    @XmlElementWrapper(name = "PMCombisWhenNotProvided")
//    @XmlElement(name = "combinationOfPMValues")
//    private List<NotProvidedValuesCombi> valuesWhenNotProvided;
//
//    @XmlElementWrapper(name = "PMCombisWhenProvided")
//    @XmlElement(name = "combinationOfPMValues")
//    private List<ProvidedValuesCombi> valuesWhenProvided;
//
//    @XmlElementWrapper(name = "dependenciesForNotProvided")
//    @XmlElement(name = "variableName")
//    private List<String> notProvidedVariableNames;
//
//    @XmlElementWrapper(name = "dependenciesForProvided")
//    @XmlElement(name = "variableName")
//    private List<String> providedVariableNames;

    @XmlElementWrapper(name = "rules")
    @XmlElement(name = "rule")
    private List<RefinedSafetyRule> rules;

    @XmlElementWrapper(name = "ruleIds")
    @XmlElement(name = "id")
    private List<UUID> ruleIds;

    public ControlAction() {super();}

    public ControlAction(UUID id, String title, String description, int number) {
        super(id, title, description, number);
    }

    public List<UnsafeControlAction> getUnsafeControlActions() {
        return unsafeControlActions;
    }

    public void setUnsafeControlActions(List<UnsafeControlAction> unsafeControlActions) {
        this.unsafeControlActions = unsafeControlActions;
    }

    public UUID getComponentLink() {
        return componentLink;
    }

    public void setComponentLink(UUID componentLink) {
        this.componentLink = componentLink;
    }

    public boolean isSafetyCritical() {
        return isSafetyCritical;
    }

    public void setSafetyCritical(boolean safetyCritical) {
        isSafetyCritical = safetyCritical;
    }

    public List<RefinedSafetyRule> getRules() {
        return rules;
    }

    public void setRules(List<RefinedSafetyRule> rules) {
        this.rules = rules;
    }

    public List<UUID> getRuleIds() {
        return ruleIds;
    }

    public void setRuleIds(List<UUID> ruleIds) {
        this.ruleIds = ruleIds;
    }
}
