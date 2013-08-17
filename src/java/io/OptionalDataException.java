/*
 * %W% %E%
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
