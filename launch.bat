@echo off
@title MapleSolaxia
set CLASSPATH=.;dist\*;cores\*;lib\*
java -Xmx2048m -Dwzpath=wz\ net.server.Server
pause
