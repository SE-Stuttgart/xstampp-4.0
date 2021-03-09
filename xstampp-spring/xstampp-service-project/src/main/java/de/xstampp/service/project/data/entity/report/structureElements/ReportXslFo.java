package de.xstampp.service.project.data.entity.report.structureElements;

/**
 * Structure Element for displaying data in the template.
 */
public class ReportXslFo extends ReportStructureElement {

    private String code;

    public ReportXslFo(String code) {
        this.code = code;
    }

    //structure element identifier for template Engine
    public String getStructureElementId() {
        return "xslFo";
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
