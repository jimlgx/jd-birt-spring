@echo off
echo [INFO] Use maven-install-plugin run the project.

cd %~dp0
cd ..

set MAVEN_OPTS=%MAVEN_OPTS% -XX:MaxPermSize=128m
call mvn clean install

cd bin
pause 