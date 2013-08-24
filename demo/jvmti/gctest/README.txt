#
# @(#)README.txt	1.7 06/01/28
#
# Copyright 2006 Sun Microsystems, Inc. All rights reserved.
# SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
#

gctest: @(#)README.txt	1.7 06/01/28

This agent library can be used to track garbage collection events.

You can use this agent library as follows:

    java -agentlib:gctest ...  
	  
To get help on the available options try:

    java -agentlib:gctest=help

See ${JAVA_HOME}/demo/jvmti/index.html for help running and building agents.

The Events JVMTI_EVENT_GARBAGE_COLLECTION_START,
JVMTI_EVENT_GARBAGE_COLLECTION_FINISH, and JVMTI_EVENT_OBJECT_FREE 
all have limitations as to what can be called directly inside the 
agent callback functions (e.g. no JNI calls are allowed, and limited 
interface calls can be made). However, by using raw monitors and a separate 
watcher thread, this agent demonstrates how these limitations can be 
easily avoided, allowing the watcher thread to do just about anything
after the JVMTI_EVENT_GARBAGE_COLLECTION_FINISH event.

