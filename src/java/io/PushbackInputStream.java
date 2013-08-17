/*
 * @(#)PushbackInputStream.java	1.17 98/07/01
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
 * This class is an input stream filter that provides a buffer into which data
 * can be "unread."  An application may unread data at any time by pushing it
 * back into the buffer, as long as the buffer has sufficient room.  Subsequent
 * reads will read all of the pushed-back data in the buffer before reading
 * from the underlying input stream.
 *
 * <p>
 * This functionality is useful when a fragment of code should read 
 * an indefinite number of data bytes that are delimited by 
 * particular byte values. After reading the terminating byte the
 * code fragment can push it back, so that the next read 
 * operation on the input stream will re-read that byte.
 *
 * @author  David Connelly
 * @author  Jonathan Payne
 * @version 1.17, 07/01/98
 * @since   JDK1.0
 */
public
class PushbackInputStream extends FilterInputStream {
    /**
     * The pushback buffer.
     * @since   JDK1.1
     */
    protected byte[] buf;

    /**
     * The position within the pushback buffer from which the next byte will
     * be read.  When the buffer is empty, <code>pos</code> is equal to
     * <code>buf.length</code>; when the buffer is full, <code>pos</code> is
     * equal to zero.
     *
     * @since   JDK1.1
     */
    protected int pos;

    /**
     * Creates a new pushback input stream with a pushback buffer
     * of the specified size.
     *
     * @param  in    the input stream from which bytes will be read.
     * @param  size  the size of the pushback buffer.
     * @since  JDK1.1
     */
    public PushbackInputStream(InputStream in, int size) {
	super(in);
	this.buf = new byte[size];
	this.pos = size;
    }

    /**
     * Creates a new pushback input stream with a one-byte pushback buffer.
     *
     * @param   in   the input stream from which bytes will be read.
     */
    public PushbackInputStream(InputStream in) {
	this(in, 1);
    }

    /**
     * Reads the next byte of data from this input stream. The value 
     * byte is returned as an <code>int</code> in the range 
     * <code>0</code> to <code>255</code>. If no byte is available 
     * because the end of the stream has been reached, the value 
     * <code>-1</code> is returned. This method blocks until input data 
     * is available, the end of the stream is detected, or an exception 
     * is thrown. 
     *
     * <p> This method returns the most recently pushed-back byte, if there is
     * one, and otherwise calls the <code>read</code> method of its underlying
     * input stream and returns whatever value that method returns.
     *
     * @return     the next byte of data, or <code>-1</code> if the end of the
     *             stream has been reached.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.InputStream#read()
     */
    public int read() throws IOException {
	if (pos < buf.length) {
	    return buf[pos++] & 0xff;
	}
	return super.read();
    }

    /**
     * Reads up to <code>len</code> bytes of data from this input stream into
     * an array of bytes.  This method first reads any pushed-back bytes; after
     * that, if fewer than than <code>len</code> bytes have been read then it
     * reads from the underlying input stream.  This method blocks until at
     * least 1 byte of input is available.
     *
     * @param      b     the buffer into which the data is read.
     * @param      off   the start offset of the data.
     * @param      len   the maximum number of bytes read.
     * @return     the total number of bytes read into the buffer, or
     *             <code>-1</code> if there is no more data because the end of
     *             the stream has been reached.
     * @exception  IOException  if an I/O error occurs.
     */
    public int read(byte[] b, int off, int len) throws IOException {
	if (len <= 0) {
	    return 0;
	}
	int avail = buf.length - pos;
	if (avail > 0) {
	    if (len < avail) {
		avail = len;
	    }
	    System.arraycopy(buf, pos, b, off, avail);
	    pos += avail;
	    off += avail;
	    len -= avail;
	}
	if (len > 0) {
	    len = super.read(b, off, len);
	    if (len == -1) {
		return avail == 0 ? -1 : avail;
	    }
	    return avail + len;
	}
	return avail;
    }

    /**
     * Pushes back a byte by copying it to the front of the pushback buffer.
     * After this method returns, the next byte to be read will have the value
     * <code>(byte)b</code>.
     *
     * @param      b   the <code>int</code> value whose low-order 
     * 			byte is to be pushed back.
     * @exception IOException If there is not enough room in the pushback
     *			      buffer for the byte.
     */
    public void unread(int b) throws IOException {
	if (pos == 0) {
	    throw new IOException("Push back buffer is full");
	}
	buf[--pos] = (byte)b;
    }

    /**
     * Pushes back a portion of an array of bytes by copying it to the front
     * of the pushback buffer.  After this method returns, the next byte to be
     * read will have the value <code>b[off]</code>, the byte after that will
     * have the value <code>b[off+1]</code>, and so forth.
     *
     * @param b the byte array to push back.
     * @param off the start offset of the data.
     * @param len the number of bytes to push back.
     * @exception IOException If there is not enough room in the pushback
     *			      buffer for the specified number of bytes.
     * @since     JDK1.1
     */
    public void unread(byte[] b, int off, int len) throws IOException {
	if (len > pos) {
	    throw new IOException("Push back buffer is full");
	}
	pos -= len;
	System.arraycopy(b, off, buf, pos, len);
    }

    /**
     * Pushes back an array of bytes by copying it to the front of the
     * pushback buffer.  After this method returns, the next byte to be read
     * will have the value <code>b[0]</code>, the byte after that will have the
     * value <code>b[1]</code>, and so forth.
     *
     * @param b the byte array to push back
     * @exception IOException If there is not enough room in the pushback
     *			      buffer for the specified number of bytes.
     * @since     JDK1.1
     */
    public void unread(byte[] b) throws IOException {
	unread(b, 0, b.length);
    }

    /**
     * Returns the number of bytes that can be read from this input stream
     * without blocking.  This method calls the <code>available</code> method
     * of the underlying input stream; it returns that value plus the number of
     * bytes that have been pushed back.
     *
     * @return     the number of bytes that can be read from the input stream
     *             without blocking.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterInputStream#in
     */
    public int available() throws IOException {
	return pos + super.available();
    }

    /**
     * Tests if this input stream supports the <code>mark</code> and
     * <code>reset</code> methods, which it does not.
     *
     * @return   <code>false</code>, since this class does not support the
     *           <code>mark</code> and <code>reset</code> methods.
     * @see     java.io.InputStream#mark(int)
     * @see     java.io.InputStream#reset()
     */
    public boolean markSupported() {
	return false;
    }

}
