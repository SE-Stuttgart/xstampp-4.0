package de.xstampp.service.project.data.dto.eclipse_project.controlAction;

import de.xstampp.service.project.data.dto.eclipse_project.abstracts.AbstractDataModel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "unsafecontrolaction")
@XmlAccessorType(XmlAccessType.NONE)
public class UnsafeControlAction extends AbstractDataModel {

    @XmlElement(name = "type")
    private UnsafeControlActionType type;

    @XmlElement(name = "correspondingSafetyConstraint")
    private CorrespondingSafetyConstraint correspondingSafetyConstraint;

    public UnsafeControlActionType getType() {
        return type;
    }

    public void setType(UnsafeControlActionType type) {
        this.type = type;
    }

    public CorrespondingSafetyConstraint getCorrespondingSafetyConstraint() {
        return correspondingSafetyConstraint;
    }

    public void setCorrespondingSafetyConstraint(CorrespondingSafetyConstraint correspondingSafetyConstraint) {
        this.correspondingSafetyConstraint = correspondingSafetyConstraint;
    }
}
