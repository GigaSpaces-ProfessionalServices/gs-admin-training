# UI Demo

#### Overview:
This repo includes number of services (pu's) that demonstrates the functionality of the GigaSpaces web UI (SpaceDeck).<br>
After Deployed, Demo-app enables to perform some operations on the cluster and see the effects on the UI. <br>


#### Project structure:
##### The project consist of the following services:
* mirror - persists the Products of products-catalog to an HSQL database using Hibernate. The HSQL server runs inside the mirror (port 9137, database files in `~/my-app-testDB`).
* products-catalog(space) - defaults topology is 2,1 (as set at './deploy_all.sh/bat').
* products-loader - populating the space with 100000 Products with each deployment.
* web-application - simulates a client that constantly performs read operations on the space for Products, with a property of maxEntriesPerSecond.
* demo-app - a web application for performing operations on the service grid include 2 kinds of operations:<br>
    The boxes on the top refer to operations that cause different alerts on the service grid.<br>
    The boxes on the bottom refer to operations include create/remove container and scale up/down service.<br>
    The port of the app is 8280, e.g when running on localhost, it's accessible using http://localhost:8280/<br>
    The URL is available only when the demo-app service is deployed.<br>
    The demo-app performs the grid operations through the REST Manager (REST v3, `http://<manager host>:9090/api/v3`).<br>
    Note: Scale up needs a free container. Create a container first, otherwise the new instance stays pending until a container is available.<br>


#### Running instructions:
1. Download GigaSpaces 18.0.1.
2. Unzip the downloaded file to a folder. We will reference it with GS_DIR.
3. Open the GS_DIR folder, edit gs-license.txt file.
    Use a license if you have or "tryme" if you don't.
4. Go to GS_DIR/bin folder and edit `setenv-overrides.sh/bat` file.<br> Add the following:<br>
    `Linux/Mac:`<br>
        `export GS_MANAGER_SERVERS="localhost"`<br>
        `export GS_GSC_OPTIONS="-Xmx256m"`<br>
    `Windows:`<br>
        `set GS_MANAGER_SERVERS=localhost`<br>
        `set GS_GSC_OPTIONS=-Xmx256m`<br>
5. Go to GS_DIR/bin folder and run<br>
   `./gs.sh host run-agent --manager --webui --restv3 --gsc=9`<br>
    This will start the Service Grid and the web UI (SpaceDeck) will be available at http://localhost:9090/
6. Within the ui-demo folder verify that the pom.xml has the correct gigaspaces.version (18.0.1).<br>
   Note: GigaSpaces 18.0 requires Java 25. Make sure `JAVA_HOME` (used by Maven) points to a JDK 25.<br>
7. Within the ui-demo folder perform the following:<br>
    `Linux/Mac:`<br>
        `mvn clean package`<br>
        `export GS_DIR=<PATH TO PRODUCT INSTALLATION>`<br>
        `./deploy_all.sh`<br>
    `Windows:`<br>
        `mvn clean package`<br>
        `set GS_DIR=<PATH TO PRODUCT INSTALLATION>`<br>
        `deploy_all.bat`<br>
* e.g deploy_all.sh output (the log lines that `gs.sh` prints before each command are not shown):<br>
```
[mirror.jar] successfully uploaded
·····
Instance [Mirror~1] successfully deployed

Processing Unit [Mirror] was successfully deployed at 2026-11-03 16:45:59

[products-catalog.jar] successfully uploaded
········
Instance [ProductsCatalog~2_1] successfully deployed
·
Instance [ProductsCatalog~1_1] successfully deployed
·
Instance [ProductsCatalog~1_2] successfully deployed
Instance [ProductsCatalog~2_2] successfully deployed

Processing Unit [ProductsCatalog] was successfully deployed at 2026-11-03 16:46:10

[products-loader.jar] successfully uploaded
···
Instance [ProductsLoader~1] successfully deployed

Processing Unit [ProductsLoader] was successfully deployed at 2026-11-03 16:46:15

[web-application.war] successfully uploaded
···
Instance [WebApplication~1] successfully deployed
···
Instance [WebApplication~2] successfully deployed

Processing Unit [WebApplication] was successfully deployed at 2026-11-03 16:46:22

[demo-app.war] successfully uploaded
·····
Instance [DemoApp~1] successfully deployed

Processing Unit [DemoApp] was successfully deployed at 2026-11-03 16:46:28
```
8. To undeploy all the services, within the ui-demo folder run (Linux/Mac):<br>
        `./undeploy_all.sh`<br>
