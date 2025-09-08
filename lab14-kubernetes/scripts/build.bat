
set SCRIPTS_DIR="%~dp0"

set CWD=%cd%

cd %SCRIPTS_DIR%\..

mvn clean install

cd %SCRIPTS_DIR%

call docker-build.bat

cd %CWD%