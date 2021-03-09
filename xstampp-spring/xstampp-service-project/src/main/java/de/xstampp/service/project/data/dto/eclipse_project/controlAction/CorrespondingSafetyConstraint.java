package de.xstampp.service.project.data.dto.eclipse_project.controlAction;

import de.xstampp.service.project.data.dto.eclipse_project.abstracts.AbstractDataModel;

import javax.xml.bind.annotation.XmlElement;

public class CorrespondingSafetyConstraint extends AbstractDataModel {

    @XmlElement
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
