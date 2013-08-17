/*
 * @(#)wrap.h	1.6 98/07/01
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
