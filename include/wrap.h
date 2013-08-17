/*
 * @(#)wrap.h	1.7 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

#ifndef _WRAP_H_
#define _WRAP_H_

#include "native.h"
#include "typecodes.h"

typedef union {
    int32_t x[2];
    long    i;			/* z,b,c,s,i */
    float   f;
    double  d;
    int64_t l;
    char    *p;
    HObject *h;
} JavaValue;

#define	T_BAD	T_XXUNUSEDXX1	/* 1 */

/*
 * Routines to wrap and unwrap primitive Java types.
 */
extern HObject *	java_wrap(JavaValue, unsigned char);
extern unsigned char	java_unwrap(HObject *, JavaValue *);

#endif /* _WRAP_H */
