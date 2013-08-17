/*
 * @(#)IOException.java	1.14 98/07/01
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
 * Signals that an I/O exception of some sort has occurred. 
 *
 * @author  unascribed
 * @version 1.14, 07/01/98
 * @see     java.io.InputStream
 * @see     java.io.OutputStream
 * @since   JDK1.0
 */
public
class IOException extends Exception {
    /**
     * Constructs an <code>IOException</code> with no detail message. 
     *
     * @since   JDK1.0
     */
    public IOException() {
	super();
    }

    /**
     * Constructs an <code>IOException</code> with the specified detail 
     * message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public IOException(String s) {
	super(s);
    }
}
