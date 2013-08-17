/*
 * @(#)common_exceptions.h	1.9 98/07/01
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
