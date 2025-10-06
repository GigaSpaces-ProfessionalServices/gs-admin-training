#!/usr/bin/env bash

SCRIPTS_DIR="`dirname \"$0\"`"
SCRIPTS_DIR="`( cd \"$SCRIPTS_DIR\" && pwd )`"

CWD=$(pwd)

if [ ! -f "$SCRIPTS_DIR/settings.sh" ]; then
  echo "Unable to source settings file."
  exit 1
fi
source $SCRIPTS_DIR/settings.sh

docker login -u $DOCKER_USERNAME

# build the processor
echo "########################################"
echo "# Building the processor for Docker..."
echo "########################################"
cd $SCRIPTS_DIR/../processor && \
docker buildx build --platform linux/amd64 --push --no-cache -t $DOCKER_USERNAME/processor:1.0-SNAPSHOT .

#docker build --no-cache -t {{docker.username}}/processor:{{app.docker.version}} .
#docker push {{docker.username}}/processor:{{app.docker.version}}

# build the feeder
echo "########################################"
echo "# Building the feeder for Docker..."
echo "########################################"
cd $SCRIPTS_DIR/../feeder && \
docker buildx build --platform linux/amd64 --push --no-cache -t $DOCKER_USERNAME/feeder:1.0-SNAPSHOT .

cd $CWD