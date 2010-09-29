@echo on
call mvn clean install -Dtarget=local -DskipTests=true -Dmaven.test.failure.ignore=true
@echo off

if 0==%ERRORLEVEL% goto :copy_ear
goto :eof


:COPY_EAR
if "" == "%JBOSS_HOME%" goto :no_jboss_home
@echo on
copy target\p2pwebnode.ear "%JBOSS_HOME%\server\default\deploy"
@echo off
goto :eof


:NO_JBOSS_HOME
echo.
echo.ERROR: The environment variable JBOSS_HOME is not defined.
echo.
goto :eof
