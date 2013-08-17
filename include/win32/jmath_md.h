/*
 * @(#)jmath_md.h	1.5 96/11/23
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 * 
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
