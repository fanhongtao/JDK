/*
 * @(#)RemoteException.java	1.7 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi;

public class RemoteException extends java.io.IOException {

    private static final long serialVersionUID = -5148567311918794206L;

    public Throwable detail;

    /**
     * Create a remote exception
     */
    public RemoteException() {}

    /**
     * Create a remote exception with the specified string
     */
    public RemoteException(String s) {
	super(s);
    }

    /**
     * Create a remote exception with the specified string, and the
     * exception specified.
     */
    public RemoteException(String s, Throwable ex) {
	super(s);
	detail = ex;
    }

    /**
     * Produce the message, include the message from the nested
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
}
