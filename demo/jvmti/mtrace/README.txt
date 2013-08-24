#
# @(#)README.txt	1.11 06/01/28
#
# Copyright 2006 Sun Microsystems, Inc. All rights reserved.
# SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
#

mtrace: @(#)README.txt	1.11 06/01/28

This agent library can be used to track method call and return counts.
It uses the same java_crw_demo library used by HPROF to do BCI on all or
selected classfiles loaded into the Virtual Machine.  It will print out a 
sorted list of the most heavily used classes (as determined by the number 
of method calls into the class) and also include the call and return counts 
for all methods that are called.  

You can use this agent library as follows:

    java -Xbootclasspath/a:mtrace.jar -agentlib:mtrace ...

To get help on the available options try:

    java -agentlib:mtrace=help

See ${JAVA_HOME}/demo/jvmti/index.html for help running and building agents.

