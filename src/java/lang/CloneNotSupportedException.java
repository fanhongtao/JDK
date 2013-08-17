/*
 * @(#)CloneNotSupportedException.java	1.4 98/07/01
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

package java.lang;

/**
 * Thrown to indicate that the <code>clone</code> method in class 
 * <code>Object</code> has been called to clone an object, but that 
 * the object's class does not implement the <code>Cloneable</code> 
 * interface. 
 * <p>
 * Applications that override the <code>clone</code> method can also 
 * throw this exception to indicate that an object could not or 
 * should not be cloned.
 *
 * @author  unascribed
 * @version 1.4, 07/01/98
 * @see     java.lang.Cloneable
 * @see     java.lang.Object#clone()
 * @since   JDK1.0
 */

public
class CloneNotSupportedException extends Exception {
    /**
     * Constructs a <code>CloneNotSupportedException</code> with no 
     * detail message. 
     *
     * @since   JDK1.0
     */
    public CloneNotSupportedException() {
	super();
    }

    /**
     * Constructs a <code>CloneNotSupportedException</code> with the 
     * specified detail message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public CloneNotSupportedException(String s) {
	super(s);
    }
}
