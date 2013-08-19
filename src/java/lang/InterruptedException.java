/*
 * @(#)InterruptedException.java	1.14 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;

/**
 * Thrown when a thread is waiting, sleeping, or otherwise paused for
 * a long time and another thread interrupts it using the
 * <code>interrupt</code>  method in class <code>Thread</code>. 
 *
 * @author  Frank Yellin
 * @version 1.14, 01/23/03
 * @see     java.lang.Object#wait()
 * @see     java.lang.Object#wait(long)
 * @see     java.lang.Object#wait(long, int)
 * @see     java.lang.Thread#sleep(long)
 * @see     java.lang.Thread#interrupt()
 * @see     java.lang.Thread#interrupted()
 * @since   JDK1.0
 */
public
class InterruptedException extends Exception {
    /**
     * Constructs an <code>InterruptedException</code> with no detail  message. 
     */
    public InterruptedException() {
	super();
    }

    /**
     * Constructs an <code>InterruptedException</code> with the 
     * specified detail message. 
     *
     * @param   s   the detail message.
     */
    public InterruptedException(String s) {
	super(s);
    }
}
