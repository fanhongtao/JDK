/*
 * @(#)InvalidKeySpecException.java	1.7 98/09/21
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.security.spec;

import java.security.GeneralSecurityException;

/**
 * This is the exception for invalid key specifications.
 *
 * @author Jan Luehe
 *
 * @version 1.7, 09/21/98
 *
 * @see KeySpec
 *
 * @since JDK1.2
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
