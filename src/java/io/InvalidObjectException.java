/*
 * @(#)InvalidObjectException.java	1.7 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.io;

/**
 * Indicates that one or more deserialized objects failed validation
 * tests.  The argument should be the reason for the failure.
 *
 * @see ObjectInputValidation
 * @since JDK1.1
 *
 * @author  unascribed
 * @version 1.7, 07/01/98
 * @since   JDK1.1
 */
public class InvalidObjectException extends ObjectStreamException {
    /**
     * Constructs an <code>InvalidObjectException</code> with a
     * detail message. The argument should be the reason why
     * the validation of the object failed.
     *
     * @see ObjectInputValidation
     * @since JDK1.1
     */
    public  InvalidObjectException(String reason) {
	super(reason);
    }
}
