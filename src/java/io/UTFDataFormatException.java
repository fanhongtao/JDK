/*
 * @(#)UTFDataFormatException.java	1.5 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;

/**
 * Signals that a malformed UTF-8 string has been read in a data 
 * input stream or by any class that implements the data input 
 * interface. See the <code>writeUTF</code> method for the format in 
 * which UTF-8 strings are read and written.
 *
 * @author  Frank Yellin
 * @version 1.5, 12/10/01
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
