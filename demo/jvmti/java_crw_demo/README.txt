#
# @(#)README.txt	1.8 06/01/28
#
# Copyright 2006 Sun Microsystems, Inc. All rights reserved.
# SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
#

java_crw_demo Library: @(#)README.txt	1.8 06/01/28

The library java_crw_demo is a small C library that is used by HPROF
and other agent libraries to do some very basic bytecode 
insertion (BCI) of class files.  This is not an agent 
library but a general purpose library that can be used to do some 
very limited bytecode insertion.

In the demo sources, look for the use of java_crw_demo.h and
the C function java_crw_demo().  The java_crw_demo library is provided 
as part of the JRE.

The basic BCI that this library does includes:

    * On entry to the java.lang.Object init method (signature "()V"), 
      a invokestatic call to tclass.obj_init_method(object); is inserted. 

    * On any newarray type opcode, immediately following it, the array 
      object is duplicated on the stack and an invokestatic call to
      tclass.newarray_method(object); is inserted. 

    * On entry to all methods, a invokestatic call to 
      tclass.call_method(cnum,mnum); is inserted. The agent can map the 
      two integers (cnum,mnum) to a method in a class, the cnum is the 
      number provided to the java_crw_demo library when the classfile was 
      modified.

    * On return from any method (any return opcode), a invokestatic call to
      tclass.return_method(cnum,mnum); is inserted.  

Some methods are not modified at all, init methods and finalize methods 
whose length is 1 will not be modified.  Classes that are designated 
"system" will not have their clinit methods modified. In addition, the 
method java.lang.Thread.currentThread() is not modified.

No methods or fields will be added to any class, however new constant 
pool entries will be added at the end of the original constant pool table.
The exception, line, and local variable tables for each method is adjusted 
for the modification. The bytecodes are compressed to use smaller offsets 
and the fewest 'wide' opcodes. 

All attempts are made to minimize the number of bytecodes at each insertion 
site, however, classes with N return opcodes or N newarray opcodes will get 
N insertions.  And only the necessary modification dictated by the input 
arguments to java_crw_demo are actually made.

