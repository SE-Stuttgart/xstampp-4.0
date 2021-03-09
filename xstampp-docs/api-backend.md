This is the documention of the XSTAMPP 4.0
The goal of this document is to help to understand how to access or modify Data via HTTP-requests.
Therefore description of: 
* Type (e.g Post/Get/PUT,...)
* necessary HTTP-Headers
* necessary HTTP-Body 
* HTTP-Queries
* the expected return (Status and Body e.g. HTTP status = 200 and decription of the returned data)

are given in this document.

Example HTTP-Query

http://dev.xstampp.de/api/auth/group/27997862-dd53-4d8a-8d6f-35810366b3ec

Server Address | Query
-------------- | -----
http://dev.xstampp.de | /api/auth/group/27997862-dd53-4d8a-8d6f-35810366b3ec

<br>

### **All APIs calls for project xstampp 4.0 related to the usage of STPA inside a project can be found in the** [xstampp4_0-API.json](https://gilbert.informatik.uni-stuttgart.de/stupro/xstampp-4.0/blob/pipeline/api-test/xstampp-docs/xstampp4_0-API.json) **file, which is a JSON file in OpenAPI format. It is recommended to open that file with http://editor.swagger.io/. <br> This document will only describe API call which are related to Authentification, Projects, Member/User and Group handling.**
### **[The APIs calls for xstampp 4.1](https://gilbert.informatik.uni-stuttgart.de/stupro/xstampp-4.0/blob/pipeline/api-test/xstampp-docs/xstampp4_1-API.postman_collection.json) is created via POSTMAN and needs to be imported to POSTMAN to open the file. The file contains all the calls of the previous xstampp 4.0 and the new api calls of xstampp 4.1. Any further documentation of the requests are in the file. To view the documentation, this [tutorial](https://learning.getpostman.com/docs/postman/api_documentation/viewing_documentation) can be helpful.**

### Authentification API
 * [login](#login) retrieves USER_TOKEN
 * [register](#register)
 * [projectTokenRequest](#projectTokenRequest) retrieves PROJEKT_TOKEN

### Project API
 * [create Project](#createProject)
 * [edit Project](#editProject)
 * [delete Project](#deleteProject)
 * [retrieve Project](#retrieveProject)

### Member/User adminstration API
 * [delete user](#deleteUser)
 * [change password](#changePasswordOfUser)
 * [list all users](#listAllUsers)

### Group API
 * [create group](#createGroup)
 * [delete group](#deleteGroup)
 * [alter group](#alterGroup)
 * [get group](#getGroup)
 * [join member](#joinMember)
 * [remove member](#removeMember)
 * [change accesslevel of member](#changeAccessLevel)
 * [get all projects for group](#getAllProjectsForGroup)
 * [get all groups of user](#getAllProjectsOfUser) 
 * [get all users of a group](#getAllUsersOfGroup) 
 * [get all groups of system](#getAllGroups) System Admin
 * [get all groups without group adming](#getAllAbandonedGroups) System Admin




# Authentification-Requests

## <a name="login"></a>**/api/auth/login**
returns a json with the usertoken

Description | Detailed
-------------- | -----
Type | POST
Necessary HTTP-Header | Not necessary
Necessary HTTP-Body | JSON with `{"email": "a@b.c", "password": "abc}`
Returns |**STATUS 200 OK:** <br> If HTTP Body is correct with expected Keys expected JSON and credentials are correct <br> **Body:** JSON File with the **USERTOKEN**`{"status":"SUCCESS","token":"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"}`<br><br>**STATUS 400 Bad Request:** <br> If Password is wrong or sent JSON is not valid <br> **Body:** Depending on the "Accept" header, either JSON or HTML error page. Ex: `{"timestamp":"2018-11-26T14:46:00.644+0000","status":400,"error":"Bad Request","message":"Login failed. Check your username and/or password.","path":"/api/auth/login"}` <br><br> **Status 500 Internal Server Error:** <br> If JSON in body is not correct e.g. send JSON file has different keys ) <br>**Body:** `{"timestamp": "2018-11-28T16:27:57.214+0000","status": 500,"error": "Internal Server Error","message": "More Detailed Error Message"`
}

## <a name="register"></a>**/api/auth/register**

Description | Detailed
-------------- | -----
Type | POST
Necessary HTTP-Header | Not necessary
Necessary HTTP-Body | JSON with `{"email": "mustermann@exmaple.com", "displayName": "Max Mustermann", "password": "passwordHere", "passwordRepeat": "passwordHere"}`
Returns |**STATUS 200 OK:** <br> If HTTP Body is correct JSON and credentials are correct <br> **Body**: JSON File with `{"status":"SUCCESS"}` <br><br> **STATUS 400 Bad Request:** <br> If sent JSON is not valid <br> **Body:**`{"timestamp": "2018-11-28T16:27:57.214+0000","status": 400,"error": "Internal Server Error","message": "More Detailed Error Message", "path": "/api/auth/register"` <br><br> **Status 500 Internal Server Error:** <br> e.g. if useremail already exist <br>**Body:** `{"retriable":false,"error":"AUTH-9002","status":500,"message":"Failed creating user.","detailsOptional":null,"timestamp":"2019-04-01T08:54:51.38238Z"}`   

## <a name="projectTokenRequest"></a>**/api/auth/project-token**
Returns a json with the projectoken

Description | Detailed
-------------- | -----
Type | POST
Necessary HTTP-Header | `Authorization: jwt.token.here` with the token obtained from the login call
Necessary HTTP-Body | JSON with `{"projectId": "ABCDEFG"}`
Returns |**STATUS 200 OK:** <br> If HTTP Body is correct JSON and credentials are correct <br> **Body**: JSON File with **PROJECTTOKEN**`{"status":"SUCCESS","token":"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"}` 

# Project

##  <a name="createProject"></a> **/api/auth/project** 
Creates a new project

Description | Detailed
-------------- | -----
Type | POST
Necessary HTTP-Header | `Authorization: jwt.token.here` with the token obtained from the login call
Necessary HTTP-Body | JSON with `{"id":"","description":"","name":"example project name","referenceNumber":"","groupId":"27997862-dd53-4d8a-8d6f-35810366b3ec"}` 
Returns | **Status 200 OK:** <br> **Body:** `{"id":"dd8ad63f-6b6b-436c-b8b6-ef625c419dc9","name":"example project name","description":"","groupId":"27997862-dd53-4d8a-8d6f-35810366b3ec","referenceNumber":"","createdAt":1554109849827}`

##  <a name="editProject"></a> **/api/auth/project/{projId}** 
Edits an existing project

Description | Detailed
-------------- | -----
Type | PUT
Necessary HTTP-Header | `Authorization: jwt.token.here` with the token obtained from the login call
Necessary HTTP-Body | JSON with `{"id":"57b819a0-8996-4a16-b459-b633e9efcd62","description":"new description","name":"example project name 1","referenceNumber":""}` 
Returns | **Status 200 OK:** <br> **Body:** `{"id":"57b819a0-8996-4a16-b459-b633e9efcd62","name":"example project name 1","description":"new description","groupId":"27997862-dd53-4d8a-8d6f-35810366b3ec","referenceNumber":"","createdAt":1554109637662}`

##  <a name="deleteProject"></a> **/api/auth/project/{projId}** 
deletes a project

Description | Detailed
-------------- | -----
Type | DELETE
Necessary HTTP-Header | `Authorization: jwt.token.here` with the token obtained from the login call
Necessary HTTP-Body | not necessary 
Returns | **Status 200 OK:** `{"status":"SUCCESS"}`

##  <a name="retrieveProject"></a> **/api/auth/project/{projId}** 
retrieves a project

Description | Detailed
-------------- | -----
Type | GET
Necessary HTTP-Header | `Authorization: jwt.token.here` with the token obtained from the login call
Necessary HTTP-Body | none
Returns | **Status 200 OK:** <br> **Body:**   {"id":"57b819a0-8996-4a16-b459-b633e9efcd62","name":"example project name 1","description":"new description","groupId":"27997862-dd53-4d8a-8d6f-35810366b3ec","referenceNumber":"","createdAt":1554109637663}


# User 
##  <a name="deleteUser"></a> **/api/auth/user/{userId}** 
deletes a user requires sysAdmin rights (user token)

Description | Detailed
-------------- | -----
Type | DELETE
Necessary HTTP-Header | `Authorization: jwt.token.here` with the token obtained from the login call
Necessary HTTP-Body | none
Returns | **Status 200 OK:** <br> **Body:**    returns {"status":"SUCCESS"} or {"status":"ERROR"}

##  <a name="changePasswordOfUser"></a> **/api/auth/user/{userId}/password** 
changes password of a user requires sysAdmin rights (user token)

Description | Detailed
-------------- | -----
Type | PUT
Necessary HTTP-Header | `Authorization: jwt.token.here` with the token obtained from the login call
Necessary HTTP-Body |JSON with {"adminPassword":"THE_ADMIN_PW","newPassword":"ü","newPasswordRepeat":"ü"}
Returns | **Status 200 OK:** <br> **Body:**   returns {"status":"SUCCESS"} or {"status":"ERROR"}

##  <a name="listAllUsers"></a> **/api/auth/user/search** 
list all users requires sysAdmin rights (user token)

Description | Detailed
-------------- | -----
Type | POST
Necessary HTTP-Header | `Authorization: jwt.token.here` with the token obtained from the login call
Necessary HTTP-Body |JSON with PageRequestDTO compare to api/project API {"from":0,"amount":100}
Returns | **Status 200 OK:** <br> **Body:**    returns an array of Users [{"uid":"ad18d0ee-498c-4ce6-b304-f74c512a3d22","email":"a@a","displayName":"Anastazia","lockedUntil":null,"systemAdmin":false}, {"uid":"b7582faf-f1ac-4445-b5f9-59e4aa4d5c72","email":"w@w","displayName":"w","lockedUntil":null,"systemAdmin":false}]



# Group

##  <a name="createGroup"></a> **/api/auth/group** 
create a new group, requires a valid usertoken 

Description | Detailed
-------------- | -----
Type | POST
Necessary HTTP-Header | `Authorization: jwt.token.here` with the token obtained from the login call
Necessary HTTP-Body |JSON with `{"name": "example group name", "description":""}`
Returns | **Status 200 OK:** <br> **Body:**   returns `{"name": "example group name", "description":""}`

##  <a name="deleteGroup"></a> **/api/auth/group/{groupId}** 
deletes a new group, requires a valid usertoken with rights to delete group (Group Adminn, System Admin)

Description | Detailed
-------------- | -----
Type | DELETE
Necessary HTTP-Header | `Authorization: jwt.token.here` with the token obtained from the login call
Necessary HTTP-Body | no body necessary
Returns | **Status 200 OK:** <br> **Body:**   returns `{"status":"SUCCESS"}` or `{"status":"ERROR"}`

##  <a name="alterGroup"></a> **/api/auth/group/{groupId}** 
alter a group, requires a valid usertoken with rights to edit group (Group Adminn, System Admin)

Description | Detailed
-------------- | -----
Type | PUT
Necessary HTTP-Header | `Authorization: jwt.token.here` with the token obtained from the login call
Necessary HTTP-Body |JSON with `{"name": "example group edited name", "description":""}`
Returns | **Status 200 OK:** <br> **Body:**   returns `{"name": "example group edited name", "description":""}`

##  <a name="getGroup"></a> **/api/auth/group/{groupId}** 
retrieve a group, requires a valid usertoken and you need to be part of this group

Description | Detailed
-------------- | -----
Type | GET
Necessary HTTP-Header | `Authorization: jwt.token.here` with the token obtained from the login call
Necessary HTTP-Body | no body necessary
Returns | **Status 200 OK:** <br> **Body:**   returns `{"uuid": "xxxx", "name": "example group edited name", "description":""}`




##  <a name="joinMember"></a> **/api/auth/group/{groupId}/member/{userIdOrEmail}** 
add a user to a group, requires a valid usertoken with rights to add users to group (Group Adminn, System Admin)
Predefined AccessLevels are GUEST, DEVELOPER, ANALYST, GROUP_ADMIN

Description | Detailed
-------------- | -----
Type | POST
Necessary HTTP-Header | `Authorization: jwt.token.here` with the token obtained from the login call
Necessary HTTP-Body | JSON with `{"accessLevel": "GUEST"}`
Returns | **Status 200 OK:** <br> **Body:**   returns `{"status":"SUCCESS"}` or `{"status":"ERROR"}`

##  <a name="removeMember"></a> **/api/auth/group/{groupId}/member/{userIdOrEmail}** 
remove a user from a group, add a user to a group, requires a valid usertoken with rights to remove users from group (Group Adminn, System Admin) 

Description | Detailed
-------------- | -----
Type | DELETE
Necessary HTTP-Header | `Authorization: jwt.token.here` with the token obtained from the login call
Necessary HTTP-Body | not necessary
Returns | **Status 200 OK:** <br> **Body:**   returns `{"status":"SUCCESS"}` or `{"status":"ERROR"}`

##  <a name="changeAccessLevel"></a> **/api/auth/group/{groupId}/member/{userIdOrEmail}** 
change accesslevel of a user inside a a group, requires a valid usertoken with rights to change user accesslevel inside group (Group Adminn, System Admin)
Predefined AccessLevels are GUEST, DEVELOPER, ANALYST, GROUP_ADMIN

Description | Detailed
-------------- | -----
Type | PUT
Necessary HTTP-Header | `Authorization: jwt.token.here` with the token obtained from the login call
Necessary HTTP-Body | JSON with `{"accessLevel": "DEVELOPER"}`
Returns | **Status 200 OK:** <br> **Body:**   returns `{"status":"SUCCESS"}` or `{"status":"ERROR"}`




##  <a name="getAllProjectsForGroup"></a> **/api/auth/group/{groupId}/projects** 
retrieve all projects for specified group, requires a valid usertoken and you need to be part of this group

Description | Detailed
-------------- | -----
Type | GET
Necessary HTTP-Header | `Authorization: jwt.token.here` with the token obtained from the login call
Necessary HTTP-Body | no body necessary
Returns | **Status 200 OK:** <br> **Body:**   returns an array of projects

##  <a name="getAllProjectsOfUser"></a> **/api/auth/user/{userId}/groups** 
retrieve all projects of the a user, requires a valid usertoken and System Admin rights <br>
or retireve all projects of the logged in user (your own projects),  requires a valid usertoken and the userid inside your token has to match with the {userId} in the url parameter.

Description | Detailed
-------------- | -----
Type | GET
Necessary HTTP-Header | `Authorization: jwt.token.here` with the token obtained from the login call
Necessary HTTP-Body | no body necessary
Returns | **Status 200 OK:** <br> **Body:**   returns an array of projects

##  <a name="getAllUsersOfGroup"></a> **/api/auth/group/{groupId}/users** 
retrieve all users of a group, requires a valid usertoken with rights to view users inside group (Group Adminn, System Admin)

Description | Detailed
-------------- | -----
Type | POST
Necessary HTTP-Header | `Authorization: jwt.token.here` with the token obtained from the login call
Necessary HTTP-Body | no body necessary
Returns | **Status 200 OK:** <br> **Body:**   returns an array of users




##  <a name="getAllGroups"></a> **/api/auth/group/search** 
retrieve all groups inside the system, requires a valid usertoken System Admin rights

Description | Detailed
-------------- | -----
Type | POST
Necessary HTTP-Header | `Authorization: jwt.token.here` with the token obtained from the login call
Necessary HTTP-Body | needs a PageRequestDTO e.g. `{"amount":0,"from": 100}`
Returns | **Status 200 OK:** <br> **Body:**   returns an array of all groups in the system

##  <a name="getAllAbandonedGroups"></a> **/api/auth/group/search/abandoned** 
retrieve all groups inside the system, which have no Group Admin( -> no Member inside that group has rights to edit/delete the group) , requires a valid usertoken System Admin rights

Description | Detailed
-------------- | -----
Type | POST
Necessary HTTP-Header | `Authorization: jwt.token.here` with the token obtained from the login call
Necessary HTTP-Body | needs a PageRequestDTO e.g. `{"amount":0,"from": 100}`
Returns | **Status 200 OK:** <br> **Body:**   returns an array of all groups in the system




