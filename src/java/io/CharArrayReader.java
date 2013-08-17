/*
 * @(#)CharArrayReader.java	1.8 98/07/01
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
 * This class implements a character buffer that can be used as a
 * character-input stream.
 *
 * @author	Herb Jellinek
 * @version 	1.8, 07/01/98
 * @since       JDK1.1
 */
public
class CharArrayReader extends Reader {
    /** Character buffer */
    protected char buf[];

    /** Current buffer position */
    protected int pos;

    /** Position of mark in buffer */
    protected int markedPos = 0;

    /** Number of valid characters in buffer */
    protected int count;

    /**
     * Create an CharArrayReader from the specified array of chars.
     * @param buf	Input buffer (not copied)
     * @since JDK1.1
     */
    public CharArrayReader(char buf[]) {
	this.buf = buf;
        this.pos = 0;
	this.count = buf.length;
    }

    /**
     * Create an CharArrayReader from the specified array of chars.
     * @param buf	Input buffer (not copied)
     * @param offset    Offset of the first char to read
     * @param length	Number of chars to read
     * @since JDK1.1
     */
    public CharArrayReader(char buf[], int offset, int length) {
	this.buf = buf;
        this.pos = offset;
	this.count = Math.min(offset + length, buf.length);
    }

    /** Check to make sure that the stream has not been closed */
    private void ensureOpen() throws IOException {
	if (buf == null)
	    throw new IOException("Stream closed");
    }

    /**
     * Read a single character.
     * 
     * @exception   IOException  If an I/O error occurs
     * @since       JDK1.1
     */
    public int read() throws IOException {
	synchronized (lock) {
	    ensureOpen();
	    if (pos >= count)
		return -1;
	    else
		return buf[pos++];
	}
    }

    /**
     * Read characters into a portion of an array.
     * @param b	 Destination buffer
     * @param off  Offset at which to start storing characters
     * @param len   Maximum number of characters to read
     * @return  The actual number of characters read, or -1 if
     * 		the end of the stream has been reached
     * 
     * @exception   IOException  If an I/O error occurs
     * @since       JDK1.1
     */
    public int read(char b[], int off, int len) throws IOException {
	synchronized (lock) {
	    ensureOpen();
	    if (pos >= count) {
		return -1;
	    }
	    if (pos + len > count) {
		len = count - pos;
	    }
	    if (len <= 0) {
		return 0;
	    }
	    System.arraycopy(buf, pos, b, off, len);
	    pos += len;
	    return len;
	}
    }

    /**
     * Skip characters.
     * @param n The number of characters to skip
     * @return	The number of characters actually skipped
     * 
     * @exception   IOException  If an I/O error occurs
     * @since       JDK1.1
     */
    public long skip(long n) throws IOException {
	synchronized (lock) {
	    ensureOpen();
	    if (pos + n > count) {
		n = count - pos;
	    }
	    if (n < 0) {
		return 0;
	    }
	    pos += n;
	    return n;
	}
    }

    /**
     * Tell whether this stream is ready to be read.  Character-array readers
     * are always ready to be read.
     *
     * @exception  IOException  If an I/O error occurs
     * @since       JDK1.1
     */
    public boolean ready() throws IOException {
	synchronized (lock) {
	    ensureOpen();
	    return (count - pos) > 0;
	}
    }

    /**
     * Tell whether this stream supports the mark() operation, which it does.
     * @since       JDK1.1
     */
    public boolean markSupported() {
	return true;
    }

    /**
     * Mark the present position in the stream.  Subsequent calls to reset()
     * will reposition the stream to this point.
     *
     * @param  readAheadLimit  Limit on the number of characters that may be
     *                         read while still preserving the mark.  Because
     *                         the stream's input comes from a character array,
     *                         there is no actual limit; hence this argument is
     *                         ignored.
     *
     * @exception  IOException  If an I/O error occurs
     * @since       JDK1.1
     */
    public void mark(int readAheadLimit) throws IOException {
	synchronized (lock) {
	    ensureOpen();
	    markedPos = pos;
	}
    }

    /**
     * Reset the stream to the most recent mark, or to the beginning if it has
     * never been marked.
     *
     * @exception  IOException  If an I/O error occurs
     * @since       JDK1.1
     */
    public void reset() throws IOException {
	synchronized (lock) {
	    ensureOpen();
	    pos = markedPos;
	}
    }

    /**
     * Close the stream.
     * @since       JDK1.1
     */
    public void close() {
	buf = null;
    }
}
