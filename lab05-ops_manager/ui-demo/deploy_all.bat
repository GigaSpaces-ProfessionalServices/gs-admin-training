@echo off

if not defined GS_DIR (
	echo GS_DIR environment variable is not set
    goto :eof
)


call %GS_DIR%\bin\gs.bat pu deploy Mirror %~dp0\mirror\target\mirror.jar

call %GS_DIR%\bin\gs.bat pu deploy --partitions=2 --backups=1 ProductsCatalog %~dp0\products-catalog\target\products-catalog.jar

call %GS_DIR%\bin\gs.bat pu deploy ProductsLoader %~dp0\products-loader\target\products-loader.jar

call %GS_DIR%\bin\gs.bat pu deploy -p=maxEntriesPerSecond=10000 --instances=2 WebApplication %~dp0\web-application\target\web-application.war

call %GS_DIR%\bin\gs.bat pu deploy --instances=1 DemoApp %~dp0\demo-app\target\demo-app.war
