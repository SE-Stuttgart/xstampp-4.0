package de.xstampp.service.project.data.dto.eclipse_project.systemDescription;

import de.xstampp.service.project.data.dto.eclipse_project.abstracts.AbstractDataModel;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.UUID;

@XmlRootElement(name = "designRequirement")
public class DesignRequirement extends AbstractDataModel {
    public DesignRequirement() {super();}
    public DesignRequirement(UUID id, String title, String description, int number) {
        super(id, title, description, number);
    }
}
