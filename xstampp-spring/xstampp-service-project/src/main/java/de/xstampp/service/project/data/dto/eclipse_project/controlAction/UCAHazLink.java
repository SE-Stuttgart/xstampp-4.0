package de.xstampp.service.project.data.dto.eclipse_project.controlAction;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.UUID;

@XmlRootElement(name = "link")
@XmlAccessorType(XmlAccessType.NONE)
public class UCAHazLink {

    @XmlElement(name = "unsafeControlActionId")
    private UUID unsafeControlActionId;

    @XmlElement(name = "hazardId")
    private UUID hazardId;

    public UUID getUnsafeControlActionId() {
        return unsafeControlActionId;
    }

    public void setUnsafeControlActionId(UUID unsafeControlActionId) {
        this.unsafeControlActionId = unsafeControlActionId;
    }

    public UUID getHazardId() {
        return hazardId;
    }

    public void setHazardId(UUID hazardId) {
        this.hazardId = hazardId;
    }
}
