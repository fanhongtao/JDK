/*
 * @(#)InvalidKeySpecException.java	1.11 00/02/02
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
 * This is the exception for invalid key specifications.
 *
 * @author Jan Luehe
 *
 * @version 1.11, 02/02/00
 *
 * @see KeySpec
 *
 * @since 1.2
 */

public class InvalidKeySpecException extends GeneralSecurityException {

    /**
     * Constructs an InvalidKeySpecException with no detail message. A
     * detail message is a String that describes this particular
     * exception.
     */
    public InvalidKeySpecException() {
	super();
    }

    /**
     * Constructs an InvalidKeySpecException with the specified detail
     * message. A detail message is a String that describes this
     * particular exception.  
     *
     * @param msg the detail message.  
     */
    public InvalidKeySpecException(String msg) {
	super(msg);
    }
}
