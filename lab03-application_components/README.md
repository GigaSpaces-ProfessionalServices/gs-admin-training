# gs-admin-training - lab03-application_components

## 	Application Level Components

###### Lab Goals 
 * Be introduced to and experience application level components.
 * Deploy, test and un-deploy applications (such as a space).
 * Experience the self-healing capability of the space.

###### Lab Description
In this lab you will start GigaSpaces service grid, deploy a Space, perform some benchmarks using a benchmark tool that will test your space, and undeploy the space. You will perform most actions using the GigaSpaces cli. You will also try to check the self-healing capabilities of the space by stopping a GSC and see how GigaSpaces heals itself.

## 1 Start the service grid

1. Go to `$GS_HOME/bin`
2. Make sure `GS_MANAGER_SERVERS` is set in `setenv-overrides.sh`:
   ```
   export GS_MANAGER_SERVERS="localhost"
   ```
3. Run: `./gs.sh host run-agent --manager --webui --restv3 --gsc=4`

###### Checking the logs
The logs are stored in the `$GS_HOME/logs` directory. `cd` to this directory.
 * The default log file name format is `<date>~<time>-gigaspaces-<process>-<hostname>-<pid>.log`
 * The **gsa** log file contains the **GridServiceAgent** logs. This log will have messages concerning the start or restart of the GigaSpaces processes.
 * The **manager** log will contain messages from the LUS, Zookeeper and the GSM. We will go into further detail in this in a later chapter. On the service grid, this log will also have messages concerning the Processing Unit (PU)/ application deployment lifecycle.
 * The **restv3** log contains the REST Manager (REST v3) logs and the **webui** log contains the web UI logs.
 * The **gsc** log is the **GridServiceContainer** log. Applications get deployed to a gsc, so deployment issues, PU issues will appear here.
   
## 2	Deploy a space
###### Introduction to gs.sh
1. In a new console window in `$GS_HOME/bin` directory, run `./gs.sh`.

   This starts `gs.sh` in the interactive mode.

   ```
   $ ./gs.sh 
   Starting interactive shell...
        _____ _              _____                              
       / ____(_)            / ____|                             
      | |  __ _  __ _  __ _| (___  _ __   __ _  ___ ___  ___    
      | | |_ | |/ _` |/ _` |\___ \| '_ \ / _` |/ __/ _ \/ __| 
      | |__| | | (_| | (_| |____) | |_) | (_| | (_|  __/\__ \ 
       \_____|_|\__, |\__,_|_____/| .__/ \__,_|\___\___||___/ 
      __   __    __/ | _____      | |                           
      \ \ / /   |/\_/ |  __ \     |_|                       
       \ V /    /  \  | |__) |                                
        > <    / /\ \ |  ___/                                 
       / . \  / ____ \| |                                     
      /_/ \_\/_/    \_\_|                                   
   
   Usage: gs.{sh|bat} [global-options] command [options] [parameters]
   
   Description: 
   Options:
         --help                Show the help information for this command
         --cli-version=<n>     Use another CLI version (set '1' for legacy CLI).
                                 Overrides XAP_CLI_VERSION environment variable
         --username=<username> Username for secured environments, overrides
                                 GS_MANAGER_USERNAME environment variable
         --password=<password> Password for secured environments, overrides
                                 GS_MANAGER_PASSWORD environment variable
         --token=<token>       Password for secured environments, overrides
                                 GS_TOKEN environment variable
         --timeout=<timeout>   Change the default timeout (60 sec) for the
                                 specified operation
         --server=<server>     Name or address of the Manager server to connect to
   Commands:
     version       Platform version
     help          Help information for this command
     demo          Deploy a Space in high availability mode (2 primaries with 1
                     backup each)
     blueprint     List of available commands for blueprints
     service, pu   List of available commands for Service (Processing Unit)
     space         List of available commands for Space operations
     maven         List of available commands for Maven-related operations
     completion    Generate completion script for bash/zsh shells.
     host          List of available commands for local host operations
     container     List of available commands for container operations
     mcp           List of available commands for MCP server operations
     info          Show the configured Manager information
     request       List of available commands for Request operations
     logger        Change log level of a service or a virtual machine
     data-gateway  List of available commands for Data Gateway operations
     cls, clear    Clears interactive shell terminal
     exit, quit    Exits interactive shell (shortcut: CTRL-D)
   
   gs$ 
   ```

   You can get information on any of the subcommands. For example, while still in interactive mode, type `space --help`. You will get a list of options related to a space.  

   ```
   gs$ space --help
   List of available commands for Space operations
   Usage: gs space [--help] [COMMAND]
   
   Description:
   Options:
         --help   Show the help information for this command
   Commands:
     run              Run a standalone Space
     list             List the deployed Spaces
     list-instances   Lists the instances of the specified Space
     info             Show information for the specified Space: operation
     statistics, data types
     info-instance    Show information for the specified Space instance: mode,
     location
     deploy           Define a Space (data grid) for deployment
     demote-instance  Demote a primary Space to backup
     query            Queries data in a space or a space instance
   
   gs$
   ```
   Note: The previous example could have been run non-interactively by running `./gs.sh space --help`.  
   
   To drill down and get more information on a subcommand associated with `space`, for example **list-instances**, type `space list-instances --help`.  

   Type `exit` or `quit` to exit interactive mode. From here on we will use the gs.sh command by entering the full command (not the interactive mode).  
   
###### Deploy a space
2. We wish to deploy a space using the following configuration:

   | Field Name| Value |
   |---|---|
   | **Space (aka Data Grid) Name** | demo |
   | **Number of Instances** | 2 |
   | **Backups (per each instance)** | 1 |
   | **Per VM** | 1 |

   Note: This guide uses the space name `demo`, because the `writeObjects.sh` script in section 3 writes to the space `demo`.

   Run `./gs.sh space deploy --partitions=2 --ha demo`  
    * `--partitions=2` - sets the number of partitions
    * `--ha` - means high availability and it is a shortcut to indicate "**Backups (per each instance)** = 1"

3. You should see output similar to text below:
   ```
   Instance [demo~1_1] successfully deployed
   Instance [demo~2_1] successfully deployed
   ·
   Instance [demo~2_2] successfully deployed
   ·
   Instance [demo~1_2] successfully deployed
   
   Space [demo] was successfully deployed at 2025-09-30 15:47:18
   ```

###### Examine the Processing Units
4. Run `./gs.sh pu list` 
   It should look like the following:
   ```
   $ ./gs.sh pu list
   
   NAME               TYPE        SPACE              TOPOLOGY           STATUS    RESOURCE    QUIESCED    INSTANCES COUNT    
   demo               STATEFUL    demo               partitioned 2,1    INTACT    datagrid    false       4                  
   
   
   SUMMARY              
   Processing Units:    1    
   Instances:           4    
   ```
###### Examine the Spaces
5. We will run `./gs.sh space list` to get general information on all currently deployed spaces.
   ```
   $ ./gs.sh space list
   
   SPACE NAME         PROCESSING UNIT NAME    TOPOLOGY           ACTUAL/PLANNED INST.    INSTANCES ID           
   demo               demo                    partitioned 2,1    4/4                     demo~1_1               
                                                                                         demo~1_2               
                                                                                         demo~2_1               
                                                                                         demo~2_2               
   
   
   SUMMARY     
   Spaces:     1    
   Actual:     4    
   Planned:    4    
   ```
   
6. To identify the primary and backup partitions, run `./gs.sh pu list-instances <PU name>`

   ```
   $ ./gs.sh pu list-instances demo
   
   INSTANCE ID            HOST ID      CONTAINER ID        
   demo~1_1               dixson-pc    dixson-pc~763534    
   demo~1_2               dixson-pc    dixson-pc~763471    
   demo~2_1               dixson-pc    dixson-pc~763506    
   demo~2_2               dixson-pc    dixson-pc~763507    
   
   
   SUMMARY        
   Instances:     4    
   Hosts:         1    
   Containers:    4       
   ```
    * The **INSTANCE ID** has the following naming convention `<space name>~<partition number>_<1|2>`
    * In this example the PU name is the same as the space name.
    * The last digit is a 1 or 2 and it indicates whether it was started as a primary or backup partition.
    * The **CONTAINER ID** is a String that contains hostname~PID. 
    

7. Which space instance is located on which GSC?

## 3	Test your space
We will now examine several operations that allow you to test, monitor and examine objects that are stored in the space.

###### Write objects to the space
1. Go to the project folder at `$GS_ADMIN_TRAINING/lab03-application_components`.
2. Run `mvn package`
3. Run the script located at `$GS_ADMIN_TRAINING/lab03-application_components/scripts/writeObjects.sh` in order to write 10,000 objects to the space.  
   Note: The script uses `$JAVA_HOME/bin/java` (or the `java` on your PATH if `JAVA_HOME` is not set). GigaSpaces 18.0 requires Java 25.

###### View operation statistics
4. To get the operation statistics from the cli, run `./gs.sh space info --operation-stats <space name>`. For example,
   ```
   $ ./gs.sh space info --operation-stats demo 
   
   SPACE DETAILS           
   Space Name              demo                   
   Processing Unit Name    demo                   
   Topology                partitioned 2,1        
   Planned Instances       4                      
   Actual Instances        4                      
   Instances               demo~1_1               
                           demo~1_2               
                           demo~2_1               
                           demo~2_2               
   
   OPERATIONS STATISTICS        
   Objects Count                10,000    
   Notify Templates Count       0         
   Active Connections Count     4         
   Active Transactions Count    0         
   Change Count                 0         
   Size                         2         
   Change per sec               0         
   Execute Count                0         
   Execute per sec              0         
   Notify Ack Count             0         
   Notify Ack per sec           0         
   Notify Registr. Count        0         
   Notify Registr. per sec      0         
   Notify Trigger Count         0         
   Notify Trigger per sec       0         
   Read Count                   2         
   Read per sec                 0         
   Take Count                   0         
   Take per sec                 0         
   Update Count                 0         
   Update per sec               0         
   Write Count                  10,000    
   Write per sec                0         
   ```

###### View data types
5. Run `./gs.sh space info --type-stats <space name>` to get a summary of the data types in the space.
   ```
   $ ./gs.sh space info --type-stats demo 
   
   SPACE DETAILS           
   Space Name              demo                   
   Processing Unit Name    demo                   
   Topology                partitioned 2,1        
   Planned Instances       4                      
   Actual Instances        4                      
   Instances               demo~1_1               
                           demo~1_2               
                           demo~2_1               
                           demo~2_2               
   
   TYPES    
    DATA TYPE NAME                             OBJECTS COUNT    TEMPLATES COUNT    
    java.lang.Object                           0                0                  
    com.gigaspaces.dev.training.MessagePOJO    10,000           0                  
   
   ```
###### Query the "MessagePOJO" type
6. Run `./gs.sh space query --max-results=<number of results> <space name> <type name>`, for example:
   ```
   $ ./gs.sh space query --max-results=10 demo com.gigaspaces.dev.training.MessagePOJO
   
   content        counter    
   [B@4587623e    9937       
   [B@627203b2    9883       
   [B@7b5dd25f    9865       
   [B@469345a4    9829       
   [B@39cf2cfa    9793       
   [B@4b52c7e2    9721       
   [B@5e882bfd    9649       
   [B@26afbed     9613       
   [B@4202afa6    9577       
   [B@1fb20dd3    9559       
   
   
   SUMMARY     
   Results:    10    
   ```
   Note: the type name should be fully qualified class name with the package name.

## 4 Self-Healing

In this exercise you will be introduced to the self-healing capabilities of a space.
Basically we will ‘kill’ (using Windows Task Manager or kill -9) a GSC process and see that it restarts automatically by the gs-agent and that new partition are created accordingly.

1. Previously, after we deployed the space, we ran `./gs.sh pu list-instances` and we could see the containerIds and therefore the PIDs associated with the GSCs. Choose 1 of the GSCs PID (with primary space instance on it) and use the Task Manager or (kill -9 for Linux) in order to kill the process.

2. Once again, run `./gs.sh space list-instances demo` in order to check the recovery status.  
   Note: The killed instance may still be listed for up to a minute, until the Manager detects that the GSC is gone.

3. The following is a summary of the self-healing process.

 * A backup was promoted to Primary.
 * GSC was re-launched by the gs-agent.
 * A new backup partition was provisioned.
 * After running `kill 763534` which was the GSC containing the primary for the 1st partition:

   ```
   $ ./gs.sh space list-instances demo
   
   INSTANCE ID            MODE       SUSPEND TYPE    HOST ID      CONTAINER ID        
   demo~1_1               BACKUP     NONE            dixson-pc    dixson-pc~898349    
   demo~1_2               PRIMARY    NONE            dixson-pc    dixson-pc~763471    
   demo~2_1               PRIMARY    NONE            dixson-pc    dixson-pc~763506    
   demo~2_2               BACKUP     NONE            dixson-pc    dixson-pc~763507    
   
   
   SUMMARY        
   Instances:     4    
   Primaries:     2    
   Backups:       2    
   Hosts:         1    
   Containers:    4    
   
   ```
   Note: new container with PID 898349 was created.
 * Recovery is performed, the backup partition is now a primary.

5. Restart a primary partition by selecting the primary partition. What happens?  
   When using the cli these commands can be used:  
   `./gs.sh container list` followed by `./gs.sh container restart <container ID>`

6. In general, if you need to change a primary space to a backup it is better to use the demote command:  
   `./gs.sh space list-instances <space name>` then  
   `./gs.sh space demote-instance <instance ID>`
    
## 5 Un-deploy a space 
To undeploy a space the following can be run:  

```
$ ./gs.sh pu undeploy demo
·······
Processing Unit [demo] was successfully undeployed at 2025-09-30 16:51:22
```
You have un-deployed the space successfully!

---
## Additional resources
 * [Instructions for REST API](./restapiv3-README.md)
 * [Instructions for SpaceDeck](./spacedeck-README.md)
 * [Instructions for gs-ui (legacy)](./gsui-README.md)
 


