/*
 * @(#)InvalidAlgorithmParameterException.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.security;

/**
 * This is the exception for invalid or inappropriate algorithm parameters.
 *
 * @author Jan Luehe
 *
 * @version 1.5, 09/21/98
 *
 * @see AlgorithmParameters
 * @see java.security.spec.AlgorithmParameterSpec
 *
 * @since JDK1.2
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
