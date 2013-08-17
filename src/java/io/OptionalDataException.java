/*
 * %W% %E%
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
 * Unexpected data appeared in an ObjectInputStream trying to read
 * an Object.
 * This exception occurs when the stream contains primitive data
 * instead of the object expected by readObject.
 * The eof flag in the exception is true to indicate that no more
 * primitive data is available.
 * The count field contains the number of bytes available to read.
 *
 * @author  unascribed
 * @version %I%, %G%
 * @since   JDK1.1
 */
public class OptionalDataException extends ObjectStreamException {
    /*
     * Create an <code>OptionalDataException</code> with a length.
     */
    OptionalDataException(int len) {
	eof = false;
	length = len;
    }

    /*
     * Create an <code>OptionalDataException</code> signifing no
     * more primitive data is available.
     */	
    OptionalDataException(boolean end) {
	length = 0;
	eof = end;
    }
    
    /**
     * The number of bytes of primitive data available to be read
     * in the current buffer.
     * @since   JDK1.1
     */
    public int length;

    /**
     * True if there is no more data in the buffered part of the stream.
     * @since   JDK1.1
     */
    public boolean eof;
}
