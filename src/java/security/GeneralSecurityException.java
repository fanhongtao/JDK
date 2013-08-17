/*
 * @(#)GeneralSecurityException.java	1.11 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package java.security;

/**
 * This is the general security exception class, which serves to group all
 * the exception classes of the <code>java.security</code> package that
 * extend from it.
 * <P>
 * The exceptions to this grouping are:
 * <UL>
 *  <LI>AccessControlException and RMISecurityException, which subclass java.lang.SecurityException
 *  <LI>ProviderException, which subclasses java.lang.RuntimeException
 *  <LI>InvalidParameterException, which subclasses java.lang.IllegalArgumentException
 * </UL> 
 * @version 1.11, 01/12/03
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
