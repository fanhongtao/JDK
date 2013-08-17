/*
 * @(#)debug.h	1.19 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
