#
# @(#)README.txt	1.1 06/01/28
#
# Copyright 2006 Sun Microsystems, Inc. All rights reserved.
# SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
#

minst: @(#)README.txt	1.1 06/01/28

This agent library can be used to inject code at method calls.
It uses the same java_crw_demo library used by HPROF to do BCI on all 
or selected classfiles loaded into the Virtual Machine.  
The class Minst.java can be customized to do whatever you wish,
within reason of course, and does not call native methods directly.

You can use this agent library as follows:

    java -agentlib:minst ...

To get help on the available options try:

    java -agentlib:minst=help

See ${JAVA_HOME}/demo/jvmti/index.html for help running and building agents.

