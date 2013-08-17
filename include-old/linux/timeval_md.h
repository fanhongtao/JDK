/*
 * @(#)timeval_md.h	1.14 98/09/21
 *
 * Copyright 1994-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

#ifndef _JAVASOFT_SOLARIS_TIMEVAL_H_
#define _JAVASOFT_SOLARIS_TIMEVAL_H_

typedef struct {
	long tv_sec;		/* seconds */
	long tv_usec;		/* microseconds (NOT milliseconds) */
} timeval_t;

#endif /* !_JAVASOFT_SOLARIS_TIMEVAL_H_ */
