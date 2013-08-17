/*
 * @(#)interpreter.h	1.213 01/01/23
 *
 * Copyright 1994-2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 * 
 */

/*
 * Definitions for the interperter	6/27/91
 */

#ifndef _JAVASOFT_INTERPRETER_H_
#define _JAVASOFT_INTERPRETER_H_

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "util.h"
#include "bool.h"
#include "jni.h"
#include "jvm.h"
#include "alloc_cache.h"
#include "sysmacros_md.h"  /* for sysAssert */
#include "dll.h"

#include "miscdefs_md.h"

/* Global variables */
extern bool_t debugging;
extern bool_t verbose;
extern bool_t verbosegc;
extern bool_t verboseclassdep;
extern bool_t classgc;
extern char *init_sysclasspath;
extern char *java_home_dir;
extern char *java_dll_dir;

extern bool_t tried_loading_jit;

extern bool_t reduce_signal_usage;
extern bool_t verbose_jni;
extern bool_t compilerInitialized;

extern int min_javastack_chunk_size;

extern jint (JNICALL *vfprintf_hook)(FILE *fp, const char *fmt, va_list args);
extern void (JNICALL *exit_hook)(jint code);
extern void (JNICALL *abort_hook)(void);

extern ClassClass *classJavaLangClass;	   /* class java/lang/Class */
extern ClassClass *classJavaLangObject;	   /* class java/lang/Object */
extern ClassClass *classJavaLangString;	   /* class java/lang/String */
extern ClassClass *classJavaLangThread;	   /* class java/lang/String */

extern ClassClass *classJavaLangClassLoader;
extern ClassClass *classJavaLangThrowable;
extern ClassClass *classJavaLangException;
extern ClassClass *classJavaLangError;
extern ClassClass *classJavaLangRuntimeException;
extern ClassClass *classJavaLangThreadDeath;

extern ClassClass *interfaceJavaLangCloneable; /* class java/lang/Cloneable */
extern ClassClass *interfaceJavaIoSerializable; /* class java/io/Serializable */

extern struct methodblock *reflect_invoke_mb; /* Used in getCallerFrame. */

#define STK_BUF_LEN 256   /* size of temp buffer allocated on stack */
#define MSG_BUF_LEN 256   /* size of stack-allocated temp buffer used to hold
			     messages */
#define MAXDIM 255        /* maximum Java array dimension allowed in class
			     files */

/* Byte to Short macro*/
#ifndef PC2SIGNEDSHORT
#define PC2SIGNEDSHORT(pc)  ((short)(((signed char *)(pc))[1] << 8) | \
                             (short)pc[2])
#endif /* PC2SIGNEDSHORT */

/* Byte to Long macro */
#ifndef PC2SIGNEDLONG
#define PC2SIGNEDLONG(pc)  ((long)(((signed char *)(pc))[1] << 24) |  \
                           (long)(pc[2] << 16) | (long)(pc[3] << 8) | \
                           (long)(pc[4]))
#endif /* PC2SIGNEDLONG */

enum { VERIFY_NONE, VERIFY_REMOTE, VERIFY_ALL };

extern int verifyclasses;

#define FINALIZER_METHOD_NAME "finalize"
#define FINALIZER_METHOD_SIGNATURE "()V"

#ifdef TRACING
  extern int trace;
  extern int tracem;
  extern void trace_method(struct execenv*, struct methodblock*, int, int);
  enum { TRACE_METHOD_ENTER, TRACE_METHOD_RETURN, TRACE_METHOD_NATIVE_RETURN };
# define TRACE_METHOD(ee, mb, args_size, type) \
      if (tracem) trace_method(ee, mb, args_size, type); else ((void) 0)

#else
# define TRACE_METHOD(ee, mb, args_size, type) 
#endif

extern char * const opnames[];

/* Get a constant pool index, from a pc */
#define GET_INDEX(ptr) (((int)((ptr)[0]) << 8) | (ptr)[1])

extern char *Object2CString(JHandle *);

/* These need to be defined differently for some machines (Unisys) */

#ifndef METHOD_FLAG_BITS
#define METHOD_FLAG_BITS 5
#endif
#ifndef FLAG_MASK
#define FLAG_MASK       ((1<<METHOD_FLAG_BITS)-1)  /* valid flag bits */
#endif
#ifndef METHOD_MASK
#define METHOD_MASK     (~FLAG_MASK)  /* valid mtable ptr bits */
#endif
#ifndef LENGTH_MASK
#define LENGTH_MASK     METHOD_MASK
#endif
#ifndef METHODALIGNMENT
#define METHODALIGNMENT (FLAG_MASK+1)
#endif /* METHODALIGNMENT */

#define obj_flags(o) \
    (((uintptr_t) (o)->methods) & FLAG_MASK)
#define obj_length(o)   \
    (((size_t) (o)->methods) >> METHOD_FLAG_BITS)

#define mkatype(t,l) ((struct methodtable *) (((l) << METHOD_FLAG_BITS)|(t)))
#define atype(m) ((m) & FLAG_MASK)


#define obj_methodtable(obj) ((obj)->methods)
#define obj_classblock(obj) ((obj)->methods->classdescriptor)

#define obj_array_methodtable(obj) \
    ((obj_flags((obj)) == T_NORMAL_OBJECT) ? obj_methodtable((obj))      \
                                           : cbMethodTable(classJavaLangObject))
#define obj_array_classblock(obj) \
    ((obj_flags((obj)) == T_NORMAL_OBJECT) ? (obj)->methods->classdescriptor \
                                           : classJavaLangObject)

#define mt_slot(methodtable, slot) (methodtable)->methods[slot]

#define uobj_getslot(o, slot) (o)[slot]
#define uobj_setslot(o, slot, v) (uobj_getslot(o, slot) = (v))

#define obj_getslot(o, slot) uobj_getslot(unhand(o), slot)
#define obj_setslot(o, slot, v) (obj_getslot(o, slot) = (v))

#define obj_monitor(handlep) ((uintptr_t) handlep)

int PrepareToExit(void);
void Exit(int status);
int AtExit(void (*func)(void));
void Abort(void);

typedef struct {
    char *sysclasspath;
    char *library_path;
    char *dll_dir;
    char *java_home;
    char *ext_dirs;
} props_md_t;

props_md_t *GetPropertiesMD(void);

typedef union stack_item {
    /* Non pointer items */
    int            i;
    float          f;
    OBJECT         o;
    /* Pointer items */
    JHandle       *h;
    void          *p;
    unsigned char *addr;
} stack_item;

typedef struct privileged_item {
    void *frame_id;               /* frame id of doPrivileged frame */
    ClassClass *clazz;            /* class this item refers to */
    jobject context;              /* for doPrivileged(context) */
    struct privileged_item *next; /* linked list of privileged item's */
} privileged_item;

/*
 * Set up thread-local storage for second order monitor cache.  The
 * number of words of thread-local storage must be a power of 2!
 */
#define TLS_MONCACHE 8 /* must be power of 2 */
#define currentCacheKey(ee) (ee->cacheKey)
#define localMonCache(ee, hash) (ee->monitorCache[hash])

struct execenv {
    /* Stuff for the JNI: */
    struct JNIEnv_    nativeInterface;

    struct javastack  *initial_stack;
    struct javaframe  *current_frame; 
    JHandle           *thread;	    /* vague type to avoid include files */
    char              exceptionKind;
    union {
	JHandle	      *exc;	    /* holds exception object */
	unsigned char *addr;	    /* holds pc for stack overflow */
    } exception;

    /* Detecting class circularities */
    struct seenclass {
	ClassClass    *cb;
	struct seenclass *next;
    } *seenclasses;

    /* Per-thread allocation cache */
    struct alloc_cache alloc_cache;

    /* starting point of native stack GC scans */
    void *stack_base;

    /* Per-thread list of privileged blocks, points to most recent block */
    privileged_item *privileged;

    /* Keep track of the number of times JNI GetPrimitiveArrayCritical
     * functions are called.
     */
    int critical_count:        16;
    int interrupt_pending:      1;
    int async_disable_count:   15;

    JHandle *pending_async_exc;

    void *RESERVED1;
    void *RESERVED2;

    /* Thread-local monitor cache */
#ifndef FAST_MONITOR
    volatile uintptr_t cacheKey;
#else
    void*  spareMonitor;
#endif
    void * volatile monitorCache[TLS_MONCACHE];

    /* per thread profile information */
    struct profile_table * profileData; 
  
    /* For HPROF. Always compiled in to make sure the SysThread2EE
     * macro stays the same for JITs.
     */
    unsigned int RESERVED3;
#ifdef __linux__
   void* stack_top;    /* Low address on stacks that grow downwards.
			 * If $esp >= (stack_top + redzone), then there is
			 * potential C stack overflow, so a JIT doing explicit
			 * stack overflow checks must now raise the exception.
			 */
#endif 
    /* Platform-dependent thread block. See threads_md.c for details.
     * This field must be placed at the end of execenv.
     * Use double to make sure sys_thr is 8-byte aligned. */
    double sys_thr[1];
};

typedef struct execenv ExecEnv;

#define SysThread2EE(tid) \
    ((ExecEnv *)((char *)(tid) - offsetof(ExecEnv, sys_thr)))

#define EE2SysThread(ee) \
    ((sys_thread_t *)((char *)(ee) + offsetof(ExecEnv, sys_thr)))

#define PRIVILEGED_EE ((ExecEnv*)-1)

struct javastack {
    struct execenv  *execenv;	    /* execenv we belong to */
    struct javastack *prev;          /* previous stack of this execenv */
    struct javastack *next;          /* next stack of this execenv */
    stack_item      *end_data;      /* address of end of data */
    unsigned int     stack_so_far;  /* total space used by this chunk and
				     * all previous chunks. */
    stack_item       data[1];    /* actual data */
};

typedef struct javastack JavaStack;

#define JAVASTACK_CHUNK_SIZE(stack) (stack->end_data - stack->data)

#define IN_JAVASTACK(ptr,stack) \
    (((ptr) >= (stack)->data) && ((ptr) < (stack)->end_data))

struct javaframe {
    cp_item_type       *constant_pool; /* constant_pool of this method */
    unsigned char      *returnpc;      /* pc of next instruction */
    stack_item         *optop;	       /* current top of stack */
    stack_item         *vars;	       /* pointer to this frame's vars */
    struct javaframe   *prev;          /* previous java frame. */
    struct javastack   *javastack;					  
    unsigned char      *lastpc;	       /* pc of last executed instruction */
    struct methodblock *current_method;/* method currently executing */
    struct sys_mon     *monitor;       /* monitor locked by this method */
    long               profiler_info;  /* if profiling, time this method began;
					* if instruction profiling, the weight
					* of bytecodes executed. */
    stack_item ostack[1];	       /* start of this frame's stack */
};

typedef struct javaframe JavaFrame; 

int ExpandJavaStackForJNI(ExecEnv *ee,
			  JavaStack **stackP, 
			  JavaFrame **frameP,
			  int capacity);

int ExpandJavaStack(ExecEnv *ee,
		    JavaStack **stackP, 
		    JavaFrame **frameP, 
		    stack_item **optopP,
		    int args_size,
		    int nlocals, 
		    int maxstack);

/*
 * These macros are used for walking the stack and determining if
 * a frame is JITed or not.  JITed frames are special in that multiple
 * JIT frames may be represented by a single JavaFrame.  The frame_buf
 * argument is normally a pointer to a JavaFrame structure on the stack.
 * The JIT will fill this frame in for the cases were multiple JIT frames
 * are being represented by a single JavaFrame.
 */

#define IS_JIT_FRAME(frame) \
    ((frame)->current_method && \
     (frame)->current_method->fb.access & ACC_MACHINE_COMPILED && \
     (frame)->constant_pool == NULL)

#define FRAME_PREV(frame, frame_buf) \
	IS_JIT_FRAME(frame) ? \
	    CompiledFramePrev(frame, frame_buf) : frame->prev;


/*
 * Javaframe.exceptionKind is used to signal why the interpreter
 * loop was exited.
 */
#define EXCKIND_NONE            0               /* return */
#define EXCKIND_THROW		1		/* throw */

/*
 * Be sure to use these macros to access the exception structure.  Do
 * not access the fields directly.
 */
#define exceptionClear(ee)			\
    ((ee)->exceptionKind = EXCKIND_NONE);

#define exceptionOccurred(ee)			\
    ((ee)->exceptionKind != EXCKIND_NONE)

#define exceptionThrow(ee, obj)			\
    if (1) { \
        (ee)->exceptionKind = EXCKIND_THROW;	\
        (ee)->exception.exc = (obj); \
    } else (void)0

extern int nbinclasses, sizebinclasses;
extern ClassClass **binclasses;

/* stuff for dealing with handles */
#define unhand(o) ((o)->obj)

/* Get class of the caller, skip n real (non-pseudo) frames */
ClassClass * getCallerClass(ExecEnv * ee, int n);
/*
 * Get frame of the caller, skiping n real (non-pseudo) frames.  frame_buf
 * is a pointer to a temporary frame which may be in the presence of
 * a JIT (see CompiledFramePrev).  The return value may be equal to
 * frame_buf.
 */
JavaFrame * getCallerFrame(JavaFrame * frame, int n, JavaFrame * frame_buf);

long ThreadCPUTimeMillis(void); /* millisecond timer */

/* signals_md.c */

extern void InitializeSignals(void);

/* jvm.c */

void InitializeObjectHash(void);
bool_t InitializeSystemClassLoader(void);
bool_t IsTrustedClassLoader(struct Hjava_lang_ClassLoader *loader);

#define NEED_VERIFY(cond) \
    ((verifyclasses == VERIFY_ALL) || \
     ((verifyclasses == VERIFY_REMOTE) && (cond)))

/* globals.c */

ClassClass** get_binclasses(void);
int get_nbinclasses(void);

/* gc.c */

HObject *cacheAlloc(ExecEnv *ee, struct methodtable *mptr, long size);
JHandle *allocArray(ExecEnv *, int, int);
JHandle *allocObject(ExecEnv *, ClassClass *);
ClassClass *allocClass(ExecEnv *);

bool_t InitializeAlloc(long max, long min);
extern int64_t TotalObjectMemory(void);
extern int64_t FreeObjectMemory(void);
extern int64_t TotalHandleMemory(void);
extern int64_t FreeHandleMemory(void);
extern int tracegc;

extern void gc(unsigned int spaceRequested);

JHandle **newJNIWeakRef(ExecEnv *ee, JHandle *obj);
bool_t deleteJNIWeakRef(ExecEnv *ee, JHandle **handleP);

extern void RunFinalizersOnExit(void);

extern void InitializeRefs(void);	/* Reference objects */

extern void InitializeZip(void);	/* Zip file support */

void EnableGC(ExecEnv *ee);
void DisableGC(ExecEnv *ee);
bool_t GCEnabled(ExecEnv *ee);
void heap_lock(ExecEnv *ee);
void heap_unlock(ExecEnv *ee);

/* interpreter.c */

/* SignalError() -- Instantiate an object of the specified class. 
 * Indicate that that error occurred.
 */
void SignalError(struct execenv *, char *, char *);

extern bool_t UseLosslessQuickOpcodes;

void lock_code(ExecEnv * ee);
void unlock_code(ExecEnv * ee);

JavaStack *
CreateNewJavaStack(ExecEnv *ee, JavaStack *previous_stack, size_t size);

ExecEnv *NewExecEnv(void);
bool_t InitializeExecEnv(ExecEnv *ee, JHandle *thread, void *stack_base);
void DeleteExecEnv(ExecEnv *ee);
struct sys_thread;
void FreeThreadBlock(struct sys_thread *tid);

HObject *execute_java_constructor(ExecEnv *,
				 char *classname,
				 ClassClass *cb,
				 char *signature, ...);
long execute_java_static_method(ExecEnv *, ClassClass *cb,
			       char *method_name, char *signature, ...);
long execute_java_dynamic_method(ExecEnv *, HObject *obj,
				char *method_name, char *signature, ...);
     
long do_execute_java_method(ExecEnv *ee, void *obj, 
			   char *method_name, char *signature, 
			   struct methodblock *mb,
			   bool_t isStaticCall, ...);

long do_execute_java_method_vararg(ExecEnv *ee, void *obj, 
				   char *method_name, char *signature, 
				   struct methodblock *mb,
				   bool_t isStaticCall, va_list args, 
				   long *highBits, bool_t shortFloats);

bool_t isSpecialSuperCall(ClassClass *current_class, struct methodblock *mb);

bool_t InitializeInterpreter(void);
bool_t is_instance_of(JHandle * h, ClassClass *dcb, ExecEnv *ee);
bool_t is_subclass_of(ClassClass *cb, ClassClass *dcb, ExecEnv *ee);
bool_t array_is_instance_of_array_type(JHandle * h, ClassClass *cb, 
				       ExecEnv *ee);
bool_t array_type_assignable_to_array_type(ClassClass *from_elt_class,
					   int from_elt_type,
					   ClassClass *to_class, ExecEnv *ee);
bool_t ImplementsInterface(ClassClass *cb, ClassClass *icb, ExecEnv *ee);
HObject *MultiArrayAlloc(int dimensions, ClassClass *, stack_item *sizes);

bool_t ExecuteJava(unsigned char  *, ExecEnv *ee);
bool_t ExecuteJava_C(unsigned char  *, ExecEnv *ee);
extern bool_t (*pExecuteJava)(unsigned char  *, ExecEnv *ee);

/*
 * Called from ExecuteJava.
 *    -1:   rewrite signalled an error
 *     0:   rewrite went okay
 *    +1:   opcode changed underneath us.  Redo
 */

int quickFieldAccess( int opcode, unsigned char * pc, struct fieldblock *fb,
			ExecEnv *ee );
int quickStaticAccess( int opcode, unsigned char * pc, struct fieldblock *fb,
			ExecEnv *ee );
int quickInvocation( int opcode, unsigned char * pc, struct methodblock *mb,
			ExecEnv *ee );
void FixupQuickInvocation(ExecEnv * pc, JavaFrame * frame);
struct methodblock * quickSelectSuperMethod(JavaFrame * frame,
					    struct methodblock * mb);

bool_t
invokeInterfaceError(ExecEnv *ee, unsigned char *pc, 
		     ClassClass *cb, ClassClass *intf);

unsigned char *
ProcedureFindThrowTag(ExecEnv *ee, 
		      JavaFrame *frame, JHandle *object, unsigned char *pc);

void *
ResolveClassConstantFromPC(unsigned char *pc, unsigned char opcode, 
			       cp_item_type *, struct execenv *ee, unsigned mask);

unsigned char *classLoaderLink(struct Hjava_lang_ClassLoader *, char *, int);
void *dynoLink(struct methodblock *, uint32_t *info);

bool_t DisableAsyncEvents(ExecEnv *ee);
bool_t EnableAsyncEvents(ExecEnv *ee);

/* classruntime.c */
HArrayOfChar *MakeString(char *, long);

ClassClass *FindClass(struct execenv *, char *, bool_t resolve);
ClassClass *FindStickySystemClass(struct execenv *, char *, bool_t resolve);
ClassClass *FindClassFromClass(struct execenv *, char *, bool_t resolve, 
			       ClassClass *from);
ClassClass *FindClassFromClassLoader(struct execenv *ee, char *name, 
				     bool_t resolve,
				     struct Hjava_lang_ClassLoader *loader,
				     bool_t throwError);
ClassClass *FindClassFromClassLoader2(struct execenv *ee, char *name, 
				     bool_t resolve,
				     struct Hjava_lang_ClassLoader *loader,
				     bool_t throwError, HObject *pd);

void InitClass(ClassClass * cb);
void PrepareInvoker(struct methodblock *mb);
Invoker getCustomInvoker(char * sig);
bool_t invokeJavaMethod(JHandle *o, struct methodblock *mb,
			int args_size, ExecEnv *ee);
bool_t invokeSynchronizedJavaMethod(JHandle *o, struct methodblock *mb, 
				    int args_size, ExecEnv *ee);
bool_t invokeNativeMethod(JHandle *o, struct methodblock *mb, int args_size, 
			  ExecEnv *ee);
bool_t invokeSynchronizedNativeMethod(JHandle *o, struct methodblock *mb, 
				      int args_size, ExecEnv *ee);
bool_t invokeJNINativeMethod(JHandle *o, struct methodblock *mb, int args_size, 
			     ExecEnv *ee);
bool_t invokeJNISynchronizedNativeMethod(JHandle *o, struct methodblock *mb, 
					 int args_size, ExecEnv *ee);
bool_t invokeLazyNativeMethod(JHandle *o, struct methodblock *mb, int args_size, 
			      ExecEnv *ee);
bool_t invokeAbstractMethod(JHandle *o, struct methodblock *mb, int args_size, 
			    ExecEnv *ee);
bool_t invokeCompiledMethod(JHandle *o, struct methodblock *mb, int args_size, 
			    ExecEnv *ee);

#ifdef DEBUG
/*
 * This is a debugging utility that can be used just before a call to
 * validate that all the Object arguments being passed into a method are
 * really valid objects.  This can be useful for debugging in the presence
 * of a JIT.  All the invokers call this by default in the DEBUG VM.
 */
void invokerValidateArgs(stack_item * optop, struct methodblock * mb);
#endif /* DEBUG */

void * FindBuiltinEntry(const char *name);
bool_t LoadJavaLibrary(void);
bool_t RunOnLoadHook(void *h);

/* classinitialize.c */
bool_t ResolveClassConstant(cp_item_type *, unsigned index, struct execenv *ee,
			    unsigned mask);
bool_t ResolveClassConstantFromClass(ClassClass *, unsigned index, 
				     struct execenv *ee, unsigned mask);
bool_t ResolveClassConstant2(cp_item_type *, unsigned index, 
			     struct execenv *ee, unsigned mask, 
			     bool_t resolve);
bool_t ResolveClassConstantFromClass2(ClassClass *, unsigned index, 
				      struct execenv *ee, unsigned mask,
				      bool_t resolve);

bool_t VerifyClassAccess(ClassClass *, ClassClass *, bool_t);
bool_t VerifyFieldAccess(ClassClass *, ClassClass *, int, bool_t);
bool_t VerifyFieldAccess2(ClassClass *, ClassClass *, int, bool_t, bool_t);
bool_t IsSameClassPackage(ClassClass *class1, ClassClass *class2); 

char *GetClassConstantClassName(cp_item_type *constant_pool, int index);


char *pc2string(unsigned char *pc, JavaFrame * frame,
		char *buf, char *limit);
int pc2lineno(struct methodblock *, unsigned short);

int sizearray(int, int);
void printStackTrace(struct execenv *ee, int limit, void (*f)(char *, ...));

/* From check_class.c */
void VerifyClass(ClassClass *cb);

#ifdef JCOV
#include "jcov.h"
#endif /* JCOV */

/* from classload.c */
void FreeClass(ClassClass *cb);
ClassClass *LoadClassLocally(char *name);
ClassClass *createInternalClass(unsigned char *bytes, 
				unsigned char *limit,
				struct Hjava_lang_ClassLoader *,
				char *utfname, 
				char *filename);
ClassClass *createFakeArrayClass(char *name, int base_type, int depth, 
				 ClassClass *inner_cb, 
				 struct Hjava_lang_ClassLoader *);
ClassClass *createPrimitiveClass(char *name, char sig, unsigned char typecode,
    unsigned char slotsize, unsigned char elementsize);
unsigned Signature2ArgsSize(char *method_signature);
bool_t LoadZipLibrary(void);

/* from classresolver.c */
void LoadSuperclasses(ClassClass * cb);
void LinkClass(ClassClass *cb);
ClassClass *FindClass(struct execenv *ee, char *name, bool_t resolve);
ClassClass *FindClassFromClass(struct execenv *ee, char *name, 
			       bool_t resolve, ClassClass *from);
void DeleteClassFromLoaderConstraints(ClassClass *cb);
bool_t CheckSignatureLoaders(char *signature, 
			     struct Hjava_lang_ClassLoader *loader1,
			     struct Hjava_lang_ClassLoader *loader2);
char * UpdateLoaderCache(ClassClass *cb,
			 struct Hjava_lang_ClassLoader *loader,
			 char *details,
			 int len);
int EnumerateOverLoaderCache(
             int (*func)(ClassClass *, 
                         struct Hjava_lang_ClassLoader *initiatingLoader, 
                         void *),
             void *arg);

extern ClassClass *class_void;
extern ClassClass *class_boolean;
extern ClassClass *class_byte;
extern ClassClass *class_char;
extern ClassClass *class_short;
extern ClassClass *class_int;
extern ClassClass *class_long;
extern ClassClass *class_float;
extern ClassClass *class_double;

extern ClassClass *FindPrimitiveClass(char *);

/* from threads.c */
struct Hjava_lang_Thread *InitializeClassThread(ExecEnv *ee);
HArrayOfChar *getThreadName(void);

/* from exception.c */
struct Hjava_lang_Throwable;
void fillInStackTrace(struct Hjava_lang_Throwable *handle, ExecEnv *ee);

void unicode2str(unicode *, char *, long);
unicode *str2unicode(char *, unicode *, long);

enum {
    MangleMethodName_JDK_1,
    MangleMethodName_JNI_SHORT,
    MangleMethodName_JNI_LONG
};
void mangleMethodName(struct methodblock *mb, char *buffer, int buflen,
		      int mangleType);
int maxMangledMethodNameLength(struct methodblock *mb);

enum { 
    MangleUTF_Class, 
    MangleUTF_Field, 
    MangleUTF_FieldStub, 
    MangleUTF_Signature,
    MangleUTF_JNI
};
int mangleUTFString(char *name, char *buffer, int buflen, int mangleType);
int mangleUTFString2(char *name, char *buffer, int buflen, int mangleType,
		     char endChar);

ExecEnv *EE(void);

void panic (const char *, ...);

/* Stuff from compiler.c */
void InitializeForCompiler(ClassClass *cb);
void CompilerFreeClass(ClassClass *cb);
void CompilerLinkClass(ClassClass *cb);
void CompilerLoadClass(ClassClass *cb, unsigned char *data, int len);
void CompilerCompileClass(ClassClass *cb);
void ReadInCompiledCode(void *context, struct methodblock *mb, 
			int attribute_length, 
			unsigned long (*get1byte)(void * context),
			unsigned long (*get2bytes)(void * context), 
			unsigned long (*get4bytes)(void * context), 
			void (*getNbytes)(void *, size_t, char *));
extern char * CompiledCodeAttribute;

bool_t PCinCompiledCode(unsigned char *pc, struct methodblock *mb);
unsigned char *CompiledCodePC(JavaFrame *frame, struct methodblock *mb);
JavaFrame *CompiledFramePrev(JavaFrame *frame, JavaFrame *buf);
void *CompiledFrameID(JavaFrame *frame);
JavaFrame *CompiledFrameUpdate(JavaFrame *frame);
bool_t CompilerRegisterNatives(ClassClass *cb);
bool_t CompilerUnregisterNatives(ClassClass *cb);
bool_t CompiledCodeSignalHandler(int sig, void *info, void *uc);
int CompiledCodePCtoLineNo(unsigned char *pc);

/*
 * returns 0 if inline succeeded, -1 if can't be inlined, 1 if call needs
 * to happen before inlining can be determined.
 */
int MethodCallInline(unsigned char *pc, struct methodblock *sourceMethod,
		     struct methodblock *mb, unsigned char *result);

#define KEEP_POINTER_ALIVE(p) if ((p) == 0) EE()

/* javai.c:
 */
extern GetInterfaceFunc GetHPI;
extern jboolean oldjava;
int InitializeJavaVM(void *args);
int GetDefaultJavaVMInitArgs(JDK1_1InitArgs *args);
void * InitializeHPI(void *);

/* 
 * BEGIN JNI STUFF: 
 * Only the stuff used by the JNI implementation, but not by JNI 
 * programmers. jni.h contains the types and function prototypes
 * used by JNI programmers
 */

extern const struct JNINativeInterface_ unchecked_jni_NativeInterface;
extern const struct JNINativeInterface_ checked_jni_NativeInterface;
extern const struct JNINativeInterface_ *jni_NativeInterfacePtr;

bool_t isValidHandle(JHandle *h);
bool_t isObject(void * ptr);
bool_t isHandle(void * ptr);

int InitializeJNI(void);

void InitializeJNIRootFrame(ExecEnv *ee);
void DeleteJNIRootFrame(ExecEnv *ee);

#define JNI_DEFAULT_LOCAL_CAPACITY 16

/* The first three slots in a JNI frame store the pointer to the list
 * of freed local refs, the number of live local refs, and the ensured 
 * capacity of the frame.
 */

#define JNI_REF_INFO_SIZE 3

#define JNI_REFS_FREELIST(frame) (frame->ostack[0].p)
#define JNI_N_REFS_IN_USE(frame) (frame->ostack[1].i)
#define JNI_REFS_CAPACITY(frame) (frame->ostack[2].i)

extern JavaFrame *globalRefFrame;

/*
 * JNIEnv <-> ExecEnv conversion
 */

#define JNIEnv2EE(env) \
    ((ExecEnv*)((char*)(env) - offsetof(ExecEnv, nativeInterface)))

#define EE2JNIEnv(ee) ((JNIEnv *)(&(ee)->nativeInterface))
    
/* Create
 * a local reference
 */
#define MkRefLocal(env, jobj) \
    ((jobject)(jni_mkRefLocal(JNIEnv2EE(env), (JHandle *)(jobj))))

/* A function version for the JIT interface */
jobject jni_mkRefLocal(ExecEnv *ee, JHandle *jobj);

/* Deref local and global reference */
#define DeRef(env, ref) ((ref) ? *(JHandle **)(ref) : 0)

void bad_critical_count_on_return(void);

#define CHECK_CRITICAL_COUNT_ON_RETURN(ee) \
    if (ee->critical_count) \
        bad_critical_count_on_return()

ClassClass *
AddToLoadedClasses(ExecEnv *ee, ClassClass *cb);

/*
 * END JNI STUFF
 */

#endif /* !_JAVASOFT_INTERPRETER_H_ */
