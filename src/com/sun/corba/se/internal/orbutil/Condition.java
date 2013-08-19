/*
 * @(#)Condition.java	1.39 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.orbutil;

//
// Traditional Condition variables, unbound to any instance or class.
//							KGH April 95

final public class Condition {

    public Condition() {
    }

    public void wait(Lock lock) {
        synchronized (this) {
	    lock.unlock();
	    boolean ok = false;
	    while (!ok) {
	        try {
		    super.wait();
		    ok = true;
	        } catch (InterruptedException ex) {
		    // Eat it and loop again.  The user doesn't care how
		    // long we wait.
	        }
	    }
	}
	lock.lock();
    }

    public void wait(Lock lock, int timeout)
	throws InterruptedException {
	synchronized (this) {
	    lock.unlock();
	    super.wait(timeout);
	}
	lock.lock();
    }

    public synchronized void signal() {
	super.notify();
    }

    public synchronized void signalAll() {
	super.notifyAll();
    }
}

