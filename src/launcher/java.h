/*
 * @(#)java.h	1.10 01/11/29
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
typedef struct {
    jint (JNICALL *CreateJavaVM)(JavaVM **pvm, void **env, void *args);
    jint (JNICALL *GetDefaultJavaVMInitArgs)(void *args);
} InvocationFunctions;

/*
 * Protoypes for launcher functions in the system specific java_md.c.
 */
jboolean LoadJavaVM(char *jvmtype, InvocationFunctions *ifn);
void GetXUsagePath(char *buf, jint bufsize);
jboolean GetApplicationHome(char *buf, jint bufsize);

/*
 * Make launcher spit debug output.
 */
extern jboolean debug;

#endif /* _JAVA_H_ */
