/*
 * @(#)java.h	1.22 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
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
LoadJavaVM(const char *jvmpath, InvocationFunctions *ifn);

void
GetXUsagePath(char *buf, jint bufsize);

jboolean
GetApplicationHome(char *buf, jint bufsize);

const char *
GetArch();

void CreateExecutionEnvironment(int *_argc,
				       char ***_argv,
				       char jrepath[],
				       jint so_jrepath,
				       char jvmpath[],
				       jint so_jvmpath,
				       char **_jvmtype,
				       char **original_argv);

/*
 * Report an error message to stderr or a window as appropriate.  The
 * flag always is set to JNI_TRUE if message is to be reported to both
 * strerr and windows and set to JNI_FALSE if the message should only
 * be sent to a window.
 */
void ReportErrorMessage(char * message, jboolean always);
void ReportErrorMessage2(char * format, char * string, jboolean always);

jboolean RemovableMachineDependentOption(char * option);
void PrintMachineDependentOptions();

/* 
 * Functions defined in java.c and used in java_md.c.
 */
jint ReadKnownVMs(const char *jrepath); 
char *CheckJvmType(int *argc, char ***argv);
void* MemAlloc(size_t size);

/*
 * Make launcher spit debug output.
 */
extern jboolean debug;

#endif /* _JAVA_H_ */
