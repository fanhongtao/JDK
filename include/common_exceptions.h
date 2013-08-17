/*
 * @(#)common_exceptions.h	1.8 96/11/23
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
