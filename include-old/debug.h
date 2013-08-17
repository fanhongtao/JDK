/*
 * @(#)debug.h	1.20 00/02/02
 *
 * Copyright 1994-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

/*
 * Debugging hooks	1/22/92
 */

#ifndef _JAVASOFT_DEBUG_H_
#define _JAVASOFT_DEBUG_H_

#include <stdarg.h>

#include "bool.h"

struct sys_thread;

void DumpThreads(void);
void DumpMonitors(void);


void HandleSignalInVM(bool_t noncritical);

void panic (const char *, ...);

#endif /* !_JAVASOFT_DEBUG_H_ */
