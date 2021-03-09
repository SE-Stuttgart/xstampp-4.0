package de.xstampp.service.project.data.dto.eclipse_project.link;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class ListOfLinks {
    @XmlElement
    private List<Entry> linkList;

    public ListOfLinks() {
        this.linkList = new ArrayList<Entry>();
    }

    public List<Entry> getLinkList() {
        return linkList;
    }

    public void setLinkList(List<Entry> linkList) {
        this.linkList = linkList;
    }
}
