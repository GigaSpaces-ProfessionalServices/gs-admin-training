#!/usr/bin/env bash

DEPLOY_DIR="`dirname \"$0\"`"
DEPLOY_DIR="`( cd \"$DEPLOY_DIR\" && pwd )`"

if [ ! -f "$DEPLOY_DIR/../settings.sh" ]; then
  echo "Unable to source settings file."
  exit 1
fi
source $DEPLOY_DIR/../settings.sh

$GS_HOME/bin/gs.sh host run-agent --auto --gsc=4
