package de.xstampp.service.project.data.entity.report;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Structure Element for displaying data in the template.
 * sections are ordered in different layers
 * sections represent proper sections in template, and contain all necessary data in segments and subsections
 */
public class ReportSection {
    public String title;
    public int layer;
    public List<ReportSegment> segments;
    public List<ReportSection> subsections;

    public ReportSection(String title) {
        this.title = title;
        this.segments = new ArrayList<>();
        this.subsections = new ArrayList<>();
    }

    public void initializeTree(int currentLayer) {
        this.layer = currentLayer;

        Iterator<ReportSection> subsectionIterator = subsections.iterator();
        while(subsectionIterator.hasNext()) {
            final ReportSection subsection = subsectionIterator.next();
            subsection.initializeTree(currentLayer + 1);
            if (subsection.toDelete()) {
                subsectionIterator.remove();
            }
        }
    }

    public boolean toDelete() {
        return this.subsections.size() == 0 && this.segments.size() == 0;
    }
}
