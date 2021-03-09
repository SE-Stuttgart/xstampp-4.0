XSTAMPP 4.1 Final State Technical Debt
===============================

:house:[Home](README.md)

Content
-------
* [Frontend](#Frontend)
    + [Description](# descirption)
    + [Responsiblity](#responsibilities)
    + [Server Status](#server-status)
    + [Loss Scenarios](#loss-scenarios)
    + [Group Handeling](#group-handeling)
    + [Url Link](#url-link)
    + [Chip Fields](#chip-fields)
    + [Table](#table)
* [Backend](#backend)

Frontend
--------
#### Description
For the following entities the user is not able to create a description:
* sub safety constraints 
* hazards
* Controller constraints

#### Responsibility
* hover for contraints(especially  longer texts)
* preselection for the chips is missing

#### Server Status
* server connection status should be checked

#### Loss Scenarios
* only allow options which are connected to the controller(source and target should be loaded automatically based on arrow)
* refactor CreateLossScenariocomponent (Button Poistion, etc.)
* decription should be optional
* fields which must not be empty, should be marked (e.g. red edges)
* controller should be shown accordingly to the controll action 
* when "Inadequate process model" is selected, the input box and the field sensor should be removed, because it can be derived from feedback/input
    + only input or feedback should be displayed
* when "Inadequate control algorithm" is selected, the field control action should be removed
* a second control action attribut should be added

#### Group Handeling
* if the user tries to edit himself on the group-handeling view, a meaningless error is thrown

#### Url Link
* URL Link to specific view in specific project correct, but no project token aquired
* some views can be called by using the url without being loged in first
    + you should be transmitted to the login page with an error message

#### Chip Fields
* if the focus on the chip field is lost, the search does not reset

#### Table
* sort table after editing 


Backend
-------
* lear error messages in lock service
* remove all sonarqube findings
* backend is overengineered 
    + if you refactor it, make it easier
