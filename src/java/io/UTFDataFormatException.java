/*
 * @(#)UTFDataFormatException.java	1.4 98/07/01
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
 * Signals that a malformed UTF-8 string has been read in a data 
 * input stream or by any class that implements the data input 
 * interface. See the <code>writeUTF</code> method for the format in 
 * which UTF-8 strings are read and written.
 *
 * @author  Frank Yellin
 * @version 1.4, 07/01/98
 * @see     java.io.DataInput
 * @see     java.io.DataInputStream#readUTF(java.io.DataInput)
 * @see     java.io.IOException
 * @since   JDK1.0
 */
public
class UTFDataFormatException extends IOException {
    /**
     * Constructs a <code>UTFDataFormatException</code> with no detail 
     * message. 
     *
     * @since   JDK1.0
     */
    public UTFDataFormatException() {
	super();
    }

    /**
     * Constructs a <code>UTFDataFormatException</code> with the 
     * specified detail message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public UTFDataFormatException(String s) {
	super(s);
    }
}
