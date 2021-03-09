package de.xstampp.service.project.data.dto.eclipse_project.link;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.UUID;

@XmlType(name = "link")
public final class Link {

    @XmlAttribute
    private UUID linkA;

    @XmlAttribute
    private UUID linkB;

    @XmlAttribute
    private UUID id;

    @XmlAttribute
    private String note;

    @XmlElement
    private String linkNote;

    private LinkingType linkType;

    public UUID getLinkA() {
        return linkA;
    }

    public void setLinkA(UUID linkA) {
        this.linkA = linkA;
    }

    public UUID getLinkB() {
        return linkB;
    }

    public void setLinkB(UUID linkB) {
        this.linkB = linkB;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getLinkNote() {
        return linkNote;
    }

    public void setLinkNote(String linkNote) {
        this.linkNote = linkNote;
    }

    public LinkingType getLinkType() {
        return linkType;
    }

    public void setLinkType(LinkingType linkType) {
        this.linkType = linkType;
    }
}
