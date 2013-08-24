@echo off
REM 
REM @(#)memory.bat	1.1 06/08/07
REM
REM Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
REM
REM Redistribution and use in source and binary forms, with or without
REM modification, are permitted provided that the following conditions are met:
REM
REM -Redistribution of source code must retain the above copyright notice, this
REM  list of conditions and the following disclaimer.
REM
REM -Redistribution in binary form must reproduce the above copyright notice,
REM  this list of conditions and the following disclaimer in the documentation
REM  and/or other materials provided with the distribution.
REM
REM Neither the name of Sun Microsystems, Inc. or the names of contributors may
REM be used to endorse or promote products derived from this software without
REM specific prior written permission.
REM
REM This software is provided "AS IS," without a warranty of any kind. ALL
REM EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
REM ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
REM OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN")
REM AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
REM AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
REM DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
REM REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
REM INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
REM OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE,
REM EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
REM
REM You acknowledge that this software is not designed, licensed or intended
REM for use in the design, construction, operation or maintenance of any
REM nuclear facility.


jrunscript -J-Dcom.sun.management.jmxremote.port=1090 -J-Dcom.sun.management.jmxremote.ssl=false -J-Dcom.sun.management.jmxremote.authenticate=false memory.js

