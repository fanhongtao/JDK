/*
 * @(#)monitor_md.h	1.18 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
