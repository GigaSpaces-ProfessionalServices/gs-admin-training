#!/usr/bin/env bash

function product_types() {
  echo "List of available product types:"
  echo "[1] xap"

  while true; do
    read -p 'Select product type by name or number:[1] ' -e gsType
      case $gsType in
        ""|"1"|"xap")
        # user pressed enter |user typed 1 enter|user typed xap enter
          gsType=xap;
          break;;
        * ) echo 'Please enter product name or menu item number.';;
      esac
    done
}

function product_versions() {
  echo "List of available product versions:"
  echo "[1] 17.3.0 - Compatible with Java 17,21"
  echo "[2] 17.1.2 - Compatible with Java 17"
  echo "[3] 17.0.0 - Compatible with Java 17"
  echo "[4] 16.4.3 - Compatible with Java 8,11"

  while true; do
    read -p 'Select product version by name or number:[1] ' -e gsVersion
      case $gsVersion in
        ""|"1"|"17.3.0")
          # user pressed enter |user typed 1 enter|user typed 17.3.0 enter
          gsVersion=17.3.0;
          break;;
        "2"|"17.1.2")
          gsVersion=17.1.2;
          break;;
        "3"|"17.0.0")
          gsVersion=17.0.0;
          break;;
        "4"|"16.4.3")
          gsVersion=16.4.3;
          break;;
        * ) echo 'Please enter product version or menu item number.';;
      esac
  done
}

function get_installation_type() {
  echo "List of available installation types:"
  echo "[1] local"
  echo "[2] cluster"
  # only configures multiple manager servers, doesn't actually run a cluster

  while true; do
    read -p 'Select installation type:[1] ' -e gsManagerServers
    case $gsManagerServers in
      ""|"1"|"local") read -p 'To override default localhost enter hostname or ip address:[localhost] ' -e host1;
        if [ -z "$host1" ]; then
          gsManagerServers=localhost
          host1="localhost"
        else
          gsManagerServers=$host1
        fi;
        break;;
      "2"|"cluster")
        echo "Please enter 3 GS manager hosts:"
        read -p 'Enter Host #1: ' -e host1;
        gsManagerServers=$host1
        read -p 'Enter Host #2: ' -e host2;
        gsManagerServers=$gsManagerServers,$host2
        read -p 'Enter Host #3: ' -e host3;
        gsManagerServers=$gsManagerServers,$host3
        break;;
      * ) echo 'Please enter installation type by name or menu item number.';;
    esac
  done
}

function get_gsc_options() {
  read -p 'To override default number of containers to raise [Default is none]: ' -e containerCnt;

  read -p 'To override default containers heap [Default is 512m] please specify units (m/g): ' -e containerMem;
}

function get_os_type() {
  echo "List of available Linux OS types:"
  echo "[1] rhel"
  echo "[2] ubuntu"

  while true; do
    read -p 'Select OS type by name or number:[1] ' -e osType
    case $osType in
      ""|"1"|"rhel")
        osType=rhel;
        break;;
      "2"|"ubuntu")
        osType=ubuntu;
        break;;
      * ) echo 'Please enter machine os type by name or number.';;
    esac
  done
}

function installJdk() {
  if [ "rhel" == "$osType" ]; then
    if [ "16.4.3" == "$gsVersion" ]; then
      #sudo yum -y install java-1.8.0-openjdk
      #sudo yum -y install java-1.8.0-openjdk-devel
      sudo yum install -y java-11-openjdk-devel
    else
      sudo yum -y install java-17-openjdk-devel
    fi
  elif [ "ubuntu" == "$osType" ]; then
    if [ "16.4.3" == "$gsVersion" ]; then
      sudo apt install -y openjdk-11-jdk
    else
      sudo apt install -y openjdk-17-jdk
    fi
  fi
  echo "Install JDK - Done!"
}

function installZip() {
  if [ "rhel" == "$osType" ]; then
    sudo yum install -y unzip
  elif [ "ubuntu" == "$osType" ]; then
    sudo apt install -y unzip
  fi
  echo "Install zip - Done!"
}

function installWget() {
  if [ "rhel" == "$osType" ]; then
    sudo yum install -y wget
  elif [ "ubuntu" == "$osType" ]; then
    sudo apt install -y wget
  fi
  echo "Install wget - Done!"
}

function downloadGS() {
  if [ "17.3.0" == "$gsVersion" ]; then
    # 17.3.0 is published under a build-number sub-path
    URL="https://gs-releases-us-east-1.s3.amazonaws.com/smart-cache/17.3.0/17.3.0/119/gigaspaces-smart-cache-enterprise-17.3.0.zip"
  else
    URL="https://gs-releases-us-east-1.s3.amazonaws.com/smart-cache/${gsVersion}/gigaspaces-smart-cache-enterprise-${gsVersion}.zip"
  fi
  wget ${URL}
  echo "Download GS - Done!"
}

function unzipGS() {
  unzip gigaspaces-smart-cache-enterprise-${gsVersion}.zip
  echo "Unzipping GS - Done!"
}

function activateGS() {
  echo "tryme">gigaspaces-smart-cache-enterprise-${gsVersion}/gs-license.txt
  echo "Activating GS - Done!"
}

function settingGsManagers() {
  echo 'Setting GS Manager(s)'
  echo -e "\nexport GS_MANAGER_SERVERS=$gsManagerServers">>gigaspaces-smart-cache-enterprise-${gsVersion}/bin/setenv-overrides.sh
  echo 'Setting GS Manager(s) - Done!'
}

function startGS() {
  if [ ! -z "$containerMem" ]; then
    echo -e "\nexport GS_GSC_OPTIONS=-Xms$containerMem -Xmx$containerMem">>gigaspaces-smart-cache-enterprise-${gsVersion}/bin/setenv-overrides.sh
  fi

  if [ -z "$containerCnt" ]; then
    nohup gigaspaces-smart-cache-enterprise-${gsVersion}/bin/gs.sh host run-agent --auto &
  else 
    nohup gigaspaces-smart-cache-enterprise-${gsVersion}/bin/gs.sh host run-agent --auto --gsc=$containerCnt &
  fi;
  echo "Starting GS - Done!"
}

#function settingNicAddr() {
#  echo "settingNicAddr - Done!"
#  echo "setting nic address GS"
#  echo -e "export GS_NIC_ADDRESS=$nicAddr">>gigaspaces-${gsType}-enterprise-${gsVersion}/bin/setenv-overrides.sh
#  echo "setting nic address GS - Done!"
#}

function endAnnouncement() {
  echo "#######################################################"
  echo "SUMMARY :  SYSTEM INSTALLED SUCCESSFULLY"
  echo "DATE `date +"%D"` / TIME `date +"%T"`"
  echo "VERSION ${gsType}"
  echo "GS_MANAGER_SERVERS : $gsManagerServers"
  echo "URL for OpsManager :  http://$host1:8090"
  echo "URL for GS Web-ui  :  http://$host1:8099"
  echo "REST               :  http://$host1:8090/v2"
  echo "#######################################################"
}


function main() {
  if [ "$1" == "-h" ]; then
    echo "Usage: `basename $0`"
    echo
    echo "Options:"
    echo "For interactive installation do not use any parameters."
    echo "  --silent  For silent installation with key value properties file."
    echo "  -h        This help message."
    exit 0
  elif [ "$1" == "--silent" ]; then
    if [ -f gs_installation.properties ]; then
      source gs_installation.properties
    fi
    if [ -z "$host1" ]; then
      gsManagerServers=localhost
      host1=localhost
    else
      gsManagerServers=$host1
    fi
    if [ ! -z "$host2" ]; then
      gsManagerServers=$host1,$host2
    fi
    if [ ! -z "$host3" ]; then
      gsManagerServers=$host1,$host2,$host3
    fi
  else

    product_types

    #echo "debug: $gsType"

    product_versions

    #echo "debug: $gsVersion"

    get_installation_type

    #echo "debug: $gsManagerServers"
    #echo "debug: $host1"

    #read -p "To override default GS_NIC_ADDRESS [Default is the machine hostname]: " -e nicAddr;

    get_gsc_options

    #echo "debug: $containerCnt"
    #echo "debug: $containerMem"

    get_os_type

    #echo "debug: $osType"
  fi

  echo "Setting up Java..."
  installJdk

  echo "Installing zip..."
  installZip

  echo "Installng wget..."
  installWget

  echo "Downloading GigaSpaces..."
  downloadGS

  echo "Unzipping GigaSpaces..."
  unzipGS

  echo "Activating GigaSpaces..."
  activateGS

  echo 'Configuring GS Manager(s)...'
  settingGsManagers

#if [ ! -z "$nicAddr" ]; then
#  echo "starting settingNicAddr"
#  settingNicAddr
#fi

  echo "Starting GigaSpaces..."
  startGS

  echo "Ending the installation."
  endAnnouncement
}


main $1

