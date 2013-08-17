/*
 * @(#)Thread.java	1.73 98/11/11
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.lang;

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
 *     performing the <code>stop</code> method. 
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
 * @version 1.73, 11/11/98
 * @see     java.lang.Runnable
 * @see     java.lang.Runtime#exit(int)
 * @see     java.lang.Thread#run()
 * @see     java.lang.Thread#stop()
 * @since   JDK1.0
 */
public
class Thread implements Runnable {
    private char	name[];
    private int         priority;
    private Thread	threadQ;
    private int 	PrivateInfo;
    private int		eetop;

    /* Whether or not to single_step this thread. */
    private boolean	single_step;

    /* Whether or not the thread is a daemon thread. */
    private boolean	daemon = false;

    /* Whether or not this thread was asked to exit before it runs.*/
    private boolean	stillborn = false;

    /* What will be run. */
    private Runnable target;

    /* The system queue of threads is linked through activeThreadQueue. */
    private static Thread activeThreadQ;

    /* The group of this thread */
    private ThreadGroup	group;

    /* For autonumbering anonymous threads. */
    private static int threadInitNumber;
    private static synchronized int nextThreadNum() {
	return threadInitNumber++;
    }

    /* The initial segment of the Java stack (not necessarily initialized) */
    private int	initial_stack_memory;

    /**
     * The minimum priority that a thread can have. 
     *
     * @since   JDK1.0
     */
    public final static int MIN_PRIORITY = 1;

   /**
     * The default priority that is assigned to a thread. 
     *
     * @since   JDK1.0
     */
    public final static int NORM_PRIORITY = 5;

    /**
     * The maximum priority that a thread can have. 
     *
     * @since   JDK1.0
     */
    public final static int MAX_PRIORITY = 10;

    /**
     * Returns a reference to the currently executing thread object.
     *
     * @return  the currently executing thread.
     * @since   JDK1.0
     */
    public static native Thread currentThread();

    /**
     * Causes the currently executing thread object to temporarily pause 
     * and allow other threads to execute. 
     *
     * @since   JDK1.0
     */
    public static native void yield();

    /**	
     * Causes the currently executing thread to sleep (temporarily cease 
     * execution) for the specified number of milliseconds. The thread 
     * does not lose ownership of any monitors.
     *
     * @param      millis   the length of time to sleep in milliseconds.
     * @exception  InterruptedException  if another thread has interrupted
     *               this thread.
     * @see        java.lang.Object#notify()
     * @since      JDK1.0
     */
    public static native void sleep(long millis) throws InterruptedException;

    /**
     * Causes the currently executing thread to sleep (cease execution) 
     * for the specified number of milliseconds plus the specified number 
     * of nanoseconds. The thread does not lose ownership of any monitors.
     *
     * @param      millis   the length of time to sleep in milliseconds.
     * @param      nanos    0-999999 additional nanoseconds to sleep.
     * @exception  IllegalArgumentException  if the value of millis is negative
     *               or the value of nanos is not in the range 0-999999.
     * @exception  InterruptedException  if another thread has interrupted
     *               this thread.
     * @see        java.lang.Object#notify()
     * @since      JDK1.0
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
     */
    private void init(ThreadGroup g, Runnable target, String name){
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
	this.target = target;
	setPriority(priority);
	g.add(this);
    }

   /**
     * Allocates a new <code>Thread</code> object. This constructor has 
     * the same effect as <code>Thread(null, null,</code>
     * <i>gname</i><code>)</code>, where <b><i>gname</i></b> is 
     * a newly generated name. Automatically generated names are of the 
     * form <code>"Thread-"+</code><i>n</i>, where <i>n</i> is an integer. 
     * <p>
     * Threads created this way must have overridden their
     * <code>run()</code> method to actually do anything.  An example
     * illustrating this method being used follows:
     * <p><blockquote><pre>
     *     import java.lang.*; 
     *
     *     class plain01 implements Runnable {
     *         String name; 
     *         plain01() {
     *             name = null;
     *         }
     *         plain01(String s) {
     *             name = s;
     *         }
     *         public void run() {
     *             if (name == null)
     *                 System.out.println("A new thread created");
     *             else
     *                 System.out.println("A new thread with name " + name +
     *                                    " created");
     *         }
     *     }
     *     class threadtest01 {
     *         public static void main(String args[] ) {
     *             int failed = 0 ;
     *
     *             <b>Thread t1 = new Thread();</b>  
     *             if (t1 != null)
     *                 System.out.println("new Thread() succeed");
     *             else {
     *                 System.out.println("new Thread() failed"); 
     *                 failed++; 
     *             }
     *         }
     *     }
     * </pre></blockquote>
     *
     * @see     java.lang.Thread#Thread(java.lang.ThreadGroup, java.lang.Runnable, java.lang.String)
     * @since   JDK1.0
     */
    public Thread() {
	init(null, null, "Thread-" + nextThreadNum());
    }

    /**
     * Allocates a new <code>Thread</code> object. This constructor has 
     * the same effect as <code>Thread(null, target,</code>
     * <i>gname</i><code>)</code>, where <i>gname</i> is 
     * a newly generated name. Automatically generated names are of the 
     * form <code>"Thread-"+</code><i>n</i>, where <i>n</i> is an integer. 
     *
     * @param   target   the object whose <code>run</code> method is called.
     * @see     java.lang.Thread#Thread(java.lang.ThreadGroup, java.lang.Runnable, java.lang.String)
     * @since   JDK1.0
     */
    public Thread(Runnable target) {
	init(null, target, "Thread-" + nextThreadNum());
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
     *               thread in the specified thread group.
     * @see        java.lang.Thread#Thread(java.lang.ThreadGroup, java.lang.Runnable, java.lang.String)
     * @since      JDK1.0
     */
    public Thread(ThreadGroup group, Runnable target) {
	init(group, target, "Thread-" + nextThreadNum());
    }

    /**
     * Allocates a new <code>Thread</code> object. This constructor has 
     * the same effect as <code>Thread(null, null, name)</code>. 
     *
     * @param   name   the name of the new thread.
     * @see     java.lang.Thread#Thread(java.lang.ThreadGroup, java.lang.Runnable, java.lang.String)
     * @since   JDK1.0
     */
    public Thread(String name) {
	init(null, null, name);
    }

    /**
     * Allocates a new <code>Thread</code> object. This constructor has 
     * the same effect as <code>Thread(group, null, name)</code> 
     *
     * @param      group   the thread group.
     * @param      name    the name of the new thread.
     * @exception  SecurityException  if the current thread cannot create a
     *               thread in the specified thread group.
     * @see        java.lang.Thread#Thread(java.lang.ThreadGroup, java.lang.Runnable, java.lang.String)
     * @since   JDK1.0
     */
    public Thread(ThreadGroup group, String name) {
	init(group, null, name);
    }

    /**
     * Allocates a new <code>Thread</code> object. This constructor has 
     * the same effect as <code>Thread(null, target, name)</code>. 
     *
     * @param   target   the object whose <code>run</code> method is called.
     * @param   name     the name of the new thread.
     * @see     java.lang.Thread#Thread(java.lang.ThreadGroup, java.lang.Runnable, java.lang.String)
     * @since   JDK1.0
     */
    public Thread(Runnable target, String name) {
	init(null, target, name);
    }

    /**
     * Allocates a new <code>Thread</code> object so that it has 
     * <code>target</code> as its run object, has the specified 
     * <code>name</code> as its name, and belongs to the thread group 
     * referred to by <code>group</code>.
     * <p>
     * If <code>group</code> is not <code>null</code>, the 
     * <code>checkAccess</code> method of that thread group is called with 
     * no arguments; this may result in throwing a 
     * <code>SecurityException</code>; if <code>group</code> is 
     * <code>null</code>, the new process belongs to the same group as 
     * the thread that is creating the new thread. 
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
     * @since      JDK1.0
     */
    public Thread(ThreadGroup group, Runnable target, String name) {
	init(group, target, name);
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
     * @since      JDK1.0
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
     * @see     java.lang.Thread#Thread(java.lang.ThreadGroup, java.lang.Runnable, java.lang.String)
     * @see     java.lang.Runnable#run()
     * @since   JDK1.0
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
     * First, the <code>checkAccess</code> method of this thread is called 
     * with no arguments. This may result in throwing a 
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
     * <p>
     * Note: the use of this method is unsafe as it can result in the 
     * corruption of invariants. Stopping a thread with Thread.stop causes 
     * it to unlock all of the monitors that it has locked (as a natural 
     * consequence of the unchecked <code>ThreadDeath</code> exception 
     * propagating up the stack). If any of the objects previously protected 
     * by these monitors were in an inconsistent state, the damaged objects 
     * become visible to other threads potentially resulting in arbitrary 
     * behaviour. Many uses of <code>stop</code> should be replaced by code 
     * that simply modifies some volatile variable to indicate that the target
     * thread should stop running. The target thread should check this 
     * variable regularly, and return from its run method in an orderly 
     * fashion if the variable indicates that it is to stop running. If the 
     * target thread waits for long periods, the interrupt method should be 
     * used to interrupt the wait.
     *
     * @exception  SecurityException  if the current thread cannot modify
     *               this thread.
     * @see        java.lang.Thread#checkAccess()
     * @see        java.lang.Thread#run()
     * @see        java.lang.Thread#start()
     * @see        java.lang.ThreadDeath
     * @see        java.lang.ThreadGroup#uncaughtException(java.lang.Thread, java.lang.Throwable)
     * @since      JDK1.0
     */
    public final void stop() {
	synchronized (this) {
	    checkAccess();
	    resume();	// Wake up thread if it was suspended; no-op otherwise
	    stop0(new ThreadDeath());
	}
    }

    /**
     * Forces the thread to stop executing. 
     * <p>
     * First, the <code>checkAccess</code> method of this thread is called 
     * with no arguments. This may result in throwing a 
     * <code>SecurityException </code>(in the current thread). 
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
     * @see        java.lang.Thread#checkAccess()
     * @see        java.lang.Thread#run()
     * @see        java.lang.Thread#start()
     * @see        java.lang.Thread#stop()
     * @since      JDK1.0
     */
    public final synchronized void stop(Throwable o) {
	checkAccess();
	resume();	// Wake up thread if it was suspended; no-op otherwise
	stop0(o);
    }

    /**
     * Interrupts this thread.
     *
     * @since   JDK1.0
     */
	// Note that this method is not synchronized.  Three reasons for this:
	// 1) It changes the API.
	// 2) It's another place where the system could hang.
	// 3) All we're doing is turning on a one-way bit.  It doesn't matter
	//    exactly when it's done WRT probes via the interrupted() method.
    public void interrupt() {
	checkAccess();
	interrupt0();
    }

    /**
     * Tests if the current thread has been interrupted.
     * Note that <code>interrupted</code> is a static method, while 
     * <code>isInterrupted</code> is called on the current 
     * <code>Thread</code> instance. 
     *
     * @return  <code>true</code> if the current thread has been interrupted;
     *          <code>false</code> otherwise.
     * @see     java.lang.Thread#isInterrupted()
     * @since   JDK1.0
     */
    public static boolean interrupted() {
	return currentThread().isInterrupted(true);
    }

    /**
     * Tests if the current thread has been interrupted.
     * Note that <code>isInterrupted</code> 
     * is called on the current <code>Thread</code> instance; by 
     * contrast, <code>interrupted</code> is a static method. 
     *
     * @return  <code>true</code> if this thread has been interrupted;
     *          <code>false</code> otherwise.
     * @see     java.lang.Thread#interrupted()
     * @since   JDK1.0
     */
    public boolean isInterrupted() {
	return isInterrupted(false);
    }

    /**
     * Ask if some Thread has been interrupted.  The interrupted state
     * is reset or not based on the value of ClearInterrupted that is
     * passed.
     */
    private native boolean isInterrupted(boolean ClearInterrupted);

    /**
     * Destroys this thread, without any cleanup. Any monitors it has 
     * locked remain locked. (This method is not implemented in 
     * Java&nbsp;1.0.2.)
     *
     * @since   JDK1.0
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
     * @since   JDK1.0
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
     * @see        java.lang.Thread#checkAccess()
     * @see        java.lang.Thread#isAlive()
     * @since      JDK1.0
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
     * <code>SecurityException</code>(in the current thread). 
     * <p>
     * If the thread is alive but suspended, it is resumed and is 
     * permitted to make progress in its execution. 
     *
     * @exception  SecurityException  if the current thread cannot modify this
     *               thread.
     * @see        java.lang.Thread#checkAccess()
     * @see        java.lang.Thread#isAlive()
     * @since      JDK1.0
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
     * @exception  IllegalArgumentException  If the priority is not in the
     *               range <code>MIN_PRIORITY</code> to
     *               <code>MAX_PRIORITY</code>.
     * @exception  SecurityException  if the current thread cannot modify
     *               this thread.
     * @see        java.lang.Thread#checkAccess()
     * @see        java.lang.Thread#getPriority()
     * @see        java.lang.Thread#getThreadGroup()
     * @see        java.lang.Thread#MAX_PRIORITY
     * @see        java.lang.Thread#MIN_PRIORITY
     * @see        java.lang.ThreadGroup#getMaxPriority()
     * @since      JDK1.0
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
     * @return  this thread's name.
     * @see     java.lang.Thread#setPriority(int)
     * @since   JDK1.0
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
     * @see        java.lang.Thread#checkAccess()
     * @see        java.lang.Thread#getName()
     * @since      JDK1.0
     */
    public final void setName(String name) {
	checkAccess();
	this.name = name.toCharArray();
    }

    /**
     * Returns this thread's name.
     *
     * @return  this thread's name.
     * @see     java.lang.Thread#setName(java.lang.String)
     * @since   JDK1.0
     */
    public final String getName() {
	return String.valueOf(name);
    }

    /**
     * Returns this thread's thread group.
     *
     * @return  this thread's thread group.
     * @since   JDK1.0
     */
    public final ThreadGroup getThreadGroup() {
	return group;
    }

    /**
     * Returns the current number of active threads in this thread group.
     *
     * @return  the current number of threads in this thread's thread group.
     * @since   JDK1.0
     */
    public static int activeCount() {
	return currentThread().getThreadGroup().activeCount();
    }

    /**
     * Copies into the specified array every active thread in this 
     * thread group and its subgroups. This method simply calls the 
     * <code>enumerate</code> method of this thread's thread group with 
     * the array argument. 
     *
     * @return  the number of threads put into the array.
     * @see     java.lang.ThreadGroup#enumerate(java.lang.Thread[])
     * @since   JDK1.0
     */
    public static int enumerate(Thread tarray[]) {
	return currentThread().getThreadGroup().enumerate(tarray);
    }

    /**
     * Counts the number of stack frames in this thread. The thread must 
     * be suspended. 
     *
     * @return     the number of stack frames in this thread.
     * @exception  IllegalThreadStateException  if this thread is not suspended.
     * @since      JDK1.0
     */
    public native int countStackFrames();

    /**
     * Waits at most <code>millis</code> milliseconds for this thread to 
     * die. A timeout of <code>0</code> means to wait forever. 
     *
     * @param      millis   the time to wait in milliseconds.
     * @exception  InterruptedException  if another thread has interrupted the
     *               current thread.
     * @since      JDK1.0
     */
    public final synchronized void join(long millis) throws InterruptedException {
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
     * @exception  InterruptedException  if another thread has interrupted the
     *               current thread.
     * @since      JDK1.0
     */
    public final synchronized void join(long millis, int nanos) throws InterruptedException {

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
     * @exception  InterruptedException  if another thread has interrupted the
     *               current thread.
     * @since      JDK1.0
     */
    public final void join() throws InterruptedException {
	join(0);
    }

    /**
     * Prints a stack trace of the current thread. This method is used 
     * only for debugging. 
     *
     * @see     java.lang.Throwable#printStackTrace()
     * @since   JDK1.0
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
     *
     * @param      on   if <code>true</code>, marks this thread as a
     *                  daemon thread.
     * @exception  IllegalThreadStateException  if this thread is active.
     * @see        java.lang.Thread#isDaemon()
     * @since      JDK1.0
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
     * @since   JDK1.0
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
     *
     * @exception  SecurityException  if the current thread is not allowed to
     *               access this thread.
     * @see        java.lang.SecurityManager#checkAccess(java.lang.Thread)
     * @since      JDK1.0
     */
    public void checkAccess() {
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
     * @since   JDK1.0
     */
    public String toString() {
	if (getThreadGroup() != null) {
	    return "Thread[" + getName() + "," + getPriority() + "," + 
		            getThreadGroup().getName() + "]";
	} else {
	    return "Thread[" + getName() + "," + getPriority() + "," + 
		            "" + "]";
	}
    }

    /* Some private helper methods */
    private native void setPriority0(int newPriority);
    private native void stop0(Object o);
    private native void suspend0();
    private native void resume0();
    private native void interrupt0();
}
