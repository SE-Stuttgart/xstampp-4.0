XSTAMPP 4.0 User Deletion Documentation
===============================

:house:[Home](README.md)

Content
-------
* [User Interface](#user-interface)
* [Groups blocking deletion](#groups-blocking-deletion)
* [Deployment von postgres-fdw](#postgres_fdw-deployment)
* [Effects of successful deletion](#effects)
* [Atomic deletion across services](#deletion-across-services)
    + [Postgres-fdw](#postgres-fdw)
    + [Alternatives and why they weren't used](#alternatives)

User Interface
------------
The user can delete his account via his profile page. As additional precaution he is asked for his password before he can do this.
If the deletion is successful he will be redirected to the login page of XSTAMPP.
In case of the deletion going wrong due to his membership in [certain groups](#groups-blocking-deletion) the user is presented with a modified group overview in which groups blocking deletion are highlighted by a warning triangle. On hovering over the triangle the user is shown suggestions for resolving the situation.

[role concept](role-concept.md).

Groups blocking deletion
---------------------

Membership of a user in certain groups makes deletion impossible. This can be resolved by the user leaving the groups or taking appropriate action. The private group never blocks deletion. Since adding members to the private group is impossible, the owning user will always be the only member of it. Projects in this group will be deleted on deletion of the user no matter what. Projects in public groups are never deleted automatically.

Below you find a summary of reasons for a group to be blocking deletion. The shorthands are enum values used
in backend code.

| Shorthand 	| Group Description	|
|-----------------------	|-----------------------	|
| ONLY_USER_GROUP_WITH_PROJECTS               	| user is only group member and group contains projects          	|
| ONLY_ADMIN_MULTI_USER | user is only group administrator and group has additional members |


`ssh username@server`

postgres_fdw deployment
---------------------

#### Fall 1: Empty Database
If the database is empty postgres_fdw will be installed and configured automatically. This holds true for both the server and local developer setup.
This is taken care of by the basic-setup.sh  script which is run in the postgres-container on its first start.  
Path Developer Setup: xstampp-4.0/xstampp-deploy/sql/postgres-initdb.d/basic_setup.sh  
Path Server: /opt/xstampp/xstampp-ci/deploy/volumes/postgres-initdb.d/basic_setup.sh.

#### Fall 2: Existing Databse
The goal of the listed commands is to copy the file install_postgres_fdw.sh to the server and run it within the postgres-container.

Local command to copy file to server:  
`scp -v xstampp-4.0/xstampp-deploy/sql/install_postgres_fdw.sh stuproadm@xstage:/home/stuproadm/`  
On server starting as user stuproadm:
`cd ~`  
`sudo su`  
`cp install_postgres_fdw.sh /opt/xstampp/xstampp-ci`
`cd /opt/xstampp/xstampp-ci`
`chown xstampp-ci install_postgres_fdw.sh`
`chgrp xstampp install_postgres_fdw.sh`
`docker cp install_postgres_fdw.sh [containerName of postgres]:/`
`docker exec -it [containerName of postgres] bash`
Preceding command gave us bash in container, now run:
`chmod +x install_postgres_fdw.sh`
`./install_postgres_fdw.sh`
`exit`
After that you need to restart the postgres container because the basic-setup.sh
Script made a change to pg_hba.conf which takes effect only after restart.
This change is commenting out the line `# host    all             all             127.0.0.1/32            trust`. Otherwise postgres_fdw won't work because it expects the xstampp user having to authenticate itself and not be trusted.


#### Atomic Deletion Across Services
Deletion of a user is supposed to cause effects on both the auth and the project service. These effects are supposed to happen in an all-or-nothing fashion.

2PC
Discarded idea due to operative complexity and necessity of manual intervention to recover from coordinator failure. The complexity of appropriate configuration in java code is also noteworthy.

Queue between Auth and Project Service without 2PC
Idea is to get at-least-once delivery and processing of messages from the auth to the project service. This can be achieved by nesting the database transaction within the transaction of taking the message from the queue. Didn't use it because we would have had to add a queue system such as rabbit-mq to deployment resulting in higher operational complexity. Also this only guarantees that the deletion takes effect on the project service at some time in the future. This is a disadvantage compared to postgres_fdw

Postgres_fdw
We make the user and project entities stored on the project service available to the auth service by using postgres_fdw. A deployment of the databases of the two services on different machines is still possible. Postgres_fdw nest the transaction on the project service within the transaction on the auth service. 

In order to view one containter's log file use `docker logs NAME`  
To see live update use 
`docker logs -f NAME`  
To filter use
`docker logs NAME | grep XX`


#### Docker Compose
Docker Compose is used to easily restart and stop the several containers used for the project.
To access docker compose, navigate to the path where the .yml or .yaml file is located. Use `docker-compose` with one of the following commands:
* `up`: to create and run applications configured in the file; use `-d`for detach, the containers will not be started within the console and thus stay even though the console might get closed
* `stop`: to stop the currently running docker containers
* `rm`: to remove the docker containers


Effects of successfull deletion
------------------------------------

#### Auth Service

1. Deletion of user, his group memberships, his private group an the projects in his private group from their respective tables

#### Project Service

1. last_editor_id and last_editor_displayname is set to null in all rows holding the data of entities which were last edited by the now deleted user.

2. Projects in users private group are deleted complete with all their entities.

3. Row representing user is removed from project service's user table. This triggers effect 1.






