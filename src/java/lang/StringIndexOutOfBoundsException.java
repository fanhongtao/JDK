/*
 * @(#)StringIndexOutOfBoundsException.java	1.14 98/07/01
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
 * Thrown by the <code>charAt</code> method in class 
 * <code>String</code> and by other <code>String</code> 
 * methods to indicate that an index is either negative or greater 
 * than or equal to the size of the string. 
 *
 * @author  unascribed
 * @version 1.14, 07/01/98
 * @see     java.lang.String#charAt(int)
 * @since   JDK1.0
 */
public
class StringIndexOutOfBoundsException extends IndexOutOfBoundsException {
    /**
     * Constructs a <code>StringIndexOutOfBoundsException</code> with no 
     * detail message. 
     *
     * @since   JDK1.0.
     */
    public StringIndexOutOfBoundsException() {
	super();
    }

    /**
     * Constructs a <code>StringIndexOutOfBoundsException</code> with 
     * the specified detail message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public StringIndexOutOfBoundsException(String s) {
	super(s);
    }

    /**
     * Constructs a new <code>StringIndexOutOfBoundsException</code> 
     * class with an argument indicating the illegal index. 
     *
     * @param   index   the illegal index.
     * @since   JDK1.0
     */
    public StringIndexOutOfBoundsException(int index) {
	super("String index out of range: " + index);
    }
}
