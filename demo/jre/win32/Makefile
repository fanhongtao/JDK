# @(#)Makefile	1.9 97/06/09 David Connelly
#
# Standalone NMAKE makefile to build the programs 'jre.exe' and 'jrew.exe'
# for launching Java runtime applications. The program 'jre.exe' can be
# used to run console-based Java applications, and 'jrew.exe' will run
# applications detached from the console. The sources to these programs
# are also provided to demonstrate how JNI can be used to invoke Java
# applications.
#

#
# Default version of Java runtime to use at startup.
#
!ifndef VERSION
VERSION = 1.1.3
!endif

#
# Default locations of shared and win32-specific JRE sources.
#
!ifndef SHARE_JRE
SHARE_JRE = ..
!endif
!ifndef WIN32_JRE
WIN32_JRE = .
!endif

#
# It's unfortunate that the Windows 95 and NT command shells have to
# be so much different...
#
!if "$(OS)" == "Windows_NT"
DEL   = del/f/q
RMDIR = rmdir/s/q
!else
DEL   = deltree/y
RMDIR = deltree/y
!endif

#
# Use nmake -DDEBUG to build executables with debug symbols. These will
# be named 'jre_g.exe' and 'jrew_g.exe'.
#
!ifdef DEBUG
G = _g
!else
G =
!endif
OBJ = obj$G

#
# Standard includes and defines
#
INCLUDES = -I$(SHARE_JRE) -I$(WIN32_JRE)
DEFINES  = -DWIN32_LEAN_AND_MEAN -DVERSION=\"$(VERSION)\"

#
# Microsoft Visual C++ 4.x compiler and linker flags
#
BASECFLAGS = -nologo -MD $(DEFINES) $(INCLUDES) $(EXTRA_CFLAGS)
BASELFLAGS = -nologo $(EXTRA_LFLAGS)
!ifdef DEBUG
CFLAGS = -Zi -Od -DDEBUG $(BASECFLAGS)
LFLAGS = -debug
!else
CFLAGS = -Ox -G5 $(BASECFLAGS)
!endif
LIBS = setargv.obj advapi32.lib user32.lib

#
# These are the objects to build both jre.exe and jrew.exe
#
JRE_obj =   $(OBJ)\jre_main.obj	\
	    $(OBJ)\jre.obj	\
	    $(OBJ)\jre_md.obj

#
# Default target to build both jre.exe and jrew.exe. The only difference
# is the use of the -subsystem flag to specify whether or not to run
# detached from a console.
#

jre : $(OBJ) jre$G.exe jrew$G.exe

jre$G.exe : $(JRE_obj)
	link -subsystem:console -out:jre$G.exe $(LFLAGS) $** $(LIBS)

jrew$G.exe : $(JRE_obj)
	link -subsystem:windows -out:jrew$G.exe $(LFLAGS) $** $(LIBS)

$(JRE_obj) : $(SHARE_JRE)\jre.h $(WIN32_JRE)\jre_md.h

#
# Default rules
#

{$(WIN32_JRE)}.c{$(OBJ)}.obj :
        $(CC) $(CFLAGS) -c -Fo$@ $<

{$(SHARE_JRE)}.c{$(OBJ)}.obj :
        $(CC) $(CFLAGS) -c -Fo$@ $<

#
# Directory setup and cleanup
#

$(OBJ) :
	mkdir $(OBJ)

clean :
	-$(RMDIR) $(OBJ)
	-$(RMDIR) $(OBJ)_g
	-$(DEL) *.exe
	-$(DEL) *.ilk
	-$(DEL) *.pdb
