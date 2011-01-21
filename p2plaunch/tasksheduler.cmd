@echo off
cd %~dp0
call mvn exec:exec
if %errorlevel%==0 goto ok
if errorlevel 1 goto nok

:ok
if exist run.cmd goto run

:run
call run.cmd
del run.cmd
goto end

:nok
echo The p2p client could not be started
goto end

:end

