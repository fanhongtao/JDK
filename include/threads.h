/*
 * @(#)threads.h	1.35 98/07/01
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

#ifndef _THREADS_H_
#define _THREADS_H_

#include "oobj.h"
#include "interpreter.h"
#include "timeval.h"
#include "monitor.h"
#include "bool.h"
#include "sys_api.h"

#include "java_lang_Thread.h"
#include "java_lang_ThreadGroup.h"

#define MinimumPriority	    java_lang_Thread_MIN_PRIORITY
#define MaximumPriority	    java_lang_Thread_MAX_PRIORITY
#define NormalPriority	    java_lang_Thread_NORM_PRIORITY

/*
 * Support for thread queue
 */

extern sys_mon_t *_queue_lock;

#define QUEUE_LOCK_INIT() monitorRegister(_queue_lock, "Thread queue lock")
#define QUEUE_LOCK()	  sysMonitorEnter(_queue_lock)
#define QUEUE_LOCKED()	  sysMonitorEntered(_queue_lock)
#define QUEUE_UNLOCK()	  sysMonitorExit(_queue_lock)
#define QUEUE_NOTIFY()	  sysMonitorNotify(_queue_lock)
#define QUEUE_WAIT()	  sysMonitorWait(_queue_lock, \
					 SYS_TIMEOUT_INFINITY, FALSE)

/*
 * Thread-related data structures
 */

typedef struct Hjava_lang_Thread HThread;
typedef struct Hjava_lang_ThreadGroup HThreadGroup;
typedef struct Hjava_lang_Thread *TID;

typedef sys_thread_t ThreadPrivate;

/* Access to thread data structures */
#define THREAD(tid)	((struct Classjava_lang_Thread *) unhand(tid))
#define SYSTHREAD(tid)	((sys_thread_t *)THREAD(tid)->PrivateInfo)

#define THR_SYSTEM 0		/* System thread */
#define THR_USER   1		/* User thread */

extern int ActiveThreadCount;		/* All threads */
extern int UserThreadCount;		/* User threads */

/* The default Java stack size is legitimately platform-independent */
#define JAVASTACKSIZE (400 * 1024)     /* Default size of a thread java stack */

extern long ProcStackSize;		/* Actual size of thread C stack */
extern long JavaStackSize;		/* Actual maximum size of java stack */

extern stackp_t mainstktop;		/* Base of primordial thread stack */

/*
 * External interface to threads support
 */

void threadBootstrap(TID tid, stackp_t sb);
int  threadCreate(TID, unsigned int, size_t, void *(*)());
TID  threadSelf(void);
void threadSleep(int);
int  threadEnumerate(TID*, int);

void threadDumpInfo(TID, bool_t);        /* Debugging help in debug.c */

int threadPostException(TID tid, void *exc);
void threadTryVMSuspend(void);

/*
 * There may be certain initialization that can't be done except by the
 * thread on itself, e.g. setting thread-local data in Solaris threads.
 * This function is called from ThreadRT0() when the thread starts up
 * to take care of such things.
 */
#define threadInit(tid, sb)		sysThreadInit(SYSTHREAD(tid), sb)

/*
 * Exit the current thread.  This function is not expected to return.
 *
 * Note that we currently never stop a thread dead in its tracks, but
 * rather throw an exception against it that causes it to unwind its
 * stack, exit monitors, etc. and exit in a single place (ThreadRT0).
 * If a thread is caused to exit precipitously by calling threadExit()
 * at random places it will corrupt the runtime and at minimum will
 * fail to clean the thread out of any monitors it currently holds.
 */
#define threadExit()			sysThreadExit()

/*
 * Note that we do not check that priorities are within Java's limits down here.
 * In fact, we make use of that for things like the idle and clock threads.
 * This may change once we work out a portable priority model.
 */
#define threadSetPriority(tid, pri)	sysThreadSetPriority(SYSTHREAD(tid), pri)
#define threadGetPriority(tid, prip)	sysThreadGetPriority(SYSTHREAD(tid), prip)

#define threadYield()			sysThreadYield()
#define threadResume(tid)		sysThreadResume(SYSTHREAD(tid))
#define threadSuspend(tid)		sysThreadSuspend(SYSTHREAD(tid))

/*
 * Return information about this thread's stack.  This is used by
 * Garbage Collection code that needs to inspect the stack.
 *
 * It is permissable to return a null stack_base for those threads
 * that don't have a known stack (e.g. not allocated by the threads
 * package).  It is also permissable to return a somewhat bogus
 * stack_pointer for the current thread.
 */
#define threadStackBase(tid)		sysThreadStackBase(SYSTHREAD(tid))
#define threadStackPointer(tid)		sysThreadStackPointer(SYSTHREAD(tid))

#define threadCheckStack()		sysThreadCheckStack()

/*
 * Interface to thread interrupt support
 */
#define threadInterrupt(tid)		sysThreadInterrupt(SYSTHREAD(tid))
#define threadIsInterrupted(tid, ClearInterrupted) \
	sysThreadIsInterrupted(SYSTHREAD(tid), ClearInterrupted)

#endif /* !_THREADS_H_ */
