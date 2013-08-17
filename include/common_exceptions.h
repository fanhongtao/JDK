/*
 * @(#)common_exceptions.h	1.10 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * Common exceptions thrown from within the interpreter (and
 * associated libraries).
 */

#ifndef	_COMMON_EXCEPTIONS_H_
#define	_COMMON_EXCEPTIONS_H_

/*
 * The following routines will instantiate a member of a specific
 * subclass of Exception, and fill in the stack backtrace
 * information from the current thread's ExecEnv stack .
 */
extern void OutOfMemoryError(void);

#endif	/* !_COMMON_EXCEPTIONS_H_ */
