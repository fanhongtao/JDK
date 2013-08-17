/*
 * @(#)javaString.h	1.22 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * Java string utilities
 */

#ifndef _JAVASOFT_JAVASTRING_H_
#define _JAVASOFT_JAVASTRING_H_

#include "oobj.h"

#include "java_lang_String.h"


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

#endif /* !_JAVASOFT_JAVASTRING_H_ */
