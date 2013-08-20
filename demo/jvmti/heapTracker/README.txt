heapTracker: @(#)README.txt	1.4 04/06/23

This agent library can be used to track object allocations.
It uses the same java_crw_demo library used by HPROF to do BCI
on all the classfiles loaded into the Virtual Machine.
You can use this agent library as follows:

    java -Xbootclasspath/a:heapTracker.jar -agentlib:heapTracker ...

To get help on the available options try:

    java -agentlib:heapTracker=help

If the Virtual Machine complains that it can't find the library, 
you may need to add the directory containing the library into the 
LD_LIBRARY_PATH environment variable (Unix), or the PATH environment 
variable (Windows).

