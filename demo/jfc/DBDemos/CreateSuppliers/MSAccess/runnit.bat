@echo off
rem Run the CreateSuppliers demo
rem
rem @(#)runnit.bat	1.2 99/07/12

if "%JDBCHOME%" == "" goto nohome

set _BUILDHOME=%JDBCHOME%\build
set _CLASSPATH=classes;%JDBCHOME%\CreateJv;%CLASSPATH%
set CMD=java -classpath %_CLASSPATH% CreateSuppliers
echo %CMD%
%CMD%
set _BUILDHOME=
set _CLASSPATH=
goto done

:nohome
echo No JDBCHOME environment variable set.

:done
