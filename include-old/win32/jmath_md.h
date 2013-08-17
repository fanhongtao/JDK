/*
 * @(#)jmath_md.h	1.10 98/09/21
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
 * An awful hack, but the dumb MSC compiler #define's exception to _exception
 * for compatibility with non-ANSI names, and this conflicts with the field
 * named 'exception' in 'struct execenv' of interpreter.h.
 */
#include <math.h>
#undef exception

#define DREM(a,b) drem(a,b)

/* drem and rint are defined in math_md.c */
extern double drem(double dividend, double divisor);
extern double rint(double a);
