# gs-admin-training - lab02-gs_basic_overview


## 	GS Overview

###### Lab Goals
*   Be introduced to and experience Grid Service Components.

###### Lab Description
In this lab you will start GigaSpaces service grid. The service grid is the core set of processes that runs GigaSpaces and typically consists of Grid Service Agents, Managers and Grid Service Containers. We will then inspect the service grid in the GS-UI.

## 1	Start the GigaSpaces service grid
1. Goto `$GS_HOME/bin`
2. Run: `./gs.sh host run-agent --auto --gsc=2`
3. Start gs-ui using: `./gs-ui.sh`
    
## 2	Examine the running environment
    
1. Examine the ‘gigaspaces-manager.log’ Check the GSM and GSCs have started and registered successfully.  

![Screenshot](./Pictures/Picture1.png)

2. Go to the gs-ui application.
3. Press the host tab.
4. Press on the gsc processes to see the process information and log.
    
![Screenshot](./Pictures/Picture2.png)

5. Open your browser and go to `localhost:8099`
6. Examine the Webui console.
7. You will learn more about it at a later session.
    
![Screenshot](./Pictures/Picture3.png)

8. Open your browser and go to `localhost:8090`
9. Examine GS Ops Manager.
10. You will learn more about it at a later session.

![Screenshot](./Pictures/Picture4.png)
