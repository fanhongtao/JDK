/*
 * @(#)interpreter.h	1.124 99/01/22
 *
 * Copyright 1995-1999 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

/*
 * Definitions for the interperter	6/27/91
 */

#ifndef _INTERPRETER_H_
#define _INTERPRETER_H_

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "bool.h"
#include "config.h"
#include "jni.h"
#include "alloc_cache.h"

extern char *progname;
extern bool_t debugging;
extern bool_t verbose;
extern bool_t verbosegc;
extern bool_t noasyncgc;
extern bool_t classgc;

extern ClassClass *classJavaLangClass;	   /* class java/lang/Class */
extern ClassClass *classJavaLangObject;	   /* class java/lang/Object */
extern ClassClass *classJavaLangString;	   /* class java/lang/String */

extern ClassClass *classJavaLangThrowable;
extern ClassClass *classJavaLangException;
extern ClassClass *classJavaLangError;
extern ClassClass *classJavaLangRuntimeException;
extern ClassClass *classJavaLangThreadDeath;

extern ClassClass *interfaceJavaLangCloneable; /* class java/lang/Cloneable */
extern ClassClass *interfaceJavaIoSerializable; /* class java/io/Serializable */

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
      if (tracem) trace_method(ee, mb, args_size, type); else

#else
# define trace  0
# define tracem 0
# define TRACE_METHOD(ee, mb, args_size, type) 
#endif

extern char * const opnames[];

/* Get a constant pool index, from a pc */
#define GET_INDEX(ptr) (((int)((ptr)[0]) << 8) | (ptr)[1])

extern char *Object2CString(JHandle *);

#define METHOD_FLAG_BITS 5
#define FLAG_MASK       ((1<<METHOD_FLAG_BITS)-1)  /* valid flag bits */
#define METHOD_MASK     (~FLAG_MASK)  /* valid mtable ptr bits */
#define LENGTH_MASK     METHOD_MASK

#define obj_flags(o) \
    (((unsigned long) (o)->methods) & FLAG_MASK)
#define obj_length(o)   \
    (((unsigned long) (o)->methods) >> METHOD_FLAG_BITS)

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

#define obj_monitor(handlep) ((int) handlep)


struct arrayinfo {
    int index;
    char sig;      /* type signature. */
    char *name;
    int factor;
};

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

struct execenv {
    struct javastack  *initial_stack;
    struct javaframe  *current_frame; 
    JHandle           *thread;	    /* vague type to avoid include files */
    char              exceptionKind;
    union {
	JHandle	      *exc;	    /* holds exception object */
	unsigned char *addr;	    /* holds pc for stack overflow */
    } exception;

    /* Stuff for the JNI: */
    struct JNIEnv_    nativeInterface;

    /* Detecting class circularities */
    struct seenclass {
	ClassClass    *cb;
	struct seenclass *next;
    } seenclasses;

    /* Per-thread allocation cache */
    struct alloc_cache alloc_cache;

    /* error message occurred during class loading */ 
    char *class_loading_msg;
};

typedef struct execenv ExecEnv;

#define PRIVILEGED_EE ((ExecEnv*)-1)

#define JAVASTACK_CHUNK_SIZE 2000
struct javastack {
    struct execenv  *execenv;	    /* execenv we belong to */
    struct javastack *prev;          /* previous stack of this execenv */
    struct javastack *next;          /* next stack of this execenv */
    stack_item      *end_data;      /* address of end of data */
    unsigned int     stack_so_far;  /* total space used by this chunk and
				     * all previous chunks. */
    stack_item       data[JAVASTACK_CHUNK_SIZE];    /* actual data */

};

typedef struct javastack JavaStack;


struct javaframe {
    /* DANGER DANGER DANGER DANGER DANGER DANGER DANGER DANGER DANGER DANGER 
     * N. B.the first two items in structure shouldn't be needed by function
     * return or by the Garbage Collector, since they may be overwritten by
     * dreturn, lreturn, etc.
     */
    cp_item_type       *constant_pool; /* constant_pool of this method */
    unsigned char      *returnpc;      /* pc of next instruction */
    /* REGNAD REGNAD REGNAD REGNAD REGNAD REGNAD REGNAD REGNAD REGNAD REGNAD */

    stack_item         *optop;	       /* current top of stack */
    stack_item         *vars;	       /* pointer to this frame's vars */
    struct javaframe   *prev;          /* previous java frame. */
    struct javastack   *javastack;					  
    unsigned char      *lastpc;	       /* pc of last executed instruction */
    struct methodblock *current_method;/* method currently executing */
    JHandle            *monitor;       /* object locked by this method */
    int	                mon_starttime; /* time this method began */
    stack_item ostack[1];	       /* start of this frame's stack */
};

typedef struct javaframe JavaFrame; 


/*
 * Javaframe.exceptionKind is used to signal why the interpreter
 * loop was exited.
 */
#define EXCKIND_NONE            0               /* return */
#define EXCKIND_THROW		1		/* throw */
#define EXCKIND_STKOVRFLW       2               /* stack overflow */

/*
 * Be sure to use these macros to access the exception structure.  Do
 * not access the fields directly.
 */
#define exceptionClear(ee)			\
    ((ee)->exceptionKind = EXCKIND_NONE);

#define exceptionOccurred(ee)			\
    ((ee)->exceptionKind != EXCKIND_NONE)

#define exceptionThrow(ee, obj)			\
    (ee)->exceptionKind = EXCKIND_THROW;	\
    (ee)->exception.exc = (obj);

/* Macro for handling specific kinds of exceptions */
#define exceptionThrowSpecial(ee, obj, kind)	\
    (ee)->exceptionKind = kind;			\
    (ee)->exception.exc = (obj);


extern long nbinclasses, sizebinclasses;
extern ClassClass **binclasses;

/* stuff for dealing with handles */
#define unhand(o) ((o)->obj)


/* globals.c */

ClassClass** get_binclasses(void);
ClassClass* get_classClass(void);
ClassClass* get_classObject(void);
long get_nbinclasses(void);

/* gc.c */

#define MINHEAPEXPANSION (1 * 1024 * 1024)
#define MAXHEAPEXPANSION (4 * 1024 * 1024)
#define MINHEAPFREEPERCENT (float)0.25
#define MAXHEAPFREEPERCENT (float)1.00

typedef struct heapoptions {
    float minHeapFreePercent;
    float maxHeapFreePercent;
    long minHeapExpansion;
    long maxHeapExpansion;
} heapoptions;

bool_t InitializeAlloc(long max, long min);
HObject *AllocHandle(struct methodtable *, ClassObject *);
extern struct arrayinfo const arrayinfo[];
extern int64_t TotalObjectMemory(void);
extern int64_t FreeObjectMemory(void);
extern int64_t TotalHandleMemory(void);
extern int64_t FreeHandleMemory(void);
extern int tracegc;

extern void gc(int async, unsigned int spaceRequested);


/* interpreter.c */

/* SignalError() -- Instantiate an object of the specified class. 
 * Indicate that that error occurred.
 */
extern bool_t UseLosslessQuickOpcodes;
void SignalError(struct execenv *, char *, char *);

JavaStack *CreateNewJavaStack(ExecEnv *ee, JavaStack *previous_stack);

void InitializeExecEnv(ExecEnv *ee, JHandle *thread);
void DeleteExecEnv(ExecEnv *ee);
extern ExecEnv *DefaultExecEnv;


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

long now(void);
bool_t isSpecialSuperCall(ClassClass *current_class, struct methodblock *mb);

void InitializeInterpreter(void);
bool_t is_instance_of(JHandle * h, ClassClass *dcb, ExecEnv *ee);
bool_t is_subclass_of(ClassClass *cb, ClassClass *dcb, ExecEnv *ee);
bool_t array_is_instance_of_array_type(JHandle * h, ClassClass *cb, 
				       ExecEnv *ee);
bool_t ImplementsInterface(ClassClass *cb, ClassClass *icb, ExecEnv *ee);
HObject *MultiArrayAlloc(int dimensions, ClassClass *, stack_item *sizes);
bool_t ExecuteJava(unsigned char  *, ExecEnv *ee);

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
void FixupQuickInvocation(unsigned char *pc, 
			  struct methodblock *currentMethod,
			  struct methodblock *targetMethod);

bool_t
invokeInterfaceError(ExecEnv *ee, unsigned char *pc, 
		     ClassClass *cb, ClassClass *intf);

unsigned char *
ProcedureFindThrowTag(ExecEnv *ee, 
		      JavaFrame *frame, JHandle *object, unsigned char *pc);

void *
ResolveClassConstantFromPC(unsigned char *pc, unsigned char opcode, 
			       cp_item_type *, struct execenv *ee, unsigned mask);



struct stat;

bool_t dynoLink(struct methodblock *);
bool_t dynoLinkJNI(struct methodblock *);
char *str2rd(char *);
char *unicode2rd(unicode *, long);

/* classruntime.c */
HArrayOfChar *MakeString(char *, long);

ClassClass *FindClass(struct execenv *, char *, bool_t resolve);
ClassClass *FindStickySystemClass(struct execenv *, char *, bool_t resolve);
ClassClass *FindClassFromClass(struct execenv *, char *, bool_t resolve, ClassClass *from);
bool_t RunStaticInitializers(ClassClass *cb);
void InitializeInvoker(ClassClass *cb);

bool_t
invokeJavaMethod(JHandle *o, struct methodblock *mb, int args_size, ExecEnv *ee);
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


void LoadClassConstants(ClassClass *cb);
bool_t ResolveClassStringConstant(ClassClass *, unsigned, struct execenv *);
bool_t ResolveClassConstant(cp_item_type *, unsigned index, struct execenv *ee,
			    unsigned mask);
bool_t ResolveClassConstantFromClass(ClassClass *, unsigned index, 
				     struct execenv *ee, unsigned mask);

void InitializeClassConstantResolver();

bool_t VerifyClassAccess(ClassClass *, ClassClass *, bool_t);
bool_t VerifyFieldAccess(ClassClass *, ClassClass *, int, bool_t);
bool_t IsSameClassPackage(ClassClass *class1, ClassClass *class2); 

char *GetClassConstantClassName(cp_item_type *constant_pool, int index);
unsigned NameAndTypeToHash(char *name, char *type);
HObject *newobject(ClassClass *cb, unsigned char *pc, struct execenv *ee);


char *pc2string(unsigned char *pc, struct methodblock *mb, char *buf, char *limit);

JHandle *ArrayAlloc(int, int);
JHandle *ObjAlloc(ClassClass *, long);
int sizearray(int, int);
extern char *remote_classname(JHandle *);
extern JHandle *remote_clone(struct execenv *);
extern long remote_cast(JHandle *, ClassClass *);
int pc2lineno(struct methodblock *, unsigned int);

/* From verify_class.c */
bool_t VerifyClass(ClassClass *cb);
bool_t IsLegalClassname(char *name, bool_t allowArrayClass);

/* From verify_code.c */
bool_t verify_class_codes(ClassClass *cb);


/* from profiler.c */
extern int java_monitor;
void javamon(int i);
void java_mon(struct methodblock *caller, struct methodblock *callee, int time);
void java_mon_dump(void);

#ifdef JCOV
#include "jcov.h"
#endif /* JCOV */

/* from classloader.c */
void FreeClass(ClassClass *cb);
void AddBinClass(ClassClass * cb);
void DelBinClass(ClassClass * cb);
ClassClass *LoadClassLocally(char *name);
bool_t createInternalClass(unsigned char *bytes, unsigned char *limit,
                           ClassClass *cb, struct Hjava_lang_ClassLoader *,
                            char *utfname, char **detail);
ClassClass *createFakeArrayClass(char *name, int base_type, int depth, 
				 ClassClass *inner_cb, 
				 struct Hjava_lang_ClassLoader *);
ClassClass *createPrimitiveClass(char *name, char sig, unsigned char typecode,
    unsigned char slotsize, unsigned char elementsize);
unsigned Signature2ArgsSize(char *method_signature);

typedef struct {
    unsigned char *class_data;
    int            class_data_len;
    unsigned char *new_class_data;
    int            new_class_data_len;
    void *       (*malloc_f)(int);
} classload_event;

typedef void (*classload_hook)(classload_event *);

/* from classresolver.c */
char *LinkClass(ClassClass *cb, char **detail);
char *InitializeClass(ClassClass * cb, char **detail);
char *ResolveClass(ClassClass * cb, char **detail);
ClassClass *FindClass(struct execenv *ee, char *name, bool_t resolve);
ClassClass *FindClassFromClass(struct execenv *ee, char *name, 
			       bool_t resolve, ClassClass *from);
ClassClass *ClassLoaderFindClass(ExecEnv *ee, 
				 struct Hjava_lang_ClassLoader *loader, 
				 char *name, bool_t resolve);

int  makeslottable(ClassClass * clb);
void lock_classes(void);
void unlock_classes(void);

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

/* from path_md.c */
char **CLASSPATH(void);

/* from threadruntime.c */
struct Hjava_lang_Thread *InitializeClassThread(ExecEnv *ee, char **errmsg);
void InitializeMainThread(void);
long *getclassvariable(ClassClass *cb, char *fname);
struct Hjava_lang_Thread;
char *thread_name(struct Hjava_lang_Thread *tid); 

void setThreadName(struct Hjava_lang_Thread *ht, HArrayOfChar *newName);
HArrayOfChar *getThreadName(void);

/* from exception.c */
struct Hjava_lang_Throwable;
void fillInStackTrace(struct Hjava_lang_Throwable *handle, ExecEnv *ee);

/* from CompSupport.c */
long CallInterpreted(register struct methodblock * mb, void *obj,...);

/* used to indicate of an object or remote or local */
extern struct methodtable *remote_methodtable;

void unicode2str(unicode *, char *, long);
unicode *str2unicode(char *, unicode *, long);

enum {
    MangleMethodName_JDK_1,
    MangleMethodName_JNI_SHORT,
    MangleMethodName_JNI_LONG
};
void mangleMethodName(struct methodblock *mb, char *buffer, int buflen,
		      int mangleType);

enum { 
    MangleUTF_Class, 
    MangleUTF_Field, 
    MangleUTF_FieldStub, 
    MangleUTF_Signature,
    MangleUTF_JNI
};
int mangleUTFString(char *name, char *buffer, int buflen, int mangleType);

/* string hash support */
struct StrIDhash;
unsigned short Str2ID(struct StrIDhash **, char *, void ***, int);
char *ID2Str(struct StrIDhash *, unsigned short, void ***);
void Str2IDFree(struct StrIDhash **);
void Str2IDCallback(struct StrIDhash **hash_ptr, void (*)(char *, void *));
ExecEnv *EE(void);

/* Miscellaneous functions in util.c */
char *unicode2rd(unicode *s, long len);
void out_of_memory(void);
void prints(char *s);
void printus(unicode *str, long len);
int jio_snprintf(char *str, size_t count, const char *fmt, ...);
int jio_vsnprintf(char *str, size_t count, const char *fmt, va_list args);

int jio_printf(const char *fmt, ...);
int jio_fprintf(FILE *, const char *fmt, ...);
int jio_vfprintf(FILE *, const char *fmt, va_list args);


/* allows you to override panic & oom "functionality" */
typedef void (*PanicHook)(const char* panicString);
typedef void (*OutOfMemoryHook) ();

extern PanicHook panic_hook;
extern OutOfMemoryHook out_of_memory_hook;

/* Stuff from compiler.c */

void InitializeForCompiler(ClassClass *cb);
void CompilerFreeClass(ClassClass *cb);
void CompilerCompileClass(ClassClass *cb);
void ReadInCompiledCode(void *context, struct methodblock *mb, 
			int attribute_length, 
			unsigned long (*get1byte)(),
			unsigned long (*get2bytes)(), 
			unsigned long (*get4bytes)(), 
			void (*getNbytes)());

bool_t PCinCompiledCode(unsigned char *pc, struct methodblock *mb);
unsigned char *CompiledCodePC(JavaFrame *frame, struct methodblock *mb);
JavaFrame *CompiledFramePrev(JavaFrame *frame, JavaFrame *buf);


/* Stuff from simplify.c */
bool_t MethodCallInline(unsigned char *pc, struct methodblock *sourceMethod,
			struct methodblock *mb, unsigned char *result);

#define KEEP_POINTER_ALIVE(p) if ((p) == 0) EE()
/* 
 * BEGIN JNI STUFF: 
 * Only the stuff used by the JNI implementation, but not by JNI 
 * programmers. jni.h contains the types and function prototypes
 * used by JNI programmers
 */

int InitializeJNI();

void InitializeJNIRootFrame(ExecEnv *ee);
void DeleteJNIRootFrame(ExecEnv *ee);

/*
 * Reference tables
 */

#define JNI_REF_COUNT_MASK 0x1fffffff

#define JNI_REF_TAG_MASK 0x60000000

#define JNI_REF_HANDLE_TAG 0x00000000

#define JNI_REF_FB_TAG 0x20000000

#define JNI_REF_MB_TAG 0x40000000

typedef struct JNIRefCell {
    uint32_t refCount;
    void *content;
} JNIRefCell;

typedef struct JNIRefTable {
    JNIRefCell *elements;
    int base;
    int top;
    int size;
} JNIRefTable;

extern jref jni_AddRefCell(JNIRefTable *table, void *entry, int kind);
extern JNIRefTable globalRefTable;

/*
 * JNIEnv <-> ExecEnv conversion
 */

#define JNIEnv2EE(env) \
    ((ExecEnv*)((char*)(env) - offsetof(ExecEnv, nativeInterface)))

#define EE2JNIEnv(ee) ((JNIEnv *)(&(ee)->nativeInterface))

/*
 * References
 * The local reference table is stored in field reserved1.
 */

#define JNIEnvGetLocalRefs(env) \
    ((JNIRefTable *)(((struct JNIEnv_ *)(env))->reserved1))

#define MkRefLocal(env, jobj, tag) \
    ((jref)(jni_AddRefCell(JNIEnvGetLocalRefs(env), (void *)(jobj), tag)))

#define DeRef(env, ref) \
    ((int)(ref) > 0 ? \
         JNIEnvGetLocalRefs(env)->elements[(int)(ref) - 1].content \
       : ((ref) ? \
              globalRefTable.elements[-((int)(ref)) - 1].content \
            : 0))

/* 
 * JavaVM <-> main EE conversion
 * The main EE is stored in field reserved0.
 */
#define JavaVMGetEE(vm) ((ExecEnv *)(((struct JavaVM_ *)vm)->reserved0))

/* used in java_main */
struct methodblock *
JNI_FindMainMethod(ClassClass *cb, char **error_message_p);
/*
 * END JNI STUFF
 */

#endif /* ! _INTERPRETER_H_ */

