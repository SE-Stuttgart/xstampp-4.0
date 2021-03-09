<?xml version="1.0" encoding="UTF-8"?>
<fo:root xml:lang="en" font-family="serif" font-size="12pt" line-height="1.4" 
  xmlns:fo="http://www.w3.org/1999/XSL/Format">
  <fo:layout-master-set>
    <fo:simple-page-master page-height="297mm" page-width="210mm" margin-top="10mm" margin-left="10mm" margin-right="10mm" margin-bottom="10mm" master-name="PageMaster">
      <fo:region-body margin-top="10mm" margin-left="10mm" margin-right="10mm" margin-bottom="10mm"/>
      <fo:region-after extent="5mm"/>
    </fo:simple-page-master>
  </fo:layout-master-set>

  <fo:page-sequence master-reference="PageMaster">
    <fo:static-content flow-name="xsl-region-after">
      <fo:block text-align="center" font-size="0.9em">
        -        <fo:page-number />
 -
      </fo:block>
    </fo:static-content>
    <fo:flow flow-name="xsl-region-body">
      <fo:block font-size="3.0em" text-align="center" font-weight="bold" margin-bottom="0.5em" th:attrappend="text-decoration=${underlineTitle}?underline">
        Table Report
      </fo:block>

      <fo:block space-before="0.5em" space-after="1.0em" keep-together.within-page="always">

        <fo:table table-layout="fixed" table-omit-name-at-break="true">

          <fo:table-column column-number="1" column-width="20mm"></fo:table-column>
          <fo:table-column column-number="2" column-width="40mm"></fo:table-column>
          <fo:table-column column-number="3" column-width="100mm"></fo:table-column>

          <fo:table-name background-color="#919191" color="white">
            <fo:table-row border-start-style="solid" border-end-style="solid" border-before-style="solid" border-width="0.1mm">
              <fo:table-cell padding="1.5mm" border-end-style="solid" border-width="0.1mm">
                <fo:block font-weight="bold" text-align="center">ID</fo:block>
              </fo:table-cell>
              <fo:table-cell padding="1.5mm">
                <fo:block font-weight="bold" text-align="center">Name</fo:block>
              </fo:table-cell>
              <fo:table-cell padding="1.5mm">
                <fo:block font-weight="bold" text-align="center">Description</fo:block>
              </fo:table-cell>
            </fo:table-row>
          </fo:table-name>

          <fo:table-body th:each="loss : ${losses}">

            <!--row-->
            <fo:table-row>

              <!-- ID -->
              <fo:table-cell border="solid thin">
                <fo:block linefeed-treatment="preserve" text-align="center" th:text="${{loss.id.id}}">
                </fo:block>
              </fo:table-cell>

              <!-- name -->
              <fo:table-cell border="solid thin">
                <fo:block linefeed-treatment="preserve" text-align="left" margin-left="0.5em" th:text="${{loss.name}}">
                </fo:block>
              </fo:table-cell>

              <!-- description -->
              <fo:table-cell border="solid thin">
                <fo:block linefeed-treatment="preserve" text-align="left" margin-left="0.5em" th:text="${{loss.description}}">
                </fo:block>
              </fo:table-cell>

            </fo:table-row>

          </fo:table-body>
        </fo:table>

      </fo:block>

    </fo:flow>
  </fo:page-sequence>
</fo:root>