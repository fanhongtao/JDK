/*
 * @(#)oobj.h	1.112 98/10/02
 *
 * Copyright 1994-1998 by Sun Microsystems, Inc.,
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
 * Java object header format
 */

#ifndef _JAVASOFT_OOBJ_H_
#define _JAVASOFT_OOBJ_H_

#ifndef JAVA_CLASSFILE_MAGIC

#include <stddef.h>

#include "typedefs.h"
#include "debug.h"
#include "bool.h"
#include "signature.h"
#include "util.h"

#define JAVA_CLASSFILE_MAGIC	          0xCafeBabe

#define JAVASRCEXT "java"
#define JAVASRCEXTLEN 4
#define JAVAOBJEXT "class"
#define JAVAOBJEXTLEN 5

#define JAVA_VERSION     46
#define JAVA_MINOR_VERSION 0

#define HandleTo(T) typedef struct H##T { Class##T *obj; struct methodtable *methods;} H##T


typedef unsigned long OBJECT;
typedef OBJECT Classjava_lang_Object;
typedef OBJECT ClassObject;
HandleTo(java_lang_Object);
typedef Hjava_lang_Object JHandle;
typedef Hjava_lang_Object HObject;

typedef unsigned short unicode;

extern unicode	*str2unicode(char *, unicode *, long);
extern char	*int642CString(int64_t number, char *buf, int buflen);

#define UCALIGN(n) ((unsigned char *)ALIGN_UP((intptr_t)(n),sizeof(int)))

struct Hjava_lang_Class;	/* forward reference for some compilers */
struct Classjava_lang_Class;	/* forward reference for some compilers */

typedef struct Classjava_lang_Class Classjava_lang_Class;

HandleTo(java_lang_Class);
typedef struct Hjava_lang_Class ClassClass;


struct fieldblock {
    ClassClass *clazz;
    char *signature;
    char *name;
    unsigned short access;
    /* two bytes wasted here */
    union {
	unsigned int offset;	/* info of data */	
	OBJECT static_value;
	void *static_address;
    } u;
};

#define fieldname(fb)    ((fb)->name)
#define fieldsig(fb)     ((fb)->signature)
#define fieldIsArray(fb) (fieldsig(fb)[0] == SIGNATURE_ARRAY)
#define fieldIsClass(fb) (fieldsig(fb)[0] == SIGNATURE_CLASS)
#define	fieldclass(fb)   ((fb)->clazz)

struct execenv;
struct methodblock;

typedef bool_t (*Invoker)(JHandle *, struct methodblock *, int, struct execenv *);

struct methodblock {
    struct fieldblock fb;
    /* For efficient JNI calls. */
    char                *terse_signature;

    unsigned char       *code;	/* the code */
    struct CatchFrame   *exception_table;
    struct lineno       *line_number_table;
    struct localvar     *localvar_table;

    unsigned short       code_length;
    unsigned short       exception_table_length;
    uint32_t             line_number_table_length;
    uint32_t             localvar_table_length;

    Invoker              invoker;

    unsigned short       args_size;	/* total size of all arguments */
    unsigned short       maxstack;	/* maximum stack usage */
    unsigned short       nlocals;	/* maximum number of locals */
    unsigned short       nexceptions;   /* number of checked exceptions */

    unsigned short      *exceptions;	/* constant pool indices */
    void                *CompiledCode; /* it's type is machine dependent */
    void                *CompiledCodeInfo; /* it's type is machine dependent */
    long                 CompiledCodeFlags; /* machine dependent bits */
    unsigned long        inlining;      /* possible inlining of code */
#ifdef JCOV 
    struct covtable      *coverage_table;
    unsigned long        coverage_table_length;
#endif
};

#define methodTerseSig(mb) ((mb)->terse_signature)

struct methodtable {
    ClassClass *classdescriptor;
    struct methodblock *methods[1];
};

struct imethodtable { 
    int icount;			/* number of interfaces to follow */
    struct { 
	ClassClass *classdescriptor;
	unsigned long *offsets;	/* info of data */	
    } itable[1];
};

typedef struct {
    int8_t body[1];
} ArrayOfByte;
typedef ArrayOfByte ClassArrayOfByte;
HandleTo(ArrayOfByte);

typedef struct {
    unicode body[1];
} ArrayOfChar;
typedef ArrayOfChar ClassArrayOfChar;
HandleTo(ArrayOfChar);

typedef struct {
    signed short body[1];
} ArrayOfShort;
typedef ArrayOfShort ClassArrayOfShort;
HandleTo(ArrayOfShort);

typedef struct {
    int32_t        body[1];
} ArrayOfInt;
typedef ArrayOfInt ClassArrayOfInt;
HandleTo(ArrayOfInt);

typedef struct {
    int64_t        body[1];
} ArrayOfLong;
typedef ArrayOfLong ClassArrayOfLong;
HandleTo(ArrayOfLong);

typedef struct {
    float       body[1];
} ArrayOfFloat;
typedef ArrayOfFloat ClassArrayOfFloat;
HandleTo(ArrayOfFloat);

typedef struct {
    double       body[1];
} ArrayOfDouble;
typedef ArrayOfDouble ClassArrayOfDouble;
HandleTo(ArrayOfDouble);

typedef struct {
    JHandle *(body[1]);
} ArrayOfArray;
typedef ArrayOfArray ClassArrayOfArray;
HandleTo(ArrayOfArray);

typedef struct {
    HObject *(body[1]);
} ArrayOfObject;
typedef ArrayOfObject ClassArrayOfObject;
HandleTo(ArrayOfObject);

typedef struct Hjava_lang_String HString;

typedef struct {
    HString  *(body[1]);
} ArrayOfString;
typedef ArrayOfString ClassArrayOfString;
HandleTo(ArrayOfString);

/* Note: any handles in this structure must also have explicit
   code in the ScanClasses() routine of the garbage collector
   to mark the handle. */
struct Classjava_lang_Class {
    /* Things following here are saved in the .class file */
    unsigned short	     major_version;
    unsigned short	     minor_version;
    char                    *name;
    char                    *super_name;
    char                    *source_name;
    ClassClass              *superclass;
    ClassClass              *HandleToSelf;
    struct Hjava_lang_ClassLoader *loader;
    struct methodblock	    *finalizer;

    union cp_item_type      *constantpool;
    struct methodblock      *methods;
    struct fieldblock       *fields;
    unsigned short          *implements;

    struct methodtable      *methodtable;
    struct methodtable	    *methodtable_mem;

    struct methodblock      *miranda_methods;

    HString		    *classname;

    struct {
	unsigned char	typecode;	  /* VM typecode */
	char		typesig;	  /* signature constant */
	unsigned char	slotsize;	  /* (bytes) in slot */
	unsigned char	elementsize;	  /* (bytes) in array */
    } cbtypeinfo;
    unsigned long	     UNUSED3;	  /* unused */

    unsigned short           constantpool_count;  /* number of items in pool */
    unsigned short           methods_count;       /* number of methods */
    unsigned short           fields_count;        /* number of fields */
    unsigned short           implements_count;    /* number of protocols */

    unsigned short           methodtable_size;    /* size of method table */
    unsigned short           instance_size;       /* (bytes) of an instance */

    unsigned short access;           /* how this class can be accesses */
    unsigned short flags;	     /* see the CCF_* macros */
    struct HArrayOfObject   *signers;
    HObject                 *protection_domain; /* ProtectionDomain */
    struct   imethodtable   *imethodtable;
    void                    *init_thread; /* EE of initializing thread */
    unsigned short          *object_offsets; /* offsets of objects in this 
						class */
    struct {				/* allocated by classload */
	unsigned char	    *clinit;	/* <clinit> */
	unsigned char	    *main;	/* other stuff, incl. constant pool */
    } classload_space;

    unsigned short          n_miranda_methods;
    unsigned short          UNUSED1;
    void                    *reserved_for_jit;
    ClassClass		    *last_subclass_of;

    unsigned short innerclass_count; /* # records in InnerClasses attribute */
    struct innerclass_info {
	unsigned short inner_class_info_index;
	unsigned short outer_class_info_index;
	unsigned short inner_name_index;
	unsigned short inner_class_access_flags;
    } *innerclasses;

    void                    *UNUSED2;

#ifdef JCOV
    char                    *absolute_source_name;
    int64_t       	     timestamp;
#endif

};

extern void FreeClass(ClassClass *cb);
extern void MakeClassSticky(ClassClass *cb);

#define cbAccess(cb)          ((unhand(cb))->access)
#define cbClassname(cb)       ((unhand(cb))->classname)
#define cbConstantPool(cb)    ((unhand(cb))->constantpool)
#define cbConstantPoolCount(cb) ((unhand(cb))->constantpool_count)
#define	cbFields(cb)          ((unhand(cb))->fields)
#define	cbFieldsCount(cb)     ((unhand(cb))->fields_count)
#define cbFinalizer(cb)       ((unhand(cb))->finalizer)
#define cbFlags(cb)           ((unhand(cb))->flags)
#define cbHandle(cb)          (cb)
#define cbImplements(cb)      ((unhand(cb))->implements)
#define cbImplementsCount(cb) ((unhand(cb))->implements_count)
#define cbInstanceSize(cb)    ((unhand(cb))->instance_size)
#define cbIntfMethodTable(cb) ((unhand(cb))->imethodtable)
#define cbLastSubclassOf(cb)  ((unhand(cb))->last_subclass_of)
#define	cbLoader(cb)	      ((unhand(cb))->loader)
#define cbMajorVersion(cb)    ((unhand(cb))->major_version)
#define	cbMethods(cb)         ((unhand(cb))->methods)
#define	cbMethodsCount(cb)    ((unhand(cb))->methods_count)
#define cbMethodTable(cb)     ((unhand(cb))->methodtable)
#define cbMethodTableMem(cb)  ((unhand(cb))->methodtable_mem)
#define cbMethodTableSize(cb) ((unhand(cb))->methodtable_size)
#define cbMinorVersion(cb)    ((unhand(cb))->minor_version)
#define cbName(cb)            ((unhand(cb))->name)
#define cbProtectionDomain(cb)         ((unhand(cb))->protection_domain)
#define cbSigners(cb)         ((unhand(cb))->signers)
#define cbSourceName(cb)      ((unhand(cb))->source_name)
#define cbSuperclass(cb)      ((unhand(cb))->superclass)
#define cbSuperName(cb)       ((unhand(cb))->super_name)
#define cbInitThread(cb)      ((unhand(cb))->init_thread)
#define cbObjectOffsets(cb)   ((unhand(cb))->object_offsets)
#define cbThisHash(cb)        ((unhand(cb))->hashinfo.cbhash.thishash)
#define cbTotalHash(cb)       ((unhand(cb))->hashinfo.cbhash.totalhash)
#define cbClinitSpace(cb)     ((unhand(cb))->classload_space.clinit)
#define cbMainSpace(cb)       ((unhand(cb))->classload_space.main)
#define cbConstraintsCapacity(cb)  ((unhand(cb))->constraints_capacity)
#define cbConstraintsCount(cb)     ((unhand(cb))->constraints_count)
#define cbConstraints(cb)          ((unhand(cb))->constraints)
#define cbMirandaMethods(cb)       ((unhand(cb))->miranda_methods)
#define cbMirandaMethodsCount(cb)  ((unhand(cb))->n_miranda_methods)
#define cbInnerClassesCount(cb)    ((unhand(cb))->innerclass_count)
#define cbInnerClasses(cb)         ((unhand(cb))->innerclasses)

#ifdef JCOV
#define cbAbsoluteSourceName(cb) ((unhand(cb))->absolute_source_name)
#define cbTimestamp(cb)       ((unhand(cb))->timestamp)
#endif

#define cbIsInterface(cb)     ((cbAccess(cb) & ACC_INTERFACE) != 0)

/* These are currently only valid for primitive types */
#define	cbIsPrimitive(cb)      (CCIs(cb, Primitive))
#define cbTypeCode(cb)	       ((unhand(cb))->cbtypeinfo.typecode)
#define cbTypeSig(cb)	       ((unhand(cb))->cbtypeinfo.typesig)
#define cbSlotSize(cb)	       ((unhand(cb))->cbtypeinfo.slotsize)
#define cbElementSize(cb)      ((unhand(cb))->cbtypeinfo.elementsize)

extern char *classname2string(char *str, char *dst, int size);

#define twoword_static_address(fb) ((fb)->u.static_address)
#define normal_static_address(fb)  (&(fb)->u.static_value)

/* ClassClass flags */
#define CCF_IsLinked	   0x02	/* been linked yet? */
#define CCF_IsError	   0x04	/* <clinit> caused an error */
#define CCF_IsReference	   0x08	/* this is class Reference or a subclass */
#define CCF_IsInitialized  0x10	/* whether the class is initialized */
#define CCF_IsLoaded       0x20	/* Is the class loaded (but not necessary
				   linked and initialized) */
#define CCF_IsVerified     0x40	/* has the verifier run on this class */

#define CCF_IsPrimitive   0x100	/* if pseudo-class for a primitive type */

#define CCF_IsSticky      0x400 /* Don't unload this class */

#define CCF_IsFieldPrepared       0x800 /* Are fields prepared */
#define CCF_IsMethodPrepared     0x1000 /* Are methods prepared */
#define CCF_IsInterfacePrepared  0x2000 /* Are interfaces prepared */

#define CCF_IsCached             0x4000 /* cached in the per-loader table */

#define CCIs(cb,flag) (((unhand(cb))->flags & CCF_Is##flag) != 0)
#define CCSet(cb,flag) \
if (1) { \
  sysStoreBarrier(); \
  (unhand(cb))->flags |= CCF_Is##flag; \
} else ((void) 0)

#define CCClear(cb,flag) \
if (1) { \
  sysStoreBarrier(); \
  (unhand(cb))->flags &= ~CCF_Is##flag; \
} else ((void) 0)


/* map from pc to line number */
struct lineno {
    unsigned short pc; 
    unsigned short line_number;
};

#ifdef JCOV
/* Jcov table entry for profiled item. 
 */
struct covtable {
    unsigned short pc; 		/* starting pc for this item */
    unsigned long type;		/* item type */
    unsigned long where_line;	/* line in source file */
    unsigned long where_pos;	/* position in source file */
    unsigned long count;	/* execution counter */
};
#endif

/* Symbol table entry for local variables and parameters.
   pc0/length defines the range that the variable is valid, slot
   is the position in the local variable array in ExecEnv.
   nameoff and sigoff are offsets into the string table for the
   variable name and type signature.  A variable is defined with
   DefineVariable, and at that time, the node for that name is
   stored in the localvar entry.  When code generate is completed
   for a particular scope, a second pass it made to replace the
   src node entry with the correct length. */

struct localvar {
    unsigned short pc0;		/* starting pc for this variable */
    unsigned short length;	/*  */
    unsigned short nameoff;	/* offset into string table */
    unsigned short sigoff;	/* offset into string table */
    unsigned short slot;	/* local variable slot */
};

/* Try/catch is implemented as follows.  On a per class basis,
   there is a catch frame handler (below) for each catch frame
   that appears in the source.  It contains the pc range of the
   corresponding try body, a pc to jump to in the event that that
   handler is chosen, and a catchType which must match the object
   being thrown if that catch handler is to be taken.

   The list of catch frames are sorted by pc.  If one range is
   inside another, then outer most range (the one that encompasses
   the other) appears last in the list.  Therefore, it is possible
   to search forward, and the first one that matches is the
   innermost one.

   Methods with catch handlers will layout the code without the
   catch frames.  After all the code is generated, the catch
   clauses are generated and table entries are created.

   When the class is complete, the table entries are dumped along
   with the rest of the class. */

struct CatchFrame {
    unsigned short  start_pc, end_pc;/* pc range of corresponding try block */
    unsigned short  handler_pc;	/* pc of catch handler */
    void*   compiled_CatchFrame; /* space to be used by machine code */
    unsigned short   catchType;	        /* type of catch parameter */
};

enum {
    CONSTANT_Utf8 = 1,
    CONSTANT_Unicode,		/* unused */
    CONSTANT_Integer,
    CONSTANT_Float,
    CONSTANT_Long,      
    CONSTANT_Double,
    CONSTANT_Class,
    CONSTANT_String,
    CONSTANT_Fieldref,
    CONSTANT_Methodref,
    CONSTANT_InterfaceMethodref,
    CONSTANT_NameAndType
};

union cp_item_type {
    unsigned int i;
    float f;
    char *cp;
    unsigned char *type;		/* for type table */
    ClassClass *clazz;
    struct methodblock *mb;
    struct fieldblock *fb;
    struct Hjava_lang_String *str;
    void *p;			        /* for very rare occassions */
};

typedef union cp_item_type cp_item_type;

#define CONSTANT_POOL_ENTRY_RESOLVED 0x80
#define CONSTANT_POOL_ENTRY_TYPEMASK 0x7F
#define CONSTANT_POOL_TYPE_TABLE_GET(cp,i) (((unsigned char *)(cp))[i])
#define CONSTANT_POOL_TYPE_TABLE_PUT(cp,i,v) \
if (1) { \
    sysAssert(cp != 0 && i >=0 && i <= 0xFFFF); \
    sysStoreBarrier(); \
    CONSTANT_POOL_TYPE_TABLE_GET(cp,i) = (v); \
} else ((void) 0)

#define CONSTANT_POOL_TYPE_TABLE_SET_RESOLVED(cp,i) \
if (1) { \
    sysAssert(cp != 0 && i >=0 && i <= 0xFFFF); \
    sysStoreBarrier(); \
    CONSTANT_POOL_TYPE_TABLE_GET(cp,i) |= CONSTANT_POOL_ENTRY_RESOLVED; \
} else ((void) 0)

#define CONSTANT_POOL_TYPE_TABLE_IS_RESOLVED(cp,i) \
	((CONSTANT_POOL_TYPE_TABLE_GET(cp,i) & CONSTANT_POOL_ENTRY_RESOLVED) != 0)
#define CONSTANT_POOL_TYPE_TABLE_GET_TYPE(cp,i) \
        (CONSTANT_POOL_TYPE_TABLE_GET(cp,i) & CONSTANT_POOL_ENTRY_TYPEMASK)

#define CONSTANT_POOL_TYPE_TABLE_INDEX 0
#define CONSTANT_POOL_UNUSED_INDEX 1

/* The following are used by the constant pool of "array" classes. */

#define CONSTANT_POOL_ARRAY_DEPTH_INDEX 1
#define CONSTANT_POOL_ARRAY_TYPE_INDEX 2
#define CONSTANT_POOL_ARRAY_CLASS_INDEX 3
#define CONSTANT_POOL_ARRAY_CLASSNAME_INDEX 4
#define CONSTANT_POOL_ARRAY_SUPERNAME_INDEX 5
#define CONSTANT_POOL_ARRAY_LENGTH 6

/* 
 * Package shorthand: this isn't obviously the correct place.
 */
#define JAVAPKG         "java/lang/"
#define JAVAIOPKG       "java/io/"
#define JAVANETPKG      "java/net/"

#endif

#endif /* !_JAVASOFT_OOBJ_H_ */
