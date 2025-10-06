# gs-admin-training - lab05-ops_manager

# GigaSpaces Ops Manager 

## Lab Goals

1. Explore from close GS Ops Manager windows.
2. Get familiar with GS Ops Manager capabilities.

## Lab Description
In this lab we will focus on GigaSpaces Ops Manager functionalities.  
To better know its capabilities you will use GS Ops manager ui demo tool for your convenience.

### 1 Download and run GS Ops manager demo tool:

 * `git clone https://github.com/GigaSpaces-ProfessionalServices/ui-demo`
 * Open README.MD and follow its instructions:  `
   https://github.com/GigaSpaces-ProfessionalServices/ui-demo/blob/master/README.MD`
 * Once done with the above README, continue with the next section **(2 UI DEMO)** 
       
### 2 UI DEMO

Open the ui demo:
 * http://localhost:8280/

![Screenshot](./Pictures/Picture1.png)

#### 2.1 CPU Alert:
 * Choose ProductsCatalog Service and trigger the CPU Alert:
  
![Screenshot](./Pictures/Picture2.png)
 * Troubleshoot it in the GS Ops Manager:
  
![Screenshot](./Pictures/Picture3.png)

#### 2.2 Memory Alert:
 * Choose ProductsCatalog Service and trigger the Memory Alert:
  
![Screenshot](./Pictures/Picture4.png)
 * Troubleshoot it in the GS Ops Manager:

![Screenshot](./Pictures/Picture5.png)

#### 2.3 Unavailability Alert:
 * Choose Mirror Service and trigger the Unavailability Alert:
  
![Screenshot](./Pictures/Picture6.png)
 * Troubleshoot it in the GS Ops Manager:
  
![Screenshot](./Pictures/Picture7.png)

#### 2.4 No-Backups Alert:
 * Choose ProductsCatalog Service and trigger the No-Backups Alert:
  
![Screenshot](./Pictures/Picture8.png)
 * Troubleshoot it in the GS Ops Manager:

![Screenshot](./Pictures/Picture9.png)

![Screenshot](Pictures/Picture10.png)

#### 2.5 Create Containers:
 * Select your host and click the 'Create' button:
  
![Screenshot](./Pictures/Picture11.png)
 * Verify that you have a free GSC in the Web Management Console:
  
![Screenshot](./Pictures/Picture12.png)

#### 2.6 Scale Up Service:
 * Choose WebApplication Service and click the Scale up button:
  
![Screenshot](./Pictures/Picture13.png)
 * Verify WebAppliaction instance was incremented in the GS Ops Manager Services view:
  
![Screenshot](./Pictures/Picture14.png)

![Screenshot](./Pictures/Picture15.png)

#### 2.7 Scale Down Service:
 * Choose WebApplication Service and click the Scale down button:

![Screenshot](./Pictures/Picture16.png)
 * Verify WebAppliaction instance was decremented in the GS Ops Manager Services view:

![Screenshot](./Pictures/Picture17.png)

#### 2.8 Remove Containers:
 * Open the Web Management Console and verify which GSC pid is empty with no service is deployed on it:

![Screenshot](./Pictures/Picture18.png)

 * Select your host and Container to remove click the Remove button:

![Screenshot](./Pictures/Picture19.png)
 * Verify in the Web Management Console spare GSC was removed:
![Screenshot](./Pictures/Picture20.png)

### 3 UI DEMO termination

From ui-demo folder run:  
 * `./undeploy_all.sh`

![Screenshot](./Pictures/Picture21.png)
 * Shutdown the GS run time environment and roll back setenv-overrides.sh (delete or comment GS_GSC_OPTIONS line).

