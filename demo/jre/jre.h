/*
 * @(#)jre.h	1.10 00/03/28
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 * 
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
