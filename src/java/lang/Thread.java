/*
 * @(#)Thread.java	1.127 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;

import java.security.AccessController;
import java.security.AccessControlContext;
import java.util.Map;
import java.util.Collections;
import sun.nio.ch.Interruptible;
import sun.security.util.SecurityConstants;


/**
 * A <i>thread</i> is a thread of execution in a program. The Java 
 * Virtual Machine allows an application to have multiple threads of 
 * execution running concurrently. 
 * <p>
 * Every thread has a priority. Threads with higher priority are 
 * executed in preference to threads with lower priority. Each thread 
 * may or may not also be marked as a daemon. When code running in 
 * some thread creates a new <code>Thread</code> object, the new 
 * thread has its priority initially set equal to the priority of the 
 * creating thread, and is a daemon thread if and only if the 
 * creating thread is a daemon. 
 * <p>
 * When a Java Virtual Machine starts up, there is usually a single 
 * non-daemon thread (which typically calls the method named 
 * <code>main</code> of some designated class). The Java Virtual 
 * Machine continues to execute threads until either of the following 
 * occurs: 
 * <ul>
 * <li>The <code>exit</code> method of class <code>Runtime</code> has been 
 *     called and the security manager has permitted the exit operation 
 *     to take place. 
 * <li>All threads that are not daemon threads have died, either by 
 *     returning from the call to the <code>run</code> method or by 
 *     throwing an exception that propagates beyond the <code>run</code>
 *     method.
 * </ul>
 * <p>
 * There are two ways to create a new thread of execution. One is to 
 * declare a class to be a subclass of <code>Thread</code>. This 
 * subclass should override the <code>run</code> method of class 
 * <code>Thread</code>. An instance of the subclass can then be 
 * allocated and started. For example, a thread that computes primes 
 * larger than a stated value could be written as follows: 
 * <p><hr><blockquote><pre>
 *     class PrimeThread extends Thread {
 *         long minPrime;
 *         PrimeThread(long minPrime) {
 *             this.minPrime = minPrime;
 *         }
 * 
 *         public void run() {
 *             // compute primes larger than minPrime
 *             &nbsp;.&nbsp;.&nbsp;.
 *         }
 *     }
 * </pre></blockquote><hr>
 * <p>
 * The following code would then create a thread and start it running: 
 * <p><blockquote><pre>
 *     PrimeThread p = new PrimeThread(143);
 *     p.start();
 * </pre></blockquote>
 * <p>
 * The other way to create a thread is to declare a class that 
 * implements the <code>Runnable</code> interface. That class then 
 * implements the <code>run</code> method. An instance of the class can 
 * then be allocated, passed as an argument when creating 
 * <code>Thread</code>, and started. The same example in this other 
 * style looks like the following: 
 * <p><hr><blockquote><pre>
 *     class PrimeRun implements Runnable {
 *         long minPrime;
 *         PrimeRun(long minPrime) {
 *             this.minPrime = minPrime;
 *         }
 * 
 *         public void run() {
 *             // compute primes larger than minPrime
 *             &nbsp;.&nbsp;.&nbsp;.
 *         }
 *     }
 * </pre></blockquote><hr>
 * <p>
 * The following code would then create a thread and start it running: 
 * <p><blockquote><pre>
 *     PrimeRun p = new PrimeRun(143);
 *     new Thread(p).start();
 * </pre></blockquote>
 * <p>
 * Every thread has a name for identification purposes. More than 
 * one thread may have the same name. If a name is not specified when 
 * a thread is created, a new name is generated for it. 
 *
 * @author  unascribed
 * @version 1.127, 01/23/03
 * @see     java.lang.Runnable
 * @see     java.lang.Runtime#exit(int)
 * @see     java.lang.Thread#run()
 * @see     java.lang.Thread#stop()
 * @since   JDK1.0
 */
public
class Thread implements Runnable {
    /* Make sure registerNatives is the first thing <clinit> does. */
    private static native void registerNatives();
    static {
        registerNatives();
    }

    private char	name[];
    private int         priority;
    private Thread	threadQ;
    private long	eetop;

    /* Whether or not to single_step this thread. */
    private boolean	single_step;

    /* Whether or not the thread is a daemon thread. */
    private boolean	daemon = false;

    /* Whether or not this thread was asked to exit before it runs.*/
    private boolean	stillborn = false;

    /* What will be run. */
    private Runnable target;

    /* The group of this thread */
    private ThreadGroup	group;

    /* The context ClassLoader for this thread */
    private ClassLoader contextClassLoader;

    /* The inherited AccessControlContext of this thread */
    private AccessControlContext inheritedAccessControlContext;

    /* For autonumbering anonymous threads. */
    private static int threadInitNumber;
    private static synchronized int nextThreadNum() {
	return threadInitNumber++;
    }

    /* ThreadLocal values pertaining to this thread. This map is maintained
     * by the ThreadLocal class. */
    ThreadLocal.ThreadLocalMap threadLocals = null;

    /*
     * InheritableThreadLocal values pertaining to this thread. This map is
     * maintained by the InheritableThreadLocal class.  
     */ 
    ThreadLocal.ThreadLocalMap inheritableThreadLocals = null;

    /*
     * The requested stack size for this thread, or 0 if the creator did
     * not specify a stack size.  It is up to the VM to do whatever it
     * likes with this number; some VMs will ignore it.
     */
    private long stackSize;

    /* The object in which this thread is blocked in an interruptible I/O
     * operation, if any.  The blocker's interrupt method() should be invoked
     * before setting this thread's interrupt status.
     */
    private volatile Interruptible blocker;

    /* Set the blocker field; invoked via reflection magic from java.nio code
     */
    private void blockedOn(Interruptible b) {
	blocker = b;
    }

    /**
     * The minimum priority that a thread can have. 
     */
    public final static int MIN_PRIORITY = 1;

   /**
     * The default priority that is assigned to a thread. 
     */
    public final static int NORM_PRIORITY = 5;

    /**
     * The maximum priority that a thread can have. 
     */
    public final static int MAX_PRIORITY = 10;

    /**
     * Returns a reference to the currently executing thread object.
     *
     * @return  the currently executing thread.
     */
    public static native Thread currentThread();

    /**
     * Causes the currently executing thread object to temporarily pause 
     * and allow other threads to execute. 
     */
    public static native void yield();

    /**	
     * Causes the currently executing thread to sleep (temporarily cease 
     * execution) for the specified number of milliseconds. The thread 
     * does not lose ownership of any monitors.
     *
     * @param      millis   the length of time to sleep in milliseconds.
     * @exception  InterruptedException if another thread has interrupted
     *             the current thread.  The <i>interrupted status</i> of the
     *             current thread is cleared when this exception is thrown.
     * @see        java.lang.Object#notify()
     */
    public static native void sleep(long millis) throws InterruptedException;

    /**
     * Causes the currently executing thread to sleep (cease execution) 
     * for the specified number of milliseconds plus the specified number 
     * of nanoseconds. The thread does not lose ownership of any monitors.
     *
     * @param      millis   the length of time to sleep in milliseconds.
     * @param      nanos    0-999999 additional nanoseconds to sleep.
     * @exception  IllegalArgumentException  if the value of millis is 
     *             negative or the value of nanos is not in the range 
     *             0-999999.
     * @exception  InterruptedException if another thread has interrupted
     *             the current thread.  The <i>interrupted status</i> of the
     *             current thread is cleared when this exception is thrown.
     * @see        java.lang.Object#notify()
     */
    public static void sleep(long millis, int nanos) 
    throws InterruptedException {
	if (millis < 0) {
            throw new IllegalArgumentException("timeout value is negative");
	}

	if (nanos < 0 || nanos > 999999) {
            throw new IllegalArgumentException(
				"nanosecond timeout value out of range");
	}

	if (nanos >= 500000 || (nanos != 0 && millis == 0)) {
	    millis++;
	}

	sleep(millis);
    }

    /**
     * Initialize a Thread.
     *
     * @param g the Thread group
     * @param target the object whose run() method gets called
     * @param name the name of the new Thread
     * @param stackSize the desired stack size for the new thread, or
     *        zero to indicate that this parameter is to be ignored.
     */
    private void init(ThreadGroup g, Runnable target, String name,
                      long stackSize) {
	Thread parent = currentThread();
	if (g == null) {
	    /* Determine if it's an applet or not */
	    SecurityManager security = System.getSecurityManager();
	    
	    /* If there is a security manager, ask the security manager
	       what to do. */
	    if (security != null) {
		g = security.getThreadGroup();
	    }

	    /* If the security doesn't have a strong opinion of the matter
	       use the parent thread group. */
	    if (g == null) {
		g = parent.getThreadGroup();
	    }
	}

	/* checkAccess regardless of whether or not threadgroup is
           explicitly passed in. */
	g.checkAccess();	    

	this.group = g;
	this.daemon = parent.isDaemon();
	this.priority = parent.getPriority();
	this.name = name.toCharArray();
	this.contextClassLoader = parent.contextClassLoader;
	this.inheritedAccessControlContext = AccessController.getContext();
	this.target = target;
	setPriority(priority);
        if (parent.inheritableThreadLocals != null)
          this.inheritableThreadLocals = 
            ThreadLocal.createInheritedMap(parent.inheritableThreadLocals);

        /* Stash the specified stack size in case the VM cares */
        this.stackSize = stackSize;

	g.add(this);
    }

   /**
     * Allocates a new <code>Thread</code> object. This constructor has 
     * the same effect as <code>Thread(null, null,</code>
     * <i>gname</i><code>)</code>, where <b><i>gname</i></b> is 
     * a newly generated name. Automatically generated names are of the 
     * form <code>"Thread-"+</code><i>n</i>, where <i>n</i> is an integer. 
     *
     * @see     java.lang.Thread#Thread(java.lang.ThreadGroup,
     *          java.lang.Runnable, java.lang.String)
     */
    public Thread() {
	init(null, null, "Thread-" + nextThreadNum(), 0);
    }

    /**
     * Allocates a new <code>Thread</code> object. This constructor has 
     * the same effect as <code>Thread(null, target,</code>
     * <i>gname</i><code>)</code>, where <i>gname</i> is 
     * a newly generated name. Automatically generated names are of the 
     * form <code>"Thread-"+</code><i>n</i>, where <i>n</i> is an integer. 
     *
     * @param   target   the object whose <code>run</code> method is called.
     * @see     java.lang.Thread#Thread(java.lang.ThreadGroup, 
     *          java.lang.Runnable, java.lang.String)
     */
    public Thread(Runnable target) {
	init(null, target, "Thread-" + nextThreadNum(), 0);
    }

    /**
     * Allocates a new <code>Thread</code> object. This constructor has 
     * the same effect as <code>Thread(group, target,</code>
     * <i>gname</i><code>)</code>, where <i>gname</i> is 
     * a newly generated name. Automatically generated names are of the 
     * form <code>"Thread-"+</code><i>n</i>, where <i>n</i> is an integer. 
     *
     * @param      group    the thread group.
     * @param      target   the object whose <code>run</code> method is called.
     * @exception  SecurityException  if the current thread cannot create a
     *             thread in the specified thread group.
     * @see        java.lang.Thread#Thread(java.lang.ThreadGroup, 
     *             java.lang.Runnable, java.lang.String)
     */
    public Thread(ThreadGroup group, Runnable target) {
	init(group, target, "Thread-" + nextThreadNum(), 0);
    }

    /**
     * Allocates a new <code>Thread</code> object. This constructor has 
     * the same effect as <code>Thread(null, null, name)</code>. 
     *
     * @param   name   the name of the new thread.
     * @see     java.lang.Thread#Thread(java.lang.ThreadGroup, 
     *          java.lang.Runnable, java.lang.String)
     */
    public Thread(String name) {
	init(null, null, name, 0);
    }

    /**
     * Allocates a new <code>Thread</code> object. This constructor has 
     * the same effect as <code>Thread(group, null, name)</code> 
     *
     * @param      group   the thread group.
     * @param      name    the name of the new thread.
     * @exception  SecurityException  if the current thread cannot create a
     *               thread in the specified thread group.
     * @see        java.lang.Thread#Thread(java.lang.ThreadGroup, 
     *          java.lang.Runnable, java.lang.String)
     */
    public Thread(ThreadGroup group, String name) {
	init(group, null, name, 0);
    }

    /**
     * Allocates a new <code>Thread</code> object. This constructor has 
     * the same effect as <code>Thread(null, target, name)</code>. 
     *
     * @param   target   the object whose <code>run</code> method is called.
     * @param   name     the name of the new thread.
     * @see     java.lang.Thread#Thread(java.lang.ThreadGroup, 
     *          java.lang.Runnable, java.lang.String)
     */
    public Thread(Runnable target, String name) {
	init(null, target, name, 0);
    }

    /**
     * Allocates a new <code>Thread</code> object so that it has 
     * <code>target</code> as its run object, has the specified 
     * <code>name</code> as its name, and belongs to the thread group 
     * referred to by <code>group</code>.
     * <p>
     * If <code>group</code> is <code>null</code> and there is a 
     * security manager, the group is determined by the security manager's 
     * <code>getThreadGroup</code> method. If <code>group</code> is 
     * <code>null</code> and there is not a security manager, or the
     * security manager's <code>getThreadGroup</code> method returns 
     * <code>null</code>, the group is set to be the same ThreadGroup 
     * as the thread that is creating the new thread.
     * 
     * <p>If there is a security manager, its <code>checkAccess</code> 
     * method is called with the ThreadGroup as its argument.
     * This may result in a SecurityException.
     * <p>
     * If the <code>target</code> argument is not <code>null</code>, the 
     * <code>run</code> method of the <code>target</code> is called when 
     * this thread is started. If the target argument is 
     * <code>null</code>, this thread's <code>run</code> method is called 
     * when this thread is started. 
     * <p>
     * The priority of the newly created thread is set equal to the 
     * priority of the thread creating it, that is, the currently running 
     * thread. The method <code>setPriority</code> may be used to 
     * change the priority to a new value. 
     * <p>
     * The newly created thread is initially marked as being a daemon 
     * thread if and only if the thread creating it is currently marked 
     * as a daemon thread. The method <code>setDaemon </code> may be used 
     * to change whether or not a thread is a daemon. 
     *
     * @param      group     the thread group.
     * @param      target   the object whose <code>run</code> method is called.
     * @param      name     the name of the new thread.
     * @exception  SecurityException  if the current thread cannot create a
     *               thread in the specified thread group.
     * @see        java.lang.Runnable#run()
     * @see        java.lang.Thread#run()
     * @see        java.lang.Thread#setDaemon(boolean)
     * @see        java.lang.Thread#setPriority(int)
     * @see        java.lang.ThreadGroup#checkAccess()
     * @see        SecurityManager#checkAccess
     */
    public Thread(ThreadGroup group, Runnable target, String name) {
	init(group, target, name, 0);
    }

    /**
     * Allocates a new <code>Thread</code> object so that it has
     * <code>target</code> as its run object, has the specified
     * <code>name</code> as its name, belongs to the thread group referred to
     * by <code>group</code>, and has the specified <i>stack size</i>.
     *
     * <p>This constructor is identical to {@link
     * #Thread(ThreadGroup,Runnable,String)} with the exception of the fact
     * that it allows the thread stack size to be specified.  The stack size
     * is the approximate number of bytes of address space that the virtual
     * machine is to allocate for this thread's stack.  <b>The effect of the
     * <tt>stackSize</tt> parameter, if any, is highly platform dependent.</b>
     *
     * <p>On some platforms, specifying a higher value for the
     * <tt>stackSize</tt> parameter may allow a thread to achieve greater
     * recursion depth before throwing a {@link StackOverflowError}.
     * Similarly, specifying a lower value may allow a greater number of
     * threads to exist concurrently without throwing an an {@link
     * OutOfMemoryError} (or other internal error).  The details of
     * the relationship between the value of the <tt>stackSize</tt> parameter
     * and the maximum recursion depth and concurrency level are
     * platform-dependent.  <b>On some platforms, the value of the
     * <tt>stackSize</tt> parameter may have no effect whatsoever.</b>
     * 
     * <p>The virtual machine is free to treat the <tt>stackSize</tt>
     * parameter as a suggestion.  If the specified value is unreasonably low
     * for the platform, the virtual machine may instead use some
     * platform-specific minimum value; if the specified value is unreasonably
     * high, the virtual machine may instead use some platform-specific
     * maximum.  Likewise, the virtual machine is free to round the specified
     * value up or down as it sees fit (or to ignore it completely).
     *
     * <p>Specifying a value of zero for the <tt>stackSize</tt> parameter will
     * cause this constructor to behave exactly like the
     * <tt>Thread(ThreadGroup, Runnable, String)</tt> constructor.
     *
     * <p><i>Due to the platform-dependent nature of the behavior of this
     * constructor, extreme care should be exercised in its use.
     * The thread stack size necessary to perform a given computation will
     * likely vary from one JRE implementation to another.  In light of this
     * variation, careful tuning of the stack size parameter may be required,
     * and the tuning may need to be repeated for each JRE implementation on
     * which an application is to run.</i>
     *
     * <p>Implementation note: Java platform implementers are encouraged to
     * document their implementation's behavior with respect to the
     * <tt>stackSize parameter</tt>.
     *
     * @param      group    the thread group.
     * @param      target   the object whose <code>run</code> method is called.
     * @param      name     the name of the new thread.
     * @param      stackSize the desired stack size for the new thread, or
     *             zero to indicate that this parameter is to be ignored.
     * @exception  SecurityException  if the current thread cannot create a
     *               thread in the specified thread group.
     */
    public Thread(ThreadGroup group, Runnable target, String name,
                  long stackSize) {
	init(group, target, name, stackSize);
    }

    /**
     * Causes this thread to begin execution; the Java Virtual Machine 
     * calls the <code>run</code> method of this thread. 
     * <p>
     * The result is that two threads are running concurrently: the 
     * current thread (which returns from the call to the 
     * <code>start</code> method) and the other thread (which executes its 
     * <code>run</code> method). 
     *
     * @exception  IllegalThreadStateException  if the thread was already
     *               started.
     * @see        java.lang.Thread#run()
     * @see        java.lang.Thread#stop()
     */
    public synchronized native void start();

    /**
     * If this thread was constructed using a separate 
     * <code>Runnable</code> run object, then that 
     * <code>Runnable</code> object's <code>run</code> method is called; 
     * otherwise, this method does nothing and returns. 
     * <p>
     * Subclasses of <code>Thread</code> should override this method. 
     *
     * @see     java.lang.Thread#start()
     * @see     java.lang.Thread#stop()
     * @see     java.lang.Thread#Thread(java.lang.ThreadGroup, 
     *          java.lang.Runnable, java.lang.String)
     * @see     java.lang.Runnable#run()
     */
    public void run() {
	if (target != null) {
	    target.run();
	}
    }

    /**
     * This method is called by the system to give a Thread
     * a chance to clean up before it actually exits.
     */
    private void exit() {
	if (group != null) {
	    group.remove(this);
	    group = null;
	}
	/* Aggressively null object connected to Thread: see bug 4006245 */
	target = null;
    }

    /** 
     * Forces the thread to stop executing.
     * <p>
     * If there is a security manager installed, its <code>checkAccess</code>
     * method is called with <code>this</code> 
     * as its argument. This may result in a 
     * <code>SecurityException</code> being raised (in the current thread). 
     * <p>
     * If this thread is different from the current thread (that is, the current
     * thread is trying to stop a thread other than itself), the
     * security manager's <code>checkPermission</code> method (with a
     * <code>RuntimePermission("stopThread")</code> argument) is called in
     * addition.
     * Again, this may result in throwing a 
     * <code>SecurityException</code> (in the current thread). 
     * <p>
     * The thread represented by this thread is forced to stop whatever 
     * it is doing abnormally and to throw a newly created 
     * <code>ThreadDeath</code> object as an exception. 
     * <p>
     * It is permitted to stop a thread that has not yet been started. 
     * If the thread is eventually started, it immediately terminates. 
     * <p>
     * An application should not normally try to catch 
     * <code>ThreadDeath</code> unless it must do some extraordinary 
     * cleanup operation (note that the throwing of 
     * <code>ThreadDeath</code> causes <code>finally</code> clauses of 
     * <code>try</code> statements to be executed before the thread 
     * officially dies).  If a <code>catch</code> clause catches a 
     * <code>ThreadDeath</code> object, it is important to rethrow the 
     * object so that the thread actually dies. 
     * <p>
     * The top-level error handler that reacts to otherwise uncaught 
     * exceptions does not print out a message or otherwise notify the 
     * application if the uncaught exception is an instance of 
     * <code>ThreadDeath</code>. 
     *
     * @exception  SecurityException  if the current thread cannot 
     *               modify this thread.
     * @see        java.lang.Thread#interrupt()
     * @see        java.lang.Thread#checkAccess()
     * @see        java.lang.Thread#run()
     * @see        java.lang.Thread#start()
     * @see        java.lang.ThreadDeath
     * @see        java.lang.ThreadGroup#uncaughtException(java.lang.Thread,
     *             java.lang.Throwable)
     * @see        SecurityManager#checkAccess(Thread)
     * @see        SecurityManager#checkPermission
     * @deprecated This method is inherently unsafe.  Stopping a thread with
     *	     Thread.stop causes it to unlock all of the monitors that it
     *	     has locked (as a natural consequence of the unchecked
     *	     <code>ThreadDeath</code> exception propagating up the stack).  If
     *       any of the objects previously protected by these monitors were in
     *       an inconsistent state, the damaged objects become visible to
     *       other threads, potentially resulting in arbitrary behavior.  Many
     *       uses of <code>stop</code> should be replaced by code that simply
     *       modifies some variable to indicate that the target thread should
     *       stop running.  The target thread should check this variable  
     *       regularly, and return from its run method in an orderly fashion
     *       if the variable indicates that it is to stop running.  If the
     *       target thread waits for long periods (on a condition variable,
     *       for example), the <code>interrupt</code> method should be used to
     *       interrupt the wait. 
     *       For more information, see 
     *       <a href="{@docRoot}/../guide/misc/threadPrimitiveDeprecation.html">Why 
     *       are Thread.stop, Thread.suspend and Thread.resume Deprecated?</a>.
     */
    public final void stop() {
	synchronized (this) {
            //if the thread is alreay dead, return
            if (!this.isAlive()) return;
	    SecurityManager security = System.getSecurityManager();
	    if (security != null) {
		checkAccess();
		if (this != Thread.currentThread()) {
		    security.checkPermission(SecurityConstants.STOP_THREAD_PERMISSION);
		}
	    }
	    resume(); // Wake up thread if it was suspended; no-op otherwise
	    stop0(new ThreadDeath());
	}
    }

    /**
     * Forces the thread to stop executing.
     * <p>
     * If there is a security manager installed, the <code>checkAccess</code>
     * method of this thread is called, which may result in a 
     * <code>SecurityException</code> being raised (in the current thread). 
     * <p>
     * If this thread is different from the current thread (that is, the current
     * thread is trying to stop a thread other than itself) or
     * <code>obj</code> is not an instance of <code>ThreadDeath</code>, the
     * security manager's <code>checkPermission</code> method (with the
     * <code>RuntimePermission("stopThread")</code> argument) is called in
     * addition.
     * Again, this may result in throwing a 
     * <code>SecurityException</code> (in the current thread). 
     * <p>
     * If the argument <code>obj</code> is null, a 
     * <code>NullPointerException</code> is thrown (in the current thread). 
     * <p>
     * The thread represented by this thread is forced to complete 
     * whatever it is doing abnormally and to throw the 
     * <code>Throwable</code> object <code>obj</code> as an exception. This 
     * is an unusual action to take; normally, the <code>stop</code> method 
     * that takes no arguments should be used. 
     * <p>
     * It is permitted to stop a thread that has not yet been started. 
     * If the thread is eventually started, it immediately terminates. 
     *
     * @param      obj   the Throwable object to be thrown.
     * @exception  SecurityException  if the current thread cannot modify
     *               this thread.
     * @see        java.lang.Thread#interrupt()
     * @see        java.lang.Thread#checkAccess()
     * @see        java.lang.Thread#run()
     * @see        java.lang.Thread#start()
     * @see        java.lang.Thread#stop()
     * @see        SecurityManager#checkAccess(Thread)
     * @see        SecurityManager#checkPermission
     * @deprecated This method is inherently unsafe.  See {@link #stop}
     *        (with no arguments) for details.  An additional danger of this
     *        method is that it may be used to generate exceptions that the
     *        target thread is unprepared to handle (including checked
     *        exceptions that the thread could not possibly throw, were it
     *        not for this method).
     *        For more information, see 
     *        <a href="{@docRoot}/../guide/misc/threadPrimitiveDeprecation.html">Why 
     *        are Thread.stop, Thread.suspend and Thread.resume Deprecated?</a>.
     */
    public final synchronized void stop(Throwable obj) {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    checkAccess();
	    if ((this != Thread.currentThread()) ||
		(!(obj instanceof ThreadDeath))) {
		security.checkPermission(SecurityConstants.STOP_THREAD_PERMISSION);
	    }
	}
	resume(); // Wake up thread if it was suspended; no-op otherwise
	stop0(obj);
    }

    /**
     * Interrupts this thread.
     * 
     * <p> First the {@link #checkAccess() checkAccess} method of this thread
     * is invoked, which may cause a {@link SecurityException} to be thrown.
     *
     * <p> If this thread is blocked in an invocation of the {@link
     * Object#wait() wait()}, {@link Object#wait(long) wait(long)}, or {@link
     * Object#wait(long, int) wait(long, int)} methods of the {@link Object}
     * class, or of the {@link #join()}, {@link #join(long)}, {@link
     * #join(long, int)}, {@link #sleep(long)}, or {@link #sleep(long, int)},
     * methods of this class, then its interrupt status will be cleared and it
     * will receive an {@link InterruptedException}.
     *
     * <p> If this thread is blocked in an I/O operation upon an {@link
     * java.nio.channels.InterruptibleChannel </code>interruptible
     * channel<code>} then the channel will be closed, the thread's interrupt
     * status will be set, and the thread will receive a {@link
     * java.nio.channels.ClosedByInterruptException}.
     *
     * <p> If this thread is blocked in a {@link java.nio.channels.Selector}
     * then the thread's interrupt status will be set and it will return
     * immediately from the selection operation, possibly with a non-zero
     * value, just as if the selector's {@link
     * java.nio.channels.Selector#wakeup wakeup} method were invoked.
     *
     * <p> If none of the previous conditions hold then this thread's interrupt
     * status will be set. </p>
     * 
     * @throws  SecurityException
     *          if the current thread cannot modify this thread
     *
     * @revised 1.4
     * @spec JSR-51
     */
    public void interrupt() {
	checkAccess();
	Interruptible b = blocker;
	if (b != null) {
	    b.interrupt();
	}
	interrupt0();
    }

    /**
     * Tests whether the current thread has been interrupted.  The
     * <i>interrupted status</i> of the thread is cleared by this method.  In
     * other words, if this method were to be called twice in succession, the
     * second call would return false (unless the current thread were
     * interrupted again, after the first call had cleared its interrupted
     * status and before the second call had examined it).
     *
     * @return  <code>true</code> if the current thread has been interrupted;
     *          <code>false</code> otherwise.
     * @see java.lang.Thread#isInterrupted()
     */
    public static boolean interrupted() {
	return currentThread().isInterrupted(true);
    }

    /**
     * Tests whether this thread has been interrupted.  The <i>interrupted
     * status</i> of the thread is unaffected by this method.
     *
     * @return  <code>true</code> if this thread has been interrupted;
     *          <code>false</code> otherwise.
     * @see     java.lang.Thread#interrupted()
     */
    public boolean isInterrupted() {
	return isInterrupted(false);
    }

    /**
     * Tests if some Thread has been interrupted.  The interrupted state
     * is reset or not based on the value of ClearInterrupted that is
     * passed.
     */
    private native boolean isInterrupted(boolean ClearInterrupted);

    /**
     * Destroys this thread, without any cleanup. Any monitors it has 
     * locked remain locked. (This method is not implemented.)
     */
    public void destroy() {
	throw new NoSuchMethodError();
    }

    /**
     * Tests if this thread is alive. A thread is alive if it has 
     * been started and has not yet died. 
     *
     * @return  <code>true</code> if this thread is alive;
     *          <code>false</code> otherwise.
     */
    public final native boolean isAlive();

    /**
     * Suspends this thread.
     * <p>
     * First, the <code>checkAccess</code> method of this thread is called 
     * with no arguments. This may result in throwing a 
     * <code>SecurityException </code>(in the current thread). 
     * <p>
     * If the thread is alive, it is suspended and makes no further 
     * progress unless and until it is resumed. 
     *
     * @exception  SecurityException  if the current thread cannot modify
     *               this thread.
     * @see #checkAccess
     * @deprecated   This method has been deprecated, as it is
     *   inherently deadlock-prone.  If the target thread holds a lock on the
     *   monitor protecting a critical system resource when it is suspended, no
     *   thread can access this resource until the target thread is resumed. If
     *   the thread that would resume the target thread attempts to lock this
     *   monitor prior to calling <code>resume</code>, deadlock results.  Such
     *   deadlocks typically manifest themselves as "frozen" processes.
     *   For more information, see 
     *   <a href="{@docRoot}/../guide/misc/threadPrimitiveDeprecation.html">Why 
     *   are Thread.stop, Thread.suspend and Thread.resume Deprecated?</a>.
     */
    public final void suspend() {
	checkAccess();
	suspend0();
    }

    /**
     * Resumes a suspended thread.
     * <p>
     * First, the <code>checkAccess</code> method of this thread is called 
     * with no arguments. This may result in throwing a 
     * <code>SecurityException</code> (in the current thread). 
     * <p>
     * If the thread is alive but suspended, it is resumed and is 
     * permitted to make progress in its execution. 
     *
     * @exception  SecurityException  if the current thread cannot modify this
     *               thread.
     * @see        #checkAccess
     * @see        java.lang.Thread#suspend()
     * @deprecated This method exists solely for use with {@link #suspend},
     *     which has been deprecated because it is deadlock-prone.
     *     For more information, see 
     *     <a href="{@docRoot}/../guide/misc/threadPrimitiveDeprecation.html">Why 
     *     are Thread.stop, Thread.suspend and Thread.resume Deprecated?</a>.
     */
    public final void resume() {
	checkAccess();
	resume0();
    }

    /**
     * Changes the priority of this thread. 
     * <p>
     * First the <code>checkAccess</code> method of this thread is called 
     * with no arguments. This may result in throwing a 
     * <code>SecurityException</code>. 
     * <p>
     * Otherwise, the priority of this thread is set to the smaller of 
     * the specified <code>newPriority</code> and the maximum permitted 
     * priority of the thread's thread group. 
     *
     * @param newPriority priority to set this thread to
     * @exception  IllegalArgumentException  If the priority is not in the
     *               range <code>MIN_PRIORITY</code> to
     *               <code>MAX_PRIORITY</code>.
     * @exception  SecurityException  if the current thread cannot modify
     *               this thread.
     * @see        #getPriority
     * @see        java.lang.Thread#checkAccess()
     * @see        java.lang.Thread#getPriority()
     * @see        java.lang.Thread#getThreadGroup()
     * @see        java.lang.Thread#MAX_PRIORITY
     * @see        java.lang.Thread#MIN_PRIORITY
     * @see        java.lang.ThreadGroup#getMaxPriority()
     */
    public final void setPriority(int newPriority) {
	checkAccess();
	if (newPriority > MAX_PRIORITY || newPriority < MIN_PRIORITY) {
	    throw new IllegalArgumentException();
	}
	if (newPriority > group.getMaxPriority()) {
	    newPriority = group.getMaxPriority();
	}
	setPriority0(priority = newPriority);
    }

    /**
     * Returns this thread's priority.
     *
     * @return  this thread's priority.
     * @see     #setPriority
     * @see     java.lang.Thread#setPriority(int)
     */
    public final int getPriority() {
	return priority;
    }

    /**
     * Changes the name of this thread to be equal to the argument 
     * <code>name</code>. 
     * <p>
     * First the <code>checkAccess</code> method of this thread is called 
     * with no arguments. This may result in throwing a 
     * <code>SecurityException</code>. 
     *
     * @param      name   the new name for this thread.
     * @exception  SecurityException  if the current thread cannot modify this
     *               thread.
     * @see        #getName
     * @see        java.lang.Thread#checkAccess()
     * @see        java.lang.Thread#getName()
     */
    public final void setName(String name) {
	checkAccess();
	this.name = name.toCharArray();
    }

    /**
     * Returns this thread's name.
     *
     * @return  this thread's name.
     * @see     #setName
     * @see     java.lang.Thread#setName(java.lang.String)
     */
    public final String getName() {
	return String.valueOf(name);
    }

    /**
     * Returns the thread group to which this thread belongs. 
     * This method returns null if this thread has died
     * (been stopped).
     *
     * @return  this thread's thread group.
     */
    public final ThreadGroup getThreadGroup() {
	return group;
    }

    /**
     * Returns the number of active threads in the current thread's thread
     * group.
     *
     * @return  the number of active threads in the current thread's thread
     *          group.
     */
    public static int activeCount() {
	return currentThread().getThreadGroup().activeCount();
    }

    /**
     * Copies into the specified array every active thread in 
     * the current thread's thread group and its subgroups. This method simply 
     * calls the <code>enumerate</code> method of the current thread's thread 
     * group with the array argument. 
     * <p>
     * First, if there is a security manager, that <code>enumerate</code>
     * method calls the security
     * manager's <code>checkAccess</code> method 
     * with the thread group as its argument. This may result 
     * in throwing a <code>SecurityException</code>. 
     *
     * @param tarray an array of Thread objects to copy to
     * @return  the number of threads put into the array
     * @exception  SecurityException  if a security manager exists and its  
     *             <code>checkAccess</code> method doesn't allow the operation.
     * @see     java.lang.ThreadGroup#enumerate(java.lang.Thread[])
     * @see     java.lang.SecurityManager#checkAccess(java.lang.ThreadGroup)
     */
    public static int enumerate(Thread tarray[]) {
	return currentThread().getThreadGroup().enumerate(tarray);
    }

    /**
     * Counts the number of stack frames in this thread. The thread must 
     * be suspended. 
     *
     * @return     the number of stack frames in this thread.
     * @exception  IllegalThreadStateException  if this thread is not
     *             suspended.
     * @deprecated The definition of this call depends on {@link #suspend},
     *		   which is deprecated.  Further, the results of this call
     *		   were never well-defined.
     */
    public native int countStackFrames();

    /**
     * Waits at most <code>millis</code> milliseconds for this thread to 
     * die. A timeout of <code>0</code> means to wait forever. 
     *
     * @param      millis   the time to wait in milliseconds.
     * @exception  InterruptedException if another thread has interrupted
     *             the current thread.  The <i>interrupted status</i> of the
     *             current thread is cleared when this exception is thrown.
     */
    public final synchronized void join(long millis) 
    throws InterruptedException {
	long base = System.currentTimeMillis();
	long now = 0;

	if (millis < 0) {
            throw new IllegalArgumentException("timeout value is negative");
	}

	if (millis == 0) {
	    while (isAlive()) {
		wait(0);
	    }
	} else {
	    while (isAlive()) {
		long delay = millis - now;
		if (delay <= 0) {
		    break;
		}
		wait(delay);
		now = System.currentTimeMillis() - base;
	    }
	}
    }

    /**
     * Waits at most <code>millis</code> milliseconds plus 
     * <code>nanos</code> nanoseconds for this thread to die. 
     *
     * @param      millis   the time to wait in milliseconds.
     * @param      nanos    0-999999 additional nanoseconds to wait.
     * @exception  IllegalArgumentException  if the value of millis is negative
     *               the value of nanos is not in the range 0-999999.
     * @exception  InterruptedException if another thread has interrupted
     *             the current thread.  The <i>interrupted status</i> of the
     *             current thread is cleared when this exception is thrown.
     */
    public final synchronized void join(long millis, int nanos) 
    throws InterruptedException {

	if (millis < 0) {
            throw new IllegalArgumentException("timeout value is negative");
	}

	if (nanos < 0 || nanos > 999999) {
            throw new IllegalArgumentException(
				"nanosecond timeout value out of range");
	}

	if (nanos >= 500000 || (nanos != 0 && millis == 0)) {
	    millis++;
	}

	join(millis);
    }

    /**
     * Waits for this thread to die. 
     *
     * @exception  InterruptedException if another thread has interrupted
     *             the current thread.  The <i>interrupted status</i> of the
     *             current thread is cleared when this exception is thrown.
     */
    public final void join() throws InterruptedException {
	join(0);
    }

    /**
     * Prints a stack trace of the current thread. This method is used 
     * only for debugging. 
     *
     * @see     java.lang.Throwable#printStackTrace()
     */
    public static void dumpStack() {
	new Exception("Stack trace").printStackTrace();
    }

    /**
     * Marks this thread as either a daemon thread or a user thread. The 
     * Java Virtual Machine exits when the only threads running are all 
     * daemon threads. 
     * <p>
     * This method must be called before the thread is started. 
      * <p>
     * This method first calls the <code>checkAccess</code> method 
     * of this thread 
     * with no arguments. This may result in throwing a 
     * <code>SecurityException </code>(in the current thread). 
    *
     * @param      on   if <code>true</code>, marks this thread as a
     *                  daemon thread.
     * @exception  IllegalThreadStateException  if this thread is active.
     * @exception  SecurityException  if the current thread cannot modify
     *               this thread.
     * @see        java.lang.Thread#isDaemon()
     * @see          #checkAccess
     */
    public final void setDaemon(boolean on) {
	checkAccess();
	if (isAlive()) {
	    throw new IllegalThreadStateException();
	}
	daemon = on;
    }

    /**
     * Tests if this thread is a daemon thread.
     *
     * @return  <code>true</code> if this thread is a daemon thread;
     *          <code>false</code> otherwise.
     * @see     java.lang.Thread#setDaemon(boolean)
     */
    public final boolean isDaemon() {
	return daemon;
    }

    /**
     * Determines if the currently running thread has permission to 
     * modify this thread. 
     * <p>
     * If there is a security manager, its <code>checkAccess</code> method 
     * is called with this thread as its argument. This may result in 
     * throwing a <code>SecurityException</code>. 
     * <p>
     * Note: This method was mistakenly non-final in JDK 1.1.
     * It has been made final in the Java 2 Platform.
     *
     * @exception  SecurityException  if the current thread is not allowed to
     *               access this thread.
     * @see        java.lang.SecurityManager#checkAccess(java.lang.Thread)
     */
    public final void checkAccess() {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkAccess(this);
	}
    }

    /**
     * Returns a string representation of this thread, including the 
     * thread's name, priority, and thread group.
     *
     * @return  a string representation of this thread.
     */
    public String toString() {
        ThreadGroup group = getThreadGroup();
	if (group != null) {
	    return "Thread[" + getName() + "," + getPriority() + "," + 
		           group.getName() + "]";
	} else {
	    return "Thread[" + getName() + "," + getPriority() + "," + 
		            "" + "]";
	}
    }

    /**    
     * Returns the context ClassLoader for this Thread. The context
     * ClassLoader is provided by the creator of the thread for use
     * by code running in this thread when loading classes and resources.
     * If not set, the default is the ClassLoader context of the parent
     * Thread. The context ClassLoader of the primordial thread is
     * typically set to the class loader used to load the application.
     *
     * <p>First, if there is a security manager, and the caller's class
     * loader is not null and the caller's class loader is not the same as or
     * an ancestor of the context class loader for the thread whose
     * context class loader is being requested, then the security manager's
     * <code>checkPermission</code> 
     * method is called with a 
     * <code>RuntimePermission("getClassLoader")</code> permission
     *  to see if it's ok to get the context ClassLoader.. 
     *
     * @return the context ClassLoader for this Thread
     *
     * @throws SecurityException
     *        if a security manager exists and its 
     *        <code>checkPermission</code> method doesn't allow 
     *        getting the context ClassLoader.
     * @see #setContextClassLoader
     * @see SecurityManager#checkPermission
     * @see java.lang.RuntimePermission
     * 
     * @since 1.2
     */
    public ClassLoader getContextClassLoader() {
	if (contextClassLoader == null)
	    return null;
	SecurityManager sm = System.getSecurityManager();
	if (sm != null) {
	    ClassLoader ccl = ClassLoader.getCallerClassLoader();
	    if (ccl != null && ccl != contextClassLoader && 
                    !contextClassLoader.isAncestor(ccl)) {
		sm.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);
	    }
	}
	return contextClassLoader;
    }

    /**   
     * Sets the context ClassLoader for this Thread. The context
     * ClassLoader can be set when a thread is created, and allows
     * the creator of the thread to provide the appropriate class loader
     * to code running in the thread when loading classes and resources.
     *
     * <p>First, if there is a security manager, its <code>checkPermission</code> 
     * method is called with a 
     * <code>RuntimePermission("setContextClassLoader")</code> permission
     *  to see if it's ok to set the context ClassLoader.. 
     *
     * @param cl the context ClassLoader for this Thread
     * 
     * @exception  SecurityException  if the current thread cannot set the 
     * context ClassLoader.
     * @see #getContextClassLoader
     * @see SecurityManager#checkPermission
     * @see java.lang.RuntimePermission
     * 
     * @since 1.2 
     */
    public void setContextClassLoader(ClassLoader cl) {
	SecurityManager sm = System.getSecurityManager();
	if (sm != null) {
	    sm.checkPermission(new RuntimePermission("setContextClassLoader"));
	}
	contextClassLoader = cl;
    }

    /**
     * Returns <tt>true</tt> if and only if the current thread holds the
     * monitor lock on the specified object.
     *
     * <p>This method is designed to allow a program to assert that
     * the current thread already holds a specified lock:
     * <pre>
     *     assert Thread.holdsLock(obj);
     * </pre>
     *
     * @param  obj the object on which to test lock ownership
     * @throws NullPointerException if obj is <tt>null</tt>
     * @return <tt>true</tt> if the current thread holds the monitor lock on
     *         the specified object.
     * @since 1.4
     */
    public static native boolean holdsLock(Object obj);

    /* Some private helper methods */
    private native void setPriority0(int newPriority);
    private native void stop0(Object o);
    private native void suspend0();
    private native void resume0();
    private native void interrupt0();
}
