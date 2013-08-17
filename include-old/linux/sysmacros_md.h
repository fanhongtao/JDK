/*
 * @(#)sysmacros_md.h	1.46 00/03/09
 *
 * Copyright 1995-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

#ifndef _JAVASOFT_SOLARIS_SYSMACROS_MD_H_
#define _JAVASOFT_SOLARIS_SYSMACROS_MD_H_

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

/*
 * Solaris always runs in Total Store Order mode for Sparc and
 * Intel doesn't seem to support a weak consistency model for MP
 * so we define this to do nothing.
 */
#ifndef __linux__
#define sysStoreBarrier() ((void) 0)
#endif

/*
 * Flush the write buffer
 */
#if defined(__GNUC__)

#ifdef __linux__
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
#endif /* linux */

#ifndef __linux__
#ifdef sparc
#define sysMemoryFlush()  __asm__ ("ldstub [%sp-4], %g0");
#elif i386
#define sysMemoryFlush()  __asm__ ("lock\nxorl $0, (%esp)");
#else
#error No definition for sysMemoryFlush!
#endif
#endif /* not linux */


#else /* !GNUC */
void sysMemoryFlush(void);
#endif

long *  sysInvokeNative(void *, void *, long *, char *, int, void *);

/*
 * Name of the default JIT that the classic VM will try to load.  Note
 * that because of "4251347: Please remove Solaris JIT from the binary
 * JDK/JRE bundles", the JIT has been turned off on Solaris.
 */
#if defined(__solaris__) || defined(__linux__)
#define DEFAULT_JIT_NAME "NONE"
#else
#define DEFAULT_JIT_NAME "sunwjit"
#endif

#define JVM_ONLOAD_SYMBOLS   {"JVM_OnLoad"}

#endif /* !_JAVASOFT_SOLARIS_SYSMACROS_MD_H_ */
