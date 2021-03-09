XSTAMPP 4.0 Login Process
=========================

:house:[Home](README.md)

Content
-------
* [Password Storage](#password-storage)
    + [Simple Hash Algorithms](#simple-hash-algorithms)
    + [Multi-Round Algorithms](#multi-round-algorithms)
    + [Recommendation in the context of XSTAMPP 4.0](#recommendation-in-the-context-of-xstampp-40)
* [Request Authorization](#request-authorization)
    + [Basics](#basics)
    + [JSON Web Tokens](#json-web-tokens)
    + [Tokens in XSTAMPP 4.0](#tokens-in-xstampp-40)
    + [Long-lived token](#long-lived-token)
    + [Short-lived token](#short-lived-token)
* [Stored information](#stored-information)
* [Data flow](#data-flow)


Password Storage
----------------
When storing passwords, the methods of storage should be adapted to the worst-case scenario: An attacker has compromised the user database and can see all contents.
For this reason, passwords should be stored as their _hash value_<sup>1</sup>. However, an attacker can still try to crack these hashes. Measures should be taken to ensure this is as hard as possible.
One way to make cracking the hashes harder is salting the hashes. In short, a random value is hashed together with the password to prevent easy lookups in a pre-computed list of hashes (a rainbow table). The random value is stored alongside the hash. For more details see https://en.wikipedia.org/wiki/Salt_(cryptography) 

Going further, there are functions with deliberately expensive algorithms. Such functions are are suitable for key derivation (hence the name key derivation function, KDF) and for password storage. They are typically configurable with a number of rounds that specify how expensive the function should be. This is done to make cracking password hashes more computationally expensive. The exact choice for the number of rounds is a trade-off between performance and security.
Some notable algorithms are laid out the following sections. Algorithms that are irrelevant for us (for example due to insufficient security such as MD-5 or SHA-1) are not shown.

<sup>1</sup>Recommended by BSI, among others: https://www.bsi.bund.de/DE/Themen/ITGrundschutz/ITGrundschutzKataloge/Inhalt/_content/m/m04/m04401.html 




### Simple Hash Algorithms


|                     | SHA-2 family                                                            | Keccak, SHA-3 family                                                                       |
|---------------------|-------------------------------------------------------------------------|--------------------------------------------------------------------------------------------|
| output length (bit) | 224 or 256 or 384 or 512                                                | 224 or 256 or 384 or 512 or variable (not better than 512)                                 |
| status              | superseded by SHA-3                                                     | up-to-date                                                                                 |
| age                 | standardized by NIST in 2001                                            | Keccak created before 2009, standardized with specific parameters by NIST as SHA-3 in 2015 |
| BSI                 | (S. 40) “als kryptographisch stark eingeschätzt” in BSI TR-02102-1<sup>2</sup> | (S. 40) “als kryptographisch stark eingeschätzt” in BSI TR-02102-1<sup>2</sup>      |
| support in Java     | supported                                                               | supported starting with Java 9                                                             |

<sup>2</sup>https://www.bsi.bund.de/SharedDocs/Downloads/DE/BSI/Publikationen/TechnischeRichtlinien/TR02102/BSI-TR-02102.pdf



### Multi-Round Algorithms

|                     	| PBKDF2                                        	| bcrypt                        	| scrypt             	|
|---------------------	|-----------------------------------------------	|-------------------------------	|--------------------	|
| output length (bit) 	| variable                                      	| 184                           	| variable           	|
| age                 	| 2000                                          	| 1999                          	| 2009               	|
| RAM required        	| constant and very little (weak against ASICs) 	| constant (weak against ASICs) 	| configurable       	|
| CPU time required   	| configurable                                  	| configurable                  	| configurable       	|
| NIST                	| recommended<sup>3</sup>                       	| no mention                    	| no mention         	|
| BSI                 	| recommended but not in an official TR<sup>4</sup>| no mention                    	| no mention         	|
| Support in Java     	| supported                                     	| requires a library            	| requires a library 	|


<sup>3</sup>https://doi.org/10.6028/NIST.SP.800-132  
<sup>4</sup>The BSI does not mention any Multi-round algorithms in a Technische Richtlinie. PBKDF2 and bcrypt are mentioned in another document for specific applications: https://www.allianz-fuer-cybersicherheit.de/ACS/DE/_/downloads/BSI-CS_069.pdf. 


### Recommendation in the context of XSTAMPP 4.0
In line with the previous comparison, XSTAMPP 4.0 should probably use seeded PBKDF2 with a sufficiently secure underlying hash algorithm (such as SHA-512) as well as a sensible round choice parameter (for example 10 000). That is a good compromise between performance and security: 10 000 rounds or SHA-512 require 20-25 ms per hash operation on a OpenJDK 64-Bit Server VM per thread on an Intel® Core™ i7-4910MQ CPU.

If performance problems arise, the round parameter can be reduced or the hashes can be migrated to another algorithm such as seeded SHA-512. These kinds of changes are possible with an existing database, data migration is to be performed slowly: All newly registered users will get password hashes with the new parameters or algorithms. When an existing user logs in with old parameters or algorithms and their password matches the hash, the password is to be immediately re-hashed with the new algorithm and updated in the database. To facilitate this, the parameters and name of the hash function should be stored in the database alongside the salt and hash.

Request Authorization
---------------------
### Basics

The traditional way to keep track of logged-in users in web applications is a _session_ ID given to the user's web browser as a _cookie_. This session ID is a secret randomly generated number that uniquely identifies the user's session. It is issued whenever a user logs in and it is checked every time a request from the user's browser arrives. A table of tokens is stored on the server in a database. A downside of this approach is that this table needs to be accessed to validate user requests – every time a request is received. This implies that every component of the system that receives and validates user requests needs access to this database. This approach has downsides to performance and scalability.

An approach without this downside is to use _signed_ tokens that contain all data about the user to process most requests. For example, they might contain the username and a list of permissions this user has. These tokens are issued when a user logs in by the authentication component and they contain a cryptographic signature (asymmetric cryptography, for example RSA) or message authentication code (MAC) issued by that component. This signature or MAC is used when verifying the tokens, ensuring that the token is really issued by the authentication component. That way, different components can easily check whether a request is authenticated and extract the information from the token without reading from the database.

In a MAC-based approach, there is one secret MAC key and all components need access to it. In a signature based approach, a key pair exists: The component issuing the tokens needs access to the private key, all other components need the public key.

### JSON Web Tokens

A specific implementation of this concept are JSON Web Tokens (JWT). These are defined in RFC 7519<sup>5</sup>. JWTs consist of three parts:


#### Header
The header contains meta information such as the used signature algorithm and time-based validity data (valid since, valid until).

#### Claims set
The claims set is the JWT payload, a JSON document. This is where you would put the username and other claims – like a claim that a user has certain permissions.


#### Signature
The signature proves that a this token was issued by the authentication service and that the claims from the claims set have not been altered by the user.

These parts are separated by dots and stored base64-encoded. Here's an example token:

| Content       	| Example                                                                                                                                                 	|
|---------------	|---------------------------------------------------------------------------------------------------------------------------------------------------------	|
| Header        	| {"alg":"HS512","typ":"JWT"}                                                                                                                             	|
| Claims set    	| {"username":"Bob"}                                                                                                                                      	|
| Signature     	| 512 bits of HMAC-SHA512 output                                                                                                                          	|
| Encoded token 	| eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6IkJvYiJ9.Xrxu8UvsCYaFh7olF5A4PhQXn6ojUpunqBSmBSJB_t5bULXmMMY8g-4E_FFYxjz1IkaPz1ioGLsNhvCwysIm0A    	|

<sup>5</sup>http://dx.doi.org/10.17487/rfc7519 

### Tokens in XSTAMPP 4.0
In our service-based architecture, session IDs are not suitable. Hence, we should use JWTs.

The list of all projects a user has access to can get very long which in turn can have a negative impact on performance: The long token would have to be transmitted and processed for every request. The solution is to use two different kinds of JWTs: A long-lived token and a short-lived token.

For the signature field, asymmetric cryptography should be used. This is beneficial to security in a distributed deployment because there is only one component requires access to the private key.

### Long-Lived Token
The long-lived token certifies that a user is _logged_ in, i.e. authorized with username and password. It only contains basic information about the user such as the user name. Notably, it does not contain a list of projects that the user has access to.

### Short-Lived Token
The short-lived token is specific to a project. When a user wants to access a project, the client asks the backend for a short-lived token – this request contains the long-lived token for authorization. The short-lived token contains the information that the long-lived token contains (such as the username) as well as a specific project that the user has access to.

Before issuing the short-lived token, the authentication component checks whether the user is allowed to access the project. Short-lived tokens expire after a short while (on the order of 5-20 minutes, the exact duration should be configurable). This duration dictates how long it takes until the possible revocation of a user's access permissions takes effect.

The client has to renew the short-lived token whenever it is about to expire. This process is identical to the initial issuance described above.

<br/><br/>

Stored Information
------------------
<table>
  <tr>
    <th>Information</th>
    <th>Description</th>
    <th>Notes</th>
  </tr>
  <tr>
    <td>User Name</td>
    <td>The user name uniquely identifying the user.</td>
    <td>In the context of web applications, it is common to only log users in with their email address. A distinct and different user name is not needed.</td>
  </tr>
  <tr>
    <td>Hashed Password</td>
    <td>The password, stored as hash. See [Password Storage](#password-storage).</td>
    <td rowspan="4">Whenever this information is accessed (checking a password when a user logs in), all of it needs to be accessed at once.<br>This is why it is common to store all of this information in one DB field, as a string representation.</td>
  </tr>
  <tr>
    <td>Salt</td>
    <td>See section about password storage.</td>
  </tr>
  <tr>
    <td>Algorithm</td>
    <td rowspan="2">In case the selected algorithm or its parameters are to be changed, this information needs to be stored to allow a migration. See [Recommendation in the context of XSTAMPP 4.0](#recommendation-in-the-context-of-xstampp-40)</td>
  </tr>
  <tr>
    <td>Algorithm Parameters</td>
  </tr>
</table>

<br/><br/>

Data Flow
---------
<table>
  <thead>
    <tr>
      <th rowspan="2">Authentication process</th>
      <th>Data needed at client</th>
      <th>Data transferred</th>
      <th>Data needed at auth component</th>
    </tr>
    <tr>
      <th colspan="3">Logic</td>
    </tr>
  </thead>
  <tr>
    <td rowspan="2">User logs in with username and password.</td>
    <td>from user:<br> &nbsp; username<br> &nbsp; password</td>
    <td>▶ username<br>▶ password<br>◀ long-lived token<br>◀ error message</td>
    <td>from DB:<br> &nbsp; username<br> &nbsp; hash and metadata<br>from configuration:<br> &nbsp; JWT private key</td>
  </tr>
  <tr>
    <td colspan="3">The server checks if the username and password from the client match the username and hash from the database. If they do, the user has been successfully authenticated and is given a freshly issued long-lived token</td>
  </tr>
  <tr>
    <td rowspan="2">Request short-lived token</td>
    <td>stored:<br> &nbsp; long-lived token<br>from user:<br> &nbsp; which project to open</td>
    <td>▶ long-lived token<br>▶ which project<br>◀ short-lived token<br>◀ error message</td>
    <td>from DB:<br> &nbsp; project permission<br>from configuration:<br> &nbsp; JWT private key</td>
  </tr>
  <tr>
    <td colspan="3">To access project data, the client needs a short-lived token (see below). The client can request this token with its long-lived token. The auth component checks if the user is allowed to access the project in question and issues a JWT or error message accordingly (see [Short-Lived Token](#short-lived-token)).</td>
  </tr>
</table>

<table>
  <thead>
    <tr>
      <th rowspan="2">Other Processes</th>
      <th>Data needed at client</th>
      <th>Data transferred</th>
      <th>Data needed at auth component</th>
    </tr>
    <tr>
      <th colspan="3">Logic</td>
    </tr>
  </thead>
  <tr>
    <td rowspan="2">Any project specific request</td>
    <td>stored:<br> &nbsp; short-lived token<br>from front-end logic:<br> &nbsp; request data</td>
    <td>▶ short-lived token<br>▶ request data<br>◀ request response<br>◀ error message</td>
    <td>from configuration:<br> &nbsp; JWT public key</td>
  </tr>
  <tr>
    <td colspan="3">Any project-specific requests don't go to the authentication component, they go to a component responsible for projects. That component only needs the JWT public key to validate the short-lived token.</td>
  </tr>
</table>

