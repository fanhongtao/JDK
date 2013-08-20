/*
 * @(#)ThreadInfo.java	1.16 04/04/18
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang.management;

import javax.management.openmbean.CompositeData;
import sun.management.ThreadInfoCompositeData;

/**
 * Thread information. <tt>ThreadInfo</tt> contains the information 
 * about a thread including:
 * <h4>General thread information</h4>
 * <ul>
 *   <li>Thread ID.</li>
 *   <li>Name of the thread.</li>
 * </ul>
 *
 * <h4>Execution information</h4>
 * <ul>
 *   <li>Thread state.</tt>
 *   <li>The object upon which the thread is blocked waiting to enter 
 *       a synchronization block or waiting to be notified in
 *       a {@link Object#wait Object.wait} call.</li>
 *   <li>The ID of the thread that owns the object
 *       that the thread is blocked.</li>
 *   <li>Stack trace of the thread.</li>
 * </ul>
 *
 * <h4>Synchronization statistics</h4>
 * <ul>
 *   <li>The number of times that the thread has blocked for 
 *       synchronization or waited for notification.</li>
 *   <li>The accumulated elapsed time that the thread has blocked
 *       for synchronization or waited for notification
 *       since thread contention monitoring
 *       was enabled. Some Java virtual machine implementation 
 *       may not support this.  The 
 *       {@link ThreadMXBean#isThreadContentionMonitoringSupported()}
 *       method can be used to determine if a Java virtual machine
 *       supports this.</li>
 * </ul>
 *
 * <p>This thread information class is designed for use in monitoring of
 * the system, not for synchronization control.
 *
 * <h4>MXBean Mapping</h4>
 * <tt>ThreadInfo</tt> is mapped to a {@link CompositeData CompositeData}
 * with attributes as specified in 
 * the {@link #from from} method.
 *
 * @see ThreadMXBean#isThreadContentionMonitoringSupported
 *
 * @author  Mandy Chung
 * @version 1.16, 04/18/04 
 * @since   1.5
 */

public class ThreadInfo {
    private final String threadName;
    private final long   threadId;
    private final long   blockedTime;
    private final long   blockedCount;
    private final long   waitedTime;
    private final long   waitedCount;
    private final String lockName;
    private final long   lockOwnerId;
    private final String lockOwnerName;
    private final boolean inNative;
    private final boolean suspended;
    private final Thread.State threadState;
    private final StackTraceElement[]     stackTrace;

    /**
     * Constructor of ThreadInfo created by the JVM
     *
     * @param t             Thread 
     * @param state         Thread state 
     * @param lockObj       Object on which the thread is blocked 
     *                      to enter or waiting 
     * @param lockOwner     the thread holding the lock 
     * @param blockedCount  Number of times blocked to enter a lock
     * @param blockedTime   Approx time blocked to enter a lock
     * @param waitedCount   Number of times waited on a lock
     * @param waitedTime    Approx time waited on a lock
     * @param stackTrace    Thread stack trace
     */
    private ThreadInfo(Thread t, int state, Object lockObj, Thread lockOwner, 
                       long blockedCount, long blockedTime,
                       long waitedCount, long waitedTime,
                       StackTraceElement[] stackTrace) {
        this.threadId = t.getId();
        this.threadName = t.getName();
        this.threadState = 
            sun.management.ManagementFactory.toThreadState(state);
        this.suspended =
            sun.management.ManagementFactory.isThreadSuspended(state);
        this.inNative =
            sun.management.ManagementFactory.isThreadRunningNative(state);
        this.blockedCount = blockedCount;
        this.blockedTime = blockedTime;
        this.waitedCount = waitedCount;
        this.waitedTime = waitedTime;

        if (lockObj == null) {
            this.lockName = null;
        } else {
            this.lockName = 
                lockObj.getClass().getName() + '@' +
                    Integer.toHexString(System.identityHashCode(lockObj));
        }
        if (lockOwner == null) {
            this.lockOwnerId = -1; 
            this.lockOwnerName = null;
        } else {;
            this.lockOwnerId = lockOwner.getId();
            this.lockOwnerName = lockOwner.getName();
        }
        this.stackTrace = stackTrace;
    }

    /*
     * Constructs a <tt>ThreadInfo</tt> object from a
     * {@link CompositeData CompositeData}.  
     */
    private ThreadInfo(CompositeData cd) {
        ThreadInfoCompositeData.validateCompositeData(cd);

        threadId = ThreadInfoCompositeData.getThreadId(cd); 
        threadName = ThreadInfoCompositeData.getThreadName(cd); 
        blockedTime = ThreadInfoCompositeData.getBlockedTime(cd);
        blockedCount = ThreadInfoCompositeData.getBlockedCount(cd);
        waitedTime = ThreadInfoCompositeData.getWaitedTime(cd);
        waitedCount = ThreadInfoCompositeData.getWaitedCount(cd);
        lockName = ThreadInfoCompositeData.getLockName(cd);
        lockOwnerId = ThreadInfoCompositeData.getLockOwnerId(cd);
        lockOwnerName = ThreadInfoCompositeData.getLockOwnerName(cd);
        threadState = ThreadInfoCompositeData.getThreadState(cd);
        suspended = ThreadInfoCompositeData.isSuspended(cd);
        inNative = ThreadInfoCompositeData.isInNative(cd);
        stackTrace = ThreadInfoCompositeData.getStackTrace(cd);
    }

                      
    /**
     * Returns the ID of the thread associated with this <tt>ThreadInfo</tt>.  
     *
     * @return the ID of the associated thread.
     */
    public long getThreadId() {
        return threadId;
    }

    /**
     * Returns the name of the thread associated with this <tt>ThreadInfo</tt>.
     *
     * @return the name of the associated thread.
     */
    public String getThreadName() {
        return threadName;
    }

    /**
     * Returns the state of the thread associated with this <tt>ThreadInfo</tt>.
     *
     * @return <tt>Thread.State</tt> of the associated thread.
     */
    public Thread.State getThreadState() {
         return threadState;
    }
    
    /**
     * Returns the approximate accumulated elapsed time (in milliseconds)
     * that the thread associated with this <tt>ThreadInfo</tt> 
     * has blocked to enter or reenter a monitor
     * since thread contention monitoring is enabled.
     * I.e. the total accumulated time the thread has been in the
     * {@link java.lang.Thread.State#BLOCKED BLOCKED} state since thread
     * contention monitoring was last enabled.
     * This method returns <tt>-1</tt> if thread contention monitoring
     * is disabled.
     *
     * <p>The Java virtual machine may measure the time with a high
     * resolution timer.  This statistic is reset when
     * the thread contention monitoring is reenabled.
     *
     * @return the approximate accumulated elapsed time in milliseconds
     * that a thread entered the <tt>BLOCKED</tt> state;
     * <tt>-1</tt> if thread contention monitoring is disabled.
     *
     * @throws java.lang.UnsupportedOperationException if the Java 
     * virtual machine does not support this operation.
     *
     * @see ThreadMXBean#isThreadContentionMonitoringSupported
     * @see ThreadMXBean#setThreadContentionMonitoringEnabled
     */
    public long getBlockedTime() {
        return blockedTime;
    }

    /**
     * Returns the total number of times that 
     * the thread associated with this <tt>ThreadInfo</tt> 
     * blocked to enter or reenter a monitor. 
     * I.e. the number of times a thread has been in the
     * {@link java.lang.Thread.State#BLOCKED BLOCKED} state. 
     *
     * @return the total number of times that the thread 
     * entered the <tt>BLOCKED</tt> state.
     */
    public long getBlockedCount() {
        return blockedCount;
    }

    /**
     * Returns the approximate accumulated elapsed time (in milliseconds)
     * that the thread associated with this <tt>ThreadInfo</tt> 
     * has waited for notification
     * since thread contention monitoring is enabled.
     * I.e. the total accumulated time the thread has been in the
     * {@link java.lang.Thread.State#WAITING WAITING}
     * or {@link java.lang.Thread.State#TIMED_WAITING TIMED_WAITING} state
     * since thread contention monitoring is enabled.
     * This method returns <tt>-1</tt> if thread contention monitoring
     * is disabled.
     *
     * <p>The Java virtual machine may measure the time with a high
     * resolution timer.  This statistic is reset when
     * the thread contention monitoring is reenabled.
     * 
     * @return the approximate accumulated elapsed time in milliseconds 
     * that a thread has been in the <tt>WAITING</tt> or
     * <tt>TIMED_WAITING</tt> state;
     * <tt>-1</tt> if thread contention monitoring is disabled.
     *
     * @throws java.lang.UnsupportedOperationException if the Java 
     * virtual machine does not support this operation.
     *
     * @see ThreadMXBean#isThreadContentionMonitoringSupported
     * @see ThreadMXBean#setThreadContentionMonitoringEnabled
     */
    public long getWaitedTime() {
        return waitedTime;
    }

    /**
     * Returns the total number of times that 
     * the thread associated with this <tt>ThreadInfo</tt> 
     * waited for notification.
     * I.e. the number of times that a thread has been
     * in the {@link java.lang.Thread.State#WAITING WAITING}
     * or {@link java.lang.Thread.State#TIMED_WAITING TIMED_WAITING} state.
     *
     * @return the total number of times that the thread 
     * was in the <tt>WAITING</tt> or <tt>TIMED_WAITING</tt> state.
     */
    public long getWaitedCount() {
        return waitedCount;
    }

    /**
     * Returns the string representation of the monitor lock that
     * the thread associated with this <tt>ThreadInfo</tt> 
     * is blocked to enter or waiting to be notified through 
     * the {@link Object#wait Object.wait} method.  
     * The returned string representation of a monitor lock consists of
     * the name of the class of which the object is an instance, the
     * at-sign character `@', and the unsigned hexadecimal representation 
     * of the <em>identity</em> hash code of the object. 
     * The returned string may not 
     * be unique depending on the implementation of the 
     * {@link System#identityHashCode} method.
     * This method returns a string equals to the value of: 
     * <blockquote>
     * <pre>
     * lock.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(lock))
     * </pre></blockquote>
     * where <tt>lock</tt> is the monitor lock object.
     *
     * <p>If the thread is not blocking to enter on any monitor object,
     * or is not waiting on a monitor object for notification in a 
     * <tt>Object.wait</tt> call, 
     * this method returns <tt>null</tt>. 
     *
     * @return the string representation of the monitor lock that 
     * the thread is blocking to enter or waiting to be notified through  
     * the <tt>Object.wait</tt> method if any;
     * <tt>null</tt> otherwise.
     *
     */
    public String getLockName() {
        return lockName;
    }

    /**
     * Returns the ID of the thread which holds the monitor lock of an object 
     * on which the thread associated with this <tt>ThreadInfo</tt>
     * is blocking.
     * This method will return <tt>-1</tt> if this thread is not blocked
     * or waiting on any monitor, or if the monitor lock is not held
     * by any thread.
     *
     * @return the thread ID of the owner thread of the monitor lock of the
     * object this thread is blocking on;
     * <tt>-1</tt> if this thread is not blocked
     * or waiting on any monitor, or if the monitor lock is not held
     * by any thread.
     *
     * @see #getLockName
     */
    public long getLockOwnerId() {
        return lockOwnerId;
    }

    /**
     * Returns the name of the thread which holds the monitor lock of an object 
     * on which the thread associated with this <tt>ThreadInfo</tt>
     * is blocking.
     * This method will return <tt>null</tt> if this thread is not blocked
     * or waiting on any monitor, or if the monitor lock is not held
     * by any thread.
     *
     * @return the name of the thread that holds the monitor lock of the object
     * this thread is blocking on;
     * <tt>null</tt> if this thread is not blocked
     * or waiting on any monitor, or if the monitor lock is not held
     * by any thread.
     *
     * @see #getLockName
     */
    public String getLockOwnerName() {
        return lockOwnerName;
    }

    /**
     * Returns the stack trace of the thread 
     * associated with this <tt>ThreadInfo</tt>.
     * If no stack trace was requested for this thread info, this method
     * will return a zero-length array.
     * If the returned array is of non-zero length then the first element of
     * the array represents the top of the stack, which is the most recent
     * method invocation in the sequence.  The last element of the array
     * represents the bottom of the stack, which is the least recent method
     * invocation in the sequence.
     *
     * <p>Some Java virtual machines may, under some circumstances, omit one
     * or more stack frames from the stack trace.  In the extreme case,
     * a virtual machine that has no stack trace information concerning
     * the thread associated with this <tt>ThreadInfo</tt> 
     * is permitted to return a zero-length array from this method.
     *
     * @return an array of <tt>StackTraceElement</tt> objects of the thread.
     */
    public StackTraceElement[] getStackTrace() {
        if (stackTrace == null) {
            return NO_STACK_TRACE;
        } else {
            return stackTrace;
        }
    }

    /**
     * Tests if the thread associated with this <tt>ThreadInfo</tt>
     * is suspended.  This method returns <tt>true</tt> if
     * {@link Thread#suspend} has been called.
     *
     * @return <tt>true</tt> if the thread is suspended;
     *         <tt>false</tt> otherwise.
     */
    public boolean isSuspended() {
         return suspended;
    }

    /**
     * Tests if the thread associated with this <tt>ThreadInfo</tt>
     * is executing native code via the Java Native Interface (JNI).
     * The JNI native code does not include
     * the virtual machine support code or the compiled native
     * code generated by the virtual machine.
     *
     * @return <tt>true</tt> if the thread is executing native code;
     *         <tt>false</tt> otherwise.
     */
    public boolean isInNative() {
         return inNative;
    }

    /**
     * Returns a string representation of this thread info.
     *
     * @return a string representation of this thread info.
     */
    public String toString() {
        return "Thread " + getThreadName() + " (Id = " + getThreadId() + ") " + 
               getThreadState() + " " + getLockName(); 
    }

    /**
     * Returns a <tt>ThreadInfo</tt> object represented by the
     * given <tt>CompositeData</tt>.
     * The given <tt>CompositeData</tt> must contain the following attributes:
     * <blockquote>
     * <table border>
     * <tr>
     *   <th align=left>Attribute Name</th>
     *   <th align=left>Type</th>
     * </tr>
     * <tr>
     *   <td>threadId</td>
     *   <td><tt>java.lang.Long</tt></td>
     * </tr>
     * <tr>
     *   <td>threadName</td>
     *   <td><tt>java.lang.String</tt></td>
     * </tr>
     * <tr>
     *   <td>threadState</td>
     *   <td><tt>java.lang.String</tt></td>
     * </tr>
     * <tr>
     *   <td>suspended</td>
     *   <td><tt>java.lang.Boolean</tt></td>
     * </tr>
     * <tr>
     *   <td>inNative</td>
     *   <td><tt>java.lang.Boolean</tt></td>
     * </tr>
     * <tr>
     *   <td>blockedCount</td>
     *   <td><tt>java.lang.Long</tt></td>
     * </tr>
     * <tr>
     *   <td>blockedTime</td>
     *   <td><tt>java.lang.Long</tt></td>
     * </tr>
     * <tr>
     *   <td>waitedCount</td>
     *   <td><tt>java.lang.Long</tt></td>
     * </tr>
     * <tr>
     *   <td>waitedTime</td>
     *   <td><tt>java.lang.Long</tt></td>
     * </tr>
     * <tr>
     *   <td>lockName</td>
     *   <td><tt>java.lang.String</tt></td>
     * </tr>
     * <tr>
     *   <td>lockOwnerId</td>
     *   <td><tt>java.lang.Long</tt></td>
     * </tr>
     * <tr>
     *   <td>lockOwnerName</td>
     *   <td><tt>java.lang.String</tt></td>
     * </tr>
     * <tr>
     *   <td>stackTrace</td>
     *   <td><tt>javax.management.openmbean.CompositeData[]</tt>
     *       <p>
     *       Each element is a <tt>CompositeData</tt> representing
     *       StackTraceElement containing the following attributes:
     *       <blockquote>
     *       <table cellspacing=1 cellpadding=0>
     *       <tr>
     *         <th align=left>Attribute Name</th>
     *         <th align=left>Type</th>
     *       </tr>
     *       <tr>
     *         <td>className</td>
     *         <td><tt>java.lang.String</tt></td>
     *       </tr>
     *       <tr>
     *         <td>methodName</td>
     *         <td><tt>java.lang.String</tt></td>
     *       </tr>
     *       <tr>
     *         <td>fileName</td>
     *         <td><tt>java.lang.String</tt></td>
     *       </tr>
     *       <tr>
     *         <td>lineNumber</td>
     *         <td><tt>java.lang.Integer</tt></td>
     *       </tr>
     *       <tr>
     *         <td>nativeMethod</td>
     *         <td><tt>java.lang.Boolean</tt></td>
     *       </tr>
     *       </table>
     *       </blockquote>
     *   </td>
     * </tr>
     * </table>
     * </blockquote>
     *
     * @param cd <tt>CompositeData</tt> representing a <tt>ThreadInfo</tt>
     *
     * @throws IllegalArgumentException if <tt>cd</tt> does not
     *   represent a <tt>ThreadInfo</tt> with the attributes described
     *   above.

     * @return a <tt>ThreadInfo</tt> object represented
     *         by <tt>cd</tt> if <tt>cd</tt> is not <tt>null</tt>;
     *         <tt>null</tt> otherwise.
     */
    public static ThreadInfo from(CompositeData cd) {
        if (cd == null) {
            return null;
        }

        if (cd instanceof ThreadInfoCompositeData) {
            return ((ThreadInfoCompositeData) cd).getThreadInfo();
        } else {
            return new ThreadInfo(cd);
        }
    }

    private static final StackTraceElement[] NO_STACK_TRACE =
        new StackTraceElement[0];
}
