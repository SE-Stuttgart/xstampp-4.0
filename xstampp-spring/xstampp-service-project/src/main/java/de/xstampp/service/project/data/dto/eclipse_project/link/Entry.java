package de.xstampp.service.project.data.dto.eclipse_project.link;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Entry {

    @XmlAttribute
    private LinkingType key;

    @XmlElementWrapper(name = "links")
    @XmlElement(name = "link")
    private List<Link> list;

    public Entry() {
        this.list = new ArrayList<Link>();
    }

    public LinkingType getKey() {
        return key;
    }

    public void setKey(LinkingType value) {
        key = value;
    }

    public List<Link> getList() {
        return list;
    }
}

