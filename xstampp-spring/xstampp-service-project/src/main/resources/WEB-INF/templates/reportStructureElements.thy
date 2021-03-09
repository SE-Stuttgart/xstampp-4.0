<?xml version="1.0" encoding="UTF-8"?>
<th:block th:fragment="segments (segments)">
    <fo:block th:each="segment : ${segments}" space-after="1.5em" th:id="${'segmentId_' + segment.referenceId}">
        <th:block th:each="element : ${segment}">
            <th:block th:switch="${element.structureElementId}">
                <th:block th:case="'header'">
                    <element th:replace="~{reportStructureElements :: headline (${element})}"/>
                </th:block>
                <th:block th:case="'plainText'">
                    <element th:replace="~{reportStructureElements :: plainText (${element})}"/>
                </th:block>
                <th:block th:case="'columns'">
                    <element th:replace="~{reportStructureElements :: columns (${element})}"/>
                </th:block>
                <th:block th:case="'titledReference'">
                    <element th:replace="~{reportStructureElements :: titledRef (${element})}"/>
                </th:block>
                <th:block th:case="'reportList'">
                    <element th:replace="~{reportStructureElements :: reportList (${element})}"/>
                </th:block>
                <th:block th:case="'xslFo'">
                    <element th:replace="~{reportStructureElements :: xslFo (${element})}"/>
                </th:block>
                <th:block th:case="'table'">
                    <element th:replace="~{reportStructureElements :: table (${element})}"/>
                </th:block>
                <th:block th:case="'svg'">
                    <element th:replace="~{reportStructureElements :: svg (${element})}"/>
                </th:block>
            </th:block>
        </th:block>
    </fo:block>
</th:block>

<th:block th:fragment="headline (element)">
    <fo:block font-size="1.25em" font-weight="bold" keep-with-next="always" th:text="${element.header}"/>
</th:block>

<th:block th:fragment="plainText (element)">
    <fo:block th:if="${element.getText() == null or element.getText().length() == 0}" th:text="${element.getEmptyMessage()}" font-style="italic"/>
    <fo:block th:if="${element.getText() != null and element.getText().length() > 0}" th:text="${element.getText()}" text-align="justify"/>
</th:block>

<th:block th:fragment="columns (element)">
    <fo:table border-style="none" font-size="0.83em" space-before="0.5em" border-collapse="separate" border-spacing="5pt" table-layout="fixed" width="100%">
        <fo:table-column column-width="50%"/>
        <fo:table-column column-width="50%"/>
        <fo:table-body>
            <fo:table-row th:each="row : ${scripts.fillTable(element.columns,2)}">
                <fo:table-cell th:each="cell : ${row}">
                    <fo:block font-weight="bold" th:text="${cell.columnName}" keep-with-next="always"></fo:block>
                    <fo:block th:if="${cell.references.size() == 0}" font-style="italic">- none -</fo:block>
                    <fo:block th:each="ref : ${cell.references}" th:text-align-last="${entityIndex.containsKey(ref) and entityIndex.get(ref).alive}? 'justify' : 'start'">
                        <!-- Reenable and remove workaround when duplicate id issue is fixed-->
                        <!--<th:block th:if="${!entityIndex.containsKey(ref)}" th:text="${scripts.throwException('ReportColumns contains a non-indexed reference: ''' + ref + ''' A ReportableEntity has to pass its id and name via createNameIdPair() to be referenced.')}"></th:block>-->
                        <th:block th:if="${entityIndex.containsKey(ref)}">
                            <fo:basic-link th:if="${entityIndex.get(ref).alive}" th:internal-destination="${'segmentId_' + ref}">
                                <fo:inline th:text="${scripts.cutString('[' + ref + '] ' + entityIndex.get(ref).entityName, 48, null)}"></fo:inline>
                                <fo:leader th:if="${entityIndex.get(ref).alive}" leader-length.minimum="0px" leader-pattern="rule"></fo:leader>
                                <fo:page-number-citation th:if="${entityIndex.get(ref).alive}" th:ref-id="${'segmentId_' + ref}"></fo:page-number-citation>
                            </fo:basic-link>
                            <fo:inline th:if="${!entityIndex.get(ref).alive}" th:text="${scripts.cutString('[' + ref + '] ' + entityIndex.get(ref).entityName, 58, null)}"></fo:inline>
                        </th:block>

                        <th:block th:if="${!entityIndex.containsKey(ref)}">REFERENCE NOT FOUND</th:block>
                    </fo:block>
                </fo:table-cell>
            </fo:table-row>
        </fo:table-body>
    </fo:table>
</th:block>

<th:block th:fragment="titledRef (element)">
    <fo:block font-size="0.83em" space-before="0.5em" th:text-align-last="${entityIndex.containsKey(element.reference) and entityIndex.get(element.reference).alive}? 'justify' : 'start'">
        <fo:inline font-weight="bold" th:text="${element.title + ': '}" />
        <fo:inline th:if="${!element.isReference()}" th:text="${element.reference}"/>
        <th:block th:if="${element.isReference()}">
            <th:block th:if="${entityIndex.containsKey(element.reference)}">
                <fo:basic-link th:if="${entityIndex.get(element.reference).alive}" th:internal-destination="${'segmentId_' + element.reference}">
                    <fo:inline th:text="${scripts.cutString('[' + element.reference + '] ' + entityIndex.get(element.reference).entityName, 92, element.title)}"></fo:inline>
                    <fo:leader th:if="${entityIndex.get(element.reference).alive}" leader-length.minimum="0px" leader-pattern="rule"></fo:leader>
                    <fo:page-number-citation th:if="${entityIndex.get(element.reference).alive}" th:ref-id="${'segmentId_' + element.reference}"></fo:page-number-citation>
                </fo:basic-link>
                <fo:inline th:if="${!entityIndex.get(element.reference).alive}" th:text="${scripts.cutString('[' + element.reference + '] ' + entityIndex.get(element.reference).entityName, 97, element.title)}"></fo:inline>
            </th:block>
            <fo:block th:if="${!entityIndex.containsKey(element.reference)}">REFERENCE NOT FOUND</fo:block>
        </th:block>
    </fo:block>
</th:block>

<th:block th:fragment="reportList (element)">
    <fo:block font-size="0.83em" space-before="0.5em">
        <fo:block font-weight="bold" th:text="${element.title}" keep-with-next="always"/>
        <fo:block th:each="row : ${element.list}" th:text="${'•  ' + row}"/>
    </fo:block>
</th:block>

<th:block th:fragment="xslFo (element)">
    <fo:block th:utext="${element.getCode()}" font-size="10pt" font-family="sans-serif" text-align="left"/>
</th:block>

<th:block th:fragment="table (element)">
    <fo:table table-layout="fixed" th:width="${element.widthPercentage + '%'}" font-size="0.83em" space-before="0.5em">
        <fo:table-column th:each="columnTitle,iterStat : ${element.columnTitles}" th:column-width="${(100 / element.columnTitles.size()) + '%'}"
        th:border-left-style="${iterStat.index != 0} ? 'solid' : 'none'"/>
        <fo:table-header font-weight="bold">
            <fo:table-row>
                <fo:table-cell th:each="columnTitle : ${element.columnTitles}" padding="3px">
                    <fo:block th:text="${columnTitle}"/>
                </fo:table-cell>
            </fo:table-row>
        </fo:table-header>
        <fo:table-body>
            <fo:table-row th:each="row : ${element.tableRows}" border-top-style="solid">
                <fo:table-cell th:each="cell : ${row.content}" padding="3px">
                    <fo:block th:text="${cell}"/>
                </fo:table-cell>
            </fo:table-row>
        </fo:table-body>
    </fo:table>
</th:block>

<th:block th:fragment="svg (element)">
  <fo:block text-align="center" space-before="0.5em">
    <fo:instream-foreign-object width="100%" height="580pt" content-width="scale-to-fit" content-height="scale-to-fit" display-align="center" text-align="center" th:utext="${element.getCode()}"/>
  </fo:block>
</th:block>
