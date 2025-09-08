
set DEPLOY_DIR="%~dp0"


call %DEPLOY_DIR%\..\settings.bat

set CWD=%cd%

cd %DEPLOY_DIR%\..\..

%GS_HOME%\bin\gs.bat service deploy processor-pu processor/target/*.jar

cd %CWD%
