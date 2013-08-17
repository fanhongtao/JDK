/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

#ifndef _JAVASOFT_JIT_H_
#define _JAVASOFT_JIT_H_

#include "bool.h"
#include "java_lang_String.h"
#include "exceptions.h"
#include "vmprofiler.h"

typedef struct {
    int * JavaVersion;
    int * MicroVersion;
    void * reserved1;
} JITInterface;

typedef struct {
    JITInterface base;
    void (**p_InitializeForCompiler)(ClassClass *cb);
    bool_t (**p_invokeCompiledMethod)(JHandle *o, struct methodblock *mb, 
				     int args_size, ExecEnv *ee); 
    bool_t (**p_CompiledCodeSignalHandler)(int sig, void *info, void *uc);
    void (**p_CompilerFreeClass)(ClassClass *cb);
    bool_t (**p_CompilerRegisterNatives)(ClassClass *cb);
    bool_t (**p_CompilerUnregisterNatives)(ClassClass *cb);
    bool_t (**p_CompilerCompileClass)(ClassClass *cb);
    bool_t (**p_CompilerCompileClasses)(Hjava_lang_String *name);
    void (**p_CompilerEnable)(void);
    void (**p_CompilerDisable)(void);
    JavaFrame *(**p_CompiledFramePrev)(JavaFrame *frame, JavaFrame *buf);
    void *(**p_CompiledFrameID)(JavaFrame *frame);
    void (**p_ReadInCompiledCode)(void *context, 
				 struct methodblock *mb, 
				 int attribute_length, 
				 unsigned long (*get1byte)(void *),
				 unsigned long (*get2bytes)(void *), 
				 unsigned long (*get4bytes)(void *), 
				 void (*getNbytes)(void *, size_t, char *));
    bool_t (**p_PCinCompiledCode)(unsigned char *, struct methodblock *);
    unsigned char *(**p_CompiledCodePC)(JavaFrame *frame,
					struct methodblock *mb);
    char **p_CompiledCodeAttribute;
    bool_t * UseLosslessQuickOpcodes;

    void * (*sysMalloc)(size_t);
    void * (*sysCalloc)(size_t, size_t);
    void * (*sysRealloc)(void*, size_t);
    char * (*sysStrdup)(const char *);
    void (*sysFree)(void*);

    ClassClass *** binclasses;
    int * nbinclasses;
    sys_mon_t **binclass_lock;
    sys_mon_t **linkclass_lock;

    ClassClass ** classJavaLangClass;
    ClassClass ** classJavaLangObject;
    ClassClass ** classJavaLangString;
    ClassClass ** classJavaLangThrowable;
    ClassClass ** classJavaLangException;
    ClassClass ** classJavaLangRuntimeException;
    ClassClass ** interfaceJavaLangCloneable;

    ExecEnv * (*EE)(void);
    void (*SignalError)(struct execenv *, char *, char *);
    exception_t (*exceptionInternalObject)(internal_exception_t exc);
    char *(*GetClassConstantClassName)(cp_item_type *constant_pool,
					 int index);
    ClassClass * (*FindClass)(struct execenv *ee, char *name,
				bool_t resolve);
    ClassClass * (*FindClassFromClass)(struct execenv *ee, char *name,
					 bool_t resolve, ClassClass *from);
    void (*InitClass)(ClassClass * cb);
    bool_t (*ResolveClassConstant2)(cp_item_type *constant_pool,
				    unsigned index,
				    struct execenv *ee,
				    unsigned mask,
				    bool_t init);
    bool_t (*ResolveClassConstantFromClass2)(ClassClass *clazz,
					     unsigned index,
					     struct execenv *ee,
					     unsigned mask,
					     bool_t init);
    bool_t (*VerifyClassAccess)(ClassClass *current_class,
				  ClassClass *new_class,
				  bool_t classloader_only);
    bool_t (*VerifyFieldAccess)(ClassClass *current_class,
				  ClassClass *field_class,
				  int access, bool_t classloader_only);
    bool_t (*is_subclass_of)(ClassClass *cb, ClassClass *dcb, ExecEnv *ee);
    bool_t (*is_instance_of)(JHandle * h, ClassClass *dcb, ExecEnv *ee);
    char * (*classname2string)(char *src, char *dst, int size);

    HObject * (*allocObject)(ExecEnv *ee, ClassClass *cb);
    HObject * (*allocArray)(ExecEnv *ee, int t, int l);
    HObject * (*MultiArrayAlloc)(int dimensions, ClassClass *array_cb,
				   stack_item *sizes);
    int (*sizearray)(int t, int l);

    void * (*dynoLink)(struct methodblock *mb, uint32_t *info);

    long (*do_execute_java_method_vararg)(ExecEnv *ee, void *obj,
					    char *method_name,
					    char *method_signature,
					    struct methodblock *mb,
					    bool_t isStaticCall, va_list args,
					    long *otherBits,
					    bool_t shortFloats);
    long (*execute_java_static_method)(ExecEnv *ee, ClassClass *cb,
					 char *method_name,
					 char *signature, ...);

    bool_t (*invokeJavaMethod)(JHandle *o, struct methodblock *mb,
				 int args_size, ExecEnv *ee);
    bool_t (*invokeSynchronizedJavaMethod)(JHandle *o,
					     struct methodblock *mb,
					     int args_size, ExecEnv *ee);
    bool_t (*invokeAbstractMethod)(JHandle *o, struct methodblock *mb,
				     int args_size, ExecEnv *ee);
    bool_t (*invokeLazyNativeMethod)(JHandle *o, struct methodblock *mb,
				       int args_size, ExecEnv *ee);
    bool_t (*invokeSynchronizedNativeMethod)(JHandle *o,
					       struct methodblock *mb,
					       int args_size, ExecEnv *ee);
    bool_t (*invokeCompiledMethod)(JHandle *o, struct methodblock *mb,
				     int args_size, ExecEnv *ee);
    bool_t (*invokeNativeMethod)(JHandle *o, struct methodblock *mb,
				   int args_size, ExecEnv *ee);
    bool_t (*invokeJNINativeMethod)(JHandle *o, struct methodblock *mb,
				      int args_size, ExecEnv *ee);
    bool_t (*invokeJNISynchronizedNativeMethod)(JHandle *o,
						  struct methodblock *mb,
						  int args_size, ExecEnv *ee);

    sys_mon_t * (*monitorEnter2)(ExecEnv *ee, uintptr_t key);
    int (*monitorExit2)(ExecEnv *ee, uintptr_t key);
    void (*monitorRegister)(sys_mon_t *mid, char *name);
    size_t (*sysMonitorSizeof)(void);
    int (*sysMonitorEnter)(sys_thread_t *tid, sys_mon_t *mid);
    int (*sysMonitorExit)(sys_thread_t *tid, sys_mon_t *mid);
    bool_t (*sysMonitorEntered)(sys_thread_t * t, sys_mon_t *mid);

    void (*DumpThreads)(void);

    int (*ExpandJavaStackForJNI)(ExecEnv *ee,
				 JavaStack **stackP, 
				 JavaFrame **frameP,
				 int capacity);
    int (*ExpandJavaStack)(ExecEnv *ee,
			   JavaStack **stackP, 
			   JavaFrame **frameP, 
			   stack_item **optopP,
			   int args_size,
			   int nlocals, 
			   int maxstack);

    bool_t (*ExecuteJava)(unsigned char *initial_pc, ExecEnv *ee);
    long (*now)(void);
    int * java_profiler_isOn;
    void (*java_profiler_log)(struct methodblock* caller,
			      struct methodblock* callee, int32_t time);
    long * JavaStackSize;
    int (*jio_snprintf)(char *str, size_t count, const char *fmt, ...);
    char * (*javaString2CString)(Hjava_lang_String *s, char *buf, int buflen);
    
    jobject (*jni_mkRefLocal)(ExecEnv *ee, JHandle *jobj);
    HObject * (*cacheAlloc)(ExecEnv *ee, struct methodtable *mptr, long size);

    unsigned int * jvmpi_event_flags;
    Invoker (*getCustomInvoker)(char *sig);
    sys_mon_t **code_lock;
  
    /*  
     * Support for JVM<--->Profiler interface from the JIT.
     * 
     * We need notification from the JIT on entry/exit of a method and 
     * load/unload of compiled method in memory if the corresponding bit 
     * is set in jvmpi_event_flags.
     *
     * event                        bit mask to & with jvmpi_event_flags
     * -----                        ------------------------------------
     * jvmpi_method_entry           JVMPI_EVENT_METHOD_ENTRY_ON
     * jvmpi_method_exit            JVMPI_EVENT_METHOD_EXIT_ON
     * jvmpi_load_compiled_method   JVMPI_EVENT_LOAD_COMPILED_METHOD_ON
     * jvmpi_unload_compiled_method JVMPI_EVENT_UNLOAD_COMPILED_METHOD_ON
     * 
     * Above flags and the necessary types are defined in vmprofiler.h
     */
    void (*jvmpi_method_entry)(ExecEnv *ee, JHandle *h);
    void (*jvmpi_method_exit)(ExecEnv *ee);
    void (*jvmpi_load_compiled_method)(compiled_method_t *compiled_method_info);
    void (*jvmpi_unload_compiled_method)(struct methodblock *mb);
    JavaFrame * (**p_CompiledFrameUpdate)(JavaFrame *frame);

    void (*monitorWait2)(ExecEnv *ee, uintptr_t key, int64_t millis);
    void (*monitorNotify2)(ExecEnv *ee, uintptr_t key);
    void (*monitorNotifyAll2)(ExecEnv *ee, uintptr_t key);

    int (*sysMonitorWait)(sys_thread_t *tid, sys_mon_t *mid, jlong millis);
    int (*sysMonitorNotify)(sys_thread_t *tid, sys_mon_t *mid);
    int (*sysMonitorNotifyAll)(sys_thread_t *tid, sys_mon_t *mid);

    void (**p_CompilerLinkClass)(ClassClass *cb);
    void (**p_CompilerLoadClass)(ClassClass *cb, unsigned char *data, int len);
    int (**p_CompiledCodePCtoLineNo)(unsigned char *);

    sys_mon_t **queue_lock;
    int	(*sysThreadSingle)(void);
    void (*sysThreadMulti)(void);
    int (*sysThreadEnumerateOver)(int (*func)(sys_thread_t *, void *), void *arg);
    int (**p_CompilerHandlesFrame)(void);

} JITInterface6;

#endif /* !_JAVASOFT_JIT_H_ */
