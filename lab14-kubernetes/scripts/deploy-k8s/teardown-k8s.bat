
set DEPLOY_DIR="%~dp0"

set CWD=%cd%

cd %DEPLOY_DIR%\yaml

kubectl delete -f job.yaml
kubectl delete -f manager-np.yaml

helm delete processor
helm delete operator
helm delete manager


cd %CWD%
