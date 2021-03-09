package de.xstampp.service.project.data.dto.eclipse_project.hazardAccident;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.UUID;

@XmlRootElement(name = "link")
@XmlType(propOrder = { "accidentId", "hazardId" })
public class HazAccLink {

    @XmlElement
    private UUID accidentId;

    @XmlElement
    private UUID hazardId;

    public UUID getAccidentId() {
        return accidentId;
    }

    public void setAccidentId(UUID accidentId) {
        this.accidentId = accidentId;
    }

    public UUID getHazardId() {
        return hazardId;
    }

    public void setHazardId(UUID hazardId) {
        this.hazardId = hazardId;
    }
}
