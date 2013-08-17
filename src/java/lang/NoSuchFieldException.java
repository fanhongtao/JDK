/*
 * @(#)NoSuchFieldException.java	1.6 98/07/01
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
 * Signals that the class doesn't have a field of a specified name.
 *
 * @author  unascribed
 * @version 1.6, 07/01/98
 * @since   JDK1.1
 */
public class NoSuchFieldException extends Exception {
    /**
     * Constructor.
     *
     * @since JDK1.1
     */
    public NoSuchFieldException() {
	super();
    }

    /**
     * Constructor with a detail message.
     *
     * @since JDK1.1
     */
    public NoSuchFieldException(String s) {
	super(s);
    }
}
