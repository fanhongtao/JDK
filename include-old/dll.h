/*
 * @(#)dll.h	1.3 98/09/15
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

#ifndef _JAVASOFT_DLL_H_
#define _JAVASOFT_DLL_H_

#include <jni.h>

/* DLL.H: A common interface for helper DLLs loaded by the VM.
 * Each library exports the main entry point "DLL_Initialize". Through
 * that function the programmer can obtain a function pointer which has
 * type "GetInterfaceFunc." Through the function pointer the programmer
 * can obtain other interfaces supported in the DLL.
 */
#ifdef __cplusplus
extern "C" {
#endif

typedef jint (JNICALL * GetInterfaceFunc)
       (void **intfP, const char *name, jint ver);

jint JNICALL DLL_Initialize(GetInterfaceFunc *, void *args);

#ifdef __cplusplus
}
#endif

#endif /* !_JAVASOFT_DLL_H_ */
