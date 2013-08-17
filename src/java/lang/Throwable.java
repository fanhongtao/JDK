/*
 * @(#)Throwable.java	1.33 98/07/01
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
 * The <code>Throwable</code> class is the superclass of all errors 
 * and exceptions in the Java language. Only objects that are 
 * instances of this class (or of one of its subclasses) are thrown 
 * by the Java Virtual Machine or can be thrown by the Java 
 * <code>throw</code> statement. Similarly, only this class or one of 
 * its subclasses can be the argument type in a <code>catch</code> 
 * clause. 
 * <p>
 * A <code>Throwable</code> class contains a snapshot of the 
 * execution stack of its thread at the time it was created. It can 
 * also contain a message string that gives more information about 
 * the error. 
 * <p>
 * Here is one example of catching an exception: 
 * <p><blockquote><pre>
 *     try {
 *         int a[] = new int[2];
 *         a[4];
 *     } catch (ArrayIndexOutOfBoundsException e) {
 *         System.out.println("exception: " + e.getMessage());
 *         e.printStackTrace();
 *     }
 * </pre></blockquote>
 *
 * @author  unascribed
 * @version 1.31, 01/26/97
 * @since   JDK1.0
 */
public class Throwable implements java.io.Serializable {
    /**
     * Native code saves some indication of the stack backtrace in this
     * slot.
     */
    private transient Object backtrace;	

    /**
     * Specific details about the Throwable.  For example,
     * for FileNotFoundThrowables, this contains the name of
     * the file that could not be found.
     */
    private String detailMessage;

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = -3042686055658047285L;

    /**
     * Constructs a new <code>Throwable</code> with no detail message. 
     * The stack trace is automatically filled in. 
     *
     * @since   JDK1.0
     */
    public Throwable() {
	fillInStackTrace();
    }

    /**
     * Constructs a new <code>Throwable</code> with the specified detail 
     * message. The stack trace is automatically filled in. 
     *
     * @param   message   the detail message.
     * @since   JDK1.0
     */
    public Throwable(String message) {
	fillInStackTrace();
	detailMessage = message;
    }

    /**
     * Returns the detail message of this throwable object.
     *
     * @return  the detail message of this <code>Throwable</code>,
     *          or <code>null</code> if this <code>Throwable</code> does not
     *          have a detail message.
     * @since   JDK1.0
     */
    public String getMessage() {
	return detailMessage;
    }

    /**
     * Creates a localized description of this <code>Throwable</code>.
     * Subclasses may override this method in order to produce a
     * locale-specific message.  For subclasses that do not override this
     * method, the default implementation returns the same result as
     * <code>getMessage()</code>.
     *
     * @since   JDK1.1
     */
    public String getLocalizedMessage() {
	return getMessage();
    }

    /**
     * Returns a short description of this throwable object.
     *
     * @return  a string representation of this <code>Throwable</code>.
     * @since   JDK1.0
     */
    public String toString() {
	String s = getClass().getName();
	String message = getMessage();
	return (message != null) ? (s + ": " + message) : s;
    }

    /**
     * Prints this <code>Throwable</code> and its backtrace to the 
     * standard error stream. 
     *
     * @see     java.lang.System#err
     * @since   JDK1.0
     */
    public void printStackTrace() { 
        System.err.println(this);
	printStackTrace0(System.err);
    }

    /**
     * Prints this <code>Throwable</code> and its backtrace to the 
     * specified print stream. 
     *
     * @since   JDK1.0
     */
    public void printStackTrace(java.io.PrintStream s) { 
        s.println(this);
	printStackTrace0(s);
    }

    /**
     * Prints this <code>Throwable</code> and its backtrace to the specified
     * print writer.
     *
     * @since   JDK1.1
     */
    public void printStackTrace(java.io.PrintWriter s) { 
        s.println(this);
	printStackTrace0(s);
    }

    /* The given object must have a void println(char[]) method */
    private native void printStackTrace0(Object s);

    /**
     * Fills in the execution stack trace. This method is useful when an 
     * application is re-throwing an error or exception. For example: 
     * <p><blockquote><pre>
     *     try {
     *         a = b / c;
     *     } catch(ArithmeticThrowable e) {
     *         a = Number.MAX_VALUE;
     *         throw e.fillInStackTrace();
     *     }
     * </pre></blockquote>
     *
     * @return  this <code>Throwable</code> object.
     * @see     java.lang.Throwable#printStackTrace()
     * @since   JDK1.0
     */
    public native Throwable fillInStackTrace();

}
