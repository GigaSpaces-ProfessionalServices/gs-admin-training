
set DEPLOY_DIR="%~dp0"

set CWD=%cd%

call %DEPLOY_DIR%\..\settings.bat

cd %DEPLOY_DIR%\yaml

echo ########################################
echo This script will deploy GigaSpaces on a Kubernetes environment.
set /p=Press enter to continue...

echo ########################################
echo # Creating 'gigaspaces' helm repo...
helm repo add gigaspaces https://resources.gigaspaces.com/helm-charts


helm repo update gigaspaces
echo # helm repo setup done.
echo ########################################
set /p=Press enter to continue...


echo ########################################
echo # Installing GigaSpaces Manager...
helm install manager gigaspaces/xap-manager --version 17.1.2 --set global.security.enabled=false,java.options="-Dcom.gs.hsqldb.all-metrics-recording.enabled=false"
echo # GigaSpaces Manager helm install done. Some resources such as pods may take some minutes to complete.
echo ########################################
set /p=Press enter to continue...

echo ########################################
echo # For demonstration purposes, we will expose the UI via NodePort.
kubectl apply -f manager-np.yaml
echo # NodePort configuration done.
echo # If using minikube, you can have it automatically open a browser to the nodeport service via: minikube service manager-np
echo ########################################
set /p=Press enter to continue...

echo ########################################
echo # Installing operator...
helm install operator gigaspaces/xap-operator --version 17.1.2 --set  global.security.enabled=false
echo # Operator helm install done.
echo ########################################
set /p=Press enter to continue...

echo ########################################
echo # Installing space...
helm install processor gigaspaces/xap-pu --version 17.1.2 --set schema=partitioned,partitions=1,ha=false,resourceUrl=pu.jar,image.repository=%DOCKER_USERNAME%/processor,image.tag=1.0-SNAPSHOT,java.options="-Dcom.gs.hsqldb.all-metrics-recording.enabled=false"
echo # Space helm install done.
echo ########################################
set /p=Press enter to continue...

cd %CWD%
