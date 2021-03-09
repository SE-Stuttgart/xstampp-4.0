<?xml version="1.0" encoding="UTF-8"?>
<fo:root xml:lang="en" font-family="serif" font-size="12pt" line-height="1.4" xmlns:fo="http://www.w3.org/1999/XSL/Format">
  <fo:layout-master-set>
    <fo:simple-page-master page-height="297mm" page-width="210mm" margin-top="10mm" margin-left="10mm" margin-right="10mm" margin-bottom="10mm" master-name="PageMaster">
      <fo:region-body margin-top="10mm" margin-left="10mm" margin-right="10mm" margin-bottom="10mm"/>
      <fo:region-before extent="5mm"/>
      <fo:region-after extent="5mm"/>
    </fo:simple-page-master>
  </fo:layout-master-set>

  <fo:bookmark-tree>
    <fo:bookmark th:if="${config.isTitlePage()}" internal-destination="titlePage">
      <fo:bookmark-title>Title Page</fo:bookmark-title>
    </fo:bookmark>
    <fo:bookmark th:if="${config.isTableOfContents()}" internal-destination="tableOfContents">
      <fo:bookmark-title>Table of Contents</fo:bookmark-title>
    </fo:bookmark>
    <th:block th:each="section : ${sections}">
      <fo:bookmark th:internal-destination="${'section_' + section.title}" th:fragment="bookmark (section)">
        <fo:bookmark-title th:text="${section.title}"/>
        <th:block th:each="subsection : ${section.subsections}">
          <section th:replace="~{:: bookmark (${subsection})}" />
        </th:block>
      </fo:bookmark>
    </th:block>
  </fo:bookmark-tree>

  <th:block th:if="${config.titlePage}">
    <title-page th:replace="~{titlePage :: main}"></title-page>
  </th:block>

  <th:block th:if="${config.tableOfContents}">
    <table-of-contents th:replace="~{reportTableOfContents :: main}"></table-of-contents>
  </th:block>

  <th:block th:each="section : ${sections}">
    <section th:replace="~{reportSection :: main (${section})}"></section>
  </th:block>
</fo:root>
