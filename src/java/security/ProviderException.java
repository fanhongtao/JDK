/*
 * @(#)ProviderException.java	1.2 00/01/12
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.security;

/** A runtime exception for Provider exceptions (such as
 * misconfiguration errors), which may be subclassed by Providers to
 * throw specialized, provider-specific runtime errors.
 *
 * @version 1.8, 98/12/03
 * @author Benjamin Renaud
 */

public class ProviderException extends RuntimeException {

    /**
     * Constructs a ProviderException with no detail message. A
     * detail message is a String that describes this particular
     * exception.
     */
    public ProviderException() {
	super();
    }

    /**
     * Constructs a ProviderException with the specified detail
     * message. A detail message is a String that describes this
     * particular exception.  
     *
     * @param s the detail message.  
     */
    public ProviderException(String s) {
	super(s);
    }
}
