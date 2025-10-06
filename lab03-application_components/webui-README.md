# gs-admin-training - lab03-application_components

## 	Application Level Components

###### Lab Goals 
 * Be introduced to and experience application level components.
 * Deploy, test and un-deploy applications (such as a space).
 * Experience the self-healing capability of the space.

###### Lab Description
In this lab you will start GigaSpaces service grid, deploy a Space, perform some benchmarks using a benchmark tool that will test your space, and undeploy the space. You will perform most actions using GigaSpaces cli. You will also try to check the self-healing capabilities of the space by stopping a GSC and see how GigaSpaces heals itself.

## 1 Start the service grid

1. Go to `$GS_HOME/bin`
2. Run: `./gs.sh host run-agent --auto --gsc=4`
3. Go to the web Management Console `localhost:8099`
4. Press on the gsc processes to see the process information and log.

![Screenshot](./Pictures/webui/gsclog.png)

## 2	Deploy a space
1. Use the “Deploy an In Memory Data Grid” option in the menu (see top left red arrow in the diagram below).
 
2. A Deployment Wizard screen will open.

![Screenshot](./Pictures/webui/spacedeployment.png)

3. Fill in the fields as follows (see red arrows for locations).


| Field Name| Value |
|---|---|
| **Space (aka Data Grid) Name** | BillBuddy-space |
| **Number of Instances** | 2 |
| **Backups (per each instance)** | 1 |
| **Per VM** | 1 |

4. Press the Deploy button. The following should be the status of the deployment wizard after all deployments are done:

![Screenshot](./Pictures/webui/spacedeploymentsuccessful.png)

5. Press the close button.

6. Examine the 'Processing Units' tab.

 * Identify the primary and backup partitions.

![Screenshot](./Pictures/webui/puprimarybackup.png)

7. Examine the 'Spaces' tab

Hint: use the table in the Service View Tab.

![Screenshot](./Pictures/webui/spaces.png)

8. Examine which space instance is located on which GSC?

![Screenshot](./Pictures/webui/spacedeployedhostgsc.png)

## 3	Test your space
We will now examine several operations that allow you to test, monitor and examine objects that are stored in the space.

###### Write objects to the space
1. Go to the project folder at `$GS_ADMIN_TRAINING/lab03-application_components`.
2. Run `mvn package`
3. Run the script located at `$GS_ADMIN_TRAINING/lab03-application_components/scripts/writeObjects.sh` in order to write 10,000 objects to the space.

##### View statistics
View the result in the statistics page. For example perform a read. Your statistics view might look something like this:

![Screenshot](./Pictures/webui/operationsstatistics.png)

4. Go to the Query operation and press on the execute button (red arrow).
What you see is the list of Space Object types (Classes) that are currently stored in the space.

![Screenshot](./Pictures/webui/queryobjects.png)

In later lessons we will use this view to query the objects

Go to the Data_Types operation select the MessagePOJO data type and press query.

![Screenshot](./Pictures/webui/datatypes.png)


## 4	Self-Healing

In this exercise you will be introduced to the self-healing capabilities of a space.
Basically we will ‘kill’ (using Windows Task Manager or kill -9) a GSC process and see that it restarts automatically by the gs-agent and that new partition are created accordingly.

1. Each process ID (all are JVMs) is shown at the Hosts tab (see red circle). Choose 1 of the GSCs PID (with primary space instance on it) and use the Task Manager or (kill -9 for Linux) in order to kill the process.
 
If the PID is not shown in the Task Manager simply choose "View -> Select Columns" and add the PID column.

![Screenshot](./Pictures/webui/spacedeployedhostgscprimary.png)

2. Return to the gs-webui in order to check the recovery status.

![Screenshot](./Pictures/webui/spacedeployedhostgsc-2.png)

3. The following is a summary of the self-healing process.

 * A backup was promoted to Primary.
 * GSC was re-launched by the gs-agent.
 * A new backup partition was provisioned.

![Screenshot](./Pictures/webui/spacedeployedhostgsc-3.png)

4. Recovery is performed, the backup partition is now a primary.

5. Restart a primary partition by selecting the primary partition and right click -> Restart BillBudy-space.1 [2]. What happens?

![Screenshot](./Pictures/webui/gscrestartcontextmenu.png)

6. In general, if you need to change a primary space to a backup it is better to use the demote command:
![Screenshot](./Pictures/webui/demotecontextmenu.png)
    
## 5	Un-deploy a space 

1. Click on 'Deployed Processing Units' tab.

2. Right click on space and click on 'Undeploy'.

![Screenshot](./Pictures/webui/undeploycontextmenu.png)

3. The following screen should appear:

![Screenshot](./Pictures/webui/undeploynopus.png)

4. You have un-deployed the space successfully!

---
## Additional resources
 * [Instructions for cli](./README.md)
 * [Instructions for REST API](./restapi-README.md)
 * [Instructions for gs-ui](./gsui-README.md)
 * [Instructions for ops-manager](./opsui-README.md)

