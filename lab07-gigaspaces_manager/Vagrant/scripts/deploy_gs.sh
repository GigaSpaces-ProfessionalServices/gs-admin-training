#!/bin/bash

function installRemoteJava {
  #sudo yum -y install java-1.8.0-openjdk
  #yum -y install java-17-openjdk
  #apt install -y openjdk-17-jdk
  apt update -y && apt install -y openjdk-17-jre-headless
  echo "install Remote JDK - Done!"
}

function installZip {
  #sudo yum -y install unzip
  #yum -y install unzip
  apt install -y unzip
  echo "install ZIP - Done!"
}

function installWget {
  #sudo yum -y install wget
  #yum -y install wget
  apt install -y wget
  echo "install wget - Done!"
}

function downloadGS {
  #wget https://gigaspaces-releases-eu.s3.amazonaws.com/xap/16.1.1/gigaspaces-xap-enterprise-16.1.1.zip
  wget https://gs-releases-us-east-1.s3.amazonaws.com/smart-cache/17.1.2/gigaspaces-smart-cache-enterprise-17.1.2.zip
  echo "download GS - Done!"
}

function unzipGS {
  unzip gigaspaces-smart-cache-enterprise-17.1.2.zip
  chown -R vagrant:vagrant gigaspaces-smart-cache-enterprise-17.1.2
  echo "unzipping GS - Done!"
}

function activateGS {
  echo "tryme">gigaspaces-smart-cache-enterprise-17.1.2/gs-license.txt
  echo "activating GS - Done!"
}

function startGS {
  echo "#!/usr/bin/env bash" > /home/vagrant/start_GS.sh
  echo "su - vagrant -c \"/usr/bin/nohup /home/vagrant/gigaspaces-smart-cache-enterprise-17.1.2/bin/gs.sh host run-agent --auto > /home/vagrant/start_GS.out 2>&1 &\"" >> /home/vagrant/start_GS.sh
  chown vagrant:vagrant /home/vagrant/start_GS.sh
  chmod +x /home/vagrant/start_GS.sh
  /home/vagrant/start_GS.sh
  echo "starting GS - Done!"
  echo "GS Web-UI http://localhost:8099"
  echo "GS Ops Manager http://localhost:8090"
}

if [ "$1" == "-h" ]; then
  echo "For cluster setup:"
  echo "Usage: `basename $0` [GS_Manager_Host1,GS_Manager_Host2,GS_Manager_Host3]"
  echo "For a non-cluster installation (one host) no parameter is required, the installation will be based on the machine's localhost."
  exit 0
fi
echo "setup java"
installRemoteJava
echo "setup zip"
installZip
echo "install wget"
installWget
echo "Download GS"
downloadGS
echo "unzipping GS"
unzipGS
echo "activating GS"
activateGS
echo "setting manager GS"
if [ -z "$1" ]; then
  echo -e "\nexport GS_MANAGER_SERVERS=\"localhost\"">>gigaspaces-smart-cache-enterprise-17.1.2/bin/setenv-overrides.sh
else
  echo -e "\nexport GS_MANAGER_SERVERS=\"$1\"">>gigaspaces-smart-cache-enterprise-17.1.2/bin/setenv-overrides.sh
fi
echo "setting manager GS - Done!"
echo "starting GS"
startGS
