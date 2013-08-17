/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

#ifndef _JAVASOFT_THREADS_H_
#define _JAVASOFT_THREADS_H_

#include "oobj.h"
#include "interpreter.h"
#include "monitor.h"
#include "sys_api.h"

#include "java_lang_Thread.h"
#include "java_lang_ThreadGroup.h"

/*
 * Thread-related data structures
 */

typedef struct Hjava_lang_Thread HThread;
typedef struct Hjava_lang_ThreadGroup HThreadGroup;
typedef struct Hjava_lang_Thread *TID;

/* Access to thread data structures */
#define THREAD(tid)	((struct Classjava_lang_Thread *) unhand(tid))

/* The default Java stack size is legitimately platform-independent */
#define JAVASTACKSIZE (400 * 1024)     /* Default size of a thread java stack */

extern HThreadGroup *maingroup;         /* the main ThreadGroup */
extern HThreadGroup *systemgroup;       /* the system ThreadGroup */

extern long ProcStackSize;		/* Actual size of thread C stack */
extern long JavaStackSize;		/* Actual maximum size of java stack */

/*
 * External interface to threads support
 */

int createSystemThread(char *name, int priority, long stack_size,
		       void (*f)(void *), void *arg);
void threadBootstrap(TID tid);
int  threadCreate(TID, size_t, int, void (*)(void *));
TID  threadSelf(void);
void threadSleep(int64_t);

int threadPostException(TID tid, void *exc);
void threadTryVMSuspend(void);

void threadInit(void);

void WaitForSingleThreaded(void);
void AdjustUserThreadCount(int n);

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
void threadExit(void);

void threadFree(void);

/*
 * Note that we do not check that priorities are within Java's limits down here.
 * In fact, we make use of that for things like the idle and clock threads.
 * This may change once we work out a portable priority model.
 */
int threadSetPriority(TID tid, int pri);
int threadGetPriority(TID tid, int *prip);

#define threadYield()			sysThreadYield()

int threadResume(TID tid);
int threadSuspend(TID tid);

/*
 * Return information about this thread's stack.  This is used by
 * Garbage Collection code that needs to inspect the stack.
 *
 * It is permissable to return a null stack_base for those threads
 * that don't have a known stack (e.g. not allocated by the threads
 * package).  It is also permissable to return a somewhat bogus
 * stack_pointer for the current thread.
 */
void *threadStackBase(TID tid);
void *threadStackPointer(TID tid);

#define threadCheckStack()		sysThreadCheckStack()

/*
 * Interface to thread interrupt support
 */
void threadInterrupt(TID tid);
int threadIsInterrupted(TID tid, long ClearInterrupted);

#endif /* !_JAVASOFT_THREADS_H_ */
