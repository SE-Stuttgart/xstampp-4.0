package de.xstampp.service.project.data.dto.eclipse_project.systemDescription;

import de.xstampp.service.project.data.dto.eclipse_project.abstracts.AbstractDataModel;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.UUID;

@XmlRootElement(name = "safetyConstraint")
public class SafetyConstraint extends AbstractDataModel {

    public SafetyConstraint() {super();}

    public SafetyConstraint(UUID id, String title, String description, int number) {
        super(id, title, description, number);
    }
}
