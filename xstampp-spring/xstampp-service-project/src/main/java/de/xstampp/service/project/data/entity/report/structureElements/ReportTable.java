package de.xstampp.service.project.data.entity.report.structureElements;

import java.util.ArrayList;
import java.util.List;

/**
 * Structure Element for displaying data in the template.
 * represents a table
 */
public class ReportTable extends ReportStructureElement {

    private List<String> columnTitles;
    private List<Row> tableRows;
    //used in template to define the size of the table
    private int widthPercentage;


    //constructor
    public ReportTable(List<String> columnTitles, int widthPercentage) {
        this.columnTitles = columnTitles;
        this.tableRows = new ArrayList<>();
        this.widthPercentage = widthPercentage;
    }

    /**
     * adds a new Row to the table
     *
     * @param content content of the row columns
     */
    public void addRow(List<String> content) {

        //list size has to match the column title list size
        if(content == null || content.size() > columnTitles.size() || content.size() < columnTitles.size()) {
            throw new IllegalArgumentException("Row size has to match defined amount columns");
        }

        Row row = new Row();
        row.setContent(content);
        tableRows.add(row);
    }

    public int size(){
        return tableRows.size();
    }

    public List<String> getColumnTitles() {
        return columnTitles;
    }

    public List<Row> getTableRows() {
        return tableRows;
    }

    public int getWidthPercentage() {
        return widthPercentage;
    }

    //structure element identifier for template Engine
    @Override
    public String getStructureElementId() {
        return "table";
    }

    /**
     * Private class defining the attribute of all rows in the table
     */
    private class Row {

        private List<String> content;

        public List<String> getContent() {
            return content;
        }

        void setContent(List<String> content) {
            this.content = content;
        }
    }
}
