/*
 * @(#)Finalizer.java	1.19 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang.ref;

import java.security.PrivilegedAction;
import java.security.AccessController;


final class Finalizer extends FinalReference { /* Package-private; must be in
						  same package as the Reference
						  class */

    /* A native method that invokes an arbitrary object's finalize method is
       required since the finalize method is protected
     */
    static native void invokeFinalizeMethod(Object o) throws Throwable;

    static private ReferenceQueue queue = new ReferenceQueue();
    static private Finalizer unfinalized = null;
    static private Object lock = new Object();

    private Finalizer
        next = null,
	prev = null;

    private boolean hasBeenFinalized() {
	return (next == this);
    }

    private void add() {
	synchronized (lock) {
	    if (unfinalized != null) {
		this.next = unfinalized;
		unfinalized.prev = this;
	    }
	    unfinalized = this;
	}
    }

    private void remove() {
	synchronized (lock) {
	    if (unfinalized == this) {
		if (this.next != null) {
		    unfinalized = this.next;
		} else {
		    unfinalized = this.prev;
		}
	    }
	    if (this.next != null) {
		this.next.prev = this.prev;
	    }
	    if (this.prev != null) {
		this.prev.next = this.next;
	    }
	    this.next = this;	/* Indicates that this has been finalized */
	    this.prev = this;
	}
    }

    private Finalizer(Object finalizee) {
	super(finalizee, queue);
	add();
    }

    /* Invoked by VM */
    static void register(Object finalizee) {
	new Finalizer(finalizee);
    }

    private void runFinalizer() {
	synchronized (this) {
	    if (hasBeenFinalized()) return;
	    remove();
	}
	try {
	    Object finalizee = this.get();
	    if (finalizee != null) {
		invokeFinalizeMethod(finalizee);
		/* Clear stack slot containing this variable, to decrease
		   the chances of false retention with a conservative GC */
		finalizee = null;
	    }
	} catch (Throwable x) { }
	super.clear();
    }

    /* Create a privileged secondary finalizer thread in the system thread
       group for the given Runnable, and wait for it to complete.

       This method is used by both runFinalization and runFinalizersOnExit.
       The former method invokes all pending finalizers, while the latter
       invokes all uninvoked finalizers if on-exit finalization has been
       enabled.

       These two methods could have been implemented by offloading their work
       to the regular finalizer thread and waiting for that thread to finish.
       The advantage of creating a fresh thread, however, is that it insulates
       invokers of these methods from a stalled or deadlocked finalizer thread.
     */
    private static void forkSecondaryFinalizer(final Runnable proc) {
	PrivilegedAction pa = new PrivilegedAction() {
	    public Object run() {
		ThreadGroup tg = Thread.currentThread().getThreadGroup();
		for (ThreadGroup tgn = tg;
		     tgn != null;
		     tg = tgn, tgn = tg.getParent());
		Thread sft = new Thread(tg, proc, "Secondary finalizer");
		sft.start();
		try {
		    sft.join();
		} catch (InterruptedException x) {
		    /* Ignore */
		}
		return null;
	    }};
	AccessController.doPrivileged(pa);
    }

    /* Called by Runtime.runFinalization() */
    static void runFinalization() {
	forkSecondaryFinalizer(new Runnable() {
	    public void run() {
		for (;;) {
		    Finalizer f = (Finalizer)queue.poll();
		    if (f == null) break;
		    f.runFinalizer();
		}
	    }
	});
    }

    /* Invoked by java.lang.Shutdown */
    static void runAllFinalizers() {
	forkSecondaryFinalizer(new Runnable() {
	    public void run() {
		for (;;) {
		    Finalizer f;
		    synchronized (lock) {
			f = unfinalized;
			if (f == null) break;
			unfinalized = f.next;
		    }
		    f.runFinalizer();
		}}});
    }

    private static class FinalizerThread extends Thread {
	FinalizerThread(ThreadGroup g) {
	    super(g, "Finalizer");
	}
	public void run() {
	    for (;;) {
		try {
		    Finalizer f = (Finalizer)queue.remove();
		    f.runFinalizer();
		} catch (InterruptedException x) {
		    continue;
		}
	    }
	}
    }

    static {
        ThreadGroup tg = Thread.currentThread().getThreadGroup();
        for (ThreadGroup tgn = tg;
             tgn != null;
             tg = tgn, tgn = tg.getParent());
	Thread finalizer = new FinalizerThread(tg);
	finalizer.setPriority(Thread.MAX_PRIORITY - 2);
	finalizer.setDaemon(true);
	finalizer.start();
    }

}
