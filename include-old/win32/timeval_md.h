/*
 * @(#)timeval_md.h	1.10 98/09/21
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

#ifndef _JAVASOFT_WIN32_TIMEVAL_H_
#define _JAVASOFT_WIN32_TIMEVAL_H_

typedef struct {
	long tv_sec;
	long tv_usec;
} timeval_t;

/*
 * Operations on timevals.
 *
 * NB: timercmp does not work for >=, <= or ==.
 */
#define timerisset(tvp)         ((tvp)->tv_sec || (tvp)->tv_usec)
#define timercmp(tvp, uvp, cmp) \
        ((tvp)->tv_sec cmp (uvp)->tv_sec || \
         (tvp)->tv_sec == (uvp)->tv_sec && (tvp)->tv_usec cmp (uvp)->tv_usec)
#define timereq(tvp, uvp) \
         ((tvp)->tv_sec == (uvp)->tv_sec && (tvp)->tv_usec == (uvp)->tv_usec)
#define timerclear(tvp)         (tvp)->tv_sec = (tvp)->tv_usec = 0

void timeradd(timeval_t*, timeval_t*);
void timersub(timeval_t*, timeval_t*);

#endif /* !_JAVASOFT_WIN32_TIMEVAL_H_ */
