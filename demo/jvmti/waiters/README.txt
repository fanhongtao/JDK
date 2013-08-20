waiters: @(#)README.txt	1.4 04/06/23

This agent library can be used to track threads that wait on 
monitors.

You can use this agent library as follows:

    java -agentlib:waiters ...

If the Virtual Machine complains that it can't find the library, 
you may need to add the directory containing the library into the 
LD_LIBRARY_PATH environment variable (Unix), or the PATH environment 
variable (Windows).

