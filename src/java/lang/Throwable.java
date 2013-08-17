/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * Instances of two subclasses, {@link java.lang.Error} and 
 * {@link java.lang.Exception}, are conventionally used to indicate 
 * that exceptional situations have occurred. Typically, these instances 
 * are freshly created in the context of the exceptional situation so 
 * as to include relevant information (such as stack trace data).
 * <p>
 * By convention, class <code>Throwable</code> and its subclasses have 
 * two constructors, one that takes no arguments and one that takes a 
 * <code>String</code> argument that can be used to produce an error 
 * message.
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
 * @version 1.45, 02/06/02
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
     *
     * @serial
     */
    private String detailMessage;

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = -3042686055658047285L;

    /**
     * Constructs a new <code>Throwable</code> with <code>null</code> as 
     * its error message string. Also, the method 
     * {@link #fillInStackTrace()} is called for this object. 
     */
    public Throwable() {
	fillInStackTrace();
    }

    /**
     * Constructs a new <code>Throwable</code> with the specified error 
     * message. Also, the method {@link #fillInStackTrace()} is called for 
     * this object.
     *
     * @param   message   the error message. The error message is saved for 
     *          later retrieval by the {@link #getMessage()} method.
     */
    public Throwable(String message) {
	fillInStackTrace();
	detailMessage = message;
    }

    /**
     * Returns the error message string of this throwable object.
     *
     * @return  the error message string of this <code>Throwable</code> 
     *          object if it was {@link #Throwable(String) created} with an 
     *          error message string; or <code>null</code> if it was 
     *          {@link #Throwable() created} with no error message. 
     *            
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
     * @return  The localized description of this <code>Throwable</code>.
     * @since   JDK1.1
     */
    public String getLocalizedMessage() {
	return getMessage();
    }

    /**
     * Returns a short description of this throwable object.
     * If this <code>Throwable</code> object was 
     * {@link #Throwable(String) created} with an error message string, 
     * then the result is the concatenation of three strings: 
     * <ul>
     * <li>The name of the actual class of this object 
     * <li>": " (a colon and a space) 
     * <li>The result of the {@link #getMessage} method for this object 
     * </ul>
     * If this <code>Throwable</code> object was {@link #Throwable() created} 
     * with no error message string, then the name of the actual class of 
     * this object is returned.
     *
     * @return  a string representation of this <code>Throwable</code>.
     */
    public String toString() {
	String s = getClass().getName();
	String message = getLocalizedMessage();
	return (message != null) ? (s + ": " + message) : s;
    }

    /**
     * Prints this <code>Throwable</code> and its backtrace to the 
     * standard error stream. This method prints a stack trace for this 
     * <code>Throwable</code> object on the error output stream that is 
     * the value of the field <code>System.err</code>. The first line of 
     * output contains the result of the {@link #toString()} method for 
     * this object. Remaining lines represent data previously recorded by 
     * the method {@link #fillInStackTrace()}. The format of this 
     * information depends on the implementation, but the following 
     * example may be regarded as typical: 
     * <blockquote><pre>
     * java.lang.NullPointerException
     *         at MyClass.mash(MyClass.java:9)
     *         at MyClass.crunch(MyClass.java:6)
     *         at MyClass.main(MyClass.java:3)
     * </pre></blockquote>
     * This example was produced by running the program: 
     * <blockquote><pre>
     * 
     * class MyClass {
     * 
     *     public static void main(String[] argv) {
     *         crunch(null);
     *     }
     *     static void crunch(int[] a) {
     *         mash(a);
     *     }
     * 
     *     static void mash(int[] b) {
     *         System.out.println(b[0]);
     *     }
     * }
     * </pre></blockquote>
     *
     * @see     java.lang.System#err
     */
    public void printStackTrace() { 
	synchronized (System.err) {
	    System.err.println(this);
	    printStackTrace0(System.err);
	}
    }

    /**
     * Prints this <code>Throwable</code> and its backtrace to the 
     * specified print stream.
     *
     * @param s <code>PrintStream</code> to use for output
     */
    public void printStackTrace(java.io.PrintStream s) { 
	synchronized (s) {
	    s.println(this);
	    printStackTrace0(s);
	}
    }

    /**
     * Prints this <code>Throwable</code> and its backtrace to the specified
     * print writer.
     *
     * @param s <code>PrintWriter</code> to use for output
     * @since   JDK1.1
     */
    public void printStackTrace(java.io.PrintWriter s) { 
	synchronized (s) {
	    s.println(this);
	    printStackTrace0(s);
	}
    }

    /* The given object must have a void println(char[]) method */
    private native void printStackTrace0(Object s);

    /**
     * Fills in the execution stack trace. This method records within this 
     * <code>Throwable</code> object information about the current state of 
     * the stack frames for the current thread. This method is useful when 
     * an application is re-throwing an error or exception. For example: 
     * <p><blockquote><pre>
     *     try {
     *         a = b / c;
     *     } catch(ArithmeticThrowable e) {
     *         a = Double.MAX_VALUE;
     *         throw e.fillInStackTrace();
     *     }
     * </pre></blockquote>
     *
     * @return  this <code>Throwable</code> object.
     * @see     java.lang.Throwable#printStackTrace()
     */
    public native Throwable fillInStackTrace();

}
