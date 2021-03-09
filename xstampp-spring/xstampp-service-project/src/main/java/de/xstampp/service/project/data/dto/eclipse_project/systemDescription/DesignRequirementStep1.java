package de.xstampp.service.project.data.dto.eclipse_project.systemDescription;

import de.xstampp.service.project.data.dto.eclipse_project.abstracts.AbstractDataModel;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.UUID;

@XmlRootElement(name = "designRequirementStep1")
public class DesignRequirementStep1 extends AbstractDataModel {

    public DesignRequirementStep1() {super();}

    public DesignRequirementStep1(UUID id, String title, String description, int number) {
        super(id, title, description, number);
    }
}
