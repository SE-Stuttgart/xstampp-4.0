XSTAMPP 4.1 Future Work On XStampp 4.1


:house:[Home](README.md)

Content
-------

* [Process Model](#process-model)
* [Loss Scenario](#loss-scenario)
* [Import Export](#import-export)
    + [Import](#import)
    + [Export](#export)
* [Dashboard](#dashboard)
* [Technical Debt](#technical-debt)

Process Model
-------------
* there should be a warning in case a process model is deleted and it still has process variable(s)
    + right now the process variable(s) will be deleted also
    + the warning should appear in table view as well as in graphik view

Loss Scenario
-------------
* should be implemented more generic

Import Export
-------------
#### Import
* the import from the old XStampp 3.0 to Xteampp 4.1 with .hazx file format works but the graphik representation of the control structure still needs work

#### Export
* if the control structure is exported as an svg graphik there should be an explanation
    + in case the svg graphik is exported as a colored graphik as well as not
    + the explanation should also be in the report

Dashboard
---------
The current dashboard component is only a click-dummy and should be implemented, as well as tested in the future. The necessary data is not processed by the backend right now. Therefore a refactoring of the backend componente (as mentioned here:[Final State Technical Debt](final-state-technical-debt.md)) should be inevitable. In order to test the dashboard in the frontend, simply add the following sequence to the url: `project-overview /:dashboard`

Technical Debt
--------------
All items, which are still listed in the following document, should be resolved: [Final State Technical Debt](final-state-technical-debt.md)
