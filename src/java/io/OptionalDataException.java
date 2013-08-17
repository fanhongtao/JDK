/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.io;

/**
 * Unexpected data appeared in an ObjectInputStream trying to read
 * an Object.
 * Occurs when the stream contains primitive data
 * instead of the object that is expected by readObject.
 * The EOF flag in the exception is true indicating that no more
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
     *
     * @serial
     */
    public int length;

    /**
     * True if there is no more data in the buffered part of the stream.
     *
     * @serial
     */
    public boolean eof;
}
