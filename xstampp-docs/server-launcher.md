Starting the system on a server using the server launcher
=========================================================

:house:[Home](README.md)

This guide has been tested with Ubuntu 18.04, it might require slightly different commands on other distributions.

1. Install Docker and Docker Compose on your server  
   Depending on your distribution, this might be possible with the command:  
   `sudo apt install docker.io docker-compose`

2. Make sure you have permission to use the docker command  
   `sudo adduser YourUsernameHere docker`  
   (Re-login to apply!)

3. Download XSTAMPP 4.0, for example:  
   `wget -O - files.xstampp.de/builds/xstampp_latest_master.tgz | tar xzv`

4. Start the server  
   `cd xstampp_*` # (change directory to the newly downloaded and extracted directory)  
   `xstampp-deploy/launch/start-server`
