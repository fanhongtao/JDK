/*
 * @(#)Shutdown.java	1.9 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;

import java.util.HashSet;
import java.util.Iterator;


/**
 * Package-private utility class containing data structures and logic
 * governing the virtual-machine shutdown sequence.
 *
 * @author   Mark Reinhold
 * @version  1.9, 03/01/23
 * @since    1.3
 */

class Shutdown {

    /* Wrapper class for registered hooks, to ensure that hook identity is
     * object identity rather than .equals identity
     */
    private static class WrappedHook {

	private Thread hook;

	WrappedHook(Thread t) {
	    hook = t;
	}

	public int hashCode() {
	    return System.identityHashCode(hook);
	}

	public boolean equals(Object o) {
	    if (!(o instanceof WrappedHook)) return false;
	    return (((WrappedHook)o).hook == hook);
	}

    }


    /* Shutdown state */
    private static final int RUNNING = 0;
    private static final int HOOKS = 1;
    private static final int FINALIZERS = 2;
    private static int state = RUNNING;

    /* Should we run all finalizers upon exit? */
    private static boolean runFinalizersOnExit = false;

    /* The set of registered, wrapped hooks, or null if there aren't any */
    private static HashSet hooks = null;

    /* The preceding static fields are protected by this lock */
    private static class Lock { };
    private static Object lock = new Lock();


    /* Invoked by Runtime.runFinalizersOnExit */
    static void setRunFinalizersOnExit(boolean run) {
	synchronized (lock) {
	    runFinalizersOnExit = run;
	}
    }


    /* Add a new shutdown hook.  Checks the shutdown state and the hook itself,
     * but does not do any security checks.
     */
    static void add(Thread hook) {
	synchronized (lock) {
	    if (state > RUNNING)
		throw new IllegalStateException("Shutdown in progress");
	    if (hook.isAlive())
		throw new IllegalArgumentException("Hook already running");
	    if (hooks == null) {
		hooks = new HashSet(11);
		hooks.add(new WrappedHook(hook));
		Terminator.setup();
	    } else {
		WrappedHook wh = new WrappedHook(hook);
		if (hooks.contains(wh))
		    throw new IllegalArgumentException("Hook previously registered");
		hooks.add(wh);
	    }
	}
    }


    /* Remove a previously-registered hook.  Like the add method, this method
     * does not do any security checks.
     */
    static boolean remove(Thread hook) {
	synchronized (lock) {
	    if (state > RUNNING)
		throw new IllegalStateException("Shutdown in progress");
	    if (hook == null) throw new NullPointerException();
	    if (hooks == null) {
		return false;
	    } else {
		boolean rv = hooks.remove(new WrappedHook(hook));
		if (rv && hooks.isEmpty()) {
		    hooks = null;
		    Terminator.teardown();
		}
		return rv;
	    }
	}
    }


    /* Run all registered shutdown hooks
     */
    private static void runHooks() {
	/* We needn't bother acquiring the lock just to read the hooks field,
	 * since the hooks can't be modified once shutdown is in progress
	 */
	if (hooks == null) return;
	for (Iterator i = hooks.iterator(); i.hasNext();) {
	    ((WrappedHook)(i.next())).hook.start();
	}
	for (Iterator i = hooks.iterator(); i.hasNext();) {
	    try {
		((WrappedHook)(i.next())).hook.join();
	    } catch (InterruptedException x) {
		continue;
	    }
	}
    }


    /* The true native halt method; also invoked by Runtime.halt
     * after doing the necessary security checks
     */
    static native void halt(int status);

    /* Wormhole for invoking java.lang.ref.Finalizer.runAllFinalizers */
    private static native void runAllFinalizers();


    /* The actual shutdown sequence is defined here.
     *
     * If it weren't for runFinalizersOnExit, this would be simple -- we'd just
     * run the hooks and then halt.  Instead we need to keep track of whether
     * we're running hooks or finalizers.  In the latter case a finalizer could
     * invoke exit(1) to cause immediate termination, while in the former case
     * any further invocations of exit(n), for any n, simply stall.  Note that
     * if on-exit finalizers are enabled they're run iff the shutdown is
     * initiated by an exit(0); they're never run on exit(n) for n != 0 or in
     * response to SIGINT, SIGTERM, etc.
     */
    private static void sequence() {
	synchronized (lock) {
	    /* Guard against the possibility of a daemon thread invoking exit
	     * after DestroyJavaVM initiates the shutdown sequence
	     */
	    if (state != HOOKS) return;
	}
	runHooks();
	boolean rfoe;
	synchronized (lock) {
	    state = FINALIZERS;
	    rfoe = runFinalizersOnExit;
	}
	if (rfoe) runAllFinalizers();
    }


    /* Invoked by Runtime.exit, which does all the security checks.
     * Also invoked by handlers for system-provided termination events,
     * which should pass a nonzero status code.
     */
    static void exit(int status) {
	boolean runMoreFinalizers = false;
	synchronized (lock) {
	    if (status != 0) runFinalizersOnExit = false;
	    switch (state) {
	    case RUNNING:	/* Initiate shutdown */
		state = HOOKS;
		break;
	    case HOOKS:		/* Stall and halt */
		break;
	    case FINALIZERS:
		if (status != 0) {
		    /* Halt immediately on nonzero status */
		    halt(status);
		} else {
		    /* Compatibility with old behavior:
		     * Run more finalizers and then halt
		     */
		    runMoreFinalizers = runFinalizersOnExit;
		}
		break;
	    }
	}
	if (runMoreFinalizers) {
	    runAllFinalizers();
	    halt(status);
	}
	synchronized (Shutdown.class) {
	    /* Synchronize on the class object, causing any other thread
             * that attempts to initiate shutdown to stall indefinitely
	     */
	    sequence();
	    halt(status);
	}
    }


    /* Invoked by the JNI DestroyJavaVM procedure when the last non-daemon
     * thread has finished.  Unlike the exit method, this method does not
     * actually halt the VM.
     */
    static void shutdown() {
	synchronized (lock) {
	    switch (state) {
	    case RUNNING:	/* Initiate shutdown */
		state = HOOKS;
		break;
	    case HOOKS:		/* Stall and then return */
	    case FINALIZERS:
		break;
	    }
	}
	synchronized (Shutdown.class) {
	    sequence();
	}
    }

}
