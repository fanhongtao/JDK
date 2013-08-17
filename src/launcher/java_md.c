/*
 * @(#)java_md.c	1.9 98/06/29
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

#include <windows.h>
#include <stdlib.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>

#include <jni.h>
#include "java.h"

#ifdef DEBUG
#define JVM_DLL "jvm_g.dll"
#define JAVA_DLL "java_g.dll"
#else
#define JVM_DLL "jvm.dll"
#define JAVA_DLL "java.dll"
#endif

/*
 * Prototypes.
 */
static jboolean
GetPublicJREHome(char *path, jint pathsize);

/*
 * Load JVM of "jvmtype", and intialize the invocation functions.  Notice that
 * if jvmtype is NULL, we try to load hotspot VM as the default.  Maybe we
 * need an environment variable that dictates the choice of default VM.
 */
jboolean
LoadJavaVM(char *jvmtype, InvocationFunctions *ifn)
{
    char home[MAXPATHLEN], javadll[MAXPATHLEN], jvmdll[MAXPATHLEN];
    HINSTANCE handle;
    struct stat s;

    /* Is JRE co-located with the application? */
    if (GetApplicationHome(home, sizeof(home))) {
	sprintf(javadll, "%s\\bin\\" JAVA_DLL, home);
	if (stat(javadll, &s) == 0)
	    goto jrefound;
    }
    /* Does this app ship a private JRE in <apphome>\jre directory? */
    sprintf(javadll, "%s\\jre\\bin\\" JAVA_DLL, home);
    if (stat(javadll, &s) == 0) {
	strcat(home, "\\jre");
	goto jrefound;
    }
    /* Look for a public JRE on this machine. */
    if (!GetPublicJREHome(home, sizeof(home))) {
	return JNI_FALSE;
    }

    /* Now we know where JRE is -- the value in the "home" variable. */
jrefound:
    /* Determine if Hotspot VM is installed. */
    if (jvmtype == NULL) {
	sprintf(jvmdll, "%s\\bin\\hotspot\\" JVM_DLL, home);
	if (stat(jvmdll, &s) < 0) {
	    jvmtype = "classic";
	} else {
	    jvmtype = "hotspot";
	}
    }

    /* We now know what jvmtype should be */
    sprintf(jvmdll, "%s\\bin\\%s\\" JVM_DLL, home, jvmtype);
    if (debug) {
	printf("Path to JVM is %s\n", jvmdll);
    }

    /* Load the Java VM DLL */
    if ((handle = LoadLibrary(jvmdll)) == 0) {
	fprintf(stderr, "Error loading: %s\n", jvmdll);
	return JNI_FALSE;
    }

    /* Now get the function addresses */
    ifn->CreateJavaVM =
	(void *)GetProcAddress(handle, "JNI_CreateJavaVM");
    ifn->GetDefaultJavaVMInitArgs =
	(void *)GetProcAddress(handle, "JNI_GetDefaultJavaVMInitArgs");
    if (ifn->CreateJavaVM == 0 ||
	ifn->GetDefaultJavaVMInitArgs == 0) {
	fprintf(stderr, "Can't find JNI interfaces in: %s\n", jvmdll);
	return JNI_FALSE;
    }

    return JNI_TRUE;
}

/*
 * Get the path to the file that has the usage message for -X options.
 */
void
GetXUsagePath(char *buf, jint bufsize)
{
    GetModuleFileName(GetModuleHandle(JVM_DLL), buf, bufsize);
    *(strrchr(buf, '\\')) = '\0';
    strcat(buf, "\\Xusage.txt");
}

/*
 * If app is "c:\foo\bin\javac", then put "c:\foo" into buf.
 */
jboolean
GetApplicationHome(char *buf, jint bufsize)
{
    char *cp;
    GetModuleFileName(0, buf, bufsize);
    *strrchr(buf, '\\') = '\0'; /* remove .exe file name */
    if ((cp = strrchr(buf, '\\')) == 0) {
	/* This happens if the application is in a drive root, and
	 * there is no bin directory. */
	buf[0] = '\0';
	return JNI_FALSE;
    }
    *cp = '\0';  /* remove the bin\ part */
    return JNI_TRUE;
}

#ifdef JAVAW
__declspec(dllimport) char **__initenv;

int WINAPI
WinMain(HINSTANCE inst, HINSTANCE previnst, LPSTR cmdline, int cmdshow)
{
    __initenv = _environ;
    return main(__argc, __argv);
}
#endif

/*
 * Helpers to look in the registry for a public JRE.
 */
#define DOTRELEASE  "1.2" /* Same for 1.2.1, 1.2.2 etc. */
#define JRE_KEY	    "Software\\JavaSoft\\Java Runtime Environment"

static jboolean
GetStringFromRegistry(HKEY key, const char *name, char *buf, jint bufsize)
{
    DWORD type, size;

    if (RegQueryValueEx(key, name, 0, &type, 0, &size) == 0
	&& type == REG_SZ
	&& (size < (unsigned int)bufsize)) {
	if (RegQueryValueEx(key, name, 0, 0, buf, &size) == 0) {
	    return JNI_TRUE;
	}
    }
    return JNI_FALSE;
}

static jboolean
GetPublicJREHome(char *buf, jint bufsize)
{
    HKEY key, subkey;
    char version[MAXPATHLEN];

    /* Find the current version of the JRE */
    if (RegOpenKeyEx(HKEY_LOCAL_MACHINE, JRE_KEY, 0, KEY_READ, &key) != 0) {
	fprintf(stderr, "Error opening registry key '" JRE_KEY "'\n");
	return JNI_FALSE;
    }

    if (!GetStringFromRegistry(key, "CurrentVersion",
			       version, sizeof(version))) {
	fprintf(stderr, "Failed reading value of registry key:\n\t"
		JRE_KEY "\\CurrentVersion\n");
	RegCloseKey(key);
	return JNI_FALSE;
    }

    if (strcmp(version, DOTRELEASE) != 0) {
	fprintf(stderr, "Registry key '" JRE_KEY "\\CurrentVersion'\nhas "
		"value '%s', but '" DOTRELEASE "' is required.\n", version);
	RegCloseKey(key);
	return JNI_FALSE;
    }

    /* Find directory where the current version is installed. */
    if (RegOpenKeyEx(key, version, 0, KEY_READ, &subkey) != 0) {
	fprintf(stderr, "Error opening registry key '"
		JRE_KEY "\\%s'\n", version);
	RegCloseKey(key);
	return JNI_FALSE;
    }

    if (!GetStringFromRegistry(subkey, "JavaHome", buf, bufsize)) {
	fprintf(stderr, "Failed reading value of registry key:\n\t"
		JRE_KEY "\\%s\\JavaHome\n", version);
	RegCloseKey(key);
	RegCloseKey(subkey);
	return JNI_FALSE;
    }

    if (debug) {
	char micro[MAXPATHLEN];
	if (!GetStringFromRegistry(subkey, "MicroVersion", micro,
				   sizeof(micro))) {
	    printf("Warning: Can't read MicroVersion\n");
	    micro[0] = '\0';
	}
	printf("Version major.minor.micro = %s.%s\n", version, micro);
    }

    RegCloseKey(key);
    RegCloseKey(subkey);
    return JNI_TRUE;
}

/*
 * Support for doing cheap, accurate interval timing.
 */
static jboolean counterAvailable = JNI_FALSE;
static jboolean counterInitialized = JNI_FALSE;
static LARGE_INTEGER counterFrequency;

jlong CounterGet()
{
    LARGE_INTEGER count;

    if (!counterInitialized) {
	counterAvailable = QueryPerformanceFrequency(&counterFrequency);
	counterInitialized = JNI_TRUE;
    }
    if (!counterAvailable) {
	return 0;
    }
    QueryPerformanceCounter(&count);
    return (jlong)(count.QuadPart);
}

jlong Counter2Micros(jlong counts)
{
    if (!counterAvailable || !counterInitialized) {
	return 0;
    }
    return (counts * 1000 * 1000)/counterFrequency.QuadPart;
}
