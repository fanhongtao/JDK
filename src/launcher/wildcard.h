/*
 * @(#)wildcard.h	1.5 05/12/05
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

#ifndef WILDCARD_H_
#define WILDCARD_H_

#ifdef EXPAND_CLASSPATH_WILDCARDS
const char *JLI_WildcardExpandClasspath(const char *classpath);
#else
#define JLI_WildcardExpandClasspath(s) (s)
#endif

#endif /* include guard */
