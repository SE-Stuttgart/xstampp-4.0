package de.xstampp.service.project.data.entity.report;

/**
 * Links an Entity Id with their name
 */
public class ReportNameIdPair {
    public String name;
    public String id;

    public ReportNameIdPair(String name, String id) {
        this.name = name;
        this.id = id;
    }
}
