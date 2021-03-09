package de.xstampp.service.project.data.entity.report.structureElements;

public class ReportSvg extends ReportStructureElement{

    String code;

    public ReportSvg(String code) {
        this.code = code;
    }

    @Override
    public String getStructureElementId() {
        return "svg";
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
