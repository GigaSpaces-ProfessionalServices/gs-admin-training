#!/usr/bin/env bash


SCRIPTS_DIR="`dirname \"$0\"`"
PROJ_DIR="`( cd \"$SCRIPTS_DIR/..\" && pwd )`"

java -jar $PROJ_DIR/target/benchmark-1.0-SNAPSHOT-jar-with-dependencies.jar BillBuddy-space
