
DEPLOY_DIR="%~dp0"

cd %DEPLOY_DIR%\..\..\feeder

mvn clean spring-boot:run -Dspring-boot.run.profiles=localhost
