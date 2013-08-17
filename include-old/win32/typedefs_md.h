/*
 * @(#)typedefs_md.h	1.37 98/09/15
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
 * Win32 dependent type definitions
 */

#ifndef _JAVASOFT_WIN32_TYPEDEF_MD_H_
#define _JAVASOFT_WIN32_TYPEDEF_MD_H_

#include <windows.h>

typedef char int8_t;

typedef unsigned int uint_t;
typedef long int32_t;
typedef unsigned long uint32_t;
typedef __int64	int64_t;
typedef unsigned __int64 uint64_t;
typedef	int intptr_t;
typedef unsigned int uintptr_t;

/* use these macros when the compiler supports the long long type */

#define ll_high(a)	((long)((a)>>32))
#define ll_low(a)	((long)(a))
#define int2ll(a)	((int64_t)(a))
#define ll2int(a)	((int)(a))
#define ll_add(a, b)	((a) + (b))
#define ll_and(a, b)	((a) & (b))
#define ll_div(a, b)	((a) / (b))
#define ll_mul(a, b)	((a) * (b))
#define ll_neg(a)	(-(a))
#define ll_not(a)	(~(a))
#define ll_or(a, b)	((a) | (b))
/* THE FOLLOWING DEFINITION IS NOW A FUNCTION CALL IN ORDER TO WORKAROUND
   OPTIMIZER BUG IN MSVC++ 2.1 (see system_md.c)
   #define ll_shl(a, n)	((a) << (n)) */
#define ll_shr(a, n)	((a) >> (n))
#define ll_sub(a, b)	((a) - (b))
#define ll_ushr(a, n)	((uint64_t)(a) >> (n))
#define ll_xor(a, b)	((a) ^ (b))
#define uint2ll(a)	((uint64_t)(unsigned long)(a))
#define ll_rem(a,b)	((a) % (b))

int32_t float2l(float f);
int32_t double2l(double f);
int64_t float2ll(float f);
int64_t double2ll(double f);
#define ll2float(a)	((float) (a))
#define ll2double(a)	((double) (a))

/* Useful on machines where jlong and jdouble have different endianness. */
#define ll2double_bits(a) ((void) 0)

/* comparison operators */
#define ll_ltz(ll)	((ll) < 0)
#define ll_gez(ll)	((ll) >= 0)
#define ll_eqz(a)	((a) == 0)
#define ll_nez(a)	((a) != 0)
#define ll_eq(a, b)	((a) == (b))
#define ll_ne(a,b)	((a) != (b))
#define ll_ge(a,b)	((a) >= (b))
#define ll_le(a,b)	((a) <= (b))
#define ll_lt(a,b)	((a) < (b))
#define ll_gt(a,b)	((a) > (b))

#define ll_zero_const	((int64_t) 0)
#define ll_one_const	((int64_t) 1)

int64_t ll_shl(int64_t a, int bits);

#define ll2ptr(a) ((void*)(a))
#define ptr2ll(a) ((jlong)(a))

#endif /* !_JAVASOFT_WIN32_TYPEDEF_MD_H_ */
