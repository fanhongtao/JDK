/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
