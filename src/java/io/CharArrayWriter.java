/*
 * @(#)CharArrayWriter.java	1.7 98/07/01
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
 * This class implements a character buffer that can be used as an Writer.
 * The buffer automatically grows when data is written to the stream.  The data
 * can be retrieved using toCharArray() and toString().
 *
 * @author	Herb Jellinek
 * @version 	1.7, 07/01/98
 * @since       JDK1.1
 */
public
class CharArrayWriter extends Writer {
    /** 
     * The buffer where data is stored.
     */
    protected char buf[];

    /**
     * The number of chars in the buffer.
     */
    protected int count;

    /**
     * Creates a new CharArrayWriter.
     */
    public CharArrayWriter() {
	this(32);
    }

    /**
     * Creates a new CharArrayWriter with the specified initial size.
     * @since   JDK1.1
     */
    public CharArrayWriter(int initialSize) {
	buf = new char[initialSize];
    }

    /**
     * Writes a character to the buffer.
     * @since   JDK1.1
     */
    public void write(int c) {
	synchronized (lock) {
	    int newcount = count + 1;
	    if (newcount > buf.length) {
		char newbuf[] = new char[Math.max(buf.length << 1, newcount)];
		System.arraycopy(buf, 0, newbuf, 0, count);
		buf = newbuf;
	    }
	    buf[count] = (char)c;
	    count = newcount;
	}
    }

    /**
     * Writes characters to the buffer.
     * @param c	the data to be written
     * @param off	the start offset in the data
     * @param len	the number of chars that are written
     * @since   JDK1.1
     */
    public void write(char c[], int off, int len) {
	synchronized (lock) {
	    int newcount = count + len;
	    if (newcount > buf.length) {
		char newbuf[] = new char[Math.max(buf.length << 1, newcount)];
		System.arraycopy(buf, 0, newbuf, 0, count);
		buf = newbuf;
	    }
	    System.arraycopy(c, off, buf, count, len);
	    count = newcount;
	}
    }

    /**
     * Write a portion of a string to the buffer.
     * @param  str  String to be written from
     * @param  off  Offset from which to start reading characters
     * @param  len  Number of characters to be written
     * @since   JDK1.1
     */
    public void write(String str, int off, int len) {
	synchronized (lock) {
	    int newcount = count + len;
	    if (newcount > buf.length) {
		char newbuf[] = new char[Math.max(buf.length << 1, newcount)];
		System.arraycopy(buf, 0, newbuf, 0, count);
		buf = newbuf;
	    }
	    str.getChars(off, off + len, buf, count);
	    count = newcount;
	}
    }

    /**
     * Writes the contents of the buffer to another character stream.
     * @param out	the output stream to write to
     * @since   JDK1.1
     */
    public void writeTo(Writer out) throws IOException {
	synchronized (lock) {
	    out.write(buf, 0, count);
	}
    }

    /**
     * Resets the buffer so that you can use it again without
     * throwing away the already allocated buffer.
     * @since   JDK1.1
     */
    public void reset() {
	count = 0;
    }

    /**
     * Returns a copy of the input data.
     * @since   JDK1.1
     */
    public char toCharArray()[] {
	synchronized (lock) {
	    char newbuf[] = new char[count];
	    System.arraycopy(buf, 0, newbuf, 0, count);
	    return newbuf;
	}
    }

    /**
     * Returns the current size of the buffer.
     * @since   JDK1.1
     */
    public int size() {
	return count;
    }

    /**
     * Converts input data to a string.
     * @return the string.
     * @since   JDK1.1
     */
    public String toString() {
	synchronized (lock) {
	    return new String(toCharArray());
	}
    }

    /**
     * Flush the stream.
     * @since   JDK1.1
     */
    public void flush() { }

    /**
     * Close the stream.  This method does not release the buffer, since its
     * contents might still be required.
     * @since   JDK1.1
     */
    public void close() { }
}
