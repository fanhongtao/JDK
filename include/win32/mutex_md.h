/*
 * @(#)mutex_md.h	1.10 98/07/01
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
 * Win32 implementation of mutexes. Here we use critical sections as
 * our mutexes. We could have used mutexes, but mutexes are heavier
 * weight than critical sections. Mutexes and critical sections are
 * semantically identical, the only difference being that mutexes
 * can operate between processes (i.e. address spaces).
 *
 * It's worth noting that the Win32 functions supporting critical
 * sections do not provide any error information whatsoever (i.e.
 * all critical section routines return (void)).
 */

#ifndef	_WIN32_MUTEX_MD_H_
#define	_WIN32_MUTEX_MD_H_

#include <windows.h>

typedef CRITICAL_SECTION mutex_t;

#define mutexInit(m)	InitializeCriticalSection(m)
#define mutexDestroy(m)	DeleteCriticalSection(m)
#define mutexLock(m)	EnterCriticalSection(m)
#define mutexUnlock(m)	LeaveCriticalSection(m)

#endif	/* !_WIN32_MUTEX_MD_H_ */
