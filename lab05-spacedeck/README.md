# gs-admin-training - lab05-spacedeck

# GigaSpaces SpaceDeck

## Lab Goals

1. Explore from close GS SpaceDeck windows.
2. Get familiar with GS SpaceDeck capabilities.

## Lab Description
In this lab we will focus on GigaSpaces SpaceDeck functionalities.  
To better know its capabilities you will use the ui demo tool in the `ui-demo` folder of this lab for your convenience.  
SpaceDeck is available at http://localhost:9090/

### 1 Build and run the ui demo tool:

 * Open [ui-demo/README.MD](./ui-demo/README.MD) and follow its instructions.
 * Once done with the above README, continue with the next section **(2 UI DEMO)** 
       
### 2 UI DEMO

Open the ui demo:
 * http://localhost:8280/

![Screenshot](./Pictures/Picture1.png)

#### 2.1 CPU Alert:
 * Choose ProductsCatalog Service and trigger the CPU Alert:
  
![Screenshot](./Pictures/Picture2.png)
 * Troubleshoot it in SpaceDeck ('Management' | 'Spaces', click the space `space`, 'Instances' tab):
  
![Screenshot](./Pictures/Picture3.png)

 * Note: The CPU Alert keeps one thread busy in the container that hosts the first ProductsCatalog partition's primary instance. Watch that container's CPU usage go up in SpaceDeck for the selected duration. It does not usually raise the built-in CPU Utilization alert: that alert is measured on the whole host, and by default fires only when the host's CPU averages over 80% for 60 seconds (`$GS_HOME/config/alerts/alerts.xml`, `CpuUtilizationAlertConfiguration`: `high-threshold-perc`, `measurement-period-milliseconds`). To raise it, the task could run one busy thread per core with a duration of more than 60 seconds, or the thresholds in `alerts.xml` could be lowered.

#### 2.2 Memory Alert:
 * Choose ProductsCatalog Service and trigger the Memory Alert:
  
![Screenshot](./Pictures/Picture4.png)
 * Troubleshoot it in SpaceDeck ('Management' | 'Processing Units'):

![Screenshot](./Pictures/Picture5.png)

#### 2.3 Unavailability Alert:
 * Choose Mirror Service and trigger the Unavailability Alert:
  
![Screenshot](./Pictures/Picture6.png)
 * Troubleshoot it in SpaceDeck ('Management' | 'Processing Units'):
  
![Screenshot](./Pictures/Picture7.png)

#### 2.4 No-Backups Alert:
 * Choose ProductsCatalog Service and trigger the No-Backups Alert:
  
![Screenshot](./Pictures/Picture8.png)
 * Troubleshoot it in SpaceDeck ('Management' | 'Spaces', click the space `space`, 'Instances' tab):

![Screenshot](./Pictures/Picture9.png)

![Screenshot](Pictures/Picture10.png)

#### 2.5 Create Containers:
 * Select your host and click the 'Create' button:
  
![Screenshot](./Pictures/Picture11.png)
 * Verify that you have a free GSC in SpaceDeck ('Management' | 'Cluster', 'Topology' tab, 'Idle container'):
  
![Screenshot](./Pictures/Picture12.png)

#### 2.6 Scale Up Service:
 * Choose WebApplication Service and click the Scale up button:
  
![Screenshot](./Pictures/Picture13.png)
 * Verify WebApplication instance was incremented in SpaceDeck ('Management' | 'Processing Units', click WebApplication):
  
![Screenshot](./Pictures/Picture14.png)

![Screenshot](./Pictures/Picture15.png)

#### 2.7 Scale Down Service:
 * Choose WebApplication Service and click the Scale down button:

![Screenshot](./Pictures/Picture16.png)
 * Verify WebApplication instance was decremented in SpaceDeck ('Management' | 'Processing Units', click WebApplication):

![Screenshot](./Pictures/Picture17.png)

#### 2.8 Remove Containers:
 * Open SpaceDeck and verify which GSC pid is empty with no service deployed on it ('Management' | 'Cluster', 'Topology' tab, 'Idle container'):

![Screenshot](./Pictures/Picture18.png)

 * Select your host and Container to remove click the Remove button:

![Screenshot](./Pictures/Picture19.png)
 * Verify in SpaceDeck that the spare GSC was removed ('Management' | 'Cluster', 'Topology' tab):
![Screenshot](./Pictures/Picture20.png)

### 3 UI DEMO termination

From ui-demo folder run:  
 * `./undeploy_all.sh`

![Screenshot](./Pictures/Picture21.png)
 * Shutdown the GS run time environment and roll back setenv-overrides.sh (delete or comment GS_GSC_OPTIONS line).
