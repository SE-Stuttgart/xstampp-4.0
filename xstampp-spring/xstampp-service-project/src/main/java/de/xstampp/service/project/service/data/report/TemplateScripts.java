package de.xstampp.service.project.service.data.report;

import de.xstampp.service.project.data.entity.report.ReportSection;

import java.util.*;

/**
 * This class contains some scripts that the report templates need to process the project data.
 */
class TemplateScripts {

    /**
     * Determine the font size of a section header.
     *
     * @param layer How strongy nested the section is. 1 for a not nested section,
     *              2 for a section inside a section and so on.
     * @return The result as a font-size attribute value.
     */
    public String calculateTitleSize(int layer) {
        switch (layer) {
            case 1:
                return "3em";
            case 2:
                return "2em";
            default:
                return "1.5em";
        }
    }

    /**
     * Fills list contents into a table with the specified number of elements per row.
     * It fills the table from top to bottom, filling each row fully before starting to fill the row below.
     *
     * <pre>Example: {1, 2, 3, 4, 5} --> {{1, 2}, {3, 4}, {5}} with columnCount == 2</pre>
     *
     * @param list The list to fill into a table
     * @param columnCount How many columns the table is supposed to have at max. The row count depends on the list size.
     * @param <T> The type of the list's contents
     * @return The filled table
     */
    public <T> List<List<T>> fillTable(List<T> list, int columnCount) {
        List<List<T>> resultList = new ArrayList<>();
        Iterator<T> iterator = list.iterator();
        for (int i = 0; i < list.size(); i += columnCount) {
            List<T> row = new ArrayList<>();
            for (int e = 0; e < columnCount; e++) {
                if (iterator.hasNext()) {
                    row.add(iterator.next());
                }
            }
            resultList.add(row);
        }
        return resultList;
    }

    /**
     * Shortens the specified string if it exceeds the maximum length. Then, two additional
     * characters are cut off the end and replaced by "...". If the string does not exceed the maximum length,
     * the original string gets returned.
     *
     * <pre>Example: cutString("Lorem ipsum", 9, null) = "Lorem i..."</pre>
     * <pre>Example: cutString("ipsum", 9, "Lorem ") = "i..."</pre>
     *
     * @param string The string to be shortened, if it is too long
     * @param maxLength The maximum string length
     * @param fixedString This string's length counts towards the original string's length but will not get included
     *                    into the result string. Use null if you don't use this functionality.
     * @return The shortened original string
     */
    public String cutString(String string, int maxLength, String fixedString) {
        if (fixedString == null) {
            fixedString = "";
        }
        if (string.length() + fixedString.length() > maxLength) {
            int substringTo = maxLength - fixedString.length() - 3;
            if (substringTo > 0) {
                return string.substring(0, substringTo) + "...";
            } else {
                return "";
            }
        } else {
            return string;
        }
    }

    /**
     * The template scripts can use this method to throw an exception.
     *
     * @param message The exception message
     * @return Won't ever return anything
     * @throws ReportConstructionException Will always be thrown
     */
    public String throwException(String message) throws ReportConstructionException {
        throw new ReportConstructionException(message);
    }

    /**
     * Returns a pre-order iterator for the sections in the report structure tree.
     *
     * @param rootList The report structure tree
     * @return The iterator iterating through all sections in pre-order
     */
    public Iterator<ReportSection> getTableOfContentsIterator(List<ReportSection> rootList) {
        return new Iterator<>() {
            Stack<ReportSection> stack;

            @Override
            public boolean hasNext() {
                return (stack != null && !stack.empty()) || (stack == null && rootList.size() > 0);
            }

            @Override
            public ReportSection next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                if (stack == null) {
                    stack = new Stack<>();
                    for (int i = rootList.size() - 1; i >= 0; i--) {
                        stack.push(rootList.get(i));
                    }
                }
                ReportSection section = stack.pop();
                for (int i = section.subsections.size() - 1; i >= 0; i--) {
                    stack.push(section.subsections.get(i));
                }
                return section;
            }
        };
    }

}
