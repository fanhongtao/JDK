/*
 * @(#)jli_util.h	1.2	06/02/25
 *
 * Copyright 2005 Sun Microsystems, Inc.  All rights reserved.
 * Use is subject to license terms.
 */

#ifndef _JLI_UTIL_H
#define _JLI_UTIL_H

#include <stdlib.h>

void *JLI_MemAlloc(size_t size);
void *JLI_MemRealloc(void *ptr, size_t size);
char *JLI_StringDup(const char *s1);
void  JLI_MemFree(void *ptr);

#endif	/* _JLI_UTIL_H */
