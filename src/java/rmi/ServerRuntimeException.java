/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi;

/**
 * A <code>ServerRuntimeException</code> is thrown as a result of a remote
 * method call if the execution of the remote method on the server machine
 * throws a <code>java.lang.RuntimeException</code>.
 * A <code>ServerRuntimeException</code> is not thrown from servers executing
 * in the Java 2 SDK v1.2 or later versions.
 * 
 * @version 1.13, 02/06/02
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
