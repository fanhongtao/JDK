/*
 * @(#)Tracker.java	1.5 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package sun.tools.hprof;

/* This class and it's methods are used by hprof when injecting bytecodes
 *   into class file images.
 *   See the directory src/share/tools/newhprof and the file README.txt
 *   for more details.
 */

public class Tracker {
 
    /* Master switch that activates calls to native functions. */
    
    private static int engaged = 0; 
  
    /* To track memory allocated, we need to catch object init's and arrays. */
    
    /* At the beginning of java.jang.Object.<init>(), a call to
     *   sun.tools.hprof.Tracker.ObjectInit() is injected.
     */

    private static native void nativeObjectInit(Object thr, Object obj);
    
    public static void ObjectInit(Object obj)
    {
	if ( engaged != 0 ) {
	    nativeObjectInit(Thread.currentThread(), obj);
	}
    }
    
    /* Immediately following any of the newarray bytecodes, a call to
     *   sun.tools.hprof.Tracker.NewArray() is injected.
     */

    private static native void nativeNewArray(Object thr, Object obj);
   
    public static void NewArray(Object obj)
    {
	if ( engaged != 0 ) {
	    nativeNewArray(Thread.currentThread(), obj);
	}
    }
   
    /* For cpu time spent in methods, we need to inject for every method. */

    /* At the very beginning of every method, a call to
     *   sun.tools.hprof.Tracker.CallSite() is injected.
     */

    private static native void nativeCallSite(Object thr, int cnum, int mnum);
    
    public static void CallSite(int cnum, int mnum)
    {
	if ( engaged != 0 ) {
	    nativeCallSite(Thread.currentThread(), cnum, mnum);
	}
    }
    
    /* Before any of the return bytecodes, a call to
     *   sun.tools.hprof.Tracker.ReturnSite() is injected.
     */

    private static native void nativeReturnSite(Object thr, int cnum, int mnum);
    
    public static void ReturnSite(int cnum, int mnum)
    {
	if ( engaged != 0 ) {
	    nativeReturnSite(Thread.currentThread(), cnum, mnum);
	}
    }
    
}

