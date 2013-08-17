/*
 * @(#)InvalidObjectException.java	1.12 00/02/02
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.io;

/**
 * Indicates that one or more deserialized objects failed validation
 * tests.  The argument should provide the reason for the failure.
 *
 * @see ObjectInputValidation
 * @since JDK1.1
 *
 * @author  unascribed
 * @version 1.12, 02/02/00
 * @since   JDK1.1
 */
public class InvalidObjectException extends ObjectStreamException {
    /**
     * Constructs an <code>InvalidObjectException</code>.
     * @param reason Detailed message explaing the reason for the failure.
     *
     * @see ObjectInputValidation
     */
    public  InvalidObjectException(String reason) {
	super(reason);
    }
}
