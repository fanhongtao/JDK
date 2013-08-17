/*
 * @(#)wrap.h	1.12 98/09/15
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

#ifndef _JAVASOFT_WRAP_H_
#define _JAVASOFT_WRAP_H_

#include "native.h"
#include "typecodes.h"

#define	T_BAD	T_XXUNUSEDXX1	/* 1 */

/*
 * Routines to wrap and unwrap primitive Java types.
 */
extern HObject *	java_wrap(ExecEnv *, jvalue, unsigned char);
extern unsigned char	java_unwrap(HObject *, jvalue *);

#endif /* !_JAVASOFT_WRAP_H_ */
