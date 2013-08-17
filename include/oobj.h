/*
 * @(#)oobj.h	1.80 98/07/14
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
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

#ifndef _OOBJ_H_
#define _OOBJ_H_

#ifndef JAVA_CLASSFILE_MAGIC

#include <stddef.h>

#include "typedefs.h"
#include "debug.h"
#include "bool.h"
#include "oobj_md.h"
#include "signature.h"

#define JAVA_CLASSFILE_MAGIC	          0xCafeBabe

#define JAVASRCEXT "java"
#define JAVASRCEXTLEN 4
#define JAVAOBJEXT "class"
#define JAVAOBJEXTLEN 5

#define JAVA_VERSION     45
#define JAVA_MINOR_VERSION 3
#define ARRAYHEADER     long alloclen

#define HandleTo(T) typedef struct H##T { Class##T *obj; struct methodtable *methods;} H##T


typedef long OBJECT;
typedef OBJECT Classjava_lang_Object;
typedef OBJECT ClassObject;
HandleTo(java_lang_Object);
typedef Hjava_lang_Object JHandle;
typedef Hjava_lang_Object HObject;

typedef unsigned short unicode;

extern unicode	*str2unicode(char *, unicode *, long);
extern char	*int642CString(int64_t number, char *buf, int buflen);

#define ALIGN(n) (((n)+3)&~3)
#define UCALIGN(n) ((unsigned char *)ALIGN((int)(n)))

struct Hjava_lang_Class;	/* forward reference for some compilers */
struct Classjava_lang_Class;	/* forward reference for some compilers */

typedef struct Classjava_lang_Class Classjava_lang_Class;

HandleTo(java_lang_Class);
typedef struct Hjava_lang_Class ClassClass;


struct fieldblock {
    ClassClass *clazz;
    char *signature;
    char *name;
    unsigned long ID;
    unsigned short access;
    union {
	unsigned long offset;	/* info of data */	
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

struct methodblock {
    struct fieldblock fb;
    unsigned char       *code;	/* the code */
    struct CatchFrame   *exception_table;
    struct lineno       *line_number_table;
    struct localvar     *localvar_table;

    unsigned long        code_length;
    unsigned long        exception_table_length;
    unsigned long        line_number_table_length;
    unsigned long        localvar_table_length;

    bool_t  (*invoker)
      (JHandle *o, struct methodblock *mb, int args_size, struct execenv *ee);
    unsigned short       args_size;	/* total size of all arguments */
    unsigned short       maxstack;	/* maximum stack usage */
    unsigned short       nlocals;	/* maximum number of locals */
    /* 2 spare bytes here */
    void                *CompiledCode; /* it's type is machine dependent */
    void                *CompiledCodeInfo; /* it's type is machine dependent */
    long                 CompiledCodeFlags; /* machine dependent bits */
    unsigned long        inlining;      /* possible inlining of code */
    unsigned short       nexceptions;   /* number of checked exceptions */
    unsigned short      *exceptions;	/* constant pool indices */
#ifdef JCOV 
    struct covtable      *coverage_table;
    unsigned long        coverage_table_length;
#endif
};

struct HIOstream;

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
    char body[1];
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
    long        body[1];
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
    short                   *implements;

    struct methodtable      *methodtable;
    struct methodtable	    *methodtable_mem;
    struct fieldblock      **slottable;

    HString		    *classname;

    union {
	struct {
	    unsigned long	thishash;	  /* unused */
	    unsigned long	totalhash;	  /* unused */
	} cbhash;
	struct {
	    unsigned char	typecode;	  /* VM typecode */
	    char		typesig;	  /* signature constant */
	    unsigned char	slotsize;	  /* (bytes) in slot */
	    unsigned char	elementsize;	  /* (bytes) in array */
	    unsigned long	xxspare;
	} cbtypeinfo;
    } hashinfo;

    unsigned short           constantpool_count;  /* number of items in pool */
    unsigned short           methods_count;       /* number of methods */
    unsigned short           fields_count;        /* number of fields */
    unsigned short           implements_count;    /* number of protocols */

    unsigned short           methodtable_size;    /* the size of method table */
    unsigned short           slottbl_size;        /* size of slottable */
    unsigned short           instance_size;       /* (bytes) of an instance */

    unsigned short access;           /* how this class can be accesses */
    unsigned short flags;	     /* see the CCF_* macros */
    struct HArrayOfObject   *signers;
    struct   imethodtable   *imethodtable;

    void                    *init_thread; /* EE of initializing thread */    

    ClassClass              *last_subclass_of;
    void		    *reserved_for_jit;
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
#define cbSigners(cb)         ((unhand(cb))->signers)
#define cbSlotTable(cb)       ((unhand(cb))->slottable)
#define cbSlotTableSize(cb)   ((unhand(cb))->slottbl_size)
#define cbSourceName(cb)      ((unhand(cb))->source_name)
#define cbSuperclass(cb)      ((unhand(cb))->superclass)
#define cbSuperName(cb)       ((unhand(cb))->super_name)
#define cbThisHash(cb)        ((unhand(cb))->hashinfo.cbhash.thishash)
#define cbTotalHash(cb)       ((unhand(cb))->hashinfo.cbhash.totalhash)
#define cbInitThread(cb)      ((unhand(cb))->init_thread)

#ifdef JCOV
#define cbAbsoluteSourceName(cb) ((unhand(cb))->absolute_source_name)
#define cbTimestamp(cb)       ((unhand(cb))->timestamp)
#endif

#define cbIsInterface(cb)     ((cbAccess(cb) & ACC_INTERFACE) != 0)

/* These are currently only valid for primitive types */
#define	cbIsPrimitive(cb)      (CCIs(cb, Primitive))
#define cbTypeCode(cb)	       ((unhand(cb))->hashinfo.cbtypeinfo.typecode)
#define cbTypeSig(cb)	       ((unhand(cb))->hashinfo.cbtypeinfo.typesig)
#define cbSlotSize(cb)	       ((unhand(cb))->hashinfo.cbtypeinfo.slotsize)
#define cbElementSize(cb)      ((unhand(cb))->hashinfo.cbtypeinfo.elementsize)

extern char *classname2string(char *str, char *dst, int size);

#define twoword_static_address(fb) ((fb)->u.static_address)
#define normal_static_address(fb)  (&(fb)->u.static_value)

/* ClassClass flags */
#define CCF_IsSysLock     0x01  /* any instance treated as a "system" lock */
#define CCF_IsResolved	  0x02	/* has <clinit> been run? */
#define CCF_IsError	  0x04	/* Resolution caused an error */
#define CCF_IsSoftRef	  0x08	/* whether this is class Ref or subclass */
#define CCF_IsInitialized 0x10	/* whether this is class has been inited */
#define CCF_IsLinked      0x20	/* Has symbolic entries been linked */
#define CCF_IsVerified    0x40	/* has the verifier been run */

#define CCF_IsPrimitive   0x100	/* if pseudo-class for a primitive type */
#define CCF_IsReferenced  0x200 /* Class is in use */
#define CCF_IsSticky      0x400 /* Don't unload this class */

#define CCIs(cb,flag) (((unhand(cb))->flags & CCF_Is##flag) != 0)
#define CCSet(cb,flag) ((unhand(cb))->flags |= CCF_Is##flag)
#define CCClear(cb,flag) ((unhand(cb))->flags &= ~CCF_Is##flag)

/* map from pc to line number */
struct lineno {
    unsigned long pc, 
    line_number;
};

extern struct lineno *lntbl;
extern long lntsize, lntused;

#ifdef JCOV
struct covtable {
    unsigned long pc, 
      type,
      where,
      count;
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
    long pc0;			/* starting pc for this variable */
    long length;		/* -1 initially, end pc - pc when we're done */
    short nameoff;		/* offset into string table */
    short sigoff;		/* offset into string table */
    long slot;			/* local variable slot */
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
    long    start_pc, end_pc;	/* pc range of corresponding try block */
    long    handler_pc;	        /* pc of catch handler */
    void*   compiled_CatchFrame; /* space to be used by machine code */
    short   catchType;	        /* type of catch parameter */
};

#define MC_SUPER        (1<<5)
#define MC_NARGSMASK    (MC_SUPER-1)
#define MC_INT          (0<<6)
#define MC_FLOAT        (1<<6)
#define MC_VOID         (2<<6)
#define MC_OTHER        (3<<6)
#define MC_TYPEMASK     (3<<6)

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
    int i;
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
#define CONSTANT_POOL_TYPE_TABLE_PUT(cp,i,v) (CONSTANT_POOL_TYPE_TABLE_GET(cp,i) = (v))
#define CONSTANT_POOL_TYPE_TABLE_SET_RESOLVED(cp,i) \
	(CONSTANT_POOL_TYPE_TABLE_GET(cp,i) |= CONSTANT_POOL_ENTRY_RESOLVED)
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
#define CONSTANT_POOL_ARRAY_LENGTH 4

/* 
 * Package shorthand: this isn't obviously the correct place.
 */
#define JAVAPKG         "java/lang/"
#define JAVAIOPKG       "java/io/"
#define JAVANETPKG      "java/net/"

#endif

#endif /* !_OOBJ_H_ */
