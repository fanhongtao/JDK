/*
 * @(#)monitor_md.h	1.16 97/02/17
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
