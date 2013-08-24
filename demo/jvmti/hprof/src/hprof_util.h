/*
 * @(#)hprof_util.h	1.25 05/09/30
 * 
 * Copyright (c) 2005 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright notice, 
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may 
 * be used to endorse or promote products derived from this software without 
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL 
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST 
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY 
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, 
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

#ifndef HPROF_UTIL_H
#define HPROF_UTIL_H

/* Macros that protect code from accidently using a local ref improperly */
#define WITH_LOCAL_REFS(env, number)            \
    {                                           \
        JNIEnv *_env = (env);                   \
        pushLocalFrame(_env, number);           \
        { /* BEGINNING OF WITH SCOPE */

#define END_WITH_LOCAL_REFS                     \
        } /* END OF WITH SCOPE */               \
        popLocalFrame(_env, NULL);              \
    }

/* Macro to check for exceptions after JNI calls. */
#define CHECK_EXCEPTIONS(env)                                           \
    {                                                                   \
        JNIEnv *_env = (env);                                           \
        jobject _exception;                                             \
        _exception = exceptionOccurred(_env);                           \
        if ( _exception != NULL ) {                                     \
            exceptionDescribe(_env);                                    \
            HPROF_ERROR(JNI_TRUE, "Unexpected Exception found beforehand");\
        }                                                               \
        {

#define END_CHECK_EXCEPTIONS                                            \
        }                                                               \
        _exception = exceptionOccurred(_env);                           \
        if ( _exception != NULL ) {                                     \
            exceptionDescribe(_env);                                    \
            HPROF_ERROR(JNI_TRUE, "Unexpected Exception found afterward");\
        }                                                               \
    }

JNIEnv *   getEnv(void);

/* JNI support functions */
jobject    newGlobalReference(JNIEnv *env, jobject object);
jobject    newWeakGlobalReference(JNIEnv *env, jobject object);
void       deleteGlobalReference(JNIEnv *env, jobject object);
jobject	   newLocalReference(JNIEnv *env, jobject object);
void	   deleteLocalReference(JNIEnv *env, jobject object);
void       deleteWeakGlobalReference(JNIEnv *env, jobject object);
jclass     getObjectClass(JNIEnv *env, jobject object);
jmethodID  getMethodID(JNIEnv *env, jclass clazz, const char* name, 
			const char *sig);
jclass     getSuperclass(JNIEnv *env, jclass klass);
jvalue     getFieldValue(JNIEnv *env, jobject object, jfieldID field, 
			char *field_sig);
jvalue     getStaticFieldValue(JNIEnv *env, jclass klass, jfieldID field, 
			char *field_sig);
jmethodID  getStaticMethodID(JNIEnv *env, jclass clazz, const char* name, 
			const char *sig);
jfieldID   getStaticFieldID(JNIEnv *env, jclass clazz, const char* name, 
			const char *sig);
jclass     findClass(JNIEnv *env, const char *name);
void       setStaticIntField(JNIEnv *env, jclass clazz, jfieldID field, 
			jint value);
jboolean   isSameObject(JNIEnv *env, jobject o1, jobject o2);
void       pushLocalFrame(JNIEnv *env, jint capacity);
void       popLocalFrame(JNIEnv *env, jobject ret);
jobject    exceptionOccurred(JNIEnv *env);
void       exceptionDescribe(JNIEnv *env);
void       exceptionClear(JNIEnv *env);
void       registerNatives(JNIEnv *env, jclass clazz, 
			JNINativeMethod *methods, jint count);

/* More JVMTI support functions */
char *    getErrorName(jvmtiError error_number);
void      disposeEnvironment(void);
jlong     getObjectSize(jobject object);
jint      getClassStatus(jclass klass);
jobject   getClassLoader(jclass klass);
jlong     getTag(jobject object);
void      setTag(jobject object, jlong tag);
void      getObjectMonitorUsage(jobject object, jvmtiMonitorUsage *uinfo);
void      getOwnedMonitorInfo(jthread thread, jobject **ppobjects, 
			jint *pcount);
void      getSystemProperty(const char *name, char **value);
void      getClassSignature(jclass klass, char**psignature, 
			char **pgeneric_signature);
void      getSourceFileName(jclass klass, char** src_name_ptr);
void      getAllClassFieldInfo(JNIEnv *env, jclass klass, 
			jint* field_count_ptr, FieldInfo** fields_ptr);
void      getMethodName(jmethodID method, char** name_ptr, 
			char** signature_ptr);
void      getMethodClass(jmethodID method, jclass *pclazz);
jboolean  isMethodNative(jmethodID method);
void      getPotentialCapabilities(jvmtiCapabilities *capabilities);
void      addCapabilities(jvmtiCapabilities *capabilities);
void      setEventCallbacks(jvmtiEventCallbacks *pcallbacks);
void      setEventNotificationMode(jvmtiEventMode mode, jvmtiEvent event, 
			jthread thread);
void *    getThreadLocalStorage(jthread thread);
void      setThreadLocalStorage(jthread thread, void *ptr);
void      getThreadState(jthread thread, jint *threadState);
void      getThreadInfo(jthread thread, jvmtiThreadInfo *info);
void      getThreadGroupInfo(jthreadGroup thread_group, jvmtiThreadGroupInfo *info);
void      getLoadedClasses(jclass **ppclasses, jint *pcount);
jint      getLineNumber(jmethodID method, jlocation location);
void      createAgentThread(JNIEnv *env, const char *name, 
                        jvmtiStartFunction func);
jlong     getThreadCpuTime(jthread thread);
void      getStackTrace(jthread thread, jvmtiFrameInfo *pframes, jint depth, 
			jint *pcount);
void      getThreadListStackTraces(jint count, jthread *threads, 
			jint depth, jvmtiStackInfo **stack_info);
void      getFrameCount(jthread thread, jint *pcount);
void      iterateOverReachableObjects(jvmtiHeapRootCallback heap_root_callback,
			jvmtiStackReferenceCallback stack_ref_callback,
			jvmtiObjectReferenceCallback object_ref_callback,
			void *user_data);

/* GC control */
void      runGC(void);

/* Get initial JVMTI environment */
void      getJvmti(void);

/* Raw monitor functions */
jrawMonitorID createRawMonitor(const char *str);
void          rawMonitorEnter(jrawMonitorID m);
void          rawMonitorWait(jrawMonitorID m, jlong pause_time);
void          rawMonitorNotifyAll(jrawMonitorID m);
void          rawMonitorExit(jrawMonitorID m);
void          destroyRawMonitor(jrawMonitorID m);

/* JVMTI alloc/dealloc */
void *        jvmtiAllocate(int size);
void          jvmtiDeallocate(void *ptr);

/* System malloc/free */
void *        hprof_malloc(int size);
void          hprof_free(void *ptr);

#include "debug_malloc.h"

#ifdef DEBUG
    void *        hprof_debug_malloc(int size, char *file, int line);
    void          hprof_debug_free(void *ptr, char *file, int line);
    #define HPROF_MALLOC(size)  hprof_debug_malloc(size, __FILE__, __LINE__)
    #define HPROF_FREE(ptr)     hprof_debug_free(ptr, __FILE__, __LINE__)
#else
    #define HPROF_MALLOC(size)  hprof_malloc(size)
    #define HPROF_FREE(ptr)     hprof_free(ptr)
#endif

#endif
