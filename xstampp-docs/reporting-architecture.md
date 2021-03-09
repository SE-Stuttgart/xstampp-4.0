# Reporting - Architecture

:house:[Home](README.md)

The reporting system allows anyone with access to a XSTAMPP project to export the project's 
contents into a printable PDF. The user can make some configurations regarding what 
should be included in the report.

To generate the Report PDF, we use [Thymeleaf](https://www.thymeleaf.org/) (a template engine) and [Apache FOP](https://xmlgraphics.apache.org/fop/) (a XSL-FO renderer).

**Processing steps (Backend)**
1. The server receives a report request along with a configuration DTO, specifying what sections should be included into the report.
2. Retrieving and collecting all entities in the currently opened project from the database.
3. Asking all entities to be included in the report, to generate their ReportSegment. Each entity builds its segment on its own, using building blocks like headers, paragraphs, lists, tables and more.
4. All those segments get categorized into sections, each entity-type has its own section. The result is a tree of sections, segments and building blocks, containing the entire report's content.
5. Every segment also got indexed, so that entities can reference other entities by page and by link, using their unique id.
6. The Thymeleaf engine now uses the tree and the index list to apply the data to its Thymeleaf templates. The templates specify how all the building blocks are supposed to look like in the final document. The product is a valid XSL-FO file.
7. Apache FOP accepts this XSL-FO file to render it into a finished PDF.
8. The server sends the PDF as a response back to the requesting client.
