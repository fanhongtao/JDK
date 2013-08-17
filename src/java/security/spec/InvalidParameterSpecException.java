/*
 * @(#)InvalidParameterSpecException.java	1.11 00/02/02
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.security.spec;

import java.security.GeneralSecurityException;

/**
 * This is the exception for invalid parameter specifications.
 *
 * @author Jan Luehe
 *
 * @version 1.11, 02/02/00
 *
 * @see java.security.AlgorithmParameters
 * @see AlgorithmParameterSpec
 * @see DSAParameterSpec
 *
 * @since 1.2
 */

public class InvalidParameterSpecException extends GeneralSecurityException {

    /**
     * Constructs an InvalidParameterSpecException with no detail message. A
     * detail message is a String that describes this particular
     * exception.
     */
    public InvalidParameterSpecException() {
	super();
    }

    /**
     * Constructs an InvalidParameterSpecException with the specified detail
     * message. A detail message is a String that describes this
     * particular exception.  
     *
     * @param msg the detail message.  
     */
    public InvalidParameterSpecException(String msg) {
	super(msg);
    }
}
