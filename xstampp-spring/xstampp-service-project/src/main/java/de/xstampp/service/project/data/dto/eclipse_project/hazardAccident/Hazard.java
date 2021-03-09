package de.xstampp.service.project.data.dto.eclipse_project.hazardAccident;

import de.xstampp.service.project.data.dto.eclipse_project.abstracts.AbstractDataModel;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.UUID;

@XmlRootElement(name = "hazard")
public class Hazard extends AbstractDataModel {

    public Hazard() {super();}

    public Hazard(UUID id, String title, String description, int number) {
        super(id, title, description, number);
    }
}
