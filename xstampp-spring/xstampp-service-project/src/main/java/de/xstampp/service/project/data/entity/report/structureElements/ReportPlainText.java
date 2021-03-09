package de.xstampp.service.project.data.entity.report.structureElements;

/**
 * Structure Element for displaying data in the template.
 * represents a text block
 */
public class ReportPlainText extends ReportStructureElement {

    private String text;
    private String emptyMessage;

    public ReportPlainText(String text) {
        this(text, "- no Description -");
    }

    public ReportPlainText(String text, String emptyMessage) {
        this.text = text;
        this.emptyMessage = emptyMessage;
    }

    //structure element identifier for template Engine
    public String getStructureElementId() {
        return "plainText";
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getEmptyMessage() {
        return emptyMessage;
    }

    public void setEmptyMessage(String emptyMessage) {
        this.emptyMessage = emptyMessage;
    }

    @Override
    public String toString() {
        return text;
    }
}
