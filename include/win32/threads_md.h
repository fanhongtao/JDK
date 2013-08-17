/*
 * @(#)threads_md.h	1.26 98/07/01
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
 * Win32 implementation of Java threads
 */

#ifndef _WIN32_THREADS_MD_H_
#define _WIN32_THREADS_MD_H_

#include <windows.h>
#include "bool.h"
#include "sysmacros_md.h"

#define N_TRACED_REGS 7

#define SYS_THREAD_NULL         ((sys_thread_t *) 0)

/*
 * Machine dependent info in a sys_thread_t: Keep these values in
 * sync with the string array used by sysThreadDumpInfo() in threads_md.c!
 */
typedef enum {
    FIRST_THREAD_STATE,
    RUNNABLE = FIRST_THREAD_STATE,
    SUSPENDED,
    MONITOR_WAIT,
    CONDVAR_WAIT,
    MONITOR_SUSPENDED,
    NUM_THREAD_STATES
} thread_state_t;

/*
 * Machine dependent thread data structure
 */
typedef struct sys_thread {
    void *cookie;		    /* Back-pointer to shared thread struct */
    HANDLE handle;		    /* Win32 thread handle */
    unsigned long id;   	    /* Win32 thread id */
    void *stack_base;   	    /* Thread stack base */
    thread_state_t state;	    /* Current thread state */
    bool_t system_thread;	    /* TRUE if this is a system thread */
    HANDLE interrupt_event;	    /* Event signaled on thread interrupt */
    bool_t interrupted;		    /* Shadow thread interruption */
    bool_t vmsuspended;		    /* This thread is suspended */
    void *(*start_proc)(void *);    /* Thread start routine address */
    void *start_parm;		    /* Thread start routine parameter */
    struct sys_thread *next;	    /* Next thread in active thread queue */
    unsigned int cacheKey;          /* Key for monitor being looked up */
    void * monitorCache[SYS_TLS_MONCACHE]; /* cache of recently monitors */
} sys_thread_t;

extern bool_t ThreadsInitialized;

#endif /* !_WIN32_THREADS_MD_H_ */
