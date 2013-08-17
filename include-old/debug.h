/*
 * @(#)debug.h	1.18 98/09/21
 *
 * Copyright 1994-1998 by Sun Microsystems, Inc.,
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
