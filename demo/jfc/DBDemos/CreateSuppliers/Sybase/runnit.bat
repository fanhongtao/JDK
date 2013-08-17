@echo off

REM
REM Copyright 2002 Sun Microsystems, Inc. All rights reserved.
REM SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
REM

rem Run the CreateSuppliers demo
rem
rem @(#)runnit.bat	1.3 02/02/06

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
