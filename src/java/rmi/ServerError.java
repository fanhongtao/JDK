/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi;

/**
 * A <code>ServerError</code> is thrown as a result of a remote method call
 * if the execution of the remote method on the server machine throws a
 * <code>java.lang.Error</code>.  The <code>ServerError</code> contains
 * a nested exception which is the <code>java.lang.Error</code> that
 * occurred during remote method execution.
 * 
 * @version 1.10, 02/06/02
 * @author  Ann Wollrath
 * @since   JDK1.1
 */
public class ServerError extends RemoteException {

    /** indicate compatibility with JDK 1.1.x version of class */
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
