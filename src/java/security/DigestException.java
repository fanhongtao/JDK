/*
 * @(#)DigestException.java	1.14 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security;

/**
 * This is the generic Message Digest exception. 
 * 
 * @version 1.14, 03/01/23
 * @author Benjamin Renaud 
 */
public class DigestException extends GeneralSecurityException {

    /** 
     * Constructs a DigestException with no detail message.  (A
     * detail message is a String that describes this particular
     * exception.)  
     */
    public DigestException() {
	super();
    }

    /** 
     * Constructs a DigestException with the specified detail
     * message.  (A detail message is a String that describes this
     * particular exception.)
     *
     * @param msg the detail message.  
     */
   public DigestException(String msg) {
       super(msg);
    }
}
