
This is the documention of the XSTAMPP 4.1
The goal of this document is to help to set up the backend with docker on a local windows computer.
Therefore description of:

* #####[Installation](#installation)
* #####[Basic Setup](#basic_setup)
* #####[PostgreSQL Container Setup](#container_setup)
* #####[IntelliJ IDEA Docker Plugin](#docker_plugin)
* #####[Troubleshooting](#troubleshooting)
* #####[Tips & Useful Commands](#useful_commands)

is given in this document.
<br>

##<a name="installation"></a>Installation

1. Install Docker Desktop
2. Install Maven & add path to bin folder to system environment variable `PATH`
3. Install Java 11 JDK & make sure correct path is set in `PATH` variable
4. Install Postman
5. Install IDEA IntelliJ Community Edition (free)

##<a name="basic_setup"></a>Basic Setup

1. Navigate to xstampp-4.0 project directory
2. Go to ..\xstampp-deploy\launch
3. Run start-server file with linux console (i.e. Git Bash) by executing "./start-server"
4. Ignore error messages after the xstampp-config folder has been created
5. Now in xstampp-4.0 directory there should be a xstampp-config folder (containing 5 subfolders)

##<a name="container_setup"></a>PostgreSQL Container Setup

To configure the postgresql database running inside the docker container do the following:

1. Start Docker Containers

	* Open a command shell in `..\xstampp-4.0\xstampp-spring`
	* Run following command
	```
    mvn clean install
    ```
	* Open a command shell
	* Navigate to xstampp-4.0 root directory
	* Run following command
	```
	docker-compose -f xstampp-deploy\docker\docker-compose.yaml -p xstampp4 up --remove-orphans --force-recreate
    ```
	* Now docker containers should run, check this by executing `docker ps` in a new shell
2. Connect to docker container of database:

	* Run `docker ps` in a command shell
	* Look for name of postgresql database container
	* Open a shell inside the docker container by running following command
	```
	docker exec -it [containerName] bash
    ```
3. Connect to database inside container as user postgres:

	* When inside docker container shell, run following command
	```
	psql -U postgres
    ```
4. Set up user & databases for xstampp
<span style="font-size: 0.6em;">*(Hint: do not forget `;` at the end of each line when running following commands!)*</span>

	* Copy password for `xstampp user from ..\xstampp-4.0\xstampp-config\postgresql\pw.env` (PG_XSTAMPP_PASSWORD)
	* Run following commands
	```
    CREATE USER xstampp WITH PASSWORD '[Paste copied password]';
    ```
	* Run following commands
	```
	CREATE DATABASE "xstampp-master" OWNER xstampp;
	CREATE DATABASE "xstampp-project" OWNER xstampp;
	```
5. Set up table structure inside databases:

	* On your PC, open xstampp project folder and navigate to `..\xstampp-deploy\sql\postgres-initdb.d\`
	* There you should find the two files `master.sql` and `project.sql`
	* Open the file `master.sql` with any text editor (i.e. Notepad++) and copy everythin except the first line
	* Go back to the command shell where you've created the databases and run following command
	```
	\connect "xstampp-master";
    ```
	* Paste everything you copied and hit enter
	* Run following command
	```
    GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO xstampp;
    ```
	* Go back by running
	```
    \q
    psql -U postgres
    ```
	* Open the file `project.sql` with any text editor (i.e. Notepad++) and again copy everythin except the first line
	* Go back to the command shell where you created the databases and run following command
	```
	\connect "xstampp-project";
    ```
	* Paste everything you copied and hit enter
	* Run following command
	```
    GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO xstampp;
    ```
	* Close command shell by running
	```
	\q
    exit
    exit
    ```

##<a name="docker_plugin"></a>IntelliJ IDEA Docker Plugin
It is easyer and better to always start docker in IntelliJ IDEA as to start in from console

1. Enable the docker deamon:

	* Start Docker Desktop
	* Open the docker settings (On windows right click the docker symbol and select settings)
	* On the menu point `General`, check the checkbox which says `Expose deamon on...`
	* Wait while docker finishes loading
2. Install Docker plugin:

	* Open IntelliJ IDEA
	* Import the xstampp-spring folder as existing maven project
	* Open the File menu from upper left corner
	* In the menu, select `Settings...`
	* Go to the menu point `Plugins` and search for `Docker integration`
	* Now click the install button to install the plugin
3. Create run configuration

	* Click the field left on the left of the run button
	* From the dropdown menu, select `Edit Configurations...`
	* In the left upper corner, click the `+` button, select `Docker` &rarr; `Docker-compose`
	* Select the path to the docker-compose.yaml file: `..\xstampp-deploy\docker\docker-compose.yaml`
	* In the services field, click the folder icon and select all services (ext-postgresql, ext-nginx, auth, project, notify, push)
	* Save the run configuration by pressing `OK` button on the bottom

##<a name="troubleshooting"></a>Troubleshooting
1. If PostgreSQL docker container shows the following error while starting
	```
	FATAL: data directory “/var/lib/postgresql/data” has wrong ownership
	HINT: The server must be started by the user that owns the data directory.
	```
	do the following:

    * Open a command shell and run the following command
    ```
    docker volume create postgres_database
    ```
    * Go to `..\xstampp-deploy\docker` and open the `docker-compose.yaml` file
    * Before altering the file copy and save the original file somewhere!
    * <div style="color: red;">**Do not add the changed file to git repository!**</div>
    * Search the config for container `ext-postgresql`
    * Replace the windows path in front of `/var/lib/postgresql/data` with `postgres_database`
    * Now the first lines of config for `ext-postgresql` should look like this
    ```
    ext-postgresql:
    	image: postgres:11
    	networks:
     	- xstampp4
    	volumes:
     	- postgre_database:/var/lib/postgresql/data
    ```
    * Then, at the end of `docker-compose.yaml` file, you insert following lines
    ```
    volumes:
  		postgres_database:
    		external: true
    ```
    * Save the file and restart docker, now the problem should be fixed
    * <div style="color: red;">**After you've started docker and the problem was solved, undo your changes in the `docker-compose.yaml` file (delete the changed file and replace it with the original one you saved a few steps before)!**</div>

1. If Docker wants you to enable Hyper-V (Virtualization)

	* On windows press "Windows Button + X" and select "Windows PowerShell (Administrator)"
	* In the PowerShell, run the following command: "Enable-WindowsOptionalFeature -Online -FeatureName Microsoft-Hyper-V -All"

##<a name="useful_commands"></a>Tips & Useful Commands
* Every time you changed the code and want to run it, do the following
	* Stop docker containers in IntelliJ IDEA
		* On the bottom left, click `Docker` and then click the red little square
	* On your pc, open a command shell in `..\xstampp-4.0\xstampp-spring`
	* Run following command
	```
    mvn clean install
    ```
    or
    ```
    mvn clean install -DskipTests
    ```
    to deploy without running tests (not recommended)

    * Now, in IntelliJ IDEA, you can start the docker containers
* Stop all docker containers
	* Open linux console (i.e. Git Bash) and run following command
	```
    docker stop $(docker ps -q)
    ```