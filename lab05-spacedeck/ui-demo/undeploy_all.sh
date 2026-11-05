#!/usr/bin/env bash
DIRNAME=$(dirname ${BASH_SOURCE[0]})



if [[ -z "${GS_DIR}" ]]; then
    echo "GS_DIR environment variable is not set"
    exit 1
fi


#SERVER=""

if [[ -n "${SERVER}" ]]; then
   SERVER_PARAM="--server=${SERVER}"
fi


cd ${GS_DIR}/bin

# PU names are the first column of the rows between the "NAME ..." header and the next empty line
deployedPus=$(./gs.sh pu list 2>/dev/null | awk '/^NAME /{found=1; next} found && NF==0 {exit} found {print $1}')
totalPus=$(echo "${deployedPus}" | grep -c .)

if [[ ${totalPus} -gt 0 ]]; then
    echo "Undeploying ${totalPus} processing units..."
    echo "${deployedPus}" | xargs -t -I {} ./gs.sh pu undeploy {}
else
    echo "No processing units are deployed"
    exit 1
fi