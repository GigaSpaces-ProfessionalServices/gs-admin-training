#!/usr/bin/env bash

DEPLOY_DIR="`dirname \"$0\"`"
DEPLOY_DIR="`( cd \"$DEPLOY_DIR\" && pwd )`"

CWD=$(pwd)

cd $DEPLOY_DIR/yaml

kubectl delete -f job.yaml
kubectl delete -f manager-np.yaml

helm delete processor
helm delete operator
helm delete manager


cd $CWD
