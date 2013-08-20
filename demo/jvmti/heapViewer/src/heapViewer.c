/*
 * @(#)heapViewer.c	1.5 04/07/27
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
#include <stddef.h>
#include <stdlib.h>
#include <string.h>

#include "jni.h"
#include "jvmti.h"

/* Global static data */
typedef struct {
    jboolean      vmDeathCalled;
    jboolean      dumpInProgress;
    jrawMonitorID lock;
    int           totalCount;
} GlobalData;
static GlobalData globalData, *gdata = &globalData;

/* Typedef to hold class details */
typedef struct {
    char *signature;
    int   count;
    int   space;
} ClassDetails;

/* Check for NULL pointer error */
#define CHECK_FOR_NULL(ptr) \
	checkForNull(ptr, __FILE__, __LINE__)
static void
checkForNull(void *ptr, char *file, int line)
{
    if ( ptr == NULL ) {
	fprintf(stderr, "ERROR: NULL pointer error in %s:%d\n", file, line);
	abort();
    }
}

/* Deallocate JVMTI memory */
static void
deallocate(jvmtiEnv *jvmti, void *p)
{
    jvmtiError err;
    
    err = (*jvmti)->Deallocate(jvmti, (unsigned char *)p);
    if ( err != JVMTI_ERROR_NONE ) {
	fprintf(stderr, "ERROR: JVMTI Deallocate error err=%d\n", err);
	abort();
    }
}

/* Get name for JVMTI error code */
static char *
getErrorName(jvmtiEnv *jvmti, jvmtiError errnum)
{
    jvmtiError err;
    char      *name;

    err = (*jvmti)->GetErrorName(jvmti, errnum, &name);
    if ( err != JVMTI_ERROR_NONE ) {
	fprintf(stderr, "ERROR: JVMTI GetErrorName error err=%d\n", err);
	abort();
    }
    return name;
}

/* Check for JVMTI error */
#define CHECK_JVMTI_ERROR(jvmti, err) \
	checkJvmtiError(jvmti, err, __FILE__, __LINE__)
static void
checkJvmtiError(jvmtiEnv *jvmti, jvmtiError err, char *file, int line)
{
    if ( err != JVMTI_ERROR_NONE ) {
	char *name;

	name = getErrorName(jvmti, err);
	fprintf(stderr, "ERROR: JVMTI error err=%d(%s) in %s:%d\n", 
		err, name, file, line);
	deallocate(jvmti, name);
	abort();
    }
}

/* Enter agent monitor protected section */
static void
enterAgentMonitor(jvmtiEnv *jvmti)
{
    CHECK_JVMTI_ERROR(jvmti, (*jvmti)->RawMonitorEnter(jvmti, gdata->lock));
}

/* Exit agent monitor protected section */
static void
exitAgentMonitor(jvmtiEnv *jvmti)
{
    CHECK_JVMTI_ERROR(jvmti, (*jvmti)->RawMonitorExit(jvmti, gdata->lock));
}

/* Heap object callback */
static jvmtiIterationControl JNICALL 
heapObject(jlong class_tag, jlong size, jlong* tag_ptr, void* user_data)
{
    if ( class_tag != (jlong)0 ) {
	ClassDetails *d;
	
	d = (ClassDetails*)(void*)(ptrdiff_t)class_tag;
	gdata->totalCount++;
	d->count++;
	d->space += size;
    }
    return JVMTI_ITERATION_CONTINUE;
}

/* Compare two ClassDetails */
static int
compareDetails(const void *p1, const void *p2)
{
    return ((ClassDetails*)p2)->space - ((ClassDetails*)p1)->space;
}

/* Callback for JVMTI_EVENT_DATA_DUMP_REQUEST (Ctrl-\ or at exit) */
static void JNICALL
dataDumpRequest(jvmtiEnv *jvmti)
{
    enterAgentMonitor(jvmti); {
	if ( !gdata->vmDeathCalled && !gdata->dumpInProgress ) {
	    jvmtiError    err;
	    void         *user_data;
	    jclass       *classes;
	    jint          count;
	    jint          i;
	    ClassDetails *details;

	    gdata->dumpInProgress = JNI_TRUE;
	    gdata->totalCount = 0;
	   
	    /* Get all the loaded classes */
	    err = (*jvmti)->GetLoadedClasses(jvmti, &count, &classes);
	    CHECK_JVMTI_ERROR(jvmti, err);

	    /* Setup an area to hold details about these classes */
	    details = (ClassDetails*)calloc(sizeof(ClassDetails), count);
            CHECK_FOR_NULL(details);
	    for ( i = 0 ; i < count ; i++ ) {
		char *sig;

		/* Get and save the class signature */
		err = (*jvmti)->GetClassSignature(jvmti, classes[i], &sig, NULL);
	        CHECK_JVMTI_ERROR(jvmti, err);
                CHECK_FOR_NULL(sig);
		details[i].signature = strdup(sig);
		deallocate(jvmti, sig);

		/* Tag this jclass */
		err = (*jvmti)->SetTag(jvmti, classes[i], 
				    (jlong)(ptrdiff_t)(void*)(&details[i]));
	        CHECK_JVMTI_ERROR(jvmti, err);
	    }
	    
	    /* Iterate over the heap and count up uses of jclass */
	    err = (*jvmti)->IterateOverHeap(jvmti, JVMTI_HEAP_OBJECT_EITHER, 
					    &heapObject, NULL);
	    CHECK_JVMTI_ERROR(jvmti, err);

	    /* Remove tags */
	    for ( i = 0 ; i < count ; i++ ) {
		/* Un-Tag this jclass */
		err = (*jvmti)->SetTag(jvmti, classes[i], (jlong)0);
	        CHECK_JVMTI_ERROR(jvmti, err);
	    }
	    
	    /* Sort details by space used */
	    qsort(details, count, sizeof(ClassDetails), &compareDetails);
	   
	    /* Print out sorted table */
	    fprintf(stdout, "Heap View, Total of %d objects found.\n\n",
			 gdata->totalCount);
	    
	    fprintf(stdout, "Space      Count      Class Signature\n");
	    fprintf(stdout, "---------- ---------- ----------------------\n");
	    
	    for ( i = 0 ; i < count ; i++ ) {
		if ( details[i].space == 0 || i > 20 ) {
		    break;
		}
	        fprintf(stdout, "%10d %10d %s\n",
		    details[i].space, details[i].count, details[i].signature);
	    }
	    fprintf(stdout, "---------- ---------- ----------------------\n\n");
	    fflush(stdout);

	    /* Free up all allocated space */
	    deallocate(jvmti, classes);
	    for ( i = 0 ; i < count ; i++ ) {
		if ( details[i].signature != NULL ) {
		    free(details[i].signature);
	        }
	    }
	    free(details);
	    
	    gdata->dumpInProgress = JNI_FALSE;
	}
    } exitAgentMonitor(jvmti);
}

/* Callback for JVMTI_EVENT_VM_INIT */
static void JNICALL 
vmInit(jvmtiEnv *jvmti, JNIEnv *env, jthread thread)
{
    enterAgentMonitor(jvmti); {
        jvmtiError          err;
	
	err = (*jvmti)->SetEventNotificationMode(jvmti, JVMTI_ENABLE, 
			    JVMTI_EVENT_DATA_DUMP_REQUEST, NULL);
	CHECK_JVMTI_ERROR(jvmti, err);
    } exitAgentMonitor(jvmti);
}

/* Callback for JVMTI_EVENT_VM_DEATH */
static void JNICALL 
vmDeath(jvmtiEnv *jvmti, JNIEnv *env)
{
    jvmtiError          err;
   
    /* Make sure everything has been garbage collected */
    err = (*jvmti)->ForceGarbageCollection(jvmti);
    CHECK_JVMTI_ERROR(jvmti, err);

    /* Disable events and dump the heap information */
    enterAgentMonitor(jvmti); {
	err = (*jvmti)->SetEventNotificationMode(jvmti, JVMTI_DISABLE, 
			    JVMTI_EVENT_DATA_DUMP_REQUEST, NULL);
	CHECK_JVMTI_ERROR(jvmti, err);
	
	dataDumpRequest(jvmti);
        
	gdata->vmDeathCalled = JNI_TRUE;
    } exitAgentMonitor(jvmti);
}

/* Agent_OnLoad() is called first, we prepare for a VM_INIT event here. */
JNIEXPORT jint JNICALL
Agent_OnLoad(JavaVM *vm, char *options, void *reserved)
{
    jint                rc;
    jvmtiError          err;
    jvmtiCapabilities   capabilities;
    jvmtiEventCallbacks callbacks;
    jvmtiEnv           *jvmti;
    
    /* Get JVMTI environment */
    jvmti = NULL;
    rc = (*vm)->GetEnv(vm, (void **)&jvmti, JVMTI_VERSION);
    if (rc != JNI_OK) {
	fprintf(stderr, "ERROR: Unable to create jvmtiEnv, GetEnv failed, error=%d\n", rc);
	return -1;
    }
    CHECK_FOR_NULL(jvmti);

    /* Get/Add JVMTI capabilities */ 
    err = (*jvmti)->GetCapabilities(jvmti, &capabilities);
    CHECK_JVMTI_ERROR(jvmti, err);
    capabilities.can_tag_objects = 1;
    capabilities.can_generate_garbage_collection_events = 1;
    err = (*jvmti)->AddCapabilities(jvmti, &capabilities);
    CHECK_JVMTI_ERROR(jvmti, err);

    /* Create the raw monitor */
    err = (*jvmti)->CreateRawMonitor(jvmti, "agent lock", &(gdata->lock));
    CHECK_JVMTI_ERROR(jvmti, err);
    
    /* Set callbacks and enable event notifications */
    memset(&callbacks, 0, sizeof(callbacks));
    callbacks.VMInit                  = &vmInit;
    callbacks.VMDeath                 = &vmDeath;
    callbacks.DataDumpRequest         = &dataDumpRequest;
    err = (*jvmti)->SetEventCallbacks(jvmti, &callbacks, sizeof(callbacks));
    CHECK_JVMTI_ERROR(jvmti, err);
    err = (*jvmti)->SetEventNotificationMode(jvmti, JVMTI_ENABLE, 
			JVMTI_EVENT_VM_INIT, NULL);
    CHECK_JVMTI_ERROR(jvmti, err);
    err = (*jvmti)->SetEventNotificationMode(jvmti, JVMTI_ENABLE, 
			JVMTI_EVENT_VM_DEATH, NULL);
    CHECK_JVMTI_ERROR(jvmti, err);
    return 0;
}

/* Agent_OnUnload() is called last */
JNIEXPORT void JNICALL
Agent_OnUnload(JavaVM *vm)
{
}

