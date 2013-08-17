/*
 * @(#)bool.h	1.9 98/09/21
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

#ifndef _JAVASOFT_BOOL_H_
#define _JAVASOFT_BOOL_H_

#undef	TRUE
#undef	FALSE

typedef	enum {
    FALSE = 0,
    TRUE = 1
} bool_t;

#endif /* !_JAVASOFT_BOOL_H_ */
