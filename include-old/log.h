/*
 * @(#)log.h	1.2 00/01/12
 *
 * Copyright 1994-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

/*
 * Logging utilities for debugging.
 */

#ifndef _JAVASOFT_LOG_H_
#define _JAVASOFT_LOG_H_

#ifdef LOGGING

#include <stdio.h>

/*
 * NOTE: I [Tim] changed command-line parsing of the -l flag to allow
 * -l0 to be passed in.  PERMANENT LOG STATEMENTS SHOULD NOT USE LEVEL 0!
 * It is intended to be used temporarily to limit logging output to
 * specific messages during debugging.  Otherwise even level 1 logging
 * buries you in output.
 */

int jio_fprintf(FILE *, const char *fmt, ...);
extern int logging_level;

#define Log(level, message) {			\
    if (level <= logging_level)			\
	jio_fprintf(stderr, message);		\
}

#define Log1(level, message, x1) {		\
    if (level <= logging_level)			\
	jio_fprintf(stderr, message, (x1));		\
}

#define Log2(level, message, x1, x2) {		\
    if (level <= logging_level)			\
	jio_fprintf(stderr, message, (x1), (x2));	\
}

#define Log3(level, message, x1, x2, x3) {		\
    if (level <= logging_level)				\
	jio_fprintf(stderr, message, (x1), (x2), (x3));	\
}

#define Log4(level, message, x1, x2, x3, x4) {			\
    if (level <= logging_level)					\
	jio_fprintf(stderr, message, (x1), (x2), (x3), (x4));	\
}

#else

#define Log(level, message)			((void) 0)
#define Log1(level, message, x1)			((void) 0)
#define Log2(level, message, x1, x2)		((void) 0)
#define Log3(level, message, x1, x2, x3)		((void) 0)
#define Log4(level, message, x1, x2, x3, x4)	((void) 0)

#endif /* LOGGING */
#endif /* !_JAVASOFT_LOG_H_ */
