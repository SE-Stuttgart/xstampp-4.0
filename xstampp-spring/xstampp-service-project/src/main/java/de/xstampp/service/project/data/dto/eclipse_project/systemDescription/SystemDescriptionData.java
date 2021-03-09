package de.xstampp.service.project.data.dto.eclipse_project.systemDescription;

import de.xstampp.service.project.data.dto.eclipse_project.link.LinkController;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;

// SDS -> SystemDescription?
@XmlAccessorType(XmlAccessType.NONE)
public class SystemDescriptionData {

    @XmlElementWrapper(name = "safetyConstraints")
    @XmlElement(name = "safetyConstraint")
    private ArrayList<SafetyConstraint> safetyConstraints;

    @XmlElementWrapper(name = "systemGoals")
    @XmlElement(name = "systemGoal")
    private ArrayList<SystemGoal> systemGoals;

    @XmlElementWrapper(name = "designRequirements")
    private ArrayList<DesignRequirement> designRequirements;

    @XmlElementWrapper(name = "designRequirementsStep1")
    private ArrayList<DesignRequirementStep1> designRequirementsStep1;

    @XmlElementWrapper(name = "designRequirementsStep2")
    private ArrayList<DesignRequirementStep2> designRequirementsStep2;

    @XmlTransient
    private LinkController linkController;

    public ArrayList<SafetyConstraint> getSafetyConstraints() {
        return safetyConstraints;
    }

    public void setSafetyConstraints(ArrayList<SafetyConstraint> safetyConstraints) {
        this.safetyConstraints = safetyConstraints;
    }

    public ArrayList<SystemGoal> getSystemGoals() {
        return systemGoals;
    }

    public void setSystemGoals(ArrayList<SystemGoal> systemGoals) {
        this.systemGoals = systemGoals;
    }

    public ArrayList<DesignRequirement> getDesignRequirements() {
        return designRequirements;
    }

    public void setDesignRequirements(ArrayList<DesignRequirement> designRequirements) {
        this.designRequirements = designRequirements;
    }

    public ArrayList<DesignRequirementStep1> getDesignRequirementsStep1() {
        return designRequirementsStep1;
    }

    public void setDesignRequirementsStep1(ArrayList<DesignRequirementStep1> designRequirementsStep1) {
        this.designRequirementsStep1 = designRequirementsStep1;
    }

    public ArrayList<DesignRequirementStep2> getDesignRequirementsStep2() {
        return designRequirementsStep2;
    }

    public void setDesignRequirementsStep2(ArrayList<DesignRequirementStep2> designRequirementsStep2) {
        this.designRequirementsStep2 = designRequirementsStep2;
    }
}
