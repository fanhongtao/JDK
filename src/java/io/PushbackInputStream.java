/*
 * @(#)PushbackInputStream.java	1.33 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;

/**
 * A <code>PushbackInputStream</code> adds
 * functionality to another input stream, namely
 * the  ability to "push back" or "unread"
 * one byte. This is useful in situations where
 * it is  convenient for a fragment of code
 * to read an indefinite number of data bytes
 * that  are delimited by a particular byte
 * value; after reading the terminating byte,
 * the  code fragment can "unread" it, so that
 * the next read operation on the input stream
 * will reread the byte that was pushed back.
 * For example, bytes representing the  characters
 * constituting an identifier might be terminated
 * by a byte representing an  operator character;
 * a method whose job is to read just an identifier
 * can read until it  sees the operator and
 * then push the operator back to be re-read.
 *
 * @author  David Connelly
 * @author  Jonathan Payne
 * @version 1.33, 01/23/03
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
     * Check to make sure that this stream has not been closed
     */
    private void ensureOpen() throws IOException {
	if (in == null)
	    throw new IOException("Stream closed");
    }

    /**
     * Creates a <code>PushbackInputStream</code>
     * with a pushback buffer of the specified <code>size</code>,
     * and saves its  argument, the input stream
     * <code>in</code>, for later use. Initially,
     * there is no pushed-back byte  (the field
     * <code>pushBack</code> is initialized to
     * <code>-1</code>).
     *
     * @param  in    the input stream from which bytes will be read.
     * @param  size  the size of the pushback buffer.
     * @exception IllegalArgumentException if size is <= 0
     * @since  JDK1.1
     */
    public PushbackInputStream(InputStream in, int size) {
	super(in);
        if (size <= 0) {
            throw new IllegalArgumentException("size <= 0");
        }
	this.buf = new byte[size];
	this.pos = size;
    }

    /**
     * Creates a <code>PushbackInputStream</code>
     * and saves its  argument, the input stream
     * <code>in</code>, for later use. Initially,
     * there is no pushed-back byte  (the field
     * <code>pushBack</code> is initialized to
     * <code>-1</code>).
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
        ensureOpen();
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
     * @see        java.io.InputStream#read(byte[], int, int)
     */
    public int read(byte[] b, int off, int len) throws IOException {
        ensureOpen();
        if ((off < 0) || (off > b.length) || (len < 0) ||
            ((off + len) > b.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        }

	if (len == 0) {
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
        ensureOpen();
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
        ensureOpen();
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
     * @see        java.io.InputStream#available()
     */
    public int available() throws IOException {
        ensureOpen();
	return (buf.length - pos) + super.available();
    }

    /**
     * Skips over and discards <code>n</code> bytes of data from this 
     * input stream. The <code>skip</code> method may, for a variety of 
     * reasons, end up skipping over some smaller number of bytes, 
     * possibly zero.  If <code>n</code> is negative, no bytes are skipped.
     * 
     * <p> The <code>skip</code> method of <code>PushbackInputStream</code>
     * first skips over the bytes in the pushback buffer, if any.  It then
     * calls the <code>skip</code> method of the underlying input stream if
     * more bytes need to be skipped.  The actual number of bytes skipped
     * is returned.
     *
     * @param      n   the number of bytes to be skipped.
     * @return     the actual number of bytes skipped.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterInputStream#in
     * @see        java.io.InputStream#skip(long n)
     * @since      1.2
     */
    public long skip(long n) throws IOException {
        ensureOpen();
	if (n <= 0) {
	    return 0;
	}

	long pskip = buf.length - pos;
	if (pskip > 0) {
	    if (n < pskip) {
		pskip = n;
	    }
	    pos += pskip;
	    n -= pskip;
	}
	if (n > 0) {
	    pskip += super.skip(n);
	}
	return pskip;
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

    /**
     * Closes this input stream and releases any system resources 
     * associated with the stream. 
     *
     * @exception  IOException  if an I/O error occurs.
     */
    public synchronized void close() throws IOException {
        if (in == null)
            return;
        in.close();
        in = null;
        buf = null;
    }

}
