/*
 * @(#)RMISecurityException.java	1.5 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.rmi;

/**
 * Signals that a security exception has occurred.
 */
public class RMISecurityException extends java.lang.SecurityException {

    private static final long serialVersionUID = -8433406075740433514L;

    /**
     * Constructs a RMISecurityException with no detail message.
     * A detail message is a String that describes this particular exception.
     */
    public RMISecurityException(String name) {
	super(System.getProperty("security." + name, "security." + name));
	System.out.println("*** Security Exception: " + name + " ***");
	printStackTrace();
    }

    /**
     * Constructs a RMISecurityException with the specified detail message.
     * A detail message is a String that describes this particular exception.
     * @param s the detail message
     */
    public RMISecurityException(String name, String arg) {
	super(System.getProperty("security." + name, "security." + name) +
	      ": " + arg);
	System.out.println("*** Security Exception: " + name +
			   ":" + arg + " ***");
	printStackTrace();
    }
}
