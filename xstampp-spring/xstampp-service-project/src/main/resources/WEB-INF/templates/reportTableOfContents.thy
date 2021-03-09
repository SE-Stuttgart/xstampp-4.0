<?xml version="1.0" encoding="UTF-8"?>
<fo:page-sequence master-reference="PageMaster" th:fragment="main">
    <header-footer th:replace="~{reportSection :: headerFooter}"></header-footer>
    <fo:flow flow-name="xsl-region-body">
        <fo:block font-size="3.0em" text-align="center" font-weight="bold" margin-bottom="0.5em" id="tableOfContents">
            Table of Contents
        </fo:block>
        <fo:block th:each="section : ${scripts.getTableOfContentsIterator(sections)}" text-align-last="justify" th:margin-left="${section.layer - 1 + 'em'}" space-before="0.3em">
            <fo:basic-link th:internal-destination="${'section_' + section.title}" th:attrappend="font-weight=${section.subsections.size() != 0} ? 'bold'">
                <fo:inline th:text="${section.title}"></fo:inline>
                <fo:leader leader-pattern="rule"></fo:leader>
                <fo:page-number-citation th:ref-id="${'section_' + section.title}"></fo:page-number-citation>
            </fo:basic-link>
        </fo:block>
    </fo:flow>
</fo:page-sequence>
