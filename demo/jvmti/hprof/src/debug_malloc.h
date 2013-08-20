/*
 * @(#)debug_malloc.h	1.5 04/07/27
 * 
 * Copyright (c) 2004 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright notice, 
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may 
 * be used to endorse or promote products derived from this software without 
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL 
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST 
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY 
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, 
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

/* ***********************************************************************
 *
 * The source file debug_malloc.c should be included with your sources.
 *
 * The object file debug_malloc.o should be included with your object files.
 *
 *   WARNING: Any memory allocattion from things like memalign(), valloc(),
 *            or any memory not coming from these macros (malloc, realloc,
 *            calloc, and strdup) will fail miserably.
 *
 * ***********************************************************************
 */

#ifndef _DEBUG_MALLOC_H
#define _DEBUG_MALLOC_H

#ifdef DEBUG

#include <stdlib.h>
#include <string.h>

/* The real functions behind the macro curtains. */

void           *debug_malloc(size_t, const char *, int);
void           *debug_realloc(void *, size_t, const char *, int);
void           *debug_calloc(size_t, size_t, const char *, int);
char           *debug_strdup(const char *, const char *, int);
void            debug_free(void *, const char *, int);

#endif

void            debug_malloc_verify(const char*, int);
#undef malloc_verify
#define malloc_verify()     debug_malloc_verify(__FILE__, __LINE__)

void            debug_malloc_police(const char*, int);
#undef malloc_police
#define malloc_police()     debug_malloc_police(__FILE__, __LINE__)

#endif
