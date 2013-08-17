/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi;

/**
 * A <code>ServerException</code> is thrown as a result of a remote method call
 * if the execution of the remote method on the server machine throws a
 * <code>RemoteException</code>.
 * 
 * @version 1.10, 02/06/02
 * @author  Ann Wollrath
 * @since   JDK1.1
 */
public class ServerException extends RemoteException {

    /* indicate compatibility with JDK 1.1.x version of class */
    private static final long serialVersionUID = -4775845313121906682L;

    /**
     * Constructs a <code>ServerException</code> with the specified
     * detail message.
     *
     * @param s the detail message
     * @since JDK1.1
     */
    public ServerException(String s) {
	super(s);
    }

    /**
     * Constructs a <code>ServerException</code> with the specified
     * detail message and nested exception.
     *
     * @param s the detail message
     * @param ex the nested exception
     * @since JDK1.1
     */
    public ServerException(String s, Exception ex) {
	super(s, ex);
    }
}
