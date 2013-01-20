@echo off
setLocal EnableDelayedExpansion
cd "%~dp0/.."
set CLASSPATH=
FOR %%F IN (3rd_party/*.jar) DO (
  set CLASSPATH=!CLASSPATH!;3rd_party/%%F
)
java -cp "%CLASSPATH%;projects/server/bin/" cryptocast.server.programs.Benchmarks