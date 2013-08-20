/*
 * @(#)README.txt	1.16 04/06/23
 *
 * Copyright (c) 2004 Sun Microsystems, Inc. All  Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * -Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 * -Redistribution in binary form must reproduct the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT
 * BE LIABLE FOR ANY DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT
 * OF OR RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THE SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN
 * IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that Software is not designed, licensed or intended for
 * use in the design, construction, operation or maintenance of any nuclear
 * facility.
 */

README
------

Design and Implementation:

    * The sun.tools.hprof.Tracker Class (Tracker.java & hprof_tracker.c)
        The internal class sun.tools.hprof.Tracker was added in J2SE 1.5.
        The BCI code will call these static methods, which will in turn
        (if engaged) call matching native methods in the hprof library,
	with the additional current Thread argument (Thread.currentThread()).
	Doing the currentThread call on the Java side was necessary due
	to the difficulty of getting the current thread while inside one
	of these Tracker native methods.  This class lives in rt.jar.

    * Byte Code Instrumentation (BCI)
        Using the ClassFileLoadHook feature and a C language
        implementation of a byte code injection transformer, the following
        bytecodes get injections:
	    - On entry to the java.lang.Object <init> method, 
	      a invokestatic call to
		sun.tools.hprof.Tracker.ObjectInit(this);
	      is injected. 
	    - On any newarray type opcode, immediately following it, 
	      the array object is duplicated on the stack and an
	      invokestatic call to
		sun.tools.hprof.Tracker.NewArray(obj);
	      is injected. 
	    - On entry to all methods, a invokestatic call to 
		sun.tools.hprof.Tracker.CallSite(cnum,mnum);
	      is injected. The hprof agent can map the two integers
	      (cnum,mnum) to a method in a class. This is the BCI based
	      "method entry" event.
	    - On return from any method (any return opcode),
	      a invokestatic call to
		sun.tools.hprof.Tracker.ReturnSite(cnum,mnum);
	      is injected.  
        All classes found via ClassFileLoadHook are injected with the
        exception of some system class methods "<init>" and "finalize" 
        whose length is 1 and system class methods with name "<clinit>",
	and also java.lang.Thread.currentThread() which is used in the
	class sun.tools.hprof.Tracker (preventing nasty recursion issue).
        System classes are currently defined as any class seen by the
	ClassFileLoadHook prior to VM_INIT. This does mean that
	objects created in the system classes inside <clinit> might not
	get tracked initially.
	See the java_crw_demo source and documentation for more info.
	The injections are based on what the hprof options
	are requesting, e.g. if heap=sites or heap=all is requested, the
	newarray and Object.<init> method injections happen.
	If cpu=times is requested, all methods get their entries and
	returns tracked. Options like cpu=samples or monitor=y
	do not require BCI.

    * BCI Allocation Tags (hprof_tag.c)
        The current jlong tag being used on allocated objects
	is an ObjectIndex, or an index into the object table inside
	the hprof code. Depending on whether heap=sites or heap=dump 
	was asked for, these ObjectIndex's might represent unique
	objects, or unique allocation sites for types of objects.
	The heap=dump option requires considerable more space
	due to the one jobject per ObjectIndex mapping.

    * BCI Performance
        The cpu=times seems to have the most negative affect on
	performance, this could be improved by not having the 
	Tracker class methods call native code directly, but accumulate
	the data in a file or memory somehow and letting it buffer down
	to the agent. The cpu=samples is probably a better way to
	measure cpu usage, varying the interval as needed.
	The heap=dump seems to use memory like crazy, but that's 
	partially the way it has always been. 

    * Sources in the J2SE workspace
        The sources and Makefiles live in:
                src/share/classes/sun/tools/hprof/*
                src/share/demo/jvmti/hprof/*
                src/share/demo/jvmti/java_crw_demo/*
                src/solaris/demo/jvmti/hprof/*
                src/windows/demo/jvmti/hprof/*
                make/java/java_hprof_demo/*
                make/java/java_crw_demo/*
   
--------
