/*
 * @(#)ProviderException.java	1.12 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security;

/** A runtime exception for Provider exceptions (such as
 * misconfiguration errors), which may be subclassed by Providers to
 * throw specialized, provider-specific runtime errors.
 *
 * @version 1.12, 03/01/23
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
