/*
 * @(#)jre_md.c	1.10 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * Win32 specific JRE support functions
 */

#include <windows.h>
#include <stdlib.h>
#include <jni.h>
#include "jre.h"

#define JRE_KEY	    "Software\\JavaSoft\\Java Runtime Environment"
#define JDK_KEY	    "Software\\JavaSoft\\Java Development Kit"

#define RUNTIME_LIB "javai.dll"

/* From jre_main.c */
extern jboolean debug;

/* Forward Declarations */
jint LoadSettings(JRESettings *set, HKEY key);
jint GetSettings(JRESettings *set, const char *version, const char *keyname);
char *GetStringValue(HKEY key, const char *name);

/*
 * Retrieve settings from registry for current runtime version. Returns
 * 0 if successful otherwise returns -1 if no installed runtime was found
 * or the registry data was invalid.
 */
jint
JRE_GetCurrentSettings(JRESettings *set)
{
    jint r = -1;
    HKEY key;

    if (RegOpenKeyEx(HKEY_LOCAL_MACHINE, JRE_KEY, 0, KEY_READ, &key) == 0) {
	char *ver = GetStringValue(key, "CurrentVersion");
	if (ver != 0) {
	    r = JRE_GetSettings(set, ver);
	}
	free(ver);
	RegCloseKey(key);
    }
    return r;
}

/*
 * Retrieves settings from registry for specified runtime version.
 * Searches for either installed JRE and JDK runtimes. Returns 0 if
 * successful otherwise returns -1 if requested version of runtime
 * could not be found.
 */
jint
JRE_GetSettings(JRESettings *set, const char *version)
{
    if (GetSettings(set, version, JRE_KEY) != 0) {
	return GetSettings(set, version, JDK_KEY);
    }
    return 0;
}

jint
GetSettings(JRESettings *set, const char *version, const char *keyname)
{
    HKEY key;
    int r = -1;

    if (RegOpenKeyEx(HKEY_LOCAL_MACHINE, keyname, 0, KEY_READ, &key) == 0) {
	char *major, *minor, *micro = 0;
	if (JRE_ParseVersion(version, &major, &minor, &micro) == 0) {
	    HKEY subkey;
	    char *ver = JRE_MakeVersion(major, minor, 0);
	    set->majorVersion = major;
	    set->minorVersion = minor;
	    if (RegOpenKeyEx(key, ver, 0, KEY_READ, &subkey) == 0) {
		if ((r = LoadSettings(set, subkey)) == 0) {
		    if (micro != 0) {
			if (set->microVersion == 0 ||
			    strcmp(micro, set->microVersion) != 0) {
			    r = -1;
			}
		    }
		}
		RegCloseKey(subkey);
	    }
	    free(ver);
	}
	RegCloseKey(key);
    }
    return r;
}

/*
 * Load runtime settings from specified registry key. Returns 0 if
 * successful otherwise -1 if the registry data was invalid.
 */
static jint
LoadSettings(JRESettings *set, HKEY key)
{
    /* Full path name of JRE home directory (required) */
    set->javaHome = GetStringValue(key, "JavaHome");
    if (set->javaHome == 0) {
	return -1;
    }
    /* Full path name of JRE runtime DLL */
    set->runtimeLib = GetStringValue(key, "RuntimeLib");
    if (set->runtimeLib == 0) {
	set->runtimeLib = JRE_GetDefaultRuntimeLib(set->javaHome);
    }
    /* Class path setting to override default */
    set->classPath = GetStringValue(key, "ClassPath");
    if (set->classPath == 0) {
	set->classPath = JRE_GetDefaultClassPath(set->javaHome);
    }
    /* Optional JIT compiler library name */
    set->compiler = GetStringValue(key, "Compiler");
    /* Release micro-version */
    set->microVersion = GetStringValue(key, "MicroVersion");
    return 0;
}

/*
 * Returns string data for the specified registry value name, or
 * NULL if not found.
 */
static char *
GetStringValue(HKEY key, const char *name)
{
    DWORD type, size;
    char *value = 0;

    if (RegQueryValueEx(key, name, 0, &type, 0, &size) == 0 &&
	type == REG_SZ ) {
	value = JRE_Malloc(size);
	if (RegQueryValueEx(key, name, 0, 0, value, &size) != 0) {
	    free(value);
	    value = 0;
	}
    }
    return value;
}

/*
 * Returns default runtime settings based on location of this program.
 * Makes best attempt at determining location of runtime. Returns 0
 * if successful or -1 if a runtime could not be found.
 */
jint
JRE_GetDefaultSettings(JRESettings *set)
{
    char buf[MAX_PATH], *bp;
    int n;

    // Try to obtain default value for Java home directory based on
    // location of this executable.

    if ((n = GetModuleFileName(0, buf, MAX_PATH)) == 0) {
	return -1;
    }
    bp = buf + n;
    while (*--bp != '\\') ;
    bp -= 4;
    if (bp < buf || strnicmp(bp, "\\bin", 4) != 0) {
	return -1;
    }
    *bp = '\0';
    set->javaHome = strdup(buf);

    // Get default runtime library
    set->runtimeLib = JRE_GetDefaultRuntimeLib(set->javaHome);

    // Get default class path
    set->classPath = JRE_GetDefaultClassPath(set->javaHome);

    // Reset other fields since these are unknown
    set->compiler = 0;
    set->majorVersion = 0;
    set->minorVersion = 0;
    set->microVersion = 0;

    return 0;
}

/*
 * Return default runtime library for specified Java home directory.
 */
char *
JRE_GetDefaultRuntimeLib(const char *dir)
{
    char *cp = JRE_Malloc(strlen(dir) + sizeof(RUNTIME_LIB) + 8);
    sprintf(cp, "%s\\bin\\" RUNTIME_LIB, dir);
    return cp;
}

/*
 * Return default class path for specified Java home directory.
 */
char *
JRE_GetDefaultClassPath(const char *dir)
{
    char *cp = JRE_Malloc(strlen(dir) * 4 + 64);
    sprintf(cp, "%s\\lib\\rt.jar;%s\\lib\\i18n.jar;%s\\lib\\classes.zip;"
		"%s\\classes", dir, dir, dir, dir);
    return cp;
}

/*
 * Loads the runtime library corresponding to 'libname' and returns
 * an opaque handle to the library.
 */
void *
JRE_LoadLibrary(const char *path)
{
    return (void *)LoadLibrary(path);
}

/*
 * Unloads the runtime library associated with handle.
 */
void
JRE_UnloadLibrary(void *handle)
{
    FreeLibrary(handle);
}

/*
 * Loads default VM args for the specified runtime library handle.
 */
jint
JRE_GetDefaultJavaVMInitArgs(void *handle, void *vmargs)
{
    FARPROC proc = GetProcAddress(handle, "JNI_GetDefaultJavaVMInitArgs");
    return proc != 0 ? ((*proc)(vmargs), 0) : -1;
}

/*
 * Creates a Java VM for the specified runtime library handle.
 */
jint
JRE_CreateJavaVM(void *handle, JavaVM **vmp, JNIEnv **envp, void *vmargs)
{
    FARPROC proc = GetProcAddress(handle, "JNI_CreateJavaVM");
    return proc != 0 ? (*proc)(vmp, envp, vmargs) : -1;
}

/*
 * Initialize heap options for the specified runtime library handle.
 */
jint
JRE_InitHeapOptions(void *handle, heapoptions *opts)
{
    FARPROC proc = GetProcAddress(handle, "Opt_InitHeapOptions");
    return proc != 0 ? (*proc)(opts) : -1;
}

/*
 * Set heap options for the specified runtime library handle.
 */
jint
JRE_SetHeapOptions(void *handle, heapoptions *opts)
{
    FARPROC proc = GetProcAddress(handle, "Opt_SetHeapOptions");
    return proc != 0 ? (*proc)(opts) : -1;
}

/*
 * Entry point for JREW (Windows-only) version of the runtime loader.
 * This entry point is called when the '-subsystem:windows' linker
 * option is used, and will cause the resulting executable to run
 * detached from the console.
 */
int WINAPI
WinMain(HINSTANCE inst, HINSTANCE prevInst, LPSTR cmdLine, int cmdShow)
{
    __declspec(dllimport) char **__initenv;

    __initenv = _environ;
    exit(main(__argc, __argv));
}
