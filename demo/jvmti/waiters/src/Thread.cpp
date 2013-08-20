/*
 * @(#)Thread.cpp	1.2 04/07/27
 * 
 * Copyright (c) 2004 Sun Microsystems, Inc. All Rights Reserved.
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

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "jni.h"
#include "jvmti.h"
#include "Thread.hpp"

/* Implementation of the Thread class */

Thread::Thread(jvmtiEnv *jvmti, JNIEnv *env, jthread thread) {
    jvmtiThreadInfo info;
    
    /* Get and save the name of the thread */
    info.name = NULL;
    (void)strcpy(name, "Unknown");
    jvmti->GetThreadInfo(thread, &info);
    if ( info.name != NULL ) {
        (void)strncpy(name, info.name, (int)sizeof(name)-1);
	name[(int)sizeof(name)-1] = 0;
	jvmti->Deallocate((unsigned char*)info.name);
    }

    /* Clear thread counters */
    contends = 0;
    waits    = 0;
    timeouts = 0;
}

Thread::~Thread() {
    /* Send out summary message */
    fprintf(stdout, "Thread %s summary: %d waits plus %d contended\n",
	name, waits, contends);
}

void Thread::monitor_contended_enter(jvmtiEnv* jvmti, JNIEnv *env, 
	     jthread thread, jobject object) {
    contends++;
}

void Thread::monitor_wait(jvmtiEnv* jvmti, JNIEnv *env,
	       jthread thread, jobject object, jlong timeout) {
    waits++;
}

void Thread::monitor_waited(jvmtiEnv* jvmti, JNIEnv *env,
	       jthread thread, jobject object, jboolean timed_out) {
    if ( timed_out ) {
	timeouts++;
    }
}

