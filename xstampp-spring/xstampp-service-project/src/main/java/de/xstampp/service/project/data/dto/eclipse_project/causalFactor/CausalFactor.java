package de.xstampp.service.project.data.dto.eclipse_project.causalFactor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.UUID;

@XmlAccessorType(XmlAccessType.NONE)
public class CausalFactor {

    @XmlAttribute(name = "number")
    private int number;

    @XmlElement(name = "id")
    private UUID id;

    @XmlElement(name = "text")
    private String text;

    public CausalFactor() {}

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
