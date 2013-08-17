/*
 * @(#)NullPointerException.java	1.12 98/07/01
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
 * Thrown when an application attempts to use <code>null</code> in a 
 * case where an object is required. These include: 
 * <ul>
 * <li>Calling the instance method of a <code>null</code> object. 
 * <li>Accessing or modifying the field of a <code>null</code> object. 
 * <li>Taking the length of <code>null</code> as if it were an array. 
 * <li>Accessing or modifying the slots of <code>null</code> as if it 
 *     were an array. 
 * <li>Throwing <code>null</code> as if it were a <code>Throwable</code> 
 *     value. 
 * </ul>
 * <p>
 * Applications should throw instances of this class to indicate 
 * other illegal uses of the <code>null</code> object. 
 *
 * @author  unascribed
 * @version 1.12, 07/01/98
 * @since   JDK1.0
 */
public
class NullPointerException extends RuntimeException {
    /**
     * Constructs a <code>NullPointerException</code> with no detail message.
     *
     * @since   JDK1.0
     */
    public NullPointerException() {
	super();
    }

    /**
     * Constructs a <code>NullPointerException</code> with the specified 
     * detail message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public NullPointerException(String s) {
	super(s);
    }
}
