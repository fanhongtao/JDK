/*
 * @(#)RemoteException.java	1.5 98/07/01
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

package java.rmi;

public class RemoteException extends java.io.IOException {

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
