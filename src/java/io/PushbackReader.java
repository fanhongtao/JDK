/*
 * @(#)PushbackReader.java	1.8 98/07/01
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
 * A character-stream reader that allows characters to be pushed back into the
 * stream.
 *
 * @version 	1.8, 98/07/01
 * @author	Mark Reinhold
 * @since	JDK1.1
 */

public class PushbackReader extends FilterReader {

    /** Pushback buffer */
    private char[] buf;

    /** Current position in buffer */
    private int pos;

    /**
     * Create a new pushback reader with a pushback buffer of the given size.
     *
     * @param   in   The reader from which characters will be read
     * @param	size The size of the pushback buffer
     */
    public PushbackReader(Reader in, int size) {
	super(in);
	this.buf = new char[size];
	this.pos = size;
    }

    /**
     * Create a new pushback reader with a one-character pushback buffer.
     *
     * @param   in  The reader from which characters will be read
     */
    public PushbackReader(Reader in) {
	this(in, 1);
    }

    /** Check to make sure that the stream has not been closed. */
    private void ensureOpen() throws IOException {
	if (buf == null)
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
	    if (pos < buf.length)
		return buf[pos++];
	    else
		return super.read();
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

	    if (len <= 0)
		return 0;
	    int avail = buf.length - pos;
	    if (avail > 0) {
		if (len < avail)
		    avail = len;
		System.arraycopy(buf, pos, cbuf, off, avail);
		pos += avail;
		off += avail;
		len -= avail;
	    }
	    if (len > 0) {
		len = super.read(cbuf, off, len);
		if (len == -1) {
		    return (avail == 0) ? -1 : avail;
		}
		return avail + len;
	    }
	    return avail;
	}
    }

    /**
     * Push back a single character.
     *
     * @param  c  The character to push back
     *
     * @exception  IOException  If the pushback buffer is full,
     *                          or if some other I/O error occurs
     */
    public void unread(int c) throws IOException {
	synchronized (lock) {
	    ensureOpen();
	    if (pos == 0)
		throw new IOException("Pushback buffer overflow");
	    buf[--pos] = (char) c;
	}
    }

    /**
     * Push back a portion of an array of characters by copying it to the
     * front of the pushback buffer.  After this method returns, the next
     * character to be read will have the value <code>cbuf[off]</code>, the
     * character after that will have the value <code>cbuf[off+1]</code>, and
     * so forth.
     *
     * @param  cbuf  Character array
     * @param  off   Offset of first character to push back
     * @param  len   Number of characters to push back
     *
     * @exception  IOException  If there is insufficient room in the pushback
     *                          buffer, or if some other I/O error occurs
     */
    public void unread(char cbuf[], int off, int len) throws IOException {
	synchronized (lock) {
	    ensureOpen();
	    if (len > pos)
		throw new IOException("Pushback buffer overflow");
	    pos -= len;
	    System.arraycopy(cbuf, off, buf, pos, len);
	}
    }

    /**
     * Push back an array of characters by copying it to the front of the
     * pushback buffer.  After this method returns, the next character to be
     * read will have the value <code>cbuf[0]</code>, the character after that
     * will have the value <code>cbuf[1]</code>, and so forth.
     *
     * @param  cbuf  Character array to push back
     *
     * @exception  IOException  If there is insufficient room in the pushback
     *                          buffer, or if some other I/O error occurs
     */
    public void unread(char cbuf[]) throws IOException {
	unread(cbuf, 0, cbuf.length);
    }

    /**
     * Tell whether this stream is ready to be read.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public boolean ready() throws IOException {
	synchronized (lock) {
	    ensureOpen();
	    return (pos < buf.length) || super.ready();
	}
    }

    /**
     * Tell whether this stream supports the mark() operation, which it does
     * not.
     */
    public boolean markSupported() {
	return false;
    }

    /**
     * Close the stream.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void close() throws IOException {
	super.close();
	buf = null;
    }

}
