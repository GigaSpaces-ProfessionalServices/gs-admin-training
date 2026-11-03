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

totalPus=$(./gs.sh pu list | grep "Processing Units:" | awk '{ print $3 }')
deployedPus=$(./gs.sh pu list | head -$((2 + $totalPus)) | tail -${totalPus} | awk '{ print $1 }')

if [[ ${totalPus} -gt 0 ]]; then
    echo "Undeploying ${totalPus} processing units..."
    ./gs.sh pu list | head -$((2 + $totalPus)) | tail -${totalPus} | awk '{ print $1 }' | xargs -t -I {} ./gs.sh pu undeploy {}
else
    echo "No processing units are deployed"
    exit 1
fi