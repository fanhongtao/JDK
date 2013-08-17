/*
 * @(#)mutex_md.h	1.9 96/11/23
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
