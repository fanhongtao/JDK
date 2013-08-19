/*
 * @(#)RMISecurityException.java	1.14 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.rmi;

/**
 * An <code>RMISecurityException</code> signals that a security exception
 * has occurred during the execution of one of
 * <code>java.rmi.RMISecurityManager</code>'s methods.
 *
 * @version 1.14, 01/23/03
 * @author  Roger Riggs
 * @since   JDK1.1
 * @deprecated Use {@link java.lang.SecurityException} instead.
 * Application code should never directly reference this class, and
 * <code>RMISecurityManager</code> no longer throws this subclass of
 * <code>java.lang.SecurityException</code>.
 */
public class RMISecurityException extends java.lang.SecurityException {

    /* indicate compatibility with JDK 1.1.x version of class */
     private static final long serialVersionUID = -8433406075740433514L;

    /**
     * Construct an <code>RMISecurityException</code> with a detail message.
     * @param name the detail message
     * @since JDK1.1
     * @deprecated no replacement
     */
    public RMISecurityException(String name) {
	super(name);
    }

    /**
     * Construct an <code>RMISecurityException</code> with a detail message.
     * @param name the detail message
     * @param arg ignored
     * @since JDK1.1
     * @deprecated no replacement
     */
    public RMISecurityException(String name, String arg) {
	this(name);
    }
}
