/*
 * Copyright (c) 1995, 2010, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */


/*
 * This file contains the main entry point into the launcher code
 * this is the only file which will be repeatedly compiled by other
 * tools. The rest of the files will be linked in.
 */

#include "defines.h"

#ifdef _MSC_VER
#if _MSC_VER > 1400 && _MSC_VER < 1600

/*
 * When building for Microsoft Windows, main has a dependency on msvcr??.dll.
 *
 * When using Visual Studio 2005 or 2008, that must be recorded in
 * the [java,javaw].exe.manifest file.
 *
 * As of VS2010 (ver=1600), the runtimes again no longer need manifests.
 *
 * Reference:
 *     C:/Program Files/Microsoft SDKs/Windows/v6.1/include/crtdefs.h
 */
#include <crtassem.h>
#ifdef _M_IX86

#pragma comment(linker,"/manifestdependency:\"type='win32' "            \
        "name='" __LIBRARIES_ASSEMBLY_NAME_PREFIX ".CRT' "              \
        "version='" _CRT_ASSEMBLY_VERSION "' "                          \
        "processorArchitecture='x86' "                                  \
        "publicKeyToken='" _VC_ASSEMBLY_PUBLICKEYTOKEN "'\"")

#endif /* _M_IX86 */

//This may not be necessary yet for the Windows 64-bit build, but it
//will be when that build environment is updated.  Need to test to see
//if it is harmless:
#ifdef _M_AMD64

#pragma comment(linker,"/manifestdependency:\"type='win32' "            \
        "name='" __LIBRARIES_ASSEMBLY_NAME_PREFIX ".CRT' "              \
        "version='" _CRT_ASSEMBLY_VERSION "' "                          \
        "processorArchitecture='amd64' "                                \
        "publicKeyToken='" _VC_ASSEMBLY_PUBLICKEYTOKEN "'\"")

#endif  /* _M_AMD64 */
#endif  /* _MSC_VER > 1400 && _MSC_VER < 1600 */
#endif  /* _MSC_VER */

/*
 * Entry point.
 */
#ifdef JAVAW

char **__initenv;

int WINAPI
WinMain(HINSTANCE inst, HINSTANCE previnst, LPSTR cmdline, int cmdshow)
{
    int margc;
    char** margv;
    const jboolean const_javaw = JNI_TRUE;

    __initenv = _environ;
    margc = __argc;
    margv = __argv;


#else /* JAVAW */
int
main(int argc, char ** argv)
{
    int margc;
    char** margv;
    const jboolean const_javaw = JNI_FALSE;

    margc = argc;
    margv = argv;
#endif /* JAVAW */

    return JLI_Launch(margc, margv,
                   sizeof(const_jargs) / sizeof(char *), const_jargs,
                   sizeof(const_appclasspath) / sizeof(char *), const_appclasspath,
                   FULL_VERSION,
                   DOT_VERSION,
                   (const_progname != NULL) ? const_progname : *margv,
                   (const_launcher != NULL) ? const_launcher : *margv,
                   (const_jargs != NULL) ? JNI_TRUE : JNI_FALSE,
                   const_cpwildcard, const_javaw, const_ergo_class);
}
