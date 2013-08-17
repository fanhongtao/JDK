/*
 * @(#)monitor_md.h	1.17 98/07/01
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
 * Win32 implementation of Java monitors
 */

#ifndef	_WIN32_MONITOR_MD_H_
#define	_WIN32_MONITOR_MD_H_

#include <windows.h>

#include "threads_md.h"
#include "mutex_md.h"
#include "condvar_md.h"

#define SYS_MID_NULL ((sys_mon_t *) 0)

typedef struct sys_mon {
    mutex_t 		mutex;	    	/* Mutex for monitor */
    condvar_t 		condvar;      	/* Condition variable for monitor */
    sys_thread_t 	*monitor_owner; /* Current owner of this monitor */
    long 		entry_count;	/* Recursion depth */
} sys_mon_t;

#endif	/* !_WIN32_MONITOR_MD_H_ */
