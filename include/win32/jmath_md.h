/*
 * @(#)jmath_md.h	1.7 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * An awful hack, but the dumb MSC compiler #define's exception to _exception
 * for compatibility with non-ANSI names, and this conflicts with the field
 * named 'exception' in 'struct execenv' of interpreter.h.
 */
#include <math.h>
#undef exception

#define DREM(a,b) drem(a,b)
#define IEEEREM(a,b) ieeerem(a,b)

/* drem, rint, and ieeerem are defined in math_md.c */
extern double drem(double dividend, double divisor);
extern double ieeerem(double dividend, double divisor);
extern double rint(double a);
