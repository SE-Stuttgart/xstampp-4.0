System Tests
==========

:house:[Home](README.md)

Content
-------
- [System Tests](#System-Tests)
  - [Content](#Content)
  - [Internal testcase state diagram](#Internal-testcase-state-diagram)
  - [Testcase Documentation](#Testcase-Documentation)
      - [Title](#Title)
      - [Labels](#Labels)
      - [Description](#Description)
      - [Teststeps](#Teststeps)
      - [Attachments](#Attachments)
  - [State](#State)
      - [Testcase state](#Testcase-state)
      - [Teststep state](#Teststep-state)
  - [Testcase execution](#Testcase-execution)
    - [Acceptance Test](#Acceptance-Test)
    - [Regression Test](#Regression-Test)
  - [Bugs](#Bugs)
      - [Errors during acceptance tests](#Errors-during-acceptance-tests)
      - [Final tests at the end of the sprint](#Final-tests-at-the-end-of-the-sprint)

Internal testcase state diagram
--------------------------
| State | Description |
|---------|--------------|
| [NEW] |Testcase is created and linked to issue. |
| [IN CREATION]	| Testcase is filled with data: description, teststeps,... |
| [READY FOR TESTING] | Testcase passed one or more reviews. Testcase can now be used for testing. |
| [OBSOLETE] | Testcase is no longer used or replaced by a different testcase. |

Testcase Documentation
----------------------
A testcase in our test portal consists of the the following fields: Title, Labels, Description, Teststeps, Attachments. These are defined in following manner:
#### Title
Short description of what's being tested, for example „Create Controlstructure“.
#### Labels
Labels categorize testcases by:
*	Frontend component
*	Logical unit (UCA, Control Structure, etc.)
#### Description
In the description field following points have to be considered:
*	Test intention
*	Test requirement
*	Min. version required to run test (for example "xstampp 4.0")
#### Teststeps
A testcase consists of one or more teststeps. A teststep is divided into two segments:
*   Description:
    *	Simple and precise description of the the teststep.
    *	Input values
    *	(Optional) screenshots
*	Result:
    *	Short description of the expected result
    *	(Optional) screenshots
#### Attachments
You can embed images in your text using the regular Markdown syntax: 
```
![alt text](Path/URL of image "Text that is displayed while mouse hovers over image")
```
For attachments a local directory exists on the test portal server. A guide on how to added images to that directory can be found in the test portal.

State
-----------------------
While executing a testcase, the individual teststeps and the whole testcase itself can have different states.  
#### Testcase state
| State| Desciption|
|-------|-------------|
|[UNEXECUTED] |	Testcase has not been executed. |
| [BLOCKED]	| Testcase could not be executed successfully, possibly due to unknown causes. If this state is given, another person has to execute this testcase. |
| [PASS] |	All teststeps have the state "PASS". The testcase has been executed successfully. |
| [FAIL] |	At least one teststep was set to "FAIL". The testcase was not executed successfully. |

#### Teststep state
| State | Description |
|--------|--------------|
| [UNEXECUTED] | Teststep has not been executed. |
| [BLOCKED] | Teststep could not be executed successfully, possibly due to unknown causes. If this state is given, another person has to execute this teststep. |
| [PASS] | Teststep has been successfully executed. |
| [FAIL] | Teststep execution was unsuccessful a second time after being given the state "BLOCKED". |

Testcase execution
------------------
For this project 2 types of testcases will be executed:

### Acceptance Test
Acceptance tests are to be executed before every merge into the master branch. Acceptance tests are defined by the acceptance criteria specified in a story. Every branch has its own designated testcycle. All testcases are added to a testcycle that ensure the acceptance critera are met. Also test cases are added that test all system components affected by the change.

### Regression Test
Regression tests are testcases that are executed at the end of of each sprint. These tests are to ensure the core functionality of the software. Regression tests are to be executed on the dev-server.
At the end of each sprint all acceptance tests are to be executed together with all regression tests in a separate testcycle.  

Bugs
-------------------
In this section we are gonna consider two kinds of bugs that occur during testing:
#### Errors during acceptance tests
If errors occur during execution of acceptance tests, depending on how much time is still available, a fix should be implemented as soon as possible. Is a fix not possible in the time available, a follow up story has to be created. Then all tests affected by the fix have to be executed again.
#### Final tests at the end of the sprint
When an error occurs at the end of the sprint, a time estimation for fixing the error has to be done. Depending on the gravity of the bug/error a new story has to be created in Jira. A major bug that affects the core functionality of the software has to be documented as seperate bug in Jira. If its just a minor bug that doesn't affect any core functionality the bug can be documented in the a story containing a collection of all open minor bugs.
