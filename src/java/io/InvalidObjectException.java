/*
 * @(#)InvalidObjectException.java	1.8 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @version 1.8, 12/10/01
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
