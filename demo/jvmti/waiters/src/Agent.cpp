/*
 * @(#)Agent.cpp	1.9 05/11/17
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

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stddef.h>

#include "jni.h"
#include "jvmti.h"

#include "agent_util.h"

#include "Monitor.hpp"
#include "Thread.hpp"
#include "Agent.hpp"

/* Implementation of the Agent class */

/* Given a jvmtiEnv* and jthread, find the Thread instance */
Thread *
Agent::get_thread(jvmtiEnv *jvmti, JNIEnv *env, jthread thread)
{
    jvmtiError err;
    Thread    *t;

    /* This should always be in the Thread Local Storage */
    t = NULL;
    err = jvmti->GetThreadLocalStorage(thread, (void**)&t);
    check_jvmti_error(jvmti, err, "get thread local storage");
    if ( t == NULL ) {
	/* This jthread has never been seen before? */
	stdout_message("WARNING: Never before seen jthread?\n");
	t = new Thread(jvmti, env, thread);
	err = jvmti->SetThreadLocalStorage(thread, (const void*)t);
        check_jvmti_error(jvmti, err, "set thread local storage");
    }
    return t;
}

/* Given a jvmtiEnv* and jobject, find the Monitor instance or create one */
Monitor *
Agent::get_monitor(jvmtiEnv *jvmti, JNIEnv *env, jobject object)
{
    jvmtiError err;
    Monitor   *m;

    /* We use tags to track these, the tag is the Monitor pointer */
    err = jvmti->RawMonitorEnter(lock); {
	check_jvmti_error(jvmti, err, "raw monitor enter");
	
	/* The raw monitor enter/exit protects us from creating two
	 *   instances for the same object.
	 */
	jlong tag;

	m   = NULL;
	tag = (jlong)0;
	err = jvmti->GetTag(object, &tag);
	check_jvmti_error(jvmti, err, "get tag");
	/*LINTED*/
	m = (Monitor *)(void *)(ptrdiff_t)tag;
	if ( m == NULL ) {
	    m = new Monitor(jvmti, env, object);
	    /*LINTED*/
	    tag = (jlong)(ptrdiff_t)(void *)m;
	    err = jvmti->SetTag(object, tag);
	    check_jvmti_error(jvmti, err, "set tag");
	    /* Save monitor on list */
	    monitor_list = (Monitor**)realloc((void*)monitor_list, 
				(monitor_count+1)*(int)sizeof(Monitor*));
	    monitor_list[monitor_count++] = m;
	}
    } err = jvmti->RawMonitorExit(lock);
    check_jvmti_error(jvmti, err, "raw monitor exit");
    
    return m;
}

/* VM initialization and VM death calls to Agent */
Agent::Agent(jvmtiEnv *jvmti, JNIEnv *env, jthread thread) 
{
    jvmtiError err;
    
    stdout_message("Agent created..\n");
    stdout_message("VMInit...\n");
    /* Create a Monitor lock to use */
    err = jvmti->CreateRawMonitor("waiters Agent lock", &lock);
    check_jvmti_error(jvmti, err, "create raw monitor");
    /* Start monitor list */
    monitor_count = 0;
    monitor_list  = (Monitor**)malloc((int)sizeof(Monitor*));
}

Agent::~Agent() 
{
    stdout_message("Agent reclaimed..\n");
}

void Agent::vm_death(jvmtiEnv *jvmti, JNIEnv *env) 
{
    jvmtiError err;
    
    /* Delete all Monitors we allocated */
    for ( int i = 0; i < (int)monitor_count; i++ ) {
	delete monitor_list[i];
    }
    free(monitor_list);
    /* Destroy the Monitor lock to use */
    err = jvmti->DestroyRawMonitor(lock);
    check_jvmti_error(jvmti, err, "destroy raw monitor");
    /* Print death message */
    stdout_message("VMDeath...\n");
}

/* Thread start event, setup a new thread */
void Agent::thread_start(jvmtiEnv *jvmti, JNIEnv *env, jthread thread) 
{
    jvmtiError err;
    Thread    *t;

    /* Allocate a new Thread instance, put it in the Thread Local
     *    Storage for easy access later.
     */
    t = new Thread(jvmti, env, thread);
    err = jvmti->SetThreadLocalStorage(thread, (const void*)t);
    check_jvmti_error(jvmti, err, "set thread local storage");
}


/* Thread end event, we need to reclaim the space */
void Agent::thread_end(jvmtiEnv *jvmti, JNIEnv *env, jthread thread) 
{
    jvmtiError err;
    Thread    *t;
   
    /* Find the thread */
    t = get_thread(jvmti, env, thread);

    /* Clear out the Thread Local Storage */
    err = jvmti->SetThreadLocalStorage(thread, (const void*)NULL);
    check_jvmti_error(jvmti, err, "set thread local storage");

    /* Reclaim the C++ object space */
    delete t;
}

/* Monitor contention begins for a thread. */
void Agent::monitor_contended_enter(jvmtiEnv* jvmti, JNIEnv *env, 
	     jthread thread, jobject object) 
{
    get_monitor(jvmti, env, object)->contended();
    get_thread(jvmti, env, thread)->
		monitor_contended_enter(jvmti, env, thread, object);
}

/* Monitor contention ends for a thread. */
void Agent::monitor_contended_entered(jvmtiEnv* jvmti, JNIEnv *env,
	       jthread thread, jobject object) 
{
    /* Do nothing for now */
}

/* Monitor wait begins for a thread. */
void Agent::monitor_wait(jvmtiEnv* jvmti, JNIEnv *env, 
	     jthread thread, jobject object, jlong timeout) 
{
    get_monitor(jvmti, env, object)->waited();
    get_thread(jvmti, env, thread)->
		monitor_wait(jvmti, env, thread, object, timeout);
}

/* Monitor wait ends for a thread. */
void Agent::monitor_waited(jvmtiEnv* jvmti, JNIEnv *env,
	       jthread thread, jobject object, jboolean timed_out) 
{
    if ( timed_out ) {
	get_monitor(jvmti, env, object)->timeout();
    }
    get_thread(jvmti, env, thread)->
		monitor_waited(jvmti, env, thread, object, timed_out);
}

/* A tagged object has been freed */
void Agent::object_free(jvmtiEnv* jvmti, jlong tag) 
{
    /* We just cast the tag to a C++ pointer and delete it.
     *   we know it can only be a Monitor *.
     */
    Monitor *m;
    /*LINTED*/
    m = (Monitor *)(ptrdiff_t)tag;
    delete m;
}

