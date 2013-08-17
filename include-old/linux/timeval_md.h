/*
 * @(#)timeval_md.h	1.16 00/02/02
 *
 * Copyright 1994-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

#ifndef _JAVASOFT_SOLARIS_TIMEVAL_H_
#define _JAVASOFT_SOLARIS_TIMEVAL_H_

typedef struct {
	long tv_sec;		/* seconds */
	long tv_usec;		/* microseconds (NOT milliseconds) */
} timeval_t;

#endif /* !_JAVASOFT_SOLARIS_TIMEVAL_H_ */
