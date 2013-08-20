versionCheck: @(#)README.txt	1.5 04/06/23

This agent library just makes some simple calls and checks 
the version of the interface being used to build the agent, 
with that supplied by the VM at runtime.

You can use this agent library as follows:

    java -agentlib:versionCheck ...

If the Java Virtual Machine complains that it can't find the library, 
you may need to add the directory containing the library into the 
LD_LIBRARY_PATH environment variable (Unix), or the PATH environment 
variable (Windows).

