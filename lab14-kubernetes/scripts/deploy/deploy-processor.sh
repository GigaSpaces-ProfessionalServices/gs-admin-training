#!/usr/bin/env bash

DEPLOY_DIR="`dirname \"$0\"`"
DEPLOY_DIR="`( cd \"$DEPLOY_DIR\" && pwd )`"

if [ ! -f "$DEPLOY_DIR/../settings.sh" ]; then
  echo "Unable to source settings file."
  exit 1
fi
source $DEPLOY_DIR/../settings.sh

CWD=$(pwd)

cd $DEPLOY_DIR/../..

$GS_HOME/bin/gs.sh service deploy processor-pu processor/target/*.jar

cd $CWD
