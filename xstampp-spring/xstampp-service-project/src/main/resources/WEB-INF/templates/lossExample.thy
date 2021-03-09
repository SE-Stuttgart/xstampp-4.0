<?xml version="1.0" encoding="UTF-8"?>
<fo:root xml:lang="en" font-family="serif" font-size="12pt" line-height="1.4" xmlns:fo="http://www.w3.org/1999/XSL/Format">
  <fo:layout-master-set>
    <fo:simple-page-master page-height="297mm" page-width="210mm" margin-top="10mm" margin-left="10mm" margin-right="10mm" margin-bottom="10mm" master-name="PageMaster">
      <fo:region-body margin-top="10mm" margin-left="10mm" margin-right="10mm" margin-bottom="10mm"/>
      <fo:region-after extent="5mm"/>
     </fo:simple-page-master>
    </fo:layout-master-set>

  <fo:page-sequence master-reference="PageMaster">
    <fo:static-content flow-name="xsl-region-after">
      <fo:block text-align="center" font-size="0.9em">
        - <fo:page-number /> -
      </fo:block>
    </fo:static-content>
    <fo:flow flow-name="xsl-region-body">
      <fo:block font-size="3.0em" text-align="center" font-weight="bold" margin-bottom="0.5em" th:attrappend="text-decoration=${underlineTitle}?underline">
        Example Report
      </fo:block>

      <fo:block space-before="0.5em" space-after="1.0em" keep-together.within-page="always" th:each="loss : ${losses}">
        <fo:block font-size="1.5em" font-weight="bold" th:text="${{loss.name}}"></fo:block>
        <fo:block space-after="0.5em" th:text="${{loss.id.id}}"></fo:block>
        <fo:block space-after="0.5em" text-align="justify" th:text="${{loss.description}}"></fo:block>
      </fo:block>

    </fo:flow>
  </fo:page-sequence>
</fo:root>