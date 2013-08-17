/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * Monitor interface
 */

#ifndef _JAVASOFT_MONITOR_H_
#define _JAVASOFT_MONITOR_H_

#include "sys_api.h"

/*
 * Used by the monitor caching machanism to mark monitors as being
 * in-use.
 */
#define MON_LOCAL_CACHE_REF		(1 << 0)

/*
 * The monitor data structure:
 *
 * The use_count field counts the number of createMonitor calls yet
 * unmatched by monitorExit calls; that is, it is a count of outstanding
 * monitor entries of all threads regardless of whether those threads
 * own the monitor or are waiting on it.
 *
 * Note that because the mid[] array will hold the system-specific
 * sys_mon_t, it needs to start on a four-byte boundary lest the fields
 * of the sys_mon_t aren't properly aligned.  Otherwise the flags field
 * would be shorter than a long.
 */
typedef struct monitor_t {
    uintptr_t 		key;		/* Monitor hash key */
    struct monitor_t   *next;
} monitor_t, *MID;

#define monitorSizeof() (sizeof(monitor_t) + sysMonitorSizeof())

/* A macro for accessing the sys_mon_t from the monitor_t */
#define sysmon(m)   ((sys_mon_t *) ((m)+1))

typedef struct reg_mon_t {
    sys_mon_t *mid;
    char *name;
    struct reg_mon_t *next;
} reg_mon_t;

/*
 * Macros
 */
#define MID_NULL 	    ((MID) 0)
#define TIMEOUT_INFINITY    (int2ll(-1))

/*
 * Support for the monitor registry
 */
extern sys_mon_t *_registry_lock;

#define REGISTRY_LOCK_INIT()    monitorRegister(_registry_lock, \
						"Monitor registry")
#define REGISTRY_LOCK(self)	sysMonitorEnter(self, _registry_lock)
#define REGISTRY_LOCKED(self)	sysMonitorEntered(self, _registry_lock)
#define REGISTRY_UNLOCK(self)	sysMonitorExit(self, _registry_lock)

/*
 * External routines.
 */

/*
 * Synchronization interface
 */
void monitorInit(monitor_t *mon);
void monitorDestroy(monitor_t *mon);
void monitorCacheInit(void);

struct execenv;

sys_mon_t * monitorEnter2(struct execenv *, uintptr_t);
int monitorExit2(struct execenv *, uintptr_t);
void monitorWait2(struct execenv *, uintptr_t, int64_t);
void monitorNotify2(struct execenv *, uintptr_t);
void monitorNotifyAll2(struct execenv *, uintptr_t);

void monitorRegistryInit(void);
void monitorRegister(sys_mon_t *, char *);
void monitorUnregister(sys_mon_t *);
void registeredEnumerate(void (*)(reg_mon_t *, void *), void *); 

char *lookupRegisteredMonitor(sys_thread_t *self, sys_mon_t *mid);

/*
 * Random non-local locks without obviously better homes 
 */

/* The system class loader lock */ 
extern sys_mon_t *_sysloader_lock;
#define SYSLOADER_LOCK_INIT() \
    monitorRegister(_sysloader_lock, "System class loader lock")
#define SYSLOADER_LOCK(self)     sysMonitorEnter(self, _sysloader_lock)
#define SYSLOADER_LOCKED(self)   sysMonitorEntered(self, _sysloader_lock)
#define SYSLOADER_UNLOCK(self)   sysMonitorExit(self, _sysloader_lock)

/* The class linking lock */ 
extern sys_mon_t *_linkclass_lock;
#define LINKCLASS_LOCK_INIT() \
    monitorRegister(_linkclass_lock, "Class linking lock")
#define LINKCLASS_LOCK(self)     sysMonitorEnter(self, _linkclass_lock)
#define LINKCLASS_LOCKED(self)   sysMonitorEntered(self, _linkclass_lock)
#define LINKCLASS_UNLOCK(self)   sysMonitorExit(self, _linkclass_lock)

/* The global class table (binclasses) lock */
extern sys_mon_t *_binclass_lock;
#define BINCLASS_LOCK_INIT() monitorRegister(_binclass_lock, "BinClass lock")
#define BINCLASS_LOCK(self)	 sysMonitorEnter(self, _binclass_lock)
#define BINCLASS_LOCKED(self)    sysMonitorEntered(self, _binclass_lock)
#define BINCLASS_UNLOCK(self)    sysMonitorExit(self, _binclass_lock)

/* JNI global reference locks */

extern sys_mon_t *_globalref_lock;
#define GLOBALREF_LOCK_INIT() \
          monitorRegister(_globalref_lock, "JNI global reference lock")
#define GLOBALREF_LOCK(self)	    sysMonitorEnter(self, _globalref_lock)
#define GLOBALREF_LOCKED(self)	    sysMonitorEntered(self, _globalref_lock)
#define GLOBALREF_UNLOCK(self)	    sysMonitorExit(self, _globalref_lock)

/*
 * Support for thread queue
 */
extern sys_mon_t *_queue_lock;	/* Protects thread queue, thread count */

#define QUEUE_LOCK_INIT() monitorRegister(_queue_lock, "Thread queue lock")
#define QUEUE_LOCK(self)    sysMonitorEnter(self, _queue_lock)
#define QUEUE_LOCKED(self)  sysMonitorEntered(self, _queue_lock)
#define QUEUE_UNLOCK(self)  sysMonitorExit(self, _queue_lock)
#define QUEUE_NOTIFY(self)  sysMonitorNotify(self, _queue_lock)
#define QUEUE_NOTIFYALL(self)  sysMonitorNotifyAll(self, _queue_lock)
#define QUEUE_WAIT(self) sysMonitorWait(self, _queue_lock, \
					SYS_TIMEOUT_INFINITY)

extern sys_mon_t *_code_lock;

#endif /* !_JAVASOFT_MONITOR_H_ */
