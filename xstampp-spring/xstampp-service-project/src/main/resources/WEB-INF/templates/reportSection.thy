<?xml version="1.0" encoding="UTF-8"?>
<th:block th:fragment="main (thisSection)">
    <th:block th:if="${thisSection.layer > 1}">
        <section th:replace="~{:: inPage (${thisSection})}"></section>
    </th:block>
    <th:block th:if="${thisSection.layer == 1}">
        <fo:page-sequence master-reference="PageMaster">
            <header-footer th:replace="~{:: headerFooter}"></header-footer>
            <fo:flow flow-name="xsl-region-body">
                <section th:replace="~{:: inPage (${thisSection})}"></section>
            </fo:flow>
        </fo:page-sequence>
    </th:block>
</th:block>

<th:block th:fragment="inPage (thisSection)">
<fo:block th:attrappend="font-size=${scripts.calculateTitleSize(thisSection.layer)},text-align=${thisSection.layer == 1}? 'center'" font-weight="bold" 
        keep-with-next="always" space-before="1em" space-after="0.5em" th:text="${thisSection.title}" th:id="${'section_' + thisSection.title}"/>

<segments th:replace="~{reportStructureElements :: segments (${thisSection.segments})}"></segments>

<th:block th:each="section : ${thisSection.subsections}">
    <section th:replace="~{reportSection :: main (${section})}"></section>
</th:block>
</th:block>

<th:block th:fragment="headerFooter">
    <fo:static-content flow-name="xsl-region-before">
        <fo:block text-align="right" font-size="0.75em" th:text="${currentDate}"/>
    </fo:static-content>
    <fo:static-content flow-name="xsl-region-after">
        <fo:block text-align="center" font-size="0.9em">
            - <fo:page-number /> -
        </fo:block>
    </fo:static-content>
</th:block>
