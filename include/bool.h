/*
 * @(#)bool.h	1.6 98/07/01
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

#ifndef	_BOOL_H_
#define	_BOOL_H_

#undef	TRUE
#undef	FALSE

typedef	enum {
    FALSE = 0,
    TRUE = 1
} bool_t;

#endif /* !_BOOL_H_ */
