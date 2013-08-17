/*
 * @(#)sysmacros_md.h	1.3 00/01/12
 *
 * Copyright 1995-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

#ifndef _JAVASOFT_LINUX_SYSMACROS_MD_H_
#define _JAVASOFT_LINUX_SYSMACROS_MD_H_

#include <stdlib.h> /* for malloc, free, ... */
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <math.h>

#define INT_OP(x,op,y)  ((x) op (y))
#define NAN_CHECK(l,r,x) x
#define IS_NAN(x) isnan(x)

#ifdef DEBUG
void panic (const char *, ...);
#define sysAssert(expression) {		\
    if (!(expression)) {		\
	panic("\"%s\", line %d: assertion failure\n", __FILE__, __LINE__); \
    }					\
}
#else
#define sysAssert(expression) ((void) 0)
#endif

#if defined(__sparc__)
#define sysMemoryFlush() __asm__ __volatile__ ("" : : : "memory")
#define sysStoreBarrier() __asm__ __volatile__ ("" : : : "memory")

#elif defined(__i386__)
#define sysMemoryFlush() __asm__ __volatile__ ("lock; addl $0,0(%%esp)" : : : "memory")
#define sysStoreBarrier() __asm__ __volatile__ ("" : : : "memory")

#elif defined(__powerpc__)
#define sysMemoryFlush()  __asm__ __volatile__ ("sync" : : : "memory")
#define sysStoreBarrier() __asm__ __volatile__ ("eieio" : : : "memory")

#elif defined(__mc68000__)
#define sysMemoryFlush()  __asm__ __volatile__ ("" : : : "memory")
#define sysStoreBarrier() __asm__ __volatile__ ("" : : : "memory")

#else
#error No definition for sysMemoryFlush && sysStoreBarrier!
#endif

long *  sysInvokeNative(void *, void *, long *, char *, int, void *);

/* name of the default JIT compiler to be used on Linux */
#if (defined(__powerpc__) || defined(__mc68000__))
#define DEFAULT_JIT_NAME "NONE"
#else
#define DEFAULT_JIT_NAME "javacomp"
#endif

#define JVM_ONLOAD_SYMBOLS   {"JVM_OnLoad"}

#endif /* !_JAVASOFT_LINUX_SYSMACROS_MD_H_ */
