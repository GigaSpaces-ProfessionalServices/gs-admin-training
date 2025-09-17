# gs-admin-training - lab09-hot_deploy

# Hot Deploy Procedure

## Lab Goals

Get experience with the hot deploy procedure using REST.  
Hot deploy can be used in the following cases: 

 * Changes in PU business logic.
 * Changes in data type schema.
 * Changes in GigaSpaces version.
 * Changes in host OS or Java version.

## Lab Description
In this lab we will focus on Hot Deploy when there are changes in PU business logic.

We will:

 * Add the Custom Rest Upgrade Plugin.
 * Run the BillBuddy application. - as you did already in lab05.
 * Change the BillBuddy application logic.
 * Create a new JAR with this change.
 * Upload the new JAR.
 * Make the application to use the new jar without any downtime. 

### Add the Custom Rest Upgrade Plugin

1. Clone the github CustomRestPlugins repository.
```
    $ git clone https://github.com/GigaSpaces-ProfessionalServices/CustomRestPlugins.git
    Cloning into 'CustomRestPlugins'...
    remote: Enumerating objects: 12, done.
    remote: Counting objects: 100% (12/12), done.
    remote: Compressing objects: 100% (7/7), done.
    remote: Total 12 (delta 1), reused 12 (delta 1), pack-reused 0
    Unpacking objects: 100% (12/12), 3.04 KiB | 389.00 KiB/s, done.
```    
2. Build the project.
```        
    $ cd CustomRestPlugins/
    $ mvn package
    [INFO] ------------------------------------------------------------------------
    [INFO] Reactor Summary for CustomRestPlugins 1.0-SNAPSHOT:
    [INFO] 
    [INFO] CustomRestPlugins .................................. SUCCESS [  0.180 s]
    [INFO] updatePlugin ....................................... SUCCESS [  0.963 s]
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS
    [INFO] ------------------------------------------------------------------------
    [INFO] Total time:  1.217 s
    [INFO] Finished at: 2020-04-21T18:33:28+03:00
    [INFO] ------------------------------------------------------------------------
```
3. Install the custom rest plugin.  
   Copy updatePlugin/target/updatePlugin.jar to `${GS_HOME}/lib/platform/manager/plugins/`

**Note:** For more information on extending the REST Manager, please visit our [online documentation](https://docs.gigaspaces.com/latest/admin/xap-manager-rest-pluggable.html).
 
### Run the BillBuddy application.
The steps are the same as you have done in lab05-BillBuddy_training_example.

#### 1	Start gs-agent

1. Navigate to `$GS_HOME/bin`
        
2. Start gs-agent with local Manager server and 4 GSCs:
   `./gs.sh host run-agent --auto --gsc=4`
    
#### 2	Deploy BillBuddy_Space.jar
    
1. Open `$GS_ADNIN_TRAINING/lab09-hot_deploy` project with Intellij (open pom.xml).
2. Run `mvn package`
```
   `~/gs-admin-training/lab09-hot_deploy$ mvn package`
    
    
    [INFO] Reactor Summary:
    [INFO] 
    [INFO] lab09-hot_deploy ............................................... SUCCESS [  0.204 s]
    [INFO] BillBuddyModel ..................................... SUCCESS [  1.087 s]
    [INFO] BillBuddy_Space .................................... SUCCESS [  0.207 s]
    [INFO] BillBuddyAccountFeeder ............................. SUCCESS [  0.189 s]
    [INFO] BillBuddyCurrentProfitDistributedExecutor .......... SUCCESS [  0.225 s]
    [INFO] BillBuddyWebApplication ............................ SUCCESS [  0.349 s]
    [INFO] BillBuddyPaymentFeeder ............................. SUCCESS [  0.190 s]
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS
```

3. Open a new terminal and navigate to `$GS_HOME/bin/`
           
4. Use the gs CLI to deploy BillBuddy_Space.  
   `./gs.sh pu deploy BillBuddyPU ~/gs-admin-training/lab09-hot_deploy/BillBuddy_Space/target/BillBuddy_Space.jar`

#### 3	Run BillBuddyAccountFeeder from Intellij

1. Set IntelliJ path variables (under preferences).

###### Add GS_LOOKUP_GROUPS & GS_LOOKUP_LOCATORS
2. Copy the `~/gs-admin-training/lab09-hot_deploy/runConfigurations` directory into Intellij's .idea directory and restart Intellij.
```
  cd ~/gs-admin-training/lab09-hot_deploy
  cp -r runConfigurations .idea/
```

3. From Intellij run configuration select BillBuddyAccountFeeder and run it.

###### This application writes Users, Merchants and Contracts to the Space
 
4. Validate Users and Merchants were written to the space using the REST API.
   
    * Go to: http://localhost:8090/v2, expand 'Spaces' and look for `spaces/{id}/objectsTypeInfo`
    * Enter in the id field: BillBuddySpace

![Screenshot](./Pictures/Picture1.png)

5. Query the list of Users by executing the following SQL.  
   Note: Click the Data Type Name and the sql will be created for you.  
   Once again in the using the REST Manager Swagger interface:
    * In the 'Spaces' section expand `/spaces/{id}/query`
    * Enter in the id field: BillBuddySpace, typeName: `com.gs.billbuddy.model.User` 
    
###### Note: Fully qualified class name is required.

![Screenshot](./Pictures/Picture2.png)

#### 4	Run BillBuddyPaymentFeeder project
The BillBuddyPaymentFeeder application creates payments by randomly choosing a user, 
a merchant and an amount and performs the initial process of a payment.  
This includes deposit and withdrawal updates of each party’s balance appropriately. 
After the payment is initially processed it is written to the space for further processing.   
A new Payment is created every second.
 
1. Run the **BillBuddyPaymentFeeder** using Intellij: 
Use the same instructions as used for the BillBuddyAccountFeeder.
2. Validate Payments were written to the space.
   Click the Payment Data Type Name as you did in the section above.
3. In the REST Manager Swagger interface, go to `get /spaces/{id}/instances/{instanceId}/statistics/operations`
4. For id, enter `BillBuddySpace`. For instanceId, enter for example `BillBuddySpace~1_1`.
5. Check to see that a payment is actually added every second.

## Hot Deploy

---

### Modify the BillBuddy application logic
We will just change a log message.

1. Open `~/gs-admin-training/lab09-hot_deploy/BillBuddy_Space/src/main/java/com/gs/billbuddy/events/ProcessingFeePollingEventContainer.java` class with IntelliJ.
2. Verify that you see this line at the end of the class:
```
    log.info("ProcessingFeeTransaction updates merchants transactionFeeAmount. Merchant: " + merchant.getName() +
                        " - new transactionFeeAmount is " + merchant.getFeeAmount());
```                        
3. Open the GSA console log (in the terminal window) and verify that you see this printing rolling for each Merchant.

![Screenshot](./Pictures/Picture4.png)

4. Go to Intellij and make a change something in the log message. For example:
```
      log.info("ProcessingFeeTransaction updates merchants transactionFeeAmount. Merchant: " + merchant.getName() +
                            " ** My new JAR ** - new transactionFeeAmount is " + merchant.getFeeAmount());
```          
          
### Create a new JAR 
         
1. Run mvn package.
```    
    ~/gs-admin-training/lab09-hot_deploy$ mvn package
    
    [INFO] ------------------------------------------------------------------------
    [INFO] Reactor Summary:
    [INFO] 
    [INFO] lab09-hot_deploy .............................................. SUCCESS [  0.215 s]
    [INFO] BillBuddyModel ..................................... SUCCESS [  1.354 s]
    [INFO] BillBuddy_Space .................................... SUCCESS [  0.693 s]
    [INFO] BillBuddyAccountFeeder ............................. SUCCESS [  0.242 s]
    [INFO] BillBuddyCurrentProfitDistributedExecutor .......... SUCCESS [  0.172 s]
    [INFO] BillBuddyWebApplication ............................ SUCCESS [  0.400 s]
    [INFO] BillBuddyPaymentFeeder ............................. SUCCESS [  0.234 s]
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS
```
    
3. Rename the jar file.
```
    cd ~/gs-admin-training/lab09-hot_deploy/BillBuddy_Space/target
    mv BillBuddy_Space.jar BillBuddy_Space_V2.jar
```

### Upload the new JAR

1. Open the REST Manager API and navigate to Processing Units.
```
   PUT /pus/resources (http://localhost:8090/v2/index.html#!/Processing_Units/put_pus_resources)
```
2. Click on "Choose File" button and select BillBuddy_Space_V2.jar.  
3. Click on "Try it out!" button and verify that the response code is 201.

![Screenshot](./Pictures/Picture6.png)

4. Verify that the new jar has been successfully uploaded:
![Screenshot](./Pictures/Picture7.png)

### Update the PU code using the plugin

1. Run the following curl command:
```
curl -X PUT --header 'Accept: application/json' 'http://localhost:8090/update/updatePu?oldResource=BillBuddy_Space.jar&newResource=BillBuddy_Space_V2.jar'
```
###### Note: The `update/updatePu` REST endpoint is not deployed under the v2 context.
2. Verify that the return code is 0.
```
        echo $?
        0
```        
3. You should receive a successful message:

![Screenshot](Pictures/Picture8.png)

4. Verify that the new jar has been removed:

![Screenshot](./Pictures/Picture9.png)

### Use the new PU
#### Restart the Containers.  
1. First restart backup only after primary.  
2. See the screenshot below for the list of commands. 
   For your convenience:
   ```
   ./gs.sh space list-instance BillBuddySpace
   ./gs.sh container restart <container id> # where container id follows the format hostname~PID
   ```

![Screenshot](./Pictures/Picture10.png)

2. Go to the GSA console log and verify the change in the log message.

![Screenshot](./Pictures/Picture11.png)
