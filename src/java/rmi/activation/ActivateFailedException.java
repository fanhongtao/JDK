/*
 * @(#)ActivateFailedException.java	1.3 98/07/08
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.rmi.activation;

/**
 * This exception is thrown by the RMI runtime when activation
 * fails during a remote call to an activatable object.
 *
 * @author 	Ann Wollrath
 * @version	1.3, 07/08/98
 * @since 	JDK1.2
 */
public class ActivateFailedException extends java.rmi.RemoteException {

    /** indicate compatibility with JDK 1.2 version of class */
    private static final long serialVersionUID = 4863550261346652506L;

    /**
     * Constructs an <code>ActivateFailedException</code> with the specified
     * detail message.
     *
     * @param s the detail message
     * @since JDK1.2
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
     * @since JDK1.2
     */
    public ActivateFailedException(String s, Exception ex) {
	super(s, ex);
    }
}
