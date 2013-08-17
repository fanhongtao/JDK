/*
 * @(#)RMISecurityException.java	1.2 96/11/18
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 */
package java.rmi;

/**
 * Signals that a security exception has occurred.
 */
public class RMISecurityException extends java.lang.SecurityException {
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
