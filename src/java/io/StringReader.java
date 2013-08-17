/*
 * @(#)StringReader.java	1.6 98/07/01
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
 * A character stream whose source is a string.
 *
 * @version 	1.6, 98/07/01
 * @author	Mark Reinhold
 * @since	JDK1.1
 */

public class StringReader extends Reader {

    private String str;
    private int length;
    private int next = 0;
    private int mark = 0;

    /**
     * Create a new string reader.
     */
    public StringReader(String s) {
	this.str = s;
	this.length = s.length();
    }

    /** Check to make sure that the stream has not been closed */
    private void ensureOpen() throws IOException {
	if (str == null)
	    throw new IOException("Stream closed");
    }

    /**
     * Read a single character.
     *
     * @return     The character read, or -1 if the end of the stream has been
     *             reached
     *
     * @exception  IOException  If an I/O error occurs
     */
    public int read() throws IOException {
	synchronized (lock) {
	    ensureOpen();
	    if (next >= length)
		return -1;
	    return str.charAt(next++);
	}
    }

    /**
     * Read characters into a portion of an array.
     *
     * @param      cbuf  Destination buffer
     * @param      off   Offset at which to start writing characters
     * @param      len   Maximum number of characters to read
     *
     * @return     The number of characters read, or -1 if the end of the
     *             stream has been reached
     *
     * @exception  IOException  If an I/O error occurs
     */
    public int read(char cbuf[], int off, int len) throws IOException {
	synchronized (lock) {
	    ensureOpen();
	    if (next >= length)
		return -1;
	    int n = Math.min(length - next, len);
	    str.getChars(next, next + n, cbuf, off);
	    next += n;
	    return n;
	}
    }

    /**
     * Skip characters.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public long skip(long ns) throws IOException {
	synchronized (lock) {
	    ensureOpen();
	    if (next >= length)
		return 0;
	    long n = Math.min(length - next, ns);
	    next += n;
	    return n;
	}
    }

    /**
     * Tell whether this stream is ready to be read.  String readers are
     * always ready to be read.
     */
    public boolean ready() {
	return true;
    }

    /**
     * Tell whether this stream supports the mark() operation, which it does.
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
     *                         the stream's input comes from a string, there
     *                         is no actual limit, so this argument is ignored.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void mark(int readAheadLimit) throws IOException {
	synchronized (lock) {
	    ensureOpen();
	    mark = next;
	}
    }

    /**
     * Reset the stream to the most recent mark, or to the beginning of the
     * string if it has never been marked.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void reset() throws IOException {
	synchronized (lock) {
	    ensureOpen();
	    next = mark;
	}
    }

    /**
     * Close the stream.
     */
    public void close() {
	str = null;
    }

}
