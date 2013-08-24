#
# @(#)README.txt	1.5 06/01/28
#
# Copyright 2006 Sun Microsystems, Inc. All rights reserved.
# SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
#

waiters: @(#)README.txt	1.5 06/01/28

This agent library can be used to track threads that wait on monitors.
This agent is written in C++.

You can use this agent library as follows:

    java -agentlib:waiters ...

To get help on the available options try:

    java -agentlib:waiters=help

See ${JAVA_HOME}/demo/jvmti/index.html for help running and building agents.

