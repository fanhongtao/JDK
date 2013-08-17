/*
 * @(#)monitor.h	1.35 98/07/01
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
 * Monitor interface
 */

#ifndef	_MONITOR_H_
#define	_MONITOR_H_

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
    unsigned int	key;		/* Monitor hash key */
    struct monitor_t   *next;
    char		mid[1];		/* The sys_mon_t */
} monitor_t, *MID;

/* A macro for accessing the sys_mon_t from the monitor_t */
#define sysmon(m)   (*(sys_mon_t *) m->mid)

typedef struct reg_mon_t {
    sys_mon_t *mid;
    char *name;
    struct reg_mon_t *next;
} reg_mon_t;

/*
 * Macros
 */
#define MID_NULL 	    ((MID) 0)
#define TIMEOUT_INFINITY    -1

/*
 * Support for the monitor registry
 */
extern sys_mon_t *_registry_lock;

#define REGISTRY_LOCK_INIT()    monitorRegister(_registry_lock, \
						"Monitor registry")
#define REGISTRY_LOCK()	  	sysMonitorEnter(_registry_lock)
#define REGISTRY_LOCKED()	sysMonitorEntered(_registry_lock)
#define REGISTRY_UNLOCK()	sysMonitorExit(_registry_lock)

/*
 * External routines.
 */

/*
 * Synchronization interface
 */
void monitorInit(monitor_t *mon);
void monitorCacheInit(void);
void monitorEnter(unsigned int);
void monitorExit(unsigned int);
void monitorWait(unsigned int, int);
void monitorNotify(unsigned int);
void monitorNotifyAll(unsigned int);

/* Registry of static monitors */
extern reg_mon_t *MonitorRegistry;

void monitorRegistryInit(void);
void monitorRegister(sys_mon_t *, char *);
void monitorUnregister(sys_mon_t *);
void registeredEnumerate(void (*)(reg_mon_t *, void *), void *); 
void registeredEnumerate_unlocked(void (*)(reg_mon_t *, void *), void *); 


/*
 * Random non-local locks without obviously better homes 
 */

/* The class loading lock */

extern sys_mon_t *_loadclass_lock;
#define LOADCLASS_LOCK_INIT() \
    monitorRegister(_loadclass_lock, "Class loading lock")
#define LOADCLASS_LOCK()     sysMonitorEnter(_loadclass_lock)
#define LOADCLASS_LOCKED()   sysMonitorEntered(_loadclass_lock)
#define LOADCLASS_UNLOCK()   sysMonitorExit(_loadclass_lock)

/* The global class table (binclasses) lock */
extern sys_mon_t *_binclass_lock;
#define BINCLASS_LOCK_INIT() monitorRegister(_binclass_lock, "BinClass lock")
#define BINCLASS_LOCK()	     sysMonitorEnter(_binclass_lock)
#define BINCLASS_LOCKED()    sysMonitorEntered(_binclass_lock)
#define BINCLASS_UNLOCK()    sysMonitorExit(_binclass_lock)

/* Locks on the interned string hash table */

extern sys_mon_t *_stringhash_lock;
#define STRINGHASH_INIT()    monitorRegister(_stringhash_lock, \
					     "String intern lock")
#define STRINGHASH_LOCK()    sysMonitorEnter(_stringhash_lock)
#define STRINGHASH_LOCKED()  sysMonitorEntered(_stringhash_lock)
#define STRINGHASH_UNLOCK()  sysMonitorExit(_stringhash_lock)

/* Locks on the method info (name and signature) hash table */

extern sys_mon_t *_nametypehash_lock;
#define NAMETYPEHASH_INIT()   monitorRegister(_nametypehash_lock, \
					      "Name and type hash table lock")
#define NAMETYPEHASH_LOCK()   sysMonitorEnter(_nametypehash_lock)
#define NAMETYPEHASH_LOCKED() sysMonitorEntered(_nametypehash_lock)
#define NAMETYPEHASH_UNLOCK() sysMonitorExit(_nametypehash_lock)

/* JNI global reference locks */

extern sys_mon_t *_globalref_lock;
#define GLOBALREF_LOCK_INIT() \
          monitorRegister(_globalref_lock, "JNI global reference lock")
#define GLOBALREF_LOCK()	    sysMonitorEnter(_globalref_lock)
#define GLOBALREF_LOCKED()	    sysMonitorEntered(_globalref_lock)
#define GLOBALREF_UNLOCK()	    sysMonitorExit(_globalref_lock)

#endif	/* !_MONITOR_H_ */
