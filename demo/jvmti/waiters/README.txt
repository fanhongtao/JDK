#
# @(#)README.txt	1.6 10/03/23
#
# Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
# ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
#

waiters: @(#)README.txt	1.6 10/03/23

This agent library can be used to track threads that wait on monitors.
This agent is written in C++.

You can use this agent library as follows:

    java -agentlib:waiters ...

To get help on the available options try:

    java -agentlib:waiters=help

See ${JAVA_HOME}/demo/jvmti/index.html for help running and building agents.

