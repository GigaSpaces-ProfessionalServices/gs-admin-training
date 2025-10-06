#!/usr/bin/env bash

DEPLOY_DIR="`dirname \"$0\"`"
DEPLOY_DIR="`( cd \"$DEPLOY_DIR\" && pwd )`"

cd $DEPLOY_DIR/../../feeder

mvn clean spring-boot:run -Dspring-boot.run.profiles=localhost