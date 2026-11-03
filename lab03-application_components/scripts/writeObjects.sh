#!/usr/bin/env bash


SCRIPTS_DIR="`dirname \"$0\"`"
PROJ_DIR="`( cd \"$SCRIPTS_DIR/..\" && pwd )`"

# GigaSpaces 18.0 requires Java 25: use JAVA_HOME if set, otherwise the java on the PATH
if [ -n "$JAVA_HOME" ]; then
  JAVACMD="$JAVA_HOME/bin/java"
else
  JAVACMD=java
fi

"$JAVACMD" -Dcom.gs.jini_lus.locators=localhost -jar $PROJ_DIR/target/benchmark-1.0-SNAPSHOT-jar-with-dependencies.jar demo
