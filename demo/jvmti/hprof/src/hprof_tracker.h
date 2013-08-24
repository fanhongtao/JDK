/*
 * @(#)hprof_tracker.h	1.9 05/11/17
 * 
 * Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright notice, 
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may 
 * be used to endorse or promote products derived from this software without 
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL 
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST 
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY 
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, 
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

#ifndef HPROF_TRACKER_H
#define HPROF_TRACKER_H

/* The internal qualified classname */

#define OBJECT_CLASS_SIG	"Ljava/lang/Object;"
#define OBJECT_INIT_NAME	"<init>"
#define OBJECT_INIT_SIG		"()V"

#define TRACKER_PACKAGE         "com/sun/demo/jvmti/hprof"
#define TRACKER_CLASS_NAME      TRACKER_PACKAGE "/Tracker"
#define TRACKER_CLASS_SIG       "L" TRACKER_CLASS_NAME ";"

#define TRACKER_NEWARRAY_NAME        "NewArray"
#define TRACKER_NEWARRAY_SIG         "(Ljava/lang/Object;)V"
#define TRACKER_NEWARRAY_NATIVE_NAME "nativeNewArray"
#define TRACKER_NEWARRAY_NATIVE_SIG  "(Ljava/lang/Object;Ljava/lang/Object;)V"

#define TRACKER_OBJECT_INIT_NAME        "ObjectInit"
#define TRACKER_OBJECT_INIT_SIG         "(Ljava/lang/Object;)V"
#define TRACKER_OBJECT_INIT_NATIVE_NAME "nativeObjectInit"
#define TRACKER_OBJECT_INIT_NATIVE_SIG  "(Ljava/lang/Object;Ljava/lang/Object;)V"

#define TRACKER_CALL_NAME               "CallSite"
#define TRACKER_CALL_SIG                "(II)V"
#define TRACKER_CALL_NATIVE_NAME        "nativeCallSite"
#define TRACKER_CALL_NATIVE_SIG         "(Ljava/lang/Object;II)V"


#define TRACKER_RETURN_NAME             "ReturnSite"
#define TRACKER_RETURN_SIG              "(II)V"
#define TRACKER_RETURN_NATIVE_NAME      "nativeReturnSite"
#define TRACKER_RETURN_NATIVE_SIG       "(Ljava/lang/Object;II)V"

#define TRACKER_ENGAGED_NAME               "engaged"
#define TRACKER_ENGAGED_SIG                "I"

void     tracker_setup_class(void);
void     tracker_setup_methods(JNIEnv *env);
void     tracker_engage(JNIEnv *env);
void     tracker_disengage(JNIEnv *env);
jboolean tracker_method(jmethodID method);

#endif
