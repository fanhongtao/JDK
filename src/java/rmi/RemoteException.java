/*
 * @(#)RemoteException.java	1.2 00/01/12
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.rmi;

/**
 * A <code>RemoteException</code> is the common superclass for a number of
 * communication-related exceptions that may occur during the execution of a
 * remote method call.  Each method of a remote interface, an interface that
 * extends <code>java.rmi.Remote</code>, must list
 * <code>RemoteException</code> in its throws clause.
 * 
 * @version 1.12, 07/12/98
 * @author  Ann Wollrath
 * @since   JDK1.1
 */
public class RemoteException extends java.io.IOException {

    /* indicate compatibility with JDK 1.1.x version of class */
    private static final long serialVersionUID = -5148567311918794206L;

    /**
     * Nested Exception to hold wrapped remote exception.
     *
     * @serial
     * @since   JDK1.1
     */
    public Throwable detail;

    /**
     * Constructs a <code>RemoteException</code> with no specified
     * detail message.
     * @since   JDK1.1
     */
    public RemoteException() {}

    /**
     * Constructs a <code>RemoteException</code> with the specified
     * detail message.
     *
     * @param s the detail message
     * @since   JDK1.1
     */
    public RemoteException(String s) {
	super(s);
    }

    /**
     * Constructs a <code>RemoteException</code> with the specified
     * detail message and nested exception.
     *
     * @param s the detail message
     * @param ex the nested exception
     * @since   JDK1.1
     */
    public RemoteException(String s, Throwable ex) {
	super(s);
	detail = ex;
    }

    /**
     * Returns the detail message, including the message from the nested
     * exception if there is one.
     * @since   JDK1.1
     */
    public String getMessage() {
	if (detail == null)
	    return super.getMessage();
	else
	    return super.getMessage() +
		"; nested exception is: \n\t" +
		detail.toString();
    }

    /**
     * Prints the composite message and the embedded stack trace to
     * the specified stream <code>ps</code>.
     * @param ps the print stream
     * @since JDK1.2
     */
    public void printStackTrace(java.io.PrintStream ps)
    {
	if (detail == null) {
	    super.printStackTrace(ps);
	} else {
	    synchronized(ps) {
		ps.println(this);
		detail.printStackTrace(ps);
	    }
	}
    }

    /**
     * Prints the composite message to <code>System.err</code>.
     * @since JDK1.2
     */
    public void printStackTrace()
    {
	printStackTrace(System.err);
    }

    /**
     * Prints the composite message and the embedded stack trace to
     * the specified print writer <code>pw</code>
     * @param pw the print writer
     * @since JDK1.2
     */
    public void printStackTrace(java.io.PrintWriter pw)
    {
	if (detail == null) {
	    super.printStackTrace(pw);
	} else {
	    synchronized(pw) {
		pw.println(this);
		detail.printStackTrace(pw);
	    }
	}
    }
}
