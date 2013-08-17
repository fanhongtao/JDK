/*
 * @(#)sysmacros_md.h	1.37 98/09/15
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

#ifndef _JAVASOFT_WIN32_SYSMACROS_MD_H_
#define _JAVASOFT_WIN32_SYSMACROS_MD_H_

#include <stdlib.h>	/* For malloc() and free() */
#include <float.h>      /* For _isnan() */

#define INT_OP(x,op,y)  (((#op[0]=='/')||(#op[0]=='%')) ?              \
			 ( (((x)==0x80000000)&&((y)==-1)) ?            \
			     ((x) op 1) :                              \
			     ((x) op (y))) :                           \
			 ((x) op (y)))
#define NAN_CHECK(l,r,x) (!_isnan((l))&&!_isnan((r))) ? (x : 0)
#define IS_NAN(x) _isnan(x)

#ifdef DEBUG
#define sysAssert(expression) {		\
    if (!(expression)) {		\
	panic("\"%s\", line %d: assertion failure\n", __FILE__, __LINE__); \
    }					\
}
#else
#define sysAssert(expression) ((void) 0)
#endif

#define sysMemoryFlush()  __asm { lock xor	DWORD PTR 0[esp], 0 }

/*
 * Intel doesn't seem to support a weak consistency model for MP
 * so we define this to do nothing.
 */
#define sysStoreBarrier()		((void) 0)

long *  sysInvokeNative(void *, void *, long *, char *, int, void *);

/* name of the default JIT compiler to be used on win32 */
#define	DEFAULT_JIT_NAME "symcjit"

#define JVM_ONLOAD_SYMBOLS   {"_JVM_OnLoad@12", "JVM_OnLoad"}

#endif /* !_JAVASOFT_WIN32_SYSMACROS_MD_H_ */
