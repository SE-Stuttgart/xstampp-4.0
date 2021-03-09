package de.xstampp.service.project.data.dto.eclipse_project.link;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Adapter extends XmlAdapter<ListOfLinks, Map<LinkingType, List<Link>>> {

    @Override
    public Map<LinkingType, List<Link>> unmarshal(ListOfLinks loe) throws Exception {
        Map<LinkingType, List<Link>> map = new HashMap<>();
        for (Entry entry : loe.getLinkList()) {
            entry.getList().forEach((link) -> link.setLinkType(entry.getKey()));
            map.put(entry.getKey(), entry.getList());
        }
        return map;
    }

    @Override
    public ListOfLinks marshal(Map<LinkingType, List<Link>> map) throws Exception {
        ListOfLinks loe = new ListOfLinks();
        for (Map.Entry<LinkingType, List<Link>> mapEntry : map.entrySet()) {
            Entry entry = new Entry();
            if (mapEntry.getKey() != null) {
                entry.setKey(mapEntry.getKey());
                entry.getList().addAll(mapEntry.getValue());
                loe.getLinkList().add(entry);
            }
        }
        return loe;
    }
}
