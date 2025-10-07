# gs-admin-training - lab07-gigaspaces_manager

# GigaSpaces manager 

## Lab Goals

1. See how easy it is to setup a GigaSpaces cluster.
2. Get familiar with GS Manager's capabilities.

## Lab Description
In this lab we will focus on the GS Manager functionality.  
To better know its capabilities you will set up 4 machine cluster based on Vagrant.

### 1 Setup Vagrant framework on your machine

#### Install VirtualBox

https://www.virtualbox.org/wiki/Downloads

#### Install Vagrant

https://www.vagrantup.com/downloads.html

### 2 "vagrant up" and provisioning your virtual machine

1. `cd ~/gs-admin-training/lab07-gigaspaces_manager/Vagrant`
2. `vagrant up`
3. Wait until all 4 virtual machines are booted and ready.

See in the following screenshot node3 is done (booted and ready) and immediately after node2 has started to boot...  
![Screenshot](./Pictures/Picture1.png)

**Note:** For more information on "How to Set Up a Local Linux Environment with Vagrant":  
`https://medium.com/@JohnFoderaro/how-to-set-up-a-local-linux-environment-with-vagrant-163f0ba4da77`


### 3 Explore the cluster

Please connect to the REST Manager using the cli. You will need to use your local `$GS_HOME` installation. See commands below.   
It should be available on all manager machines: node1, node2 and node3.  
On node4 you shouldn't be able to connect to the REST Manager as it is a machine without GigaSpaces Manager.  
*Note: Use the --server option to connect to a specific GigaSpaces Manager node.*  
```
dixson@dixson-pc:~/gigaspaces-smart-cache-enterprise-17.1.2/bin$ ./gs.sh --server=10.211.55.101:8090 host list

HOST NAME    HOST ADDRESS     CONTAINERS COUNT    
node4        10.211.55.104    0                   
node2        10.211.55.102    0                   
node3        10.211.55.103    0                   
node1        10.211.55.101    0                   


SUMMARY        
Hosts:         4    
Containers:    0    

dixson@dixson-pc:~/gigaspaces-smart-cache-enterprise-17.1.2/bin$ ./gs.sh --server=10.211.55.101:8090 info

INFO
Lookups Groups   xap-17.1.2
Managers         node1,node2,node3
Revision         17.1.2
Started On       2025-10-07 18:30:25.514
Version          17.1.2

```

If you see the above, lab has been completed successfully by you :-)


**Tip:**  
To login to the machines goto the Vagrant directory and run:<br>
    `vagrant ssh node1` (e.g. login to node1)

### 4 Explore Apache ZooKeeper under the hood (optional section if time permits)

 * Open `$GS_TRAINING_HOME/lab07-gigaspaces_manager/zk-client-example` project with Intellij (open pom.xml).
 * Copy the [runConfigurations](zk-client-example/runConfigurations) directory to .idea folder. Restart Intellij.
 * The project has a simple Java program that can traverse the Zookeeper nodes and print its data.
 * Run the zk-client-example run configuration from Intellij.
 * Explore GS ZK tree to better understand GigaSpaces and the information stored in ZK.

### 5 Note:

 * If you see the following error:

The IP address configured for the host-only network is not within the
allowed ranges. Please update the address used to be within the allowed
ranges and run the command again.

Continue with the recommendations mentioned.

Edit `/etc/vbox/networks.conf`, add the following:
```
      * 10.0.0.0/8 192.168.0.0/16
      * 2001::/64
```
 * To determine the ip address to access the ui:
```
vagrant ssh node1
ip address show
```
For example:
| Node | IP Address |
|------|------------|
| Node1 | http://10.211.55.101:8090/ |
| Node2 | http://10.211.55.102:8090/ |
| Node3 | http://10.211.55.103:8090/ |
| Node4 | 10.211.55.104 (No web-ui or GigaSpaces Manager running)|     
