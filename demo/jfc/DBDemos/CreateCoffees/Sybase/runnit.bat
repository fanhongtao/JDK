REM
REM @(#)runnit.bat	1.2 01/11/29
REM
REM Copyright 2002 Sun Microsystems, Inc. All rights reserved.
REM SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
REM

@echo off
rem Run the CreateCoffees demo
rem
rem @(#)runnit.bat      1.1 97/09/22

if "%JDBCHOME%" == "" goto nohome

set _BUILDHOME=%JDBCHOME%\build
set _CLASSPATH=classes;%JDBCHOME%\CreateJv;%CLASSPATH%
set CMD=java -classpath %_CLASSPATH% CreateCoffees
echo %CMD%
%CMD%
set _BUILDHOME=
set _CLASSPATH=
goto done

:nohome
echo No JDBCHOME environment variable set.

:done
