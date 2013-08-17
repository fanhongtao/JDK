/*
 * @(#)FileNotFoundException.java	1.12 98/07/01
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
 * Signals that a file could not be found. 
 *
 * @author  unascribed
 * @version 1.12, 07/01/98
 * @since   JDK1.0
 */
public class FileNotFoundException extends IOException {
    /**
     * Constructs a <code>FileNotFoundException</code> with no detail message.
     *
     * @since   JDK1.0
     */
    public FileNotFoundException() {
	super();
    }

    /**
     * Constructs a <code>FileNotFoundException</code> with the 
     * specified detail message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public FileNotFoundException(String s) {
	super(s);
    }
}
