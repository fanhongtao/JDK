/*
 * @(#)RuntimeException.java	1.5 98/07/01
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
 * <code>RuntimeException</code> is the superclass of those 
 * exceptions that can be thrown during the normal operation of the 
 * Java Virtual Machine. 
 * <p>
 * A method is not required to declare in its <code>throws</code> 
 * clause any subclasses of <code>RuntimeException</code> that might 
 * be thrown during the execution of the method but not caught. 
 *
 *
 * @author  Frank Yellin
 * @version 1.5, 07/01/98
 * @since   JDK1.0
 */
public
class RuntimeException extends Exception {
    /**
     * Constructs a <code>RuntimeException</code> with no detail  message.
     *
     * @since   JDK1.0
     */
    public RuntimeException() {
	super();
    }

    /**
     * Constructs a <code>RuntimeException</code> with the specified 
     * detail message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public RuntimeException(String s) {
	super(s);
    }
}
