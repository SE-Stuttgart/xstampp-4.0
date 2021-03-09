package de.xstampp.service.project.data.entity.report.structureElements;

/**
 * Structure Element for displaying data in the template.
 * Header for each reportable Entity
 */
public class ReportHeader extends ReportStructureElement {

    private String name;
    private String id;

    //constructors

    public ReportHeader(String name) {
        this.name = name;
        this.id = null;
    }

    public ReportHeader(String name, String id) {
        this.name = name;
        this.id = id;
    }

    //structure element identifier for template Engine
    public String getStructureElementId() {
        return "header";
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getHeader() {
        if (id == null) {
            return name;
        } else {
            return "[" + id + "] " + name;
        }
    }
}
