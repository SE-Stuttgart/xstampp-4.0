package de.xstampp.service.project.data.entity.report.structureElements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Structure Element for displaying data in the template.
 * Used to list referenced Data
 */
public class ReportColumns extends ReportStructureElement {

    private List<Column> columns;

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    //structure element identifier for template Engine
    public String getStructureElementId() {
        return "columns";
    }

    public ReportColumns() {
        this.columns = new ArrayList<>();
    }


    /**
     * creates the reference Strings needed for Indexing in Template
     *
     * @param referenceIds IDs of Entity
     * @param prefix       Entity prefix
     * @return correct referenceList
     */
    private List<String> createReferences(List<Integer> referenceIds, String prefix) {
        List<String> references = new ArrayList<>();

        for (int referenceId : referenceIds) {
            references.add(prefix + referenceId);
        }

        return references;
    }

    /**
     * adds a column to the column list
     *
     * @param columnName   name of the column
     * @param referenceIds IDs of referenced Entities
     * @param prefix       report prefix of the Entity
     */
    public void addColumn(String columnName, List<Integer> referenceIds, String prefix) {

        //Sorting Ids before adding them to the column
        Collections.sort(referenceIds);
        Column column = new Column();
        column.setColumnName(columnName);
        column.setReferences(createReferences(referenceIds, prefix));

        columns.add(column);
    }

    //

    /**
     * alternative method to create a new column
     * Used for Entity-dependant Entities that also contain parent ID in addition to own ID in reference, as their own ID is not unique
     *
     * @param columnName column name displayed
     * @param references values of the column
     */
    public void addColumn(String columnName, List<String> references) {

        if (references == null) {
            return;
        } else if (!references.isEmpty()) {
            // Expected String: "Prefix" + "-" + "ID"
            if (references.get(0).contains("-")) {
                String[] prefix = references.get(0).split("-");
                //Entity-Dependent Entities; Expected String: "Prefix" + "-" + "Parent ID" + "." + "ID"
                if (prefix.length > 0 && prefix[0].contains(".")) {
                    references = sortEntityDependantList(references);
                }
            } else {
                //lexical Sort
                Collections.sort(references);
            }
        }

        Column column = new Column();
        column.setColumnName(columnName);
        column.setReferences(references);

        columns.add(column);
    }


    /**
     * Sorts a list of Entity-dependant Entity ID strings
     *
     * @param references ID strings
     * @return sorted List
     */
    private static List<String> sortEntityDependantList(List<String> references) {

        //Bubble Sort
        for (int i = 0; i < references.size() - 2; i++) {

            String currentString = references.get(i);
            String nextString = references.get(i + 1);
            //Separate Prefix from string
            String[] idPartsCurrent = currentString.split("-");
            String[] idPartsNext = nextString.split("-");

            if (idPartsCurrent[0].compareTo(idPartsNext[0]) != 0) {
                //separate parent ID from Entity ID
                String[] lastIdPartsThis = idPartsCurrent[1].split("\\.");
                String[] lastIdPartsForeign = idPartsNext[1].split("\\.");

                //comparing ID values. lower ID values are moved to the front of the list
                //compares parent IDs
                int beforeDot = Integer.valueOf(lastIdPartsThis[0]).compareTo(Integer.valueOf(lastIdPartsForeign[0]));
                if (beforeDot > 0) {
                    String tmp = references.get(i);
                    references.set(i, references.get(i + 1));
                    references.set(i + 1, tmp);
                } else if (beforeDot == 0) {
                    //if parent ID is the same Entity ID is compared
                    int afterDot = Integer.valueOf(lastIdPartsThis[0]).compareTo(Integer.valueOf(lastIdPartsForeign[0]));

                    if (afterDot > 0) {
                        String tmp = references.get(i);
                        references.set(i, references.get(i + 1));
                        references.set(i + 1, tmp);
                    }
                }
            }
        }

        return references;
    }

    /**
     * removes a column from the column list
     *
     * @param columnName name of the column
     */
    public void removeColumn(String columnName) {

        for (Column column : columns) {
            if (column.getColumnName() != null && column.getColumnName().equals(columnName)) {
                columns.remove(column);
                return;
            }
        }
    }

    /**
     * Private Column class defining attribute of columns saved to column List
     */
    public class Column {

        private String columnName;
        private List<String> references;

        // getter and setters

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public List<String> getReferences() {
            return references;
        }

        public void setReferences(List<String> references) {
            this.references = references;
        }
    }
}
