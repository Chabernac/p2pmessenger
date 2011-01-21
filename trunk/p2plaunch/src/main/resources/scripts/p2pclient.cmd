cd %~dp0
call mvn exec:exec
call run.cmd
del run.cmd
