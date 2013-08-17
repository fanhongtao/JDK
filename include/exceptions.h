/*
 * @(#)exceptions.h	1.11 98/07/01
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
 * The Java runtime exception handling mechanism.
 */

#ifndef _EXCEPTIONS_H_
#define _EXCEPTIONS_H_

/*
 * Header files.
 */

#include "oobj.h"
#include "threads.h"

/*
 * Type definitions.
 */

/*
 * Exceptions, as defined in the Java93 spec, are subclasses of Object.
 *
 * The list of exceptions thrown by the runtime and other commonly
 * used classes can be found in StandardDefs.gt.
 */
typedef JHandle *exception_t;

/*
 * The exception mechanism has a set of preallocated exception objects
 * that can be thrown in the face of utter confusion and system meltdown.
 * internal_exception_t enumerates these objects.
 */
typedef enum {
    IEXC_NONE,					/* A null object */
    IEXC_NoClassDefinitionFound,
    IEXC_OutOfMemory,
    IEXC_END					/* Keep this last */
} internal_exception_t;


/*
 * External routines.
 */

/*
 * exceptionInit() -- Initialize the exception subsystem.
 */
extern void exceptionInit(void);

/*
 * exceptionInternalObject() -- Return an internal, preallocated
 *	exception object.  These are shared by all threads, so they
 *	should only be used in a last ditch effort.
 */
extern JHandle *exceptionInternalObject(internal_exception_t exc);

/*
 * exceptionDescribe() -- Print out a description of a given exception
 * 	object.
 */
extern void exceptionDescribe(struct execenv *ee);

#endif /* !_EXCEPTIONS_H_ */

