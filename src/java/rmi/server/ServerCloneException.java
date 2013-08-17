/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi.server;

/**
 * A <code>ServerCloneException</code> is thrown if a remote exception occurs
 * during the cloning of a <code>UnicastRemoteObject</code>.
 *
 * @version 1.16, 02/06/02
 * @author  Ann Wollrath
 * @since   JDK1.1
 * @see     java.rmi.server.UnicastRemoteObject#clone()
 */
public class ServerCloneException extends CloneNotSupportedException {

    /**
     * Nested exception for ServerCloneException.
     *
     * @serial
     * @since JDK1.1
     */
    public Exception detail;

    /* indicate compatibility with JDK 1.1.x version of class */
    private static final long serialVersionUID = 6617456357664815945L;

    /**
     * Constructs an <code>ServerCloneException</code> with the specified
     * detail message.
     *
     * @param s the detail message.
     * @since JDK1.1
     */
    public ServerCloneException(String s) {
	super(s);
    }

    /**
     * Constructs an <code>ServerCloneException</code> with the specified
     * detail message and nested exception.
     *
     * @param s the detail message.
     * @param ex the nested exception
     * @since JDK1.1
     */
    public ServerCloneException(String s, Exception ex) {
	super(s);
	detail = ex;
    }

    /**
     * Obtains the message, include the message from the nested
     * exception if there is one.
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
     * @since 1.2
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
     * Returns the detail message, including the message from the nested
     * exception if there is one.
     * @since   JDK1.1
     */
    public void printStackTrace()
    {
	printStackTrace(System.err);
    }

    /**
     * Prints the composite message and the embedded stack trace to
     * the specified writer <code>pw</code>.
     * @param pw the print writer
     * @since 1.2
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
