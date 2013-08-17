/*
 * @(#)java.h	1.20 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

#ifndef _JAVA_H_
#define _JAVA_H_

/*
 * Get system specific defines.
 */
#include "jni.h"
#include "java_md.h"

/*
 * Pointers to the needed JNI invocation API, initialized by LoadJavaVM.
 */
typedef jint (JNICALL *CreateJavaVM_t)(JavaVM **pvm, void **env, void *args);
typedef jint (JNICALL *GetDefaultJavaVMInitArgs_t)(void *args);

typedef struct {
    CreateJavaVM_t CreateJavaVM;
    GetDefaultJavaVMInitArgs_t GetDefaultJavaVMInitArgs;
} InvocationFunctions;

/*
 * Protoypes for launcher functions in the system specific java_md.c.
 */
jboolean
GetJVMPath(const char *jrepath, const char *jvmtype,
	   char *jvmpath, jint jvmpathsize);

jboolean
GetJREPath(char *path, jint pathsize);

jboolean
LoadJavaVM(const char *jvmpath, InvocationFunctions *ifn);

void
GetXUsagePath(char *buf, jint bufsize);

jboolean
GetApplicationHome(char *buf, jint bufsize);

const char *
GetArch();

/* Defined in java.c; used in java_md.c.  Gets the name of the current executable. */
extern char*
GetExecname();

/*
 * Make launcher spit debug output.
 */
extern jboolean debug;

#endif /* _JAVA_H_ */
