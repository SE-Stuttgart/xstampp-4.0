Technologies XSTAMPP 4.0
========================

:house:[Home](README.md)

extracted from [Architecture Concept](architecture-description.md)

Content
-------
* [Backend Framework](#backend-framework)
* [Request Proxy](#request-proxy)
* [Database](#database)
* [Backend persistence](#backend-perstistence)
* [Containerization](#containerization)
* [Build tool](#build-tool)
* [Frontend framework](#frontend-framework)
    + [Vaadin](#vaadin)
    + [Vue.js](#vue.js)
    + [Angular](#angular)
* [Frontend Framework selection](#frontend-framework-selection)


See also
--------
* [Required Reading](required-reading.md)



Backend Framework
-----------------
The backend logic will be implemented as web services in Java to provide maximal maintainability in the University of Stuttgart. Furthermore, keeping Java allows us to import classes from the old XSTAMPP 3.0 for import and export. Java SE version 11 will be used, see Table 1 below for a comparison of available java versions.
One of the most powerful web-frameworks in Java is Spring. Therefore a Spring Boot application will be built for the system components. Spring Boot 2.1 supports Java 11.

| Java version  | Release | Notes                                                                                                                         |
|---------------|---------|-------------------------------------------------------------------------------------------------------------------------------|
| 7 and earlier | various | no longer supported                                                                                                           |
| 8             | 2014-03 | Long-term support release. Oracle will start limiting support on January 2019 (updates will have a more restrictive license). |
| 9             | 2017-09 | Short-term support release. No longer supported.                                                                              |
| 10            | 2018-03 | Short-term support release. No longer supported.                                                                              |
| 11            | 2018-09 | Long-term support release.                                                                                                    |
| 12 and later  | not yet | Not yet released                                                                                                              |
Request Proxy
-------------
To provide a general open API for our and other clients we need a proxy to combine all open endpoints of the system components. It also routes all incoming requests to the desired component. For this service a nginx will be used and set up.

Database
--------
The data schema fits in a relational schema. Therefore a relational database system will be selected. PostgreSQL will provide powerful features and performance as most used open-source database. PostgreSQL also provide functionalities to handle JSON objects efficient. This allows to realize an document store with this technology if needed.

The object oriented database was identified as the best approach for storing the data of diagrams like control structure, because the concept provides the best performance. A possible implementation is the Perst embedded database. However, an embedded database does not suit our purposes. Our architecture requires a standalone DB server.

Backend persistence
-------------------
We use Spring and a relational database as technologies. The initial database schema and further migrations are managed via [Flyway Spring Plugin](required-reading.md) in the corresponding spring services.

Hibernate will come with spring integration and brings a easy way to access the database from a Spring service. Hibernate is a open-source persistence layer which provides ORM functionalities.

Containerization
----------------
For easier deployment, the project will be built as containers. That way, all running services will run in the same environment, no matter which host they're run on. Docker is a popular containerization technology so it will be used in this project. Notably, Docker containers can be run in Kubernetes, a cluster management and orchestration system. This will turn out handy if our architecture is to deployed on a big scale (c.f. section [Cloud architecture](architecture-description.md/#cloud-architecture)).

Build tool
----------
The chosen backend framework (Spring) uses Maven as a build tool, and most libraries we use are published in Maven repositories. This leaves us with a choice between Maven and Gradle, two popular build tools.
While Gradle offers more flexibility, Maven is more widespread and has better tool support, for example in IDEs. This is why Maven will be used to build the backend in this project.
The frontend requires a technology-specific build tool: It is compiled to a set of static web resources using npm and ng.

Frontend framework
------------------
This section is about the selection of the frontend framework and the different alternatives with their pros and cons. First the different shortlisted frameworks will be described. Then a frontend technology will be chosen and argued.

### Vaadin
One of the considered frameworks for the frontend, which could also be used for the backend, is Vaadin. It combines frontend and backend in one package and can also be combined with Spring. As such it could have replaced our backend and simplified development by reducing the used technologies if desired or be used easily in combination with our selected backend. Furthermore, we have available knowledge in our team which would was a big factor in our decision making process for the sake of higher productivity.
The Critical Factor against using Vaadin for our Frontend Framework was the required Graph Framework Joint.js which we decided to use for the creation of the Diagrams. With the current version of Vaadin (Vaadin 8), the integration of javascript comes with additional complexity and required effort. Additionally, with using javascript in addition to java, the Advantage of removing the necessity of javascript becomes void.

### Vue.js
A client-side javascript framework for building single-page applications. It is simple to learn and has freely available ui component libraries which components are designed after the material guidelines. Typescript is also supported, this could be an advantage due to the knowledge of the team about statically typed language (e.g. Java). 

### Angular
Angular is a frontend framework designed and implemented by Google. Angular is widely used and has a large and active community. Angular has a open and free component library and also themings for material design which will be used in our web client. Therefore, it is not needed to create and style own components for basic ui interactions. for newer Angular versions (currently Angular 7) its recommended to use Typescript for type safety and more language dependent advantages which can prevent runtime errors.
Angular comes with many build-in features which helps in the development to organize the program structure. Many advanced paradigms (eg. dependency injection or routing) are usable out of the box without additional plugins or third party libraries.
In case third party libraries do need be included, there's an easy way to do it described [here](#using-third-party-libraries).
In Angular there is also available knowledge in the team. This is a big advantage for the productivity of the team. As Angular has a higher entry hurdle. This knowledge can help to spread this knowledge in the team and prevent the team of having a long initial training time.

### Frontend Framework selection
In case that there is already knowledge in the team and the possibilities are promising a big freedom in designing and implementing a great and modern web application we chose Angular as our web frontend framework. We discussed a lot about the different pros and cons and created small prototypes to determine a fitting technology for our needs and came to the result that angular fulfills all our needs and provides a flexible and easy way to archive that. Furthermore, Angular is a widely used tools which is actively developed. This is important for an constant stream of updates and improvements for the future development.
