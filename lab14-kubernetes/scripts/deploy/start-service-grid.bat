
DEPLOY_DIR="%~dp0"

call %DEPLOY_DIR%\..\settings.bat

%GS_HOME%\bin\gs.bat host run-agent --auto --gsc=4
