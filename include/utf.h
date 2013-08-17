/*
 * @(#)utf.h	1.7 98/07/01
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
 * Prototypes for the various UTF support functions.
 */

#ifndef _UTF_H_
#define _UTF_H_

char *unicode2utf(unicode *unistring, int length, char *buffer, int buflength);
int unicode2utfstrlen(unicode *unistring, int unilength);
int utfstrlen(char *utfstring);
void utf2unicode(char *utfstring, unicode *unistring, 
		int max_length, int *lengthp);
bool_t is_simple_utf(char *utfstring);

unicode next_utf2unicode(char **utfstring);

#endif /* !_UTF_H_ */ 
