/*
 * @(#)javaString.h	1.16 98/07/01
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
 * Java string utilities
 */

#ifndef _JAVASTRING_H_
#define _JAVASTRING_H_

#include "oobj.h"

#include "java_lang_String.h"


/*
 * Print the String object with prints.
 */
void javaStringPrint(Hjava_lang_String *);

/*
 * Return the length of the String object.
 */
int javaStringLength(Hjava_lang_String *);


/*
 * Create and return a new Java String object, initialized from the C string.
 */
Hjava_lang_String *makeJavaString(char *, int);

/*
 * Create a new C string initialized from the specified Java string,
 * and return a pointer to it.
 * For makeCString, temporary storage is allocated and released automatically
 * when all references to the returned value are eliminated. WARNING: You 
 * must keep this pointer in a variable to prevent the storage from getting
 * garbage collected.
 * For allocCString, a "malloc" is used to get the storage; the caller is
 * responsible for "free"ing the pointer that is returned.
 * 
 */
char *makeCString(Hjava_lang_String *s);
char *allocCString(Hjava_lang_String *s);

/*
 * Get the characters of the String object into a unicode string buffer.
 * No allocation occurs. Assumes that len is less than or equal to
 * the length of the string, and that the buf is at least len+1 unicodes
 * in size. The unicode buffer's address is returned.
 */
unicode *javaString2unicode(Hjava_lang_String *, unicode *, int);

/*
 * Get the characters of the String object into a C string buffer.
 * No allocation occurs. Assumes that len is the size of the buffer.
 * The C string's address is returned.
 */
char *javaString2CString(Hjava_lang_String *, char *, int);

/*
 * convert Java String to platform encoding string.
 */
char *makePlatformCString(Hjava_lang_String *);

/*
 * convert platform encoding string to Java String.
 */
Hjava_lang_String *makeJavaStringFromPlatformCString(char *, int);

/*
 * Returns the number of bytes needed to hold a Java string in UTF
 * format, NOT including the terminating NULL.
 *
 * Returns -1 if something is wrong.
 */
int
javaStringUTFLength(HString *s);

/*
 * Fill buf with unicode representation of hstr, upto buflen chars.
 * The UTF string will be NUL-terminated.
 *
 * If both buf and buflen are 0, malloc an appropriately sized
 * buffer for the result.
 *
 * Return result buffer.
 */
char *javaString2UTF(HString *, char *, int);

/*
 * Create a Java String whose characters are initialized from
 * the supplied NUL-terminated UTF string.
 */
HString *makeJavaStringUTF(char *);

#endif /* !_JAVASTRING_H_ */
