package de.xstampp.service.project.data.dto.eclipse_project.link;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LinkController {

    @XmlElement
    @XmlJavaTypeAdapter(Adapter.class)
    private Map<LinkingType, List<Link>> linkMap;

    @XmlElementWrapper
    @XmlElement(name = "id")
    private List<UUID> deletedIds;

    public Map<LinkingType, List<Link>> getLinkMap() {
        return linkMap;
    }

    public void setLinkMap(Map<LinkingType, List<Link>> linkMap) {
        this.linkMap = linkMap;
    }

    public List<UUID> getDeletedIds() {
        return deletedIds;
    }

    public void setDeletedIds(List<UUID> deletedIds) {
        this.deletedIds = deletedIds;
    }
}
