/*
 * @(#)Object.java	1.40 98/07/01
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
 * Class <code>Object</code> is the root of the class hierarchy. 
 * Every class has <code>Object</code> as a superclass. All objects, 
 * including arrays, implement the methods of this class. 
 *
 * @author  unascribed
 * @version 1.40, 07/01/98
 * @see     java.lang.Class
 * @since   JDK1.0
 */
public class Object {
    /**
     * Returns the runtime class of an object. 
     *
     * @return  the object of type <code>Class</code> that represents the
     *          runtime class of the object.
     * @since   JDK1.0
     */
    public final native Class getClass();

    /**
     * Returns a hash code value for the object. This method is 
     * supported for the benefit of hashtables such as those provided by 
     * <code>java.util.Hashtable</code>. 
     * <p>
     * The general contract of <code>hashCode</code> is: 
     * <ul>
     * <li>Whenever it is invoked on the same object more than once during 
     *     an execution of a Java application, the <code>hashCode</code> method 
     *     must consistently return the same integer. This integer need not 
     *     remain consistent from one execution of an application to another 
     *     execution of the same application. 
     * <li>If two objects are equal according to the <code>equals</code> 
     *     method, then calling the <code>hashCode</code> method on each of the 
     *     two objects must produce the same integer result. 
     * </ul>
     *
     * @return  a hash code value for this object.
     * @see     java.lang.Object#equals(java.lang.Object)
     * @see     java.util.Hashtable
     * @since   JDK1.0
     */
    public native int hashCode();

    /**
     * Compares two Objects for equality.
     * <p>
     * The <code>equals</code> method implements an equivalence relation: 
     * <ul>
     * <li>It is <i>reflexive</i>: for any reference value <code>x</code>, 
     *     <code>x.equals(x)</code> should return <code>true</code>. 
     * <li>It is <i>symmetric</i>: for any reference values <code>x</code> and 
     *     <code>y</code>, <code>x.equals(y)</code> should return 
     *     <code>true</code> if and only if <code>y.equals(x)</code> returns 
     *     <code>true</code>. 
     * <li>It is <i>transitive</i>: for any reference values <code>x</code>, 
     *     <code>y</code>, and <code>z</code>, if <code>x.equals(y)</code>
     *     returns  <code>true</code> and <code>y.equals(z)</code> returns 
     *     <code>true</code>, then <code>x.equals(z)</code> should return 
     *     <code>true</code>. 
     * <li>It is <i>consistent</i>: for any reference values <code>x</code> 
     *     and <code>y</code>, multiple invocations of <code>x.equals(y)</code> 
     *     consistently return <code>true</code> or consistently return 
     *     <code>false</code>. 
     * <li>For any reference value <code>x</code>, <code>x.equals(null)</code> 
     *     should return <code>false</code>.
     * </ul>
     * <p>
     * The equals method for class <code>Object</code> implements the most 
     * discriminating possible equivalence relation on objects; that is, 
     * for any reference values <code>x</code> and <code>y</code>, this 
     * method returns <code>true</code> if and only if <code>x</code> and 
     * <code>y</code> refer to the same object (<code>x==y</code> has the 
     * value <code>true</code>). 
     *
     * @param   obj   the reference object with which to compare.
     * @return  <code>true</code> if this object is the same as the obj
     *          argument; <code>false</code> otherwise.
     * @see     java.lang.Boolean#hashCode()
     * @see     java.util.Hashtable
     * @since   JDK1.0
     */
    public boolean equals(Object obj) {
	return (this == obj);
    }

    /**
     * Creates a new object of the same class as this object. It then 
     * initializes each of the new object's fields by assigning it the 
     * same value as the corresponding field in this object. No 
     * constructor is called. 
     * <p>
     * The <code>clone</code> method of class <code>Object</code> will 
     * only clone an object whose class indicates that it is willing for 
     * its instances to be cloned. A class indicates that its instances 
     * can be cloned by declaring that it implements the 
     * <code>Cloneable</code> interface. 
     *
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not
     *               support the <code>Cloneable</code> interface. Subclasses
     *               that override the <code>clone</code> method can also
     *               throw this exception to indicate that an instance cannot
     *               be cloned.
     * @exception  OutOfMemoryError            if there is not enough memory.
     * @see        java.lang.Cloneable
     * @since      JDK1.0
     */
    protected native Object clone() throws CloneNotSupportedException;

    /**
     * Returns a string representation of the object. In general, the 
     * <code>toString</code> method returns a string that 
     * "textually represents" this object. The result should 
     * be a concise but informative representation that is easy for a 
     * person to read.
     * It is recommendedthat all subclasses override this method.
     * <p>
     * The <code>toString</code> method for class <code>Object</code> 
     * returns a string consisting of the name of the class of which the 
     * object is an instance, the at-sign character `<code>@</code>', and 
     * the unsigned hexadecimal representation of the hash code of the 
     * object. 
     *
     * @return  a string representation of the object.
     * @since   JDK1.0
     */
    public String toString() {
	return getClass().getName() + "@" + Integer.toHexString(hashCode());
    }

    /**
     * Wakes up a single thread that is waiting on this object's 
     * monitor. A thread waits on an object's monitor by calling one of 
     * the <code>wait</code> methods.
     * <p>
     * This method should only be called by a thread that is the owner 
     * of this object's monitor. A thread becomes the owner of the 
     * object's monitor in one of three ways: 
     * <ul>
     * <li>By executing a synchronized instance method of that object. 
     * <li>By executing the body of a <code>synchronized</code> statement 
     *     that synchronizes on the object. 
     * <li>For objects of type <code>Class,</code> by executing a 
     *     synchronized static method of that class. 
     * </ul>
     * <p>
     * Only one thread at a time can own an object's monitor. 
     *
     * @exception  IllegalMonitorStateException  if the current thread is not
     *               the owner of this object's monitor.
     * @see        java.lang.Object#notifyAll()
     * @see        java.lang.Object#wait()
     * @since      JDK1.0
     */
    public final native void notify();

    /**
     * Wakes up all threads that are waiting on this object's monitor. A 
     * thread waits on an object's monitor by calling one of the 
     * <code>wait</code> methods.
     * <p>
     * This method should only be called by a thread that is the owner 
     * of this object's monitor. See the <code>notify</code> method for a 
     * description of the ways in which a thread can become the owner of 
     * a monitor. 
     *
     * @exception  IllegalMonitorStateException  if the current thread is not
     *               the owner of this object's monitor.
     * @see        java.lang.Object#notify()
     * @see        java.lang.Object#wait()
     * @since      JDK1.0
     */
    public final native void notifyAll();

    /**
     * Waits to be notified by another thread of a change in this object.
     * <p>
     * The current thread must own this object's monitor. The thread 
     * releases ownership of this monitor and waits until either of the 
     * following two conditions has occurred: 
     * <ul>
     * <li>Another thread notifies threads waiting on this object's monitor 
     *     to wake up either through a call to the <code>notify</code> method 
     *     or the <code>notifyAll</code> method. 
     * <li>The timeout period, specified by the <code>timeout</code> 
     *     argument in milliseconds, has elapsed. 
     * </ul>
     * <p>
     * The thread then waits until it can re-obtain ownership of the 
     * monitor and resumes execution. 
     * <p>
     * This method should only be called by a thread that is the owner 
     * of this object's monitor. See the <code>notify</code> method for a 
     * description of the ways in which a thread can become the owner of 
     * a monitor. 
     *
     * @param      timeout   the maximum time to wait in milliseconds.
     * @exception  IllegalArgumentException      if the value of timeout is
     *		     negative.
     * @exception  IllegalMonitorStateException  if the current thread is not
     *               the owner of the object's monitor.
     * @exception  InterruptedException          if another thread has
     *               interrupted this thread.
     * @see        java.lang.Object#notify()
     * @see        java.lang.Object#notifyAll()
     * @since      JDK1.0
     */
    public final native void wait(long timeout) throws InterruptedException;

    /**
     * Waits to be notified by another thread of a change in this object.
     * <p>
     * This method is similar to the <code>wait</code> method of one 
     * argument, but it allows finer control over the amount of time to 
     * wait for a notification before giving up. 
     * <p>
     * The current thread must own this object's monitor. The thread 
     * releases ownership of this monitor and waits until either of the 
     * following two conditions has occurred: 
     * <ul>
     * <li>Another thread notifies threads waiting on this object's monitor 
     *     to wake up either through a call to the <code>notify</code> method 
     *     or the <code>notifyAll</code> method. 
     * <li>The timeout period, specified by <code>timeout</code> 
     *     milliseconds plus <code>nanos</code> nanoseconds arguments, has 
     *     elapsed. 
     * </ul>
     * <p>
     * The thread then waits until it can re-obtain ownership of the 
     * monitor and resumes execution 
     * <p>
     * This method should only be called by a thread that is the owner 
     * of this object's monitor. See the <code>notify</code> method for a 
     * description of the ways in which a thread can become the owner of 
     * a monitor. 
     *
     * @param      timeout   the maximum time to wait in milliseconds.
     * @param      nano      additional time, in nanoseconds range
     *                       0-999999.
     * @exception  IllegalArgumentException      if the value of timeout is
     *			    negative or the value of nanos is
     *			    not in the range 0-999999.
     * @exception  IllegalMonitorStateException  if the current thread is not
     *               the owner of this object's monitor.
     * @exception  InterruptedException          if another thread has
     *               interrupted this thread.
     * @since      JDK1.0
     */
    public final void wait(long timeout, int nanos) throws InterruptedException {
        if (timeout < 0) {
            throw new IllegalArgumentException("timeout value is negative");
        }

        if (nanos < 0 || nanos > 999999) {
            throw new IllegalArgumentException(
				"nanosecond timeout value out of range");
        }

	if (nanos >= 500000 || (nanos != 0 && timeout == 0)) {
	    timeout++;
	}

	wait(timeout);
    }

    /**
     * Waits to be notified by another thread of a change in this object. 
     * <p>
     * The current thread must own this object's monitor. The thread 
     * releases ownership of this monitor and waits until another thread 
     * notifies threads waiting on this object's monitor to wake up 
     * either through a call to the <code>notify</code> method or the 
     * <code>notifyAll</code> method. The thread then waits until it can 
     * re-obtain ownership of the monitor and resumes execution. 
     * <p>
     * This method should only be called by a thread that is the owner 
     * of this object's monitor. See the <code>notify</code> method for a 
     * description of the ways in which a thread can become the owner of 
     * a monitor. 
     *
     * @exception  IllegalMonitorStateException  if the current thread is not
     *               the owner of the object's monitor.
     * @exception  InterruptedException          if another thread has
     *               interrupted this thread.
     * @see        java.lang.Object#notify()
     * @see        java.lang.Object#notifyAll()
     * @since      JDK1.0
     */
    public final void wait() throws InterruptedException {
	wait(0);
    }

    /**
     * Called by the garbage collector on an object when garbage collection
     * determines that there are no more references to the object.
     * A subclass overrides the <code>finalize</code> method to dispose of
     * system resources or to perform other cleanup. 
     * <p>
     * Any exception thrown by the <code>finalize</code> method causes 
     * the finalization of this object to be halted, but is otherwise 
     * ignored. 
     * <p>
     * The <code>finalize</code> method in <code>Object</code> does 
     * nothing. 
     *
     * @exception  java.lang.Throwable  [Need description!]
     * @since      JDK1.0
     */
    protected void finalize() throws Throwable { }
}
