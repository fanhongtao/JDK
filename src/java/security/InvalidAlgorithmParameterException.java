/*
 * @(#)InvalidAlgorithmParameterException.java	1.10 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security;

/**
 * This is the exception for invalid or inappropriate algorithm parameters.
 *
 * @author Jan Luehe
 *
 * @version 1.10, 12/03/01
 *
 * @see AlgorithmParameters
 * @see java.security.spec.AlgorithmParameterSpec
 *
 * @since 1.2
 */

public class InvalidAlgorithmParameterException
extends GeneralSecurityException {

    /**
     * Constructs an InvalidAlgorithmParameterException with no detail
     * message.
     * A detail message is a String that describes this particular
     * exception.
     */
    public InvalidAlgorithmParameterException() {
	super();
    }

    /**
     * Constructs an InvalidAlgorithmParameterException with the specified
     * detail message.
     * A detail message is a String that describes this
     * particular exception.  
     *
     * @param msg the detail message.  
     */
    public InvalidAlgorithmParameterException(String msg) {
	super(msg);
    }
}
