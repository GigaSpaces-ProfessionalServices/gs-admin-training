
DEPLOY_DIR="%~dp0"

CWD=%cd%

call %DEPLOY_DIR%\..\settings.bat

cd %DEPLOY_DIR%\yaml

REM We mimic a heredoc to create a kustomization file
REM we use kubectl kustomize to substitute the image name

echo resources: > kustomization.yaml
echo resources: > kustomization.yaml
echo - job.yaml > kustomization.yaml
echo images: > kustomization.yaml
echo - name: my-app > kustomization.yaml
echo newName: %FEEDER_IMAGE_NAME% > kustomization.yaml

REM to debug use
REM kubectl kustomize .

kubectl apply -k .

cd %CWD%
