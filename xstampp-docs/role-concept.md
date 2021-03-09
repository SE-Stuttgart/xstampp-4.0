XSTAMPP 4.0 Role Concept
========================

:house:[Home](README.md)


There are two types of roles defined: group dependent and system dependent roles.
 
| Group Dependent Roles 	| System Dependent Role 	|
|-----------------------	|-----------------------	|
| Guest                 	| System Admin          	|
| Developer             	|                       	|
| Analyst               	|                       	|
| Group Leader          	|                       	|
 
Each user can have different privileges in each group.
The roles have different types of rights.

| Rights / Roles            	| System Admin 	| Group Admin 	| Analyst 	| Developer 	| Guest 	|
|---------------------------	|--------------	|-------------	|---------	|-----------	|-------	|
| system wide configuration 	| ✔            	| ✘           	| ✘       	| ✘         	| ✘     	|
| add/delete user to group  	| ✔            	| ✔           	| ✘       	| ✘         	| ✘     	|
| create project            	| ✔            	| ✔           	| ✔       	| ✘         	| ✘     	|
| delete project            	| ✔            	| ✔           	| ✔       	| ✘         	| ✘     	|
| edit project              	| ✔            	| ✔           	| ✔       	| ✔         	| ✘     	|
| view project              	| ✔            	| ✔           	| ✔       	| ✔         	| ✔     	|

The authentication is enforced via access token as described in [login process](login-process.md#recommendation-in-the-context-of-xstampp-40).