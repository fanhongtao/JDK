/*
 * @(#)Lock.java	1.30 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.orbutil;

//
// Traditional mutex, unbound to any instance or class.
//							KGH April 95

final public class Lock {

    public synchronized void lock()
    {
	while (busy) {
	    try {
	        wait();
	    } catch (java.lang.InterruptedException ex) {
		// Eat it.
	    }
	}
	busy = true;
    }

    public synchronized void unlock()
    {
	if (!busy) {
	    throw new Error("freeing non-busy lock");
	}
	busy = false;
	notifyAll();
    }

    boolean busy = false;

}
