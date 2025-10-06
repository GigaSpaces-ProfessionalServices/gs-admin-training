#!/usr/bin/env bash

DEPLOY_DIR="`dirname \"$0\"`"
DEPLOY_DIR="`( cd \"$DEPLOY_DIR\" && pwd )`"

CWD=$(pwd)

if [ ! -f "$DEPLOY_DIR/../settings.sh" ]; then
  echo "Unable to source settings file."
  exit 1
fi
source $DEPLOY_DIR/../settings.sh

cd $DEPLOY_DIR/yaml

# use a heredoc to create a kustomization file
# we use kubectl kustomize to substitute the image name
cat << EOF > kustomization.yaml
resources:
  - job.yaml
images:
- name: my-app
  newName: $FEEDER_IMAGE_NAME
EOF

# to debug use
#kubectl kustomize .

kubectl apply -k .

cd $CWD
