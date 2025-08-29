#!/usr/bin/env bash

# this script should receive the docker username as the first parameter
# it will pass the parameters to docker-build

SCRIPTS_DIR="`dirname \"$0\"`"
SCRIPTS_DIR="`( cd \"$SCRIPTS_DIR\" && pwd )`"

CWD=$(pwd)

cd $SCRIPTS_DIR/..

mvn clean install

cd $SCRIPTS_DIR

./docker-build.sh $@

cd $CWD