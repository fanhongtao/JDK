/*
 * @(#)GeneralSecurityException.java	1.13 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package java.security;

/**
 * The <code>GeneralSecurityException</code> class is a generic
 * security exception class that provides type safety for all the
 * security-related exception classes that extend from it.
 *
 * @version 1.13, 03/01/23
 * @author Jan Luehe
 */

public class GeneralSecurityException extends Exception {

    /** 
     * Constructs a GeneralSecurityException with no detail message.  
     */
    public GeneralSecurityException() {
        super();
    }

    /**
     * Constructs a GeneralSecurityException with the specified detail
     * message.
     * A detail message is a String that describes this particular
     * exception.
     *
     * @param msg the detail message.  
     */
    public GeneralSecurityException(String msg) {
        super(msg);
    }
}
