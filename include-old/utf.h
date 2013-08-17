/*
 * @(#)utf.h	1.2 00/01/12
 *
 * Copyright 1994-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

/*
 * Prototypes for the various UTF support functions.
 */

#ifndef _JAVASOFT_UTF_H_
#define _JAVASOFT_UTF_H_

char *unicode2utf(unicode *unistring, int length, char *buffer, int buflength);
int unicode2utfstrlen(unicode *unistring, int unilength);
int utfstrlen(char *utfstring);
void utf2unicode(char *utfstring, unicode *unistring, 
		int max_length, int *lengthp);
bool_t is_simple_utf(char *utfstring);

unicode next_utf2unicode(char **utfstring);

#endif /* !_JAVASOFT_UTF_H_ */
