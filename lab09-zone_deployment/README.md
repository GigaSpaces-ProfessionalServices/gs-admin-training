# gs-admin-training - lab09-zone_deployment

# Zone-Based Deployment with the GigaSpaces CLI

## Lab Goals

1. Use the GigaSpaces Command Line Interface (`gs.sh`) to build and inspect a grid.
2. Use zones to control which GSCs a Processing Unit is deployed on.

## Lab Description
In this lab you start GSCs in two zones, `stateful` and `stateless`, and use the CLI to deploy the BillBuddy space to the `stateful` GSCs and the BillBuddy web application to the `stateless` GSCs.
You then check the placement with the CLI and with the REST Manager (REST v3).

**Note:** Perform steps 1-5 **only** with the **GigaSpaces CLI**.

### Prerequisites
Build lab04 (`mvn package` in `~/gs-admin-training/lab04-BillBuddy_training_example`). This lab uses its `BillBuddy_Space.jar` and `BillBuddyWebApplication.war`.

### 1 Start the GigaSpaces manager without GSCs

1. Go to `$GS_HOME/bin`
2. Make sure `GS_MANAGER_SERVERS` is set in `setenv-overrides.sh`, and that `GS_GSC_OPTIONS` is **not** set there (it would override the zones below):
   ```
   export GS_MANAGER_SERVERS="localhost"
   ```
3. Start the manager, the web UI and REST v3, with no GSCs:
   ```
   ./gs.sh host run-agent --manager --webui --restv3
   ```

### 2 Start 4 GSCs in zone `stateful`
Open a new terminal:
```
cd $GS_HOME/bin
export GS_GSC_OPTIONS="-Dcom.gs.zones=stateful"
./gs.sh host run-agent --gsc=4
```

### 3 Start 2 GSCs in zone `stateless`
Open another terminal:
```
cd $GS_HOME/bin
export GS_GSC_OPTIONS="-Dcom.gs.zones=stateless"
./gs.sh host run-agent --gsc=2
```

Each `run-agent` starts its own agent (GSA). Its GSCs get the zone that was set in `GS_GSC_OPTIONS` when it started.

See also: [Customizing the GSA Components](https://docs.gigaspaces.com/latest/admin/the-runtime-environment.html#CustomizingtheGSAComponents)

### 4 Deploy BillBuddySpace on the `stateful` GSCs
Use the jar from lab04:
```
./gs.sh pu deploy --zones=stateful BillBuddyPU ~/gs-admin-training/lab04-BillBuddy_training_example/BillBuddy_Space/target/BillBuddy_Space.jar
```
The space is partitioned 2,1 (its `META-INF/spring/sla.xml` sets this), so 4 instances are deployed, one on each `stateful` GSC.

### 5 Deploy 2 instances of BillBuddyWebApplication on the `stateless` GSCs
Use the war from lab04:
```
./gs.sh pu deploy --zones=stateless --instances=2 BillBuddyWebApp ~/gs-admin-training/lab04-BillBuddy_training_example/BillBuddyWebApplication/target/BillBuddyWebApplication.war
```

### 6 Verify with the CLI
List the GSCs with their zones and the instances they host:
```
./gs.sh container list
```
Every `BillBuddyPU` instance should be on a `stateful` GSC, and every `BillBuddyWebApp` instance on a `stateless` GSC (container IDs and PIDs will differ):
```
CONTAINER ID         PID        ZONES        PROCESSING UNITS INSTANCES
myhost~2291587    2291587    stateless    BillBuddyWebApp~1
myhost~2291606    2291606    stateless    BillBuddyWebApp~2
myhost~2291693    2291693    stateful     BillBuddyPU~2_1
myhost~2291602    2291602    stateful     BillBuddyPU~1_1
myhost~2291594    2291594    stateful     BillBuddyPU~1_2
myhost~2291595    2291595    stateful     BillBuddyPU~2_2
```
Also try `./gs.sh pu list` and `./gs.sh pu info BillBuddyPU` (the `Zones` line shows `stateful`).

### 7 Verify with the REST Manager (REST v3)
```
curl -X GET --header 'Accept: application/json' 'http://localhost:9090/api/v3/pus'
```
This is the request that the Swagger UI at `http://localhost:9090/api/v3/swagger-ui/index.html` sends for **Processing Units** | `GET /api/v3/pus` (`Lists processing units`).
You should get the response below (pretty-printed here):
```
[
    {
        "name": "BillBuddyPU",
        "resource": "BillBuddy_Space.jar",
        "topology": {
            "partitions": 2,
            "backupsPerPartition": 1,
            "schema": "partitioned"
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
        ],
        "processingUnitType": "stateful",
        "status": "intact"
    },
    {
        "name": "BillBuddyWebApp",
        "resource": "BillBuddyWebApplication.war",
        "topology": {
            "instances": 2
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
        "quiesceDetails": {
            "quiesced": false,
            "description": "initial",
            "readonly": false
        },
        "instances": [
            "BillBuddyWebApp~1",
            "BillBuddyWebApp~2"
        ],
        "processingUnitType": "web",
        "status": "intact"
    }
]
```
To see which GSC hosts each instance, use `GET /api/v3/pus/{id}/instances`, for example:
```
curl -s 'http://localhost:9090/api/v3/pus/BillBuddyPU/instances'
```

### 8 Clean up
```
./gs.sh pu undeploy BillBuddyWebApp
./gs.sh pu undeploy BillBuddyPU
./gs.sh host kill-agent --all
```
`--all` is needed because three agents are running on this host.
