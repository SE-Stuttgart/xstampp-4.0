package de.xstampp.service.project.data.entity.report.structureElements;

/**
 * Structure Element for displaying data in the template.
 * represents a text block with a title
 */
public class ReportTitledReference extends ReportStructureElement {


    private String title;
    private String reference;
    //determins if a text block represents a reference to another Entity
    private boolean isReference;

    //constructor
    public ReportTitledReference(String title, String reference, boolean isReference) {
        this.title = title;
        this.reference = reference;
        this.isReference = isReference;
    }

    //structure element identifier for template Engine
    public String getStructureElementId() {
        return "titledReference";
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isReference() {
        return isReference;
    }
}
