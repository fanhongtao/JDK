/*
 * @(#)ServerError.java	1.12 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi;

/**
 * A <code>ServerError</code> is thrown as a result of a remote method
 * invocation when an <code>Error</code> is thrown while processing
 * the invocation on the server, either while unmarshalling the arguments,
 * executing the remote method itself, or marshalling the return value.
 *
 * A <code>ServerError</code> instance contains the original
 * <code>Error</code> that occurred as its cause.
 * 
 * @version 1.12, 01/23/03
 * @author  Ann Wollrath
 * @since   JDK1.1
 */
public class ServerError extends RemoteException {

    /* indicate compatibility with JDK 1.1.x version of class */
    private static final long serialVersionUID = 8455284893909696482L;
    
    /**
     * Constructs a <code>ServerError</code> with the specified
     * detail message and nested error.
     *
     * @param s the detail message
     * @param err the nested error
     * @since JDK1.1
     */
    public ServerError(String s, Error err) {
	super(s, err);
    }
}
