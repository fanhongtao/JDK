/*
 * @(#)finalize.h	1.21 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

#ifndef _FINALIZE_H_
#define _FINALIZE_H_

#include "oobj.h"
#include "sys_api.h"

/*
 * The HasFinalizerQ and FinalizeMeQ queues contain finalizer_t
 * structures.  Handles rather than objects are used to avoid relocating
 * the contents of the queues on GC.
 */
typedef struct {
    JHandle *next;              /* The next finalizer object */
} finalizer_t;

extern JHandle *HasFinalizerQ;
extern JHandle *FinalizeMeQ;
extern JHandle *FinalizeMeQLast;
extern JHandle *BeingFinalized;

extern bool_t finalize_on_exit;

#define FINALIZER_T(h) \
        ((finalizer_t *)((char *)h->obj + cbInstanceSize(obj_classblock(h))))

/*
 * Locks for the finalization queues
 */
extern sys_mon_t *_hasfinalq_lock;
#define HASFINALQ_LOCK_INIT()	monitorRegister(_hasfinalq_lock, \
						"Has finalization queue lock")
#define HASFINALQ_LOCK()	sysMonitorEnter(_hasfinalq_lock)
#define HASFINALQ_LOCKED()	sysMonitorEntered(_hasfinalq_lock)
#define HASFINALQ_UNLOCK()	sysMonitorExit(_hasfinalq_lock)
#define HASFINALQ_NOTIFY()	sysMonitorNotify(_hasfinalq_lock)

extern sys_mon_t *_finalmeq_lock;
#define FINALMEQ_LOCK_INIT()	monitorRegister(_finalmeq_lock, \
						"Finalize me queue lock")
#define FINALMEQ_LOCK()		sysMonitorEnter(_finalmeq_lock)
#define FINALMEQ_LOCKED()	sysMonitorEntered(_finalmeq_lock)
#define FINALMEQ_UNLOCK()	sysMonitorExit(_finalmeq_lock)
#define FINALMEQ_NOTIFY()	sysMonitorNotify(_finalmeq_lock)
#define FINALMEQ_WAIT()		sysMonitorWait(_finalmeq_lock, \
					       SYS_TIMEOUT_INFINITY, FALSE)

extern void InitializeFinalizer(void);
extern void InitializeFinalizerThread(void);

extern void runFinalization(void);
extern void finalizeOnExit(void);

#endif /* _FINALIZE_H_ */
