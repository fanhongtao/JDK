/*
 * @(#)mutex_md.h	1.11 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
