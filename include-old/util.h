/*
 * @(#)util.h	1.5 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

#ifndef _JAVASOFT__JAVASOFT_UTIL_h__
#define _JAVASOFT__JAVASOFT_UTIL_h__

#include <stdio.h>
#include <stdarg.h> 
#include "miscdefs_md.h"
#include "bool.h" 

struct execenv;
struct Hjava_lang_Object;
struct Hjava_lang_Class;


void lock_utf8_hash(struct execenv *ee);
void unlock_utf8_hash(struct execenv *ee);

/* global UTF-8 pool */
extern char *utf8_literal_obj_init_name;  /* "<init>" */
extern char *utf8_literal_cls_init_name;  /* "<clinit>" */
extern char *utf8_literal_null_init_sig;  /* "()V" */
extern char *utf8_literal_java_lang_ref_Reference;
extern char *utf8_literal_finalizer_name;
extern char *utf8_literal_finalizer_sig;
extern char *utf8_literal_java_lang_Class;
extern char *utf8_literal_java_lang_Object;
extern char *utf8_literal_LineNumberTable;
extern char *utf8_literal_CoverageTable;
extern char *utf8_literal_LocalVariableTable;
extern char *utf8_literal_ConstantValue;
extern char *utf8_literal_Code;
extern char *utf8_literal_Exceptions;
extern char *utf8_literal_SourceFile;
extern char *utf8_literal_InnerClasses;
extern char *utf8_literal_AbsoluteSourcePath;
extern char *utf8_literal_TimeStamp;

typedef struct {
    char *name;
    char *signature;
} HashedNameAndType;

void HashNameAndType(struct execenv * ee,
		     const char *name, 
		     const char *signature,
		     HashedNameAndType *hashed);

#define NAMETYPE_MATCH(hashed, fb) \
    (((hashed)->name == (fb)->name) && \
     ((hashed)->signature == (fb)->signature))

char *AddUTF8(struct execenv * ee, const char *str);
void ReleaseUTF8(struct execenv * ee, const char *str);
char *HashUTF8(struct execenv * ee, const char *str);

struct Hjava_lang_Class * getClass(struct Hjava_lang_Object *p);

#ifdef HPROF

bool_t jvmpi_utf8_marked(const char *str);
bool_t jvmpi_utf8_mark(const char *str);

#endif /* HPROF */

/* allows you to override panic & oom "functionality" */
typedef void (*PanicHook)(const char* panicString);
typedef void (*OutOfMemoryHook) (void);

extern PanicHook panic_hook;
extern OutOfMemoryHook out_of_memory_hook;

/*
 * The __attribute__ statements allow gcc to typecheck the arguments
 * to jio_* function with the format string.  The warnings it might
 * generate aren't guaranteed to be correct because I'm not sure that
 * jio_vsnprintf is exactly the same as sprintf and friends.
 */


void out_of_memory(void);
int jio_snprintf(char *str, size_t count, const char *fmt, ...)
#ifdef __GNUC__
__attribute ((format (printf, 3, 4)))
#endif
;
int jio_vsnprintf(char *str, size_t count, const char *fmt, va_list args);

int jio_printf(const char *fmt, ...)
#ifdef __GNUC__
__attribute ((format (printf, 1, 2)))
#endif
;
int jio_fprintf(FILE *, const char *fmt, ...)
#ifdef __GNUC__
__attribute ((format (printf, 2, 3)))
#endif
;
int jio_vfprintf(FILE *, const char *fmt, va_list args);

/*
 * To override these byte alignment macros, define platform
 * specific versions in miscdefs_md.h on your platform.
 */
#ifndef ALIGN_UP
#define ALIGN_UP(n,align_grain) (((n) + ((align_grain) - 1)) & ~((align_grain)-1))
#endif /* ALIGN_UP */

#ifndef ALIGN_DOWN
#define ALIGN_DOWN(value, align_grain) ((value) & ~((align_grain) - 1))
#endif /* ALIGN_DOWN */

#ifndef IS_ALIGNED
#define IS_ALIGNED(value, align_grain) (((value) & ((align_grain) - 1)) == 0)
#endif /* IS_ALIGNED */

#ifndef ALIGN_NATURAL
#define ALIGN_NATURAL(p) ALIGN_UP(p, sizeof(double))
#endif /* ALIGN_NATURAL */

#endif /* !_JAVASOFT__JAVASOFT_UTIL_h__ */
