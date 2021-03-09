package de.xstampp.service.project.data.entity.report.structureElements;

import java.util.List;

/**
 * Structure Element for displaying data in the template.
 * Simple list
 */
public class ReportList extends ReportStructureElement {

    private String title;
    private List<String> list;

    //structure element identifier for template Engine
    @Override
    public String getStructureElementId() {
        return "reportList";
    }

    //constructor
    public ReportList(String title, List<String> list) {
        this.title = title;
        this.list = list;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getList() {
        return list;
    }

    public void deleteEntry(String entry) {
        if (list.contains(entry))
            list.remove(entry);
    }
}
