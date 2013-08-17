/*
 * @(#)ActivateFailedException.java	1.7 00/02/02
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.rmi.activation;

/**
 * This exception is thrown by the RMI runtime when activation
 * fails during a remote call to an activatable object.
 *
 * @author 	Ann Wollrath
 * @version	1.7, 02/02/00
 * @since 	1.2
 */
public class ActivateFailedException extends java.rmi.RemoteException {

    /** indicate compatibility with the Java 2 SDK v1.2 version of class */
    private static final long serialVersionUID = 4863550261346652506L;

    /**
     * Constructs an <code>ActivateFailedException</code> with the specified
     * detail message.
     *
     * @param s the detail message
     * @since 1.2
     */
    public ActivateFailedException(String s) {
	super(s);
    }

    /**
     * Constructs an <code>ActivateFailedException</code> with the specified
     * detail message and nested exception.
     *
     * @param s the detail message
     * @param ex the nested exception
     * @since 1.2
     */
    public ActivateFailedException(String s, Exception ex) {
	super(s, ex);
    }
}
