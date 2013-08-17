/*
 * @(#)log.h	1.11 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * Logging utilities for debugging.
 */

#ifndef _LOG_H_
#define _LOG_H_

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

#define Log(level, message)
#define Log1(level, message, x1)
#define Log2(level, message, x1, x2)
#define Log3(level, message, x1, x2, x3)
#define Log4(level, message, x1, x2, x3, x4)

#endif /* LOGGING */
#endif /* !_LOG_H_ */
