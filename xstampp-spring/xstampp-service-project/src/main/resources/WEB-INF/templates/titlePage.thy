<?xml version="1.0" encoding="UTF-8"?>
<fo:page-sequence master-reference="PageMaster" th:fragment="main">
    <fo:flow flow-name="xsl-region-body" display-align="center">
      <fo:block-container height="100%" display-align="center" text-align="center" id="titlePage">
        <fo:block>XSTAMPP 4.1 Report</fo:block>
        <fo:block font-size="3.0em" text-align="center" font-weight="bold" th:text="${config.reportName}"/>
        <fo:block th:text="${currentDate}"/>
      </fo:block-container>
    </fo:flow>
</fo:page-sequence>
