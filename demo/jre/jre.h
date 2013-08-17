/*
 * @(#)jre.h	1.11 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * Portable JRE support functions.
 */

#include <stdio.h>
#include <stdlib.h>
#include <jni.h>

#include "jre_md.h"

/*
 * Java runtime settings.
 */
typedef struct JRESettings {
    char *javaHome;	    /* Java home directory */
    char *runtimeLib;	    /* Runtime shared library or DLL */
    char *classPath;	    /* Default class path */
    char *compiler;	    /* Just-in-time (JIT) compiler */
    char *majorVersion;	    /* Major version of runtime */
    char *minorVersion;	    /* Minor version of runtime */
    char *microVersion;	    /* Micro version of runtime */
} JRESettings;

/*
 * Java heap option settings.
 */
typedef struct heapoptions {
    float minHeapFreePercent; /* -minf */
    float maxHeapFreePercent; /* -maxf */
    long minHeapExpansion;    /* -mine */
    long maxHeapExpansion;    /* -maxe */
} heapoptions;

/*
 * JRE functions.
 */
void *JRE_LoadLibrary(const char *path);
void  JRE_UnloadLibrary(void *handle);
jint JRE_GetDefaultJavaVMInitArgs(void *handle, void *vmargsp);
jint JRE_CreateJavaVM(void *handle, JavaVM **vmp, JNIEnv **envp,
		      void *vmargsp);
jint JRE_GetCurrentSettings(JRESettings *set);
jint JRE_GetSettings(JRESettings *set, const char *ver);
jint JRE_GetDefaultSettings(JRESettings *set);
jint JRE_ParseVersion(const char *version,
		      char **majorp, char **minorp, char **microp);
char *JRE_MakeVersion(const char *major, const char *minor, const char *micro);
void *JRE_Malloc(size_t size);
void JRE_FatalError(JNIEnv *env, const char *msg);
char *JRE_GetDefaultRuntimeLib(const char *dir);
char *JRE_GetDefaultClassPath(const char *dir);

jint JRE_InitHeapOptions(void *handle, heapoptions *);
jint JRE_SetHeapOptions(void *handle, heapoptions *);
