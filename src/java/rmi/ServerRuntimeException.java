/*
 * @(#)ServerRuntimeException.java	1.2 00/01/12
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.rmi;

/**
 * A <code>ServerRuntimeException</code> is thrown as a result of a remote
 * method call if the execution of the remote method on the server machine
 * throws a <code>java.lang.RuntimeException</code>.
 * A <code>ServerRuntimeException</code> is not thrown from servers executing
 * in JDK1.2 or later versions.
 * 
 * @version 1.8, 09/21/98
 * @author  Ann Wollrath
 * @since   JDK1.1
 * @deprecated no replacement
 */
public class ServerRuntimeException extends RemoteException {

    /* indicate compatibility with JDK 1.1.x version of class */
    private static final long serialVersionUID = 7054464920481467219L;

    /**
     * Constructs a <code>ServerRuntimeException</code> with the specified
     * detail message and nested exception.
     *
     * @param s the detail message
     * @param ex the nested exception
     * @deprecated no replacement
     * @since JDK1.1
     */
    public ServerRuntimeException(String s, Exception ex) {
	super(s, ex);
    }
}
