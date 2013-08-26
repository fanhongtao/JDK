#
# @(#)README.txt	1.7 10/03/23
#
# Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
# ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
#

versionCheck: @(#)README.txt	1.7 10/03/23

This agent library just makes some simple calls and checks 
the version of the interface being used to build the agent, 
with that supplied by the VM at runtime.

You can use this agent library as follows:

    java -agentlib:versionCheck ...

See ${JAVA_HOME}/demo/jvmti/index.html for help running and building agents.

