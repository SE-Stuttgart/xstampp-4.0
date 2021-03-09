package de.xstampp.service.project.data.dto.eclipse_project.systemDescription;

import de.xstampp.service.project.data.dto.eclipse_project.abstracts.AbstractDataModel;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.UUID;

@XmlRootElement(name = "designRequirementStep2")
public class DesignRequirementStep2 extends AbstractDataModel {

    public DesignRequirementStep2() {super();}

    public DesignRequirementStep2(UUID id, String title, String description, int number) {
        super(id, title, description, number);
    }
}
