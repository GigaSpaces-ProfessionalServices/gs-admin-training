# gs-admin-training - lab12-java_admin_api

# GigaSpaces Java Admin API 

## Lab Goals

1. Explore GigaSpaces Java Admin API’s capabilities.
2. Learn to capture the alerts using the GigaSpace Java Admin APIs.

## Lab Description
In this lab we will focus on GigaSpaces Java Admin API.  
In the lab we change the default of service limit 1 in order to allow deployment of more than one instance in the same GSC.  
We will run a feeder to populate data, and run relocate in order to start a new GSC to allow more memory per GSC instance.
We also run a monitor script that show us various alerts related to CPU, memory utilization and instances.

### 1 Start gs-agent

1. Navigate to `$GS_HOME/bin`.

2. Start the following:
```
  ./gs.sh host run-agent --auto
  ./gs.sh container create --vm-option -Dcom.gigaspaces.grid.gsc.serviceLimit=2 --memory 100m --count 2 localhost
  ./gs.sh space deploy --partitions 2 --ha demo
```

3. Edit `$GS_HOME/config/alerts/alerts.xml` to reduce memory (HeapMemoryUtilizationAlertConfiguration) and cpu (CpuUtilizationAlertConfiguration) thresholds.
4. Edit runFeeder, runMonitor, runRelocate scripts. Set the installation home dir and lookupgroup.
5. Verify deployment distribution in the web-ui (There should be 2 containers each having 2 instances).
6. Run the following scripts:
 * build
 * runMonitor
 * runRelocate
 * runFeeder
7. Examine console of the scripts, verify the deployment distribution in web-ui.

####  Relocate 
1. The Admin script will test the runtime 20 times (then the scripts exits).
2. The script Looks for GSCs where the UsedHeapSize is more than 50MB.
3. If such a GSC exists, it checks if there are more than one PU instance deployed on that GSC.
4. If such a GSC is found, the script looks for an empty GSC to relocate one of the PU instances to.
5. If it finds an empty GSC, then a PU instance will be relocated to that GSC, else a new GSC will be started with AdminApiZone in zones & then the PU instance will be relocated.
6. The same will be repeated until each GSC contains only one PU instance.
7. Between each check the script sleeps for 20 seconds.

#### 2 Alert 
1. Terminate one of the GSCs from the webui.
2. Watch the alerts in the command console where you activated the script.

![Screenshot](./Pictures/Picture2.png)

