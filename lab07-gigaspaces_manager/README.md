# gs-admin-training - lab07-gigaspaces_manager

# GigaSpaces manager 

## Lab Goals

1. Get familiar with GS Manager's capabilities.

## Lab Description
In this lab we will focus on the GS Manager functionality.  
To better know its capabilities you will set up a 4 node cluster with Docker Compose: 3 nodes run a GigaSpaces Manager (node1, node2, node3) and 1 node runs without a manager (node4). Each node is a separate container with its own GigaSpaces agent, like a separate machine.

**Note:** Docker Compose is used here only as a lab convenience, to simulate several machines on one computer. It is not a recommended way to deploy GigaSpaces. For a containerized or virtualized environment, use Kubernetes (see [lab13-kubernetes](../lab13-kubernetes)).

*An older version of this lab, which uses VirtualBox and Vagrant, is in [Vagrant/README.md](Vagrant/README.md).*

### 1 Prerequisites

* Docker with Docker Compose (`docker compose`): https://docs.docker.com/compose/install/
* Your local GigaSpaces installation (`$GS_HOME`), to run the CLI. GigaSpaces 18.0 requires Java 25.
* About 6 GB of disk space for the `gigaspaces/smart-cache-enterprise` image.

### 2 Start the cluster

1. `cd $GS_ADMIN_TRAINING/lab07-gigaspaces_manager/docker`
2. `docker compose up -d`  
   The first run downloads the `gigaspaces/smart-cache-enterprise:18.0.1` image. To use a different version, set `GS_IMAGE_TAG`, for example `GS_IMAGE_TAG=18.0.0 docker compose up -d`.
3. Wait about 30 seconds until the managers are up. `docker compose ps` should show node1 to node4 running.

The [docker-compose.yaml](docker/docker-compose.yaml) file sets `GS_MANAGER_SERVERS=node1,node2,node3` on every node, the same setting you put in `setenv-overrides.sh` in earlier labs. The managers on node1 to node3 form the cluster (ZooKeeper quorum, lookup service and GSM), and node4 joins it as a host with one container (GSC).

Each manager's REST Manager (port 9090 inside the container) is mapped to a different port on your machine:

| Node | Runs | From your machine |
|------|------|-------------------|
| node1 | GigaSpaces Manager, REST Manager, web UI | http://localhost:9091 (SpaceDeck), ZooKeeper on localhost:2181 |
| node2 | GigaSpaces Manager, REST Manager | localhost:9092 |
| node3 | GigaSpaces Manager, REST Manager | localhost:9093 |
| node4 | 1 container (GSC), no GigaSpaces Manager | localhost:9094 (nothing answers) |

### 3 Explore the cluster

Please connect to the REST Manager using the cli. You will need to use your local `$GS_HOME` installation. See commands below.   
It should be available on all manager nodes: node1, node2 and node3 (ports 9091, 9092 and 9093).  
On node4 (port 9094) you shouldn't be able to connect to the REST Manager as it is a node without GigaSpaces Manager.  
*Note: Use the --server option to connect to a specific GigaSpaces Manager node.*  
```
$ cd $GS_HOME/bin
$ ./gs.sh --server=localhost:9091 host list

HOST NAME    HOST ADDRESS    CONTAINERS COUNT    
node3        172.18.0.3      0                   
node1        172.18.0.4      0                   
node4        172.18.0.5      1                   
node2        172.18.0.2      0                   


SUMMARY        
Hosts:         4    
Containers:    1    

$ ./gs.sh --server=localhost:9091 info

INFO
Lookups Groups   xap-18.0.1
Managers         node1,node2,node3
Revision         18.0.1
Started On       2026-11-06 16:31:15.852
Version          18.0.1

$ ./gs.sh --server=localhost:9094 host list

Failed to identify cluster type, please make sure the Manager is up and running
```
Run `host list` with `--server=localhost:9092` and `--server=localhost:9093` too: every manager reports the same 4 hosts.

If you see the above, lab has been completed successfully by you :-)

**Tips:**
* To open a shell on a node, run `docker exec -it node1 bash` (e.g. login to node1). GigaSpaces is installed in `/opt/gigaspaces`, and its logs are in `/opt/gigaspaces/logs`.
* To see a node's console output, run `docker compose logs node1`.

### 4 Explore Apache ZooKeeper under the hood (optional section if time permits)

 * Open `$GS_ADMIN_TRAINING/lab07-gigaspaces_manager/zk-client-example` project with Intellij (open pom.xml).
 * Copy the [runConfigurations](zk-client-example/runConfigurations) directory to .idea folder. Restart Intellij.
 * The project has a simple Java program that can traverse the Zookeeper nodes and print its data.
 * Run the zk-client-example run configuration from Intellij. It connects to node1's ZooKeeper at `localhost:2181`.
 * Explore GS ZK tree to better understand GigaSpaces and the information stored in ZK. For example, `/zookeeper/config` lists the 3 ZooKeeper servers (node1, node2, node3), and `/xap/managers/gsm/leader-election/participants` has one entry per manager.

### 5 Stop the cluster

```
cd $GS_ADMIN_TRAINING/lab07-gigaspaces_manager/docker
docker compose down
```
