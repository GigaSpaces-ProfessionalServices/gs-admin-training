
set SCRIPTS_DIR="%~dp0"

set CWD=%cd%

call %SCRIPTS_DIR%\settings.bat

docker login -u %DOCKER_USERNAME%

rem build the processor
echo ########################################
echo # Building the processor for Docker...
echo ########################################
cd %SCRIPTS_DIR%\..\processor

docker buildx build --platform linux/amd64 --push --no-cache -t %DOCKER_USERNAME%/processor:1.0-SNAPSHOT .

rem  build the feeder
echo ########################################
echo # Building the feeder for Docker...
echo ########################################
cd %SCRIPTS_DIR%\..\feeder
docker buildx build --platform linux/amd64 --push --no-cache -t %DOCKER_USERNAME%/feeder:1.0-SNAPSHOT .

cd %CWD%
