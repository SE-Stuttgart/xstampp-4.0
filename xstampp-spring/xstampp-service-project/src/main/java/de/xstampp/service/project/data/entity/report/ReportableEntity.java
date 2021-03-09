package de.xstampp.service.project.data.entity.report;


import de.xstampp.service.project.service.data.XStamppProject.XStamppProject;

/**
 * Classes implementing this interface can be integrated into the report file
 */
public interface ReportableEntity {

    /**
     * creates a ReportSegment needed for the report generation
     * @return	ReportSegment
     */
    public ReportSegment createReportSegment(XStamppProject xstamppProject);

    /**
     * creates a Name-ID-Pair for indexing within the report generation.
     * @return  Name-ID-Pair-Object
     */
    public ReportNameIdPair createNameIdPair();
}
