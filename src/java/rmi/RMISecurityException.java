/*
 * @(#)RMISecurityException.java	1.4 98/08/12
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
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
