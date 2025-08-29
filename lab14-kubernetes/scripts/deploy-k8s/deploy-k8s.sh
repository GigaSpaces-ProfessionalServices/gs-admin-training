#!/usr/bin/env bash

DEPLOY_DIR="`dirname \"$0\"`"
DEPLOY_DIR="`( cd \"$DEPLOY_DIR\" && pwd )`"

CWD=$(pwd)

if [ ! -f "$DEPLOY_DIR/../settings.sh" ]; then
  echo "Unable to source settings file."
  exit 1
fi
source "$DEPLOY_DIR/../settings.sh"

cd $DEPLOY_DIR/yaml

echo "########################################"
echo "This script will deploy GigaSpaces on a Kubernetes environment."
read -p "Press enter to continue"


echo "########################################"
echo "# Creating 'gigaspaces' helm repo..."
helm repo add gigaspaces https://resources.gigaspaces.com/helm-charts


helm repo update gigaspaces
echo "# helm repo setup done."
echo "########################################"
read -p "Press enter to continue"


echo "########################################"
echo "# Installing GigaSpaces Manager..."
helm install manager gigaspaces/xap-manager --version 17.1.2 --set global.security.enabled=false,java.options="-Dcom.gs.hsqldb.all-metrics-recording.enabled=false"

echo "# GigaSpaces Manager helm install done. Some resources such as pods may take some minutes to complete."
echo "########################################"
read -p "Press enter to continue"


echo "########################################"
echo "# For demonstration purposes, we will expose the UI via NodePort."
kubectl apply -f manager-np.yaml
echo "# NodePort configuration done."
echo "# If using minikube, you can have it automatically open a browser to the nodeport service via: minikube service manager-np"
echo "########################################"
read -p "Press enter to continue"


echo "########################################"
echo "# Installing operator..."
helm install operator gigaspaces/xap-operator --version 17.1.2 --set  global.security.enabled=false
echo "# Operator helm install done."
echo "########################################"
read -p "Press enter to continue"


echo "########################################"
echo "# Installing space..."
helm install processor gigaspaces/xap-pu --version 17.1.2 --set schema=partitioned,partitions=1,ha=false,resourceUrl=pu.jar,image.repository=$DOCKER_USERNAME/processor,image.tag=1.0-SNAPSHOT,java.options="-Dcom.gs.hsqldb.all-metrics-recording.enabled=false"
echo "# Space helm install done."
echo "########################################"
read -p "Press enter to exit"

cd $CWD
