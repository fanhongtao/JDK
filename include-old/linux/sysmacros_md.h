/*
 * @(#)sysmacros_md.h	1.43 00/08/03
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

#else /* linux */

/*
 * Flush the write buffer
 */
#if defined(__GNUC__)

#ifdef sparc
#define sysMemoryFlush()  __asm__ ("ldstub [%sp-4], %g0");
#elif i386
#define sysMemoryFlush()  __asm__ ("lock\nxorl $0, (%esp)");
#else
#error No definition for sysMemoryFlush!
#endif

#else
void sysMemoryFlush(void);
#endif
#endif /* linux */

long *  sysInvokeNative(void *, void *, long *, char *, int, void *);

/* name of the default JIT compiler to be used on Solaris */

#ifdef __linux__
#if (defined(__powerpc__) || defined(__mc68000__))
#define DEFAULT_JIT_NAME "NONE"
#else
#define DEFAULT_JIT_NAME "javacomp"
#endif
#else
#define DEFAULT_JIT_NAME "sunwjit"
#endif
#define JVM_ONLOAD_SYMBOLS   {"JVM_OnLoad"}

#endif /* !_JAVASOFT_SOLARIS_SYSMACROS_MD_H_ */
