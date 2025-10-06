# ODSX Lab-A

## Lab Summary
   
1. Download required files
2. Environment setup and AWS Configuration (STATELESS ODSX)
3. Prerequisties - prepare your files before running the scripts
4. Prerequisites - Run the scripts  
5. ODSX Configuration
     * ODSX Configuration security setup
     * ODSX Configuration new host / supporting jar setup
6. DIH
     * DI (Zookeeper+kafka+telegraf+DI-services)
     * Grafana
     * Influxdb
     * Manager
     * Space
     * Northbound
        * Applicative
        * Management
        * Agent
  ----------------------------------------------
<br />

### 1. Download required files

* Main : https://github.com/GigaSpaces-ProfessionalServices/gs-odsx.git
* Release : https://github.com/GigaSpaces-ProfessionalServices/gs-odsx/archive/refs/tags/v3.55-release.tar.gz 
* https://github.com/GigaSpaces-ProfessionalServices/CSM-Magic-Tools.git
  

### 2. Environment setup and AWS Configuration (STATELESS ODSX)

* Create EC2 Instances on AWS based on your requirement (OS: Redhat:7.7 RHEL)
  <br />
  * Pivot (ODSX)-1
  * Manager-3
  * Space-2
  * DataIntegration-1
  * Grafana and Influx - (on pivot)
  * Northbound applicative-1
  * Northbound management-1
  
<br />
      
* Prepare pivot and other insatnces by using the lab scripts.
  The following steps will be done:

  * On pivot:

    1. Create required local folders
    2. Install NFS server (/dbagigashare will be shared for all the remote hosts)
    3. Allow SSH to Pivot as root (blocked by default in aws)
    4. Install the required rpms.
    5. Create gsods user
   

  * On remote hosts:
    1. Creates required local folders
    2. Allow SSH to Pivot as root (blocked by default in aws)
    3. create gsods user

-----------------------------------------------------------------
   - Connect to the PIVOT machine and swith user to root.
   - Upload the pre-req script, odsx zip file and your pem (ssh-key) to /tmp. 
   - Your work dirctory should contain:

   ```
[root@ip-10-0-3-104 lab17_odsx_prereq_scripts]# ll

-r--------. 1 centos centos    1675 Sep 28 19:29 aharon_ami.pem
-rw-rw-r--. 1 centos centos 3918775 Sep 28 19:29 gs-odsx-3.55-release.zip
-rw-rw-r--. 1 centos centos      96 Sep 28 19:30 lab17_aws_hosts.txt
-rwxrwxr-x. 1 centos centos   15543 Sep 28 20:12 odsx_aws_prereq.sh

   ```
  - Create a txt (i.e: lab17_aws_hosts.txt) file which contains your aws instances IP or hostname, one IP on each line.
  ```
  cat lab17_aws_hosts.txt

10.0.1.29
10.0.2.30
10.0.3.40

  ```
- Run the scripts one-by-one, as root, change mode if required:
  
```
chmod +x odsx_aws_prereq.sh 
./odsx_aws_prereq.sh

```
Provide the correct details:
```
ODSX lab prerequisits script
============================
Pleae enter the aws user [centos] :
Please enter the pivot private ip or hostname (don't use localhost/127.0.0.1) [10.0.3.104]:
Please provide the hosts text file wich contains all the aws instances ips [./lab17_aws_hosts.txt]:
Enter downloads destination [/dbagigashare/current]:
Please provide gs-odsx ZIP file full path [/tmp/lab17_odsx_prereq_scripts/gs-odsx-3.55-release.zip]:

Log file: lab17_prereq.log
Pivot IP: 10.0.3.104
ssh_key: ./aharon_ami.pem
Remote aws hosts ip:
Save downloads to: /dbagigashare/current
ODSX arcive file: /tmp/lab17_odsx_prereq_scripts/gs-odsx-3.55-release.zip
10.0.3.39
10.0.3.198
10.0.1.66
10.0.3.104
10.0.3.211
10.0.1.247
10.0.2.145
10.0.3.25
10.0.3.245

The following steps will be done:

On pivot:

1. Create required local folders
2. Install NFS server (/dbagigashare will be shared for all the remote hosts)
3. Allow SSH to Pivot as root (blocked by default in aws)
4. Install the required rpms.
5. Create gsods user
6. Download require files
7. Install ODSX

On remote hosts:

1. Creates required local folders
2. Allow SSH to Pivot as root (blocked by default in aws)
3. create gsods user

Pleas enter [Y/y] to continue or any key to abort: y
```
```
Running prerequisits in PIVOT only...
Creating local folders in Pivot ...
Downloading sources to Pivot [/dbagigashare/current] ...
--2022-09-28 20:45:25--  https://tapangigaspaces.s3.us-east-2.amazonaws.com/odsx/install/unzip/unzip-6.0-21.el7.x86_64.rpm
Resolving tapangigaspaces.s3.us-east-2.amazonaws.com (tapangigaspaces.s3.us-east-2.amazonaws.com)... 52.219.177.74
Connecting to tapangigaspaces.s3.us-east-2.amazonaws.com (tapangigaspaces.s3.us-east-2.amazonaws.com)|52.219.177.74|:443... connected.
HTTP request sent, awaiting response... 200 OK
Length: 175412 (171K) [binary/octet-stream]
.
.
.

Running prerequisits script on remote host[10.0.3.39] ...
Running prerequisits script on remote host[10.0.3.198] ...
Running prerequisits script on remote host[10.0.1.66] ...
Running prerequisits script on remote host[10.0.3.104] ...
Running prerequisits script on remote host[10.0.3.211] ...
Running prerequisits script on remote host[10.0.1.247] ...
Running prerequisits script on remote host[10.0.2.145] ...
Running prerequisits script on remote host[10.0.3.25] ...
Running prerequisits script on remote host[10.0.3.245] ...

```
After a while the script will test ssh connection as root and dbagigashare mount point.
<br />
NOTE: The Pivot should not have a /dbagiashare mount, in this case a the expected result is 'does NOT exist'.

```
Testing SSH connection between Pivot and other hosts...
=======================================================

Testing SSH as root from Pivot[10.0.3.104] to 10.0.3.39 ...
SSH[root]: OK
mount point /dbagigashare OK
gsods user: OK
---------------------------------------------------------------------------------------------
Testing SSH as root from Pivot[10.0.3.104] to 10.0.3.198 ...
SSH[root]: OK
mount point /dbagigashare OK
gsods user: OK
---------------------------------------------------------------------------------------------
Testing SSH as root from Pivot[10.0.3.104] to 10.0.1.66 ...
SSH[root]: OK
mount point /dbagigashare OK
gsods user: OK
---------------------------------------------------------------------------------------------
Testing SSH as root from Pivot[10.0.3.104] to 10.0.3.104 ...
SSH[root]: OK
mount point /dbagigashare does NOT exist.
gsods user: OK
---------------------------------------------------------------------------------------------
Testing SSH as root from Pivot[10.0.3.104] to 10.0.3.211 ...
SSH[root]: OK
mount point /dbagigashare OK
gsods user: OK
---------------------------------------------------------------------------------------------
Testing SSH as root from Pivot[10.0.3.104] to 10.0.1.247 ...
SSH[root]: OK
mount point /dbagigashare OK
gsods user: OK
---------------------------------------------------------------------------------------------
Testing SSH as root from Pivot[10.0.3.104] to 10.0.2.145 ...
SSH[root]: OK
mount point /dbagigashare OK
gsods user: OK
---------------------------------------------------------------------------------------------
Testing SSH as root from Pivot[10.0.3.104] to 10.0.3.25 ...
SSH[root]: OK
mount point /dbagigashare OK
gsods user: OK
---------------------------------------------------------------------------------------------
Testing SSH as root from Pivot[10.0.3.104] to 10.0.3.245 ...
SSH[root]: OK
mount point /dbagigashare OK
gsods user: OK

```     
The script will be finished with the following:
```
Extracting odsx zip file...
Creatting symlink...
Updating gs-license.txt [tryme]...
Installing odsx...

.
.
.

Copying odsx configuration files to /dbagigashare ...


========================================================
To use odsx:

        - Please update the file 'hosts.yaml' at /dbagigashare/current/odsx.
        - Logout & login as root
        - cd /dbagiga/gs-odsx
        - ./odsx.py

Log file: lab17_prereq.log

```

When odsx requires a pem (ssh private key) to connect remote hosts, update the app.config file, for example:
```
cluster.usingPemFile=True
cluster.pemFile=aharon_ami.pem

```

<br />

Run below will start displaying various menu options 

./odsx.py

![Screenshot](./pictures/odsx_sox_menu.png)

### 4. DIH
#### 4.1 DI server(Kafka+zk+telegraf+DI-services) Menu -> Servers -> DI

##### 4.1.1 Install

 - Install DI with cluster mode
   - 3/4 host / servers needed to install 
   - Single node also supports.
   - ![Screenshot](./pictures/odsx_sox_di_install_0.png)
   - ![Screenshot](./pictures/odsx_sox_di_install.png)
   - ![Screenshot](./pictures/odsx_sox_di_install_1.png)
   - Verify by List
   - ![Screenshot](./pictures/odsx_sox_di_install_2.png)

##### 4.1.2 Start   
- After installation complete start DI servers
- Verify DI servers by logged in to any of host and execute below command:

 - ![Screenshot](./pictures/odsx_sox_di_start_1.png)
 - ![Screenshot](./pictures/odsx_sox_di_start_2.png)
 - Verify by List
 - ![Screenshot](./pictures/odsx_sox_di_start_3.png)

    source /home/dbsh/setenv.sh;
    $ZOOKEEPERPATH/bin/zkCli.sh -server localhost:2181 <<< "ls /brokers/ids" | tail -n 2

 ![Screenshot](./pictures/DI-Cluster.png)

##### 4.1.3 Stop
   - ![Screenshot](./pictures/odsx_sox_di_stop_1.png)
   - Verify by list  

##### 4.1.4 Remove
   - ![Screenshot](./pictures/odsx_sox_di_remove_1.png)
   - ![Screenshot](./pictures/odsx_sox_di_remove_2.png)
   - Verify by List

#### 4.2 Grafana Server (Menu -> Servers -> Grafana)**

##### 4.2.1 Install

- Keep Grafana rpm file under /dbagigashare/current/grafana/grafana-7.3.7-1.x86_64.rpm

- ![Screenshot](./pictures/odsx_sox_grafana_install.png)

##### 4.2.2 Start

- It will start Grafana server
 - Verify Grafana started or not by http://< Grafana host >:3000/ web browser

 ![Screenshot](./pictures/odsx_sox_grafana_start.png)
 - Verify after starting grafana on UI
 - <host>:3000 
 - Username/Password : admin/admin
 ![Screenshot](./pictures/odsx_sox_grafana.png)

##### 4.2.3 Stop

- It will stop Grafana service on installed host
  - ![Screenshot](./pictures/odsx_sox_grafana_stop.png)
##### 4.2.4 Remove

   - It will Remove and uninstall Grafana host

#### 4.3 Influxdb (Menu -> Servers -> Influxdb)
 
##### 4.3.1 Install
- Keep rpm file under /dbagigashare/current/influx/ 
- By default service will start and create default database mydb

##### 4.3.2 Start

- It will start influxdb service 
- Verify it by :
    - [root@ip-10-0-0-165 ~]# influx
      -Connected to http://localhost:8086 version 1.8.4
      =InfluxDB shell version: 1.8.4
      
      influx> show databases
      - name: databases
        
        name
        ----
        mydb
        _internal
        telegraf
        
      influx> use mydb
        
        Using database mydb
        
      influx> show measurements
        
        name: measurements
        name
        ----
        jvm_memory_gc_count
        jvm_memory_gc_time
        .....
##### 4.3.3 Stop

- It will stop influxdb service on installed host

##### 4.3.4 Remove
- It will Remove and uninstll Infuxdb host


#### 4.4 Manager (Menu -> Servers -> Manager)
 
##### 4.4.1 Install  
  - This option will install manager and prerequisite software for running Manager (**On Remote machine**).
    ![Screenshot](./pictures/odsx_sox_manager_install_1.png)
  - Example  :

    java : /dbagigashare/current/jdk/jdk-11.0.11_linux-x64_bin.rpm 

    unzip : /dbagigashare/current/unzip/unzip-6.0-21.el7.x86_64.rpm 

    gs  : /dbagigashare/current/gs/gigaspaces-xap-enterprise-16.0.0.zip
    license : Configure license under /dbagigashare/current/gs/config/license/gs-license.txt
    ##### Read summary carefully each parameter need to configure    
    ![Screenshot](./pictures/odsx_sox_manager_install_2.png)
    - Proceed with Installation Param up to final confirmation of installation.
   
    ![Screenshot](./pictures/odsx_sox_manager_install_3.png)
    After completion of installation do list
    
    - Menu -> Servers -> Manager -> List
    ![Screenshot](./pictures/odsx_sox_manager_install_4.png)
    
  
   - It will add the newly installed manager into cluster.config file as place holders instead of host.

##### 4.4.2 Start

- This option will start the manager which is installed on remote machine.
- By default it will start all, you can choose menu for individual start
  ![Screenshot](./pictures/odsx_sox_manager_start.png)
  
  Verify manager start with browser. 
  - Verify with old ui
  ![Screenshot](./pictures/odsx_sox_manager_start_1.png)
  - Verify with ops manager
  ![Screenshot](./pictures/odsx_sox_manager_start_2.png)
  - Verify with odsx
  ![Screenshot](./pictures/odsx_sox_manager_start_3.png)
##### 4.4.3 Stop

- This option will stop the manager which is currently running on remote machine.
  - Verify manager stop with browser. 
  - Verify with old ui
  ![Screenshot](./pictures/odsx_sox_manager_stop_1.png)
  - Verify with ops manager
  ![Screenshot](./pictures/odsx_sox_manager_stop_2.png)
  - Verify with odsx
  ![Screenshot](./pictures/odsx_sox_manager_stop_3.png)

##### 4.4.4 List

- It will list out the configured manager from the cluster.config file.
 -   ![Screenshot](./pictures/odsx_sox_manager_list.png)

  

##### 4.4.5 Remove

- It will remove installed manager servers from cluster config and optional to remove supported software java / unzip

##### 4.4.6 Upgrade
   - You can change the version of manager 
   - Put GS installer under /dbagigashare/current/gs/upgrade/ 
     - [ec2-user@ip-10-0-0-121 gs-odsx-sox]$ ls /dbagigashare/current/gs/upgrade/
       gigaspaces-smart-dih-enterprise-16.2.0-m4-sun-10.zip
   ![Screenshot](./pictures/odsx_sox_manager_upgrade_1.png)
   ![Screenshot](./pictures/odsx_sox_manager_upgrade_2.png)
   ![Screenshot](./pictures/odsx_sox_manager_upgrade_3.png)
   ![Screenshot](./pictures/odsx_sox_manager_upgrade_4.png) 
   - Verify it by odsx List
   ![Screenshot](./pictures/odsx_sox_manager_upgrade_5.png)
   
##### 4.4.7 Rollback
   - This option will rollback upgraded version to previous one
   ![Screenshot](./pictures/odsx_sox_manager_rollback.png)
   ![Screenshot](./pictures/odsx_sox_manager_rollback_1.png)
   - Verify it by odsx list
   ![Screenshot](./pictures/odsx_sox_manager_rollback_2.png)



#### 4.5 Space Server (Menu -> Servers -> Space)
 
##### 4.5.1 Install

  - This option will install space server and prerequisite software for running Space server (**On Remote machine**).
    ![Screenshot](./pictures/odsx_server_space_install_0.png)
  - Example  :

    java : /dbagigashare/current/jdk/jdk-11.0.11_linux-x64_bin.rpm 

    unzip : /dbagigashare/current/unzip/unzip-6.0-21.el7.x86_64.rpm 

    gs  : /dbagigashare/current/gs/gigaspaces-xap-enterprise-16.0.0.zip
    ![Screenshot](./pictures/odsx_server_space_install_1.png)
    - Verify number of GSC and Memory required for GSCs
    ![Screenshot](./pictures/odsx_server_space_install_2.png)
    ![Screenshot](./pictures/odsx_server_space_install_3.png)
    
##### 4.5.2 Start 

- This option will start the stopped space servers with specified GSC / manager / region at the time of installation
 - Space sever will take time curerntly sleep time is 60 Secs it will up all GSCs
 - Based on number of input GSCs it will take time to up.

  ![Screenshot](./pictures/odsx_server_space_start_1.png)

  ![Screenshot](./pictures/odsx_server_space_start_2.png)
  
  ![Screenshot](./pictures/odsx_server_space_start_3.png)
       
##### 4.5.3 Stop 

- This option will stop the space server

  ![Screenshot](./pictures/odsx_server_space_stop_1.png)
  
  ![Screenshot](./pictures/odsx_server_space_stop_2.png)

##### 4.5.4 Remove

- It will remove installed space servers from cluster config and optional to remove supported software java / unzip 


#### 4.6 Northbound (Menu -> Servers -> Northbound)
 
##### 4.6.1 Install
  - NB Applicative - different host
  - NB Management -  different host
  - NB Agent - Space host
  - Installation is two type all or individual
    -Individual - one can perform individual installation of NB Applicative / Management / Agent
    - All - one can install all at once
  - Copy <odsx>/config/nb.conf.template into 
    - /dbagigashare/current/nb/management
    - /dbagigashare/current/nb/applicative
    ![Screenshot](./pictures/odsx_sox_nb_config.png)
  - Applicative : 
   -  Verify configuration and sever details
    ![Screenshot](./pictures/odsx_sox_nb_app_install.png)
    
    ![Screenshot](./pictures/odsx_sox_nb_app_install_2.png)
    
    ![Screenshot](./pictures/odsx_sox_nb_app_install_3.png)
    
    
    - Verify it on odsx status
  - Management :
   -  Verify configuration and sever details
    ![Screenshot](./pictures/odsx_sox_nb_mgt_install.png)
    
    ![Screenshot](./pictures/odsx_sox_nb_mgt_install_1.png)
    
    ![Screenshot](./pictures/odsx_sox_nb_mgt_install_list.png)
    - Verify it on odsx status
  - Agent :
    -  Verify configuration and sever details
    ![Screenshot](./pictures/odsx_sox_nb_agt_install.png)
    
    ![Screenshot](./pictures/odsx_sox_nb_agt_install_1.png)
    
    ![Screenshot](./pictures/odsx_sox_nb_agt_install_2.png)
    - Verify it on odsx status
    ![Screenshot](./pictures/odsx_sox_nb_agt_install_list.png)
##### 4.6.2 Start

- It will start northbound service on specified server   
##### 4.6.3 Stop

- It will stop northbound service on specified server

##### 4.6.4 Remove

  - It will remove and uninstall northbound service on specified server
