/*
 * @(#)RMISecurityException.java	1.8 98/09/21
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
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

/**
 * An <code>RMISecurityException</code> signals that a security exception
 * has occurred during the execution of one of
 * <code>java.rmi.RMISecurityManager</code>'s methods.
 *
 * @version 1.8, 09/21/98
 * @author  Roger Riggs
 * @since   JDK1.1
 * @deprecated no replacement.  <code>RMISecurityManager</code> no longer
 * throws this subclass of <code>java.lang.SecurityException</code>.
 */
public class RMISecurityException extends java.lang.SecurityException {

    /* indicate compatibility with JDK 1.1.x version of class */
     private static final long serialVersionUID = -8433406075740433514L;

    /**
     * Construct an <code>RMISecurityException</code> with a detail message.
     * @param s the detail message
     * @since JDK1.1
     * @deprecated no replacement
     */
    public RMISecurityException(String name) {
	super(name);
    }

    /**
     * Construct an <code>RMISecurityException</code> with a detail message.
     * @param s the detail message
     * @param arg ignored
     * @since JDK1.1
     * @deprecated no replacement
     */
    public RMISecurityException(String name, String arg) {
	this(name);
    }
}
