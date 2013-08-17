/*
 * @(#)monitor_cache.h	1.35 00/02/02
 *
 * Copyright 1994-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

/*
 * Monitor cache definitions
 */

#ifndef _JAVASOFT_MONITOR_CACHE_H_
#define _JAVASOFT_MONITOR_CACHE_H_

#include "monitor.h"		/* For TIMEOUT_INFINITY */

/*
 * There is a distinct recursive lock (not necessarily a monitor) for
 * the monitor cache.
 */
extern sys_mon_t *_cache_lock;

#define CACHE_LOCK_INIT() _cache_lock = sysMalloc(sysMonitorSizeof()); \
                          monitorRegister(_cache_lock, "Monitor cache lock")
#define CACHE_LOCK(self)	  sysMonitorEnter(self, _cache_lock)
#define CACHE_LOCKED(self)	  sysMonitorEntered(self, _cache_lock)
#define CACHE_UNLOCK(self)	  sysMonitorExit(self, _cache_lock)

void *lookupJavaMonitor(sys_mon_t *mid);

#ifndef FAST_MONITOR

 /*
 * External routines.
 */
monitor_t * lookupMonitor(sys_thread_t *self, uintptr_t key, bool_t create);
void monitorEnumerate(void (*)(monitor_t *, void *), void *);

void monitorCacheExpand(int);
void monitorCacheDestroy(void);
extern int monCount;

#else  /* FAST_MONITOR */

extern void monitorEnumerate(void (*)(monitor_t *, void *), void *);

extern void monitorCacheGC(uintptr_t hp);
extern void monitorCacheFree(void * mid);

extern int  monCount;
extern int  useCount;

#define is_monitor(m)     (4 & ((int32_t) m))
#define key2mid(hp)       ((sys_mon_t*) *(((JHandle*) (hp))->obj - 1))

#endif /* FAST_MONITOR */

#endif /* !_JAVASOFT_MONITOR_CACHE_H_ */
