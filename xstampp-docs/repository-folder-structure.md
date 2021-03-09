XSTAMPP 4.0 Repository Folder Structure
=======================================

:house:[Home](README.md)

Content
-------
* [Backend](#backend) (Spring)
* [Frontend](#frontend) (Angular)
* [Deploy](#deploy)
* [Documentation](#documentation)

Backend
-------

The two most important services in the backend are the project and auth service respectively. 
Both of these services are seperated into three logical layers. The REST Controller layer which accepts requests from outside the service and returns the results,
the Data Service layer which processes these requests and the DAO layer which sends requests over hibernate to the databases.
Furthermore, both services posess data entity classes which reflect the tables in the database 
and dto classes which are filled with the information transmited over the REST call.

Notable for the project service is that the data entities and dto's are split into Control Structure and non Control Structure entities.



Frontend
--------
The angular application is stored in xstampp-angular, as the source code is located in `src/app/`. In this folder system-wide components, types and services are filed. In `common` the different generic components used in the project can be found. They are described in more detail in [Component Catalog](component-catalog.md). The different views for the STPA process are stored in `sub-app`. For more information see also [system composition](system-composition.md).

Deploy
------
This folder contains scripts and configuration files for deploying the system. See [admin documentation](admin-documentation.md) for further information.

Documentation
-------------
All documents are stored in the documentation folder in markdown format. The main file is [README](README.md). It contains the structure and links to all files. Images are all stored in the images subfolder.