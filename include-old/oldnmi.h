/*
 * @(#)oldnmi.h	1.4 00/02/02
 *
 * Copyright 1994-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

#ifndef _JAVASOFT__OLDNMI_H__
#define _JAVASOFT__OLDNMI_H__

#include "oobj.h"
#include "interpreter.h"
#include "java_lang_String.h"

ClassClass * Thread_classblock;

ClassClass* get_classClass(void);
ClassClass* get_classObject(void);

/*
 * Old allocation interfaces.
 */

JHandle *ObjAlloc(ClassClass *cb, int ignored);
JHandle *ArrayAlloc(int type, int size);

/*
 * Old version of the monitor interface.
 */

void monitorEnter(uintptr_t);
void monitorExit(uintptr_t);
void monitorWait(uintptr_t, int64_t);
void monitorNotify(uintptr_t);
void monitorNotifyAll(uintptr_t);

/*
 * Get the characters of the String object into a unicode string buffer.
 * No allocation occurs. Assumes that len is less than or equal to
 * the length of the string, and that the buf is at least len+1 unicodes
 * in size. The unicode buffer's address is returned.
 */
unicode *javaString2unicode(Hjava_lang_String *, unicode *, int);

HObject *newobject(ClassClass *cb, unsigned char *pc, struct execenv *ee);


#endif /* !_JAVASOFT__OLDNMI_H__ */
