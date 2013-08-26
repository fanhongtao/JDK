#
# @(#)README.txt	1.9 10/03/23
#
# Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
# ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
#

heapViewer: @(#)README.txt	1.9 10/03/23

This agent library demonstrates how to get an easy view of the
heap in terms of total object count and space used.
It uses GetLoadedClasses(), SetTag(), and IterateThroughHeap()
to count up all the objects of all the current loaded classes.
The heap dump will happen at the event JVMTI_EVENT_VM_DEATH, or the
event JVMTI_EVENT_DATA_DUMP_REQUEST.

It also demonstrates some more robust agent error handling using 
GetErrorName(),

Using the heap iterate functions, lots of statistics can be generated
without resorting to using Byte Code Instrumentation (BCI).

You can use this agent library as follows:

    java -agentlib:heapViewer ...

To get help on the available options try:

    java -agentlib:heapViewer=help

See ${JAVA_HOME}/demo/jvmti/index.html for help running and building agents.

