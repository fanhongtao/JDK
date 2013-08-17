/*
 * @(#)wrap.h	1.14 00/02/02
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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
