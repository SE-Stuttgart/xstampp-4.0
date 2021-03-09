#API Testing
- The automatic api tests can be found under the file and is created via Postman: **api-tests.postman_collection.json**

- With it being automatic tests, only repeatable HTTP requests are used. Requests like delete will be avoided as it won't get the same result with the token being already deleted.

- The HTTP- request in the projects are mostly( PUT, POST, GET, DELETE).

- The environment file: **tokens.postman_environment.json** contains the values and variables of IDs and Tokens of the requests which are neccessary for the execution.

- Possible Status coming for HTTP-request

    | Status | Meaning |
    |-----|------|
    |200 | Succesfully carried |
    |201 | Succesfully created object |
    |202 | Accepted|
    |204 | No content |
    |400 | Bad Request |
    |401 | Not authorized |
    |403 | Forbidden |
    |404 | Can't be found |

- To test the services of xstampp, the api tests run under the dev server: http://dev.xstampp.de/

To change to a different server to test on, one has to change the value of the variable xstamppURL in the environment file
- Example: http://{{xstamppURL}}/ url used in the collection
- In the environment file : `"key": "**xstamppURL**","value": "**dev.xstampp.de**",	"enabled": true`


With the addition of: `api/auth/`
- For: (groups, login, register, user, project)
- Example: http://dev.xstampp.de/api/auth/login
	 **api/project/**
- Example: : http://dev.xstampp.de/api/project/{projectID}/system-description
- Further documentaion of apis in backend can be found in [here](https://gilbert.informatik.uni-stuttgart.de/stupro/xstampp-4.0/blob/pipeline/api-test/xstampp-docs/api-backend.md)

- For more urls which HTTP-requests can be performed on can be found in the directory:
`xstampp-angular/src/app/services`
- Not every requests is in the automatic collection, due some needing Ids which can only be created by manually interfering like the arrows in the control-structure

- To save values to the defined variables in the environment, so it can be used for other requests, code in the test script catches the result and sets it up
`var jsonData = JSON.parse(responseBody);
postman.setEnvironmentVariable("auth-token", jsonData.token);`

To create new api tests:
- [Tutorial to create api tests with postman](https://www.toolsqa.com/postman/api-testing-with-postman/ "Tutorial to create api tests with postman")
- Import the current collection and environment into your postman set up
- Once done extending the collection, export it
- Replace the current test file with the new one
- If new Ids are made like groupId etc., one has to add the variables in the environment file and replace the current one with the new one

Since the tests are integrated with the pipeline, the test collection will be run once the pipeline starts.

