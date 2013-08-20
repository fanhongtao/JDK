
mtrace: @(#)README.txt	1.10 04/06/23

This agent library can be used to track method call and return counts.
It uses the same java_crw_demo library used by HPROF to do BCI on all the 
classfiles loaded into the Virtual Machine.  It will print out a 
sorted list of the most heavily used classes (as determined by the number 
of method calls into the class) and also include the call and return counts 
for all methods that are called.  

You can use this agent library as follows:

    java -Xbootclasspath/a:mtrace.jar -agentlib:mtrace ...

To get help on the available options try:

    java -agentlib:mtrace=help

If the Virtual Machine complains that it can't find the library, you 
may need to add the directory containing the library into the 
LD_LIBRARY_PATH environment variable (Unix), or the PATH environment 
variable (Windows).

