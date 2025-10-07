# gs-admin-training - lab09-cli

# GigaSpaces Command Line Interface

## Lab Goals

1. Explore in more detail the GigaSpaces Command Line Interface.
2. Get familiar with GigaSpaces Command Line Interface's capabilities.

## Lab Description
In this lab we will focus on GigaSpaces Command Line Interface.  
To better understand its capabilities you will practice using the GigaSpaces Command Line Interface.

### 1 GigaSpaces Command Line Interface

Please perform the following steps:

1. Run the grid without any GSC (only the GS manager).
2. Raise 6 GSC as follows:
 * 4 with zone stateful.
```
  cd $GS_HOME/bin
  export GS_GSC_OPTIONS="-Dcom.gs.zones=stateful"
  ./gs.sh host run-agent --gsc=4
```
 * 2 with zone stateless.
```
  export GS_GSC_OPTIONS="-Dcom.gs.zones=stateless"
  ./gs.sh host run-agent --gsc=2
```
3. Deploy BillBuddySpace on the 4 GSC with zone stateful, use the jar from lab04:
```   
  ./gs.sh pu deploy --zones=stateful BillBuddyPU ~/gs-admin-training/lab04-BillBuddy_training_example/BillBuddy_Space/target/BillBuddy_Space.jar
```
5. Deploy 2 instances of BillBuddyWebApplication, both instances should be located on 2 GSC of zone stateless.  
   Use the war from lab5:
```
  ./gs.sh pu deploy --zones=stateless BillBuddyWebApp ~/gs-admin-training/lab04-BillBuddy_training_example/BillBuddyWebApplication/target/BillBuddyWebApplication.war
```
**Note:** Please perform sections 1-4 **only** by using **GigaSpaces CLI**.

Once done please use GigaSpaces REST Manager and curl to verify the outcome:  
```
curl -X GET --header 'Accept: application/json' 'http://localhost:8090/v2/pus'
```
Note the above command was copied from the Swagger UI at `http://localhost:8090`, Processing Units | 'GET' `/pus` `List processing units`.  
You should the get below response in JSON *which has been pretty-printed* below.  
```
[
    {
        "name": "BillBuddyPU",
        "processingUnitType": "stateful",
        "resource": "BillBuddy_Space.jar",
        "topology": {
            "schema": "partitioned",
            "partitions": 2,
            "backupsPerPartition": 1
        },
        "sla": {
            "requiresIsolation": false,
            "zones": [
                "stateful"
            ],
            "primaryZones": [],
            "maxInstancesPerVM": 1,
            "maxInstancesPerMachine": 0
        },
        "spaces": [
            "BillBuddySpace"
        ],
        "scalable": false,
        "status": "intact",
        "quiesceDetails": {
            "quiesced": false,
            "description": "initial",
            "readonly": false
        },
        "instances": [
            "BillBuddyPU~1_1",
            "BillBuddyPU~1_2",
            "BillBuddyPU~2_1",
            "BillBuddyPU~2_2"
        ]
    },
    {
        "name": "BillBuddyWebApp",
        "processingUnitType": "web",
        "resource": "BillBuddyWebApplication.war",
        "topology": {
            "instances": 1
        },
        "sla": {
            "requiresIsolation": false,
            "zones": [
                "stateless"
            ],
            "primaryZones": [],
            "maxInstancesPerVM": 0,
            "maxInstancesPerMachine": 0
        },
        "spaces": [],
        "scalable": true,
        "status": "intact",
        "quiesceDetails": {
            "quiesced": false,
            "description": "initial",
            "readonly": false
        },
        "instances": [
            "BillBuddyWebApp~1"
        ]
    }
]
```





   
