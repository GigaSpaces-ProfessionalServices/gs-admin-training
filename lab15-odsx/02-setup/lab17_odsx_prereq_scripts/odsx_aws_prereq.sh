#!/bin/bash
pivot_ip=`hostname -i`
aws_user=centos
ssh_key=./aharon_ami.pem
listfile=./lab17_aws_hosts.txt
logfile=lab17_prereq.log
default_path=/dbagigashare/current
wget_cmd="wget -o --no-check-certificate -P"

LINE="${pivot_ip}:/dbagigashare    /dbagigashare   nfs defaults 0 0"
FILE=/etc/fstab

echo > $logfile
clear
echo "ODSX lab prerequisits script"
echo "============================"

if [ `whoami` != "root" ];then
		echo "This script must be run as root."
		exit
fi


read -p "Pleae enter the aws user [$aws_user] :" aws_new_user
if [ ! -z $aws_new_user ]; then
        aws_user=$aws_new_user
fi

if [ `grep $aws_user /etc/passwd |wc -l` = 0 ];then
        echo "User $aws_user does not exist. "
        exit
fi

read -p "Please enter the pivot private ip or hostname (don't use localhost/127.0.0.1) [$pivot_ip]: " user_pivot
if [ ! -z "$user_pivot" ]; then
	pivot_ip=$user_pivot
fi

if [[ "$user_pivot" == "127.0.0.1" || "$user_pivot" == "localhost" ]]; then
        echo "localhost is not allowed."
	exit
fi


read -p "Please provide the hosts text file wich contains all the aws instances ips [$listfile]: " user_listfile
if [ ! -z "$user_listfile" ]; then
        listfile=$user_listfile
fi



if [ -z "$listfile" ]; then
        echo "You must provide an IPs/hostnames list file and full path for ssh-key."
        echo "i.e: ./fixRootLogin.sh hosts_list.txt ./aharon_ami.pem"
        echo "examlpe of a host file list:"
        echo
        echo "ip1"
        echo "ip2"
        echo "ip3"
        echo "hostname4"

        exit
elif [ ! -f "$listfile" ]; then
         echo "The file $listfile does not exit."
         exit
elif [ -z $ssh_key ]; then
        echo "Please specify a ssh-key path."
        exit
elif [ ! -f "$ssh_key" ]; then
         echo "$ssh_key does not exit."
         exit
fi

read -p "Enter downloads destination [${default_path}]: " user_dest_download

if [[ -z ${user_dest_download} ]];then
        
        dest_download=${default_path}
else
        dest_download=${user_dest_download}
fi

odsx_path=$(find / -type f -name "gs-odsx-*.zip" |head -1)
read -p "Please provide gs-odsx ZIP file full path [${odsx_path}]: " user_odsx_path
if [ ! -f  $user_odsx_path ];then
        echo "The file does not exist."
        exit
fi

echo "
Log file: $logfile
Pivot IP: ${pivot_ip}
ssh_key: ${ssh_key}
Remote aws hosts ip:
Save downloads to: ${default_path}
ODSX arcive file: ${odsx_path}
$(cat ${listfile})

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
" |tee -a ${logfile}

read -p "Pleas enter [Y/y] to continue or any key to abort: " continue 
if [[ "$continue" == "Y" || "$continue" == "y" ]]; then
	echo
else        
	echo "Aborted by user. " |tee -a ${logfile}
	exit
fi

# In pivot: set NFS server, set permissions, create gsods user, install required packages
echo "Running prerequisits in PIVOT only..." |tee -a ${logfile}
{
id -u gsods  &>/dev/null || useradd gsods #|tee -a ${logfile} > /dev/null
mkdir -m 777 -p /dbagigashare /dbagiga #|tee -a ${logfile} > /dev/null
yum install nfs-utils vim wget unzip -y #|tee -a ${logfile} > /dev/null
echo "/dbagigashare *" > /etc/exports #|tee -a ${logfile} > /dev/null
systemctl enable rpcbind #|tee -a ${logfile} > /dev/null
systemctl enable nfs-server #|tee -a ${logfile} > /dev/null
systemctl enable nfs-lock #|tee -a ${logfile} > /dev/null
systemctl enable nfs-idmap #|tee -a ${logfile} > /dev/null
systemctl start rpcbind #|tee -a ${logfile} > /dev/null
systemctl start nfs-server #|tee -a ${logfile} > /dev/null
systemctl start nfs-lock #|tee -a ${logfile} > /dev/null
systemctl start nfs-idmap #|tee -a ${logfile} > /dev/null
} |tee -a ${logfile} > /dev/null
echo "Creating local folders in Pivot ..."
{
mkdir -p \
/dbagigashare/current/data-integration/di-manager/ \
/dbagigashare/current/data-integration/di-mdm/ \
/dbagigashare/current/data-integration/di-flink/ \
/dbagigashare/current/telegraf/jars \
/dbagigashare/current/grafana/dashboards/ \
/dbagigashare/current/telegraf/scripts/space \
/dbagigashare/current/telegraf/config/pivot \
/dbagigashare/current/telegraf/config/agent \
/dbagigashare/current/kapacitor/config \
/dbagigashare/current/kapacitor/templates \
/dbagigashare/current/kapacitor/alerts \
/dbagigashare/current/gs/upgrade \
/dbagigashare/current/gs/config/space \
/dbagigashare/current/gs/config/metrics \
/dbagigashare/current/gs/jars/space \
/dbagigashare/current/gs/config/license \
/dbagigashare/current/grafana/catalog/jars \
/dbagigashare/current/data-validator/files \
/dbagigashare/current/data-validator/jars \
/dbagigashare/current/gs/jars/ts \
/dbagigashare/current/mq-connector/adabas/jars \
/dbagigashare/current/mq-connector/adabas/config \
/dbagigashare/current/mssql/files \
/dbagigashare/current/mq-connector \
/dbagigashare/current/security/jars/cef \
/dbagigashare/current/gs/config/log/ \
/dbagigashare/current/gs/jars \
/dbagigashare/current/gs/config/ts \
/dbagigashare/current/odsx \
/dbagigashare/current/mssql/jars \
/dbagigashare/current/mssql/scripts \
/dbagigashare/current/db2/jars \
/dbagigashare/current/db2/scripts \
/dbagigashare/current/cr8 \
/dbagigashare/current/grafana \
/dbagigashare/current/influx \
/dbagigashare/current/gs \
/dbagigashare/current/jdk \
/dbagigashare/current/kafka \
/dbagigashare/current/nb \
/dbagigashare/current/nb/applicative/ssl \
/dbagigashare/current/nb/management/ssl \
/dbagigashare/current/sqlite /dbagigashare/current/security \
/dbagigashare/current/unzip \
/dbagigashare/current/zk \
/dbagigashare/current/telegraf
} |tee -a ${logfile} > /dev/null

echo "Downloading sources to Pivot [${dest_download}] ..."
{
mkdir -p ${dest_download}
if wget -o --no-check-certificate -P ${dest_download}/unzip                              -N https://tapangigaspaces.s3.us-east-2.amazonaws.com/odsx/install/unzip/unzip-6.0-21.el7.x86_64.rpm; then echo "Downloaded";fi
if wget -o --no-check-certificate -P ${dest_download}/gs                                 -N https://gigaspaces-releases-eu.s3.amazonaws.com/insightedge/16.1.1/gigaspaces-smart-dih-enterprise-16.1.1.zip ; then echo "Downloaded";fi
if wget -o --no-check-certificate -P ${dest_download}/gs/config/                         -N https://tapangigaspaces.s3.us-east-2.amazonaws.com/odsx/install/gs/xap_logging.properties ; then echo "Downloaded";fi
if wget -o --no-check-certificate -P ${dest_download}/gs/config/                         -N https://tapangigaspaces.s3.us-east-2.amazonaws.com/odsx/install/gs/space.properties ; then echo "Downloaded";fi
if wget -o --no-check-certificate -P ${dest_download}/gs/config/                         -N https://tapangigaspaces.s3.us-east-2.amazonaws.com/odsx/install/gs/spaceproperty.properties ; then echo "Downloaded";fi
if wget -o --no-check-certificate -P ${dest_download}/gs/config/                         -N https://tapangigaspaces.s3.us-east-2.amazonaws.com/odsx/install/gs/TieredCriteria.tab ; then echo "Downloaded";fi
if wget -o --no-check-certificate -P ${dest_download}/db2/jars/                          -N https://tapangigaspaces.s3.us-east-2.amazonaws.com/odsx/install/db2/db2jcc_license_cu-4.16.53.jar ; then echo "Downloaded";fi
if wget -o --no-check-certificate -P ${dest_download}/db2/jars/                          -N https://tapangigaspaces.s3.us-east-2.amazonaws.com/odsx/install/db2/db2jcc-4.26.14.jar ; then echo "Downloaded";fi
if wget -o --no-check-certificate -P ${dest_download}/db2/scripts/                       -N https://tapangigaspaces.s3.us-east-2.amazonaws.com/odsx/install/db2/load_JOTBMF11_MATI_ISKY.sh ; then echo "Downloaded";fi
if wget -o --no-check-certificate -P ${dest_download}/mssql/files/                       -N https://tapangigaspaces.s3.us-east-2.amazonaws.com/odsx/install/mssql/SQLJDBCDriver.conf ; then echo "Downloaded";fi
if wget -o --no-check-certificate -P ${dest_download}/mssql/files/                       -N https://tapangigaspaces.s3.us-east-2.amazonaws.com/odsx/install/mssql/udkods2.keytab ; then echo "Downloaded";fi
if wget -o --no-check-certificate -P ${dest_download}/mssql/jars/                        -N https://tapangigaspaces.s3.us-east-2.amazonaws.com/odsx/install/mssql/mssqlFeeder-1.0.0.jar ; then echo "Downloaded";fi
if wget -o --no-check-certificate -P ${dest_download}/mssql/scripts/                     -N https://tapangigaspaces.s3.us-east-2.amazonaws.com/odsx/install/mssql/load_JOTBMF01_TN_MATI.sh ; then echo "Downloaded";fi
if wget -o --no-check-certificate -P ${dest_download}/nb                                 -N https://tapangigaspaces.s3.us-east-2.amazonaws.com/odsx/install/nb/nb-infra-0.0.21.tar.gz ; then echo "Downloaded";fi
if wget -o --no-check-certificate -P ${dest_download}/nb/applicative/                    -N https://tapangigaspaces.s3.us-east-2.amazonaws.com/odsx/install/nb/nb.conf.template ; then echo "Downloaded";fi
if wget -o --no-check-certificate -P ${dest_download}/nb/management/                     -N https://tapangigaspaces.s3.us-east-2.amazonaws.com/odsx/install/nb/nb.conf.template ; then echo "Downloaded";fi
if wget -o --no-check-certificate -P ${dest_download}/grafana/                           -N https://tapangigaspaces.s3.us-east-2.amazonaws.com/odsx/install/grafana/grafana-7.3.7-1.x86_64.rpm ; then echo "Downloaded";fi
if wget -o --no-check-certificate -P ${dest_download}/grafana/                           -N https://tapangigaspaces.s3.us-east-2.amazonaws.com/odsx/install/grafana/gs_config.yaml ; then echo "Downloaded";fi
if wget -o --no-check-certificate -P ${dest_download}/grafana/catalog/jars/              -N https://tapangigaspaces.s3.us-east-2.amazonaws.com/odsx/install/grafana/catalogue-service.jar ; then echo "Downloaded";fi
if wget -o --no-check-certificate -P ${dest_download}/influx                             -N https://tapangigaspaces.s3.us-east-2.amazonaws.com/odsx/install/influxdb/influxdb-1.8.4.x86_64.rpm ; then echo "Downloaded";fi
if wget -o --no-check-certificate -P ${dest_download}/kafka/                             -N https://tapangigaspaces.s3.us-east-2.amazonaws.com/odsx/install/kafka/kafka_2.13-2.8.1.tgz ; then echo "Downloaded";fi
if wget -o --no-check-certificate -P ${dest_download}/zk/                                -N https://dlcdn.apache.org/zookeeper/zookeeper-3.6.3/apache-zookeeper-3.6.3-bin.tar.gz ; then echo "Downloaded";fi
if wget -o --no-check-certificate -P ${dest_download}/kafka/                             -N https://tapangigaspaces.s3.us-east-2.amazonaws.com/odsx/install/jolokia/jolokia-agent.jar ; then echo "Downloaded";fi
if wget -o --no-check-certificate -P ${dest_download}/telegraf/                          -N https://dl.influxdata.com/telegraf/releases/telegraf-1.19.3-1.x86_64.rpm ; then echo "Downloaded";fi
if wget -o --no-check-certificate -P ${dest_download}/kapacitor/                         -N https://repos.influxdata.com/rhel/8/x86_64/stable/kapacitor-1.6.2-1.x86_64.rpm ; then echo "Downloaded";fi
if wget -o --no-check-certificate -P ${dest_download}/kapacitor/                         -N http://www6.atomicorp.com/channels/atomic/centos/7/x86_64/RPMS/jq-1.6-2.el7.x86_64.rpm ; then echo "Downloaded";fi
if wget -o --no-check-certificate -P ${dest_download}/kapacitor/                         -N http://www6.atomicorp.com/channels/atomic/centos/7/x86_64/RPMS/oniguruma-6.8.2-1.el7.x86_64.rpm ; then echo "Downloaded";fi
if wget -o --no-check-certificate -P ${dest_download}/security/config/                   -N https://tapangigaspaces.s3.us-east-2.amazonaws.com/odsx/install/security/ldap-security-config.xml ; then echo "Downloaded";fi
if wget -o --no-check-certificate -P ${dest_download}/security/jars/                     -N https://tapangigaspaces.s3.us-east-2.amazonaws.com/odsx/install/security/spring-ldap-core-2.3.3.RELEASE.jar ; then echo "Downloaded";fi
if wget -o --no-check-certificate -P ${dest_download}/security/jars/                     -N https://tapangigaspaces.s3.us-east-2.amazonaws.com/odsx/install/security/spring-security-ldap-5.1.7.RELEASE.jar ; then echo "Downloaded";fi
if wget -o --no-check-certificate -P ${dest_download}/security/jars/                     -N https://tapangigaspaces.s3.us-east-2.amazonaws.com/odsx/install/security/VaultSupport-1.0-SNAPSHOT.jar ; then echo "Downloaded";fi
if wget -o --no-check-certificate -P ${dest_download}/security/cef/jars/                 -N https://tapangigaspaces.s3.us-east-2.amazonaws.com/odsx/install/security/CEFLogger-1.0-SNAPSHOT.jar ; then echo "Downloaded";fi
if wget -o --no-check-certificate -P ${dest_download}/data-integration/di-manager/       -N https://giga-di.s3.eu-west-1.amazonaws.com/di-packages/manualBuild/di-manager-0.0.12.4.tar.gz ; then echo "Downloaded";fi
if wget -o --no-check-certificate -P ${dest_download}/data-integration/di-mdm/           -N https://giga-di.s3.eu-west-1.amazonaws.com/di-packages/manualBuild/di-mdm-0.0.12.4.tar.gz ; then echo "Downloaded";fi
if wget -o --no-check-certificate -P ${dest_download}/data-integration/di-flink/         -N https://giga-di.s3.eu-west-1.amazonaws.com/Flink/flink-1.15.0-bin-scala_2.12.tgz ; then echo "Downloaded";fi
if wget -o --no-check-certificate -P ${dest_download}/jdk/                               -N https://tapangigaspaces.s3.us-east-2.amazonaws.com/odsx/install/java/jdk-11.0.11_linux-x64_bin.rpm ; then echo "Downloaded";fi
} |tee -a ${logfile}

# Allow to connect to Pivot  via SSH as root
{
sed -e 's/^PermitRootLogin.*/PermitRootLogin without-password/g' -i /etc/ssh/sshd_config 
sed -e 's/^AllowUsers.*/AllowUsers ${aws_user} root/g' -i /etc/ssh/sshd_config 
sed -e 's/no-port/#no-port/g' -i ~/.ssh/authorized_keys 
sed -e 's/ssh-rsa/\nssh-rsa/g' -i ~/.ssh/authorized_keys 
cp /home/${aws_user}/.ssh/authorized_keys ~/.ssh/ 
# set permissions for the pem file.
chmod 400 $ssh_key |tee -a ${logfile}
} |tee -a ${logfile} /dev/null
while read current_host;
do
  echo "Running prerequisits script on remote host[${current_host}] ..." |tee -a ${logfile}
  ssh -tt -q -i ${ssh_key} -o ConnectTimeout=3 -o PasswordAuthentication=no -o StrictHostKeyChecking=no ${aws_user}@${current_host} << EOF |tee -a ${logfile} > /dev/null
#!/usr/bin/bash
# Enable root login
sudo sed -e 's/^PermitRootLogin.*/PermitRootLogin without-password/g' -i /etc/ssh/sshd_config
sudo sed -e 's/^AllowUsers.*/AllowUsers centos root/g' -i /etc/ssh/sshd_config
sudo -s
cd
sudo sed -e 's/no-port/#no-port/g' -i ~/.ssh/authorized_keys
sudo sed -e 's/ssh-rsa/\nssh-rsa/g' -i ~/.ssh/authorized_keys

# Install nfs utils
yum install nfs-utils -y

# Create gsods user (if does not exist)
id -u gsods  &>/dev/null || useradd gsods

# Create a mountpoint to pivot:/dbagigashare
if [ ! -d /dbagigashare ]; then
	mkdir /dbagigashare
	LINE="${pivot_ip}:/dbagigashare"
	FILE=/etc/fstab
	grep -q ${LINE} ${FILE} || echo '${pivot_ip}:/dbagigashare   /dbagigashare   nfs defaults 0 0' >> ${FILE}
	
fi
mount -a
# Create local folders if does not exist:
mkdir -p /dbagigalogs /dbagigadata /dbagiga
exit
exit
EOF

sleep 2
done < $listfile


# Test SSH netween Pivot and other hosts
echo "
Testing SSH connection between Pivot and other hosts...
=======================================================
" |tee -a ${logfile}

for host in $(cat ${listfile});do
	echo "Testing SSH as root from Pivot[${pivot_ip}] to ${host} ..."
	ssh -i ${ssh_key} -o PasswordAuthentication=no -o StrictHostKeyChecking=no root@${host} 'echo SSH[root]: OK && if [[ $(df -h |grep dbagigashare |wc -l) -eq 1 ]];then echo "mount point /dbagigashare OK";else echo "mount point /dbagigashare does NOT exist."; fi && if [[ $(cat /etc/passwd |grep gsods |wc -l) -gt 0 ]];then echo "gsods user: OK";else echo "gsods user does not exist";fi' |tee -a ${logfile}
	echo ---------------------------------------------------------------------------------------------
done


###### Installing ODSX on Pivot
# Extract gs-odsx ip file
{
echo "Extracting odsx archive file ..."
unzip -qq -o $odsx_path -d /dbagiga

# Create a soft link
echo "Creating a symlink for odsx ..."
symlink=`echo $(basename ${odsx_path//.zip/})`
ln -nsf $symlink /dbagiga/gs-odsx

#Create a tryme license
echo "Updating gs-license.txt with 'tryme' license ..."
echo "tryme" > /dbagigashare/current/gs/config/license/gs-license.txt
chmod 400 /dbagiga/gs-odsx/*.pem
} |tee -a ${logfile}

# Run setup.sh to install odsx
echo "Installing odsx ..."
{
cd /dbagiga/gs-odsx/scripts
./setup.sh 

#copy files to /dbagigashare
echo "Copying required files from /dbagiga.gs-odsx to /dbagigashre ..."
cp /dbagiga/gs-odsx/config/metrics.xml.template /dbagigashare/current/gs/config/metrics/
cp /dbagiga/gs-odsx/config/nb.conf.template /dbagigashare/current/nb/management/
cp /dbagiga/gs-odsx/config/nb.conf.template /dbagigashare/current/nb/applicative/
cp /dbagiga/gs-odsx/config/app.yaml /dbagigashare/current/odsx/
cp /dbagiga/gs-odsx/config/host.yaml /dbagigashare/current/odsx/
cp /dbagiga/gs-odsx/config/app.config /dbagigashare/current/odsx/

echo
source ~/.bash_profile
source ~/.bashrc
} |tee -a ${logfile} > /dev/null

{
echo
echo "========================================================"
echo "To use odsx:

        - Please update the file 'hosts.yaml' at /dbagigashare/current/odsx.
        - Run logout & login as root
        - cd /dbagiga/gs-odsx
        - ./odsx.py
"
} |tee -a ${logfile}

echo "Log file: ${logfile}"
