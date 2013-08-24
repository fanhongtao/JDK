#
# @(#)README.txt	1.6 06/01/28
#
# Copyright 2006 Sun Microsystems, Inc. All rights reserved.
# SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
#

heapTracker: @(#)README.txt	1.6 06/01/28

This agent library can be used to track object allocations.
It uses the same java_crw_demo library used by HPROF to do BCI
on all classfiles loaded into the Virtual Machine.

You can use this agent library as follows:

    java -agentlib:heapTracker ...

To get help on the available options try:

    java -agentlib:heapTracker=help

See ${JAVA_HOME}/demo/jvmti/index.html for help running and building agents.

