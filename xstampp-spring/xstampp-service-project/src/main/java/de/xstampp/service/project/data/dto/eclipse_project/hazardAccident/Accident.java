package de.xstampp.service.project.data.dto.eclipse_project.hazardAccident;

import de.xstampp.service.project.data.dto.eclipse_project.abstracts.AbstractDataModel;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.UUID;

@XmlRootElement(name = "accident")
public class Accident extends AbstractDataModel {

    public Accident() {super();}

    public Accident(UUID id, String title, String description, int number) {
        super(id, title, description, number);
    }
}
