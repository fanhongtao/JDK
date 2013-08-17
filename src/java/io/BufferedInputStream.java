/*
 * @(#)BufferedInputStream.java	1.27 98/07/01
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
 * The class implements a buffered input stream. By setting up such 
 * an input stream, an application can read bytes from a stream 
 * without necessarily causing a call to the underlying system for 
 * each byte read. The data is read by blocks into a buffer; 
 * subsequent reads can access the data directly from the buffer. 
 *
 * @author  Arthur van Hoff
 * @version 1.27, 07/01/98
 * @since   JDK1.0
 */
public
class BufferedInputStream extends FilterInputStream {
    /**
     * The buffer where data is stored. 
     *
     * @since   JDK1.0
     */
    protected byte buf[];

    /**
     * The index one greater than the index of the last valid byte in 
     * the buffer. 
     *
     * @since   JDK1.0
     */
    protected int count;

    /**
     * The current position in the buffer. This is the index of the next 
     * character to be read from the <code>buf</code> array. 
     *
     * @see     java.io.BufferedInputStream#buf
     * @since   JDK1.0
     */
    protected int pos;
    
    /**
     * The value of the <code>pos</code> field at the time the last 
     * <code>mark</code> method was called. The value of this field is 
     * <code>-1</code> if there is no current mark. 
     *
     * @see     java.io.BufferedInputStream#mark(int)
     * @see     java.io.BufferedInputStream#pos
     * @since   JDK1.0
     */
    protected int markpos = -1;

    /**
     * The maximum read ahead allowed after a call to the 
     * <code>mark</code> method before subsequent calls to the 
     * <code>reset</code> method fail. 
     *
     * @see     java.io.BufferedInputStream#mark(int)
     * @see     java.io.BufferedInputStream#reset()
     * @since   JDK1.0
     */
    protected int marklimit;

    /**
     * Creates a new buffered input stream to read data from the 
     * specified input stream with a default 512-byte buffer size. 
     *
     * @param   in   the underlying input stream.
     * @since   JDK1.0
     */
    public BufferedInputStream(InputStream in) {
	this(in, 2048);
    }

    /**
     * Creates a new buffered input stream to read data from the 
     * specified input stream with the specified buffer size. 
     *
     * @param   in     the underlying input stream.
     * @param   size   the buffer size.
     * @since   JDK1.0
     */
    public BufferedInputStream(InputStream in, int size) {
	super(in);
	buf = new byte[size];
    }

    /**
     * Fills the buffer with more data, taking into account
     * shuffling and other tricks for dealing with marks.
     * Assumes that it is being called by a synchronized method.
     * This method also assumes that all data has already been read in,
     * hence pos > count.
     */
    private void fill() throws IOException {
	if (markpos < 0)
	    pos = 0;		/* no mark: throw away the buffer */
	else if (pos >= buf.length)	/* no room left in buffer */
	    if (markpos > 0) {	/* can throw away early part of the buffer */
		int sz = pos - markpos;
		System.arraycopy(buf, markpos, buf, 0, sz);
		pos = sz;
		markpos = 0;
	    } else if (buf.length >= marklimit) {
		markpos = -1;	/* buffer got too big, invalidate mark */
		pos = 0;	/* drop buffer contents */
	    } else {		/* grow buffer */
		int nsz = pos * 2;
		if (nsz > marklimit)
		    nsz = marklimit;
		byte nbuf[] = new byte[nsz];
		System.arraycopy(buf, 0, nbuf, 0, pos);
		buf = nbuf;
	    }
	int n = in.read(buf, pos, buf.length - pos);
	count = n <= 0 ? pos : n + pos;
    }

    /**
     * Reads the next byte of data from this buffered input stream. The 
     * value byte is returned as an <code>int</code> in the range 
     * <code>0</code> to <code>255</code>. If no byte is available 
     * because the end of the stream has been reached, the value 
     * <code>-1</code> is returned. This method blocks until input data 
     * is available, the end of the stream is detected, or an exception 
     * is thrown. 
     * <p>
     * The <code>read</code> method of <code>BufferedInputStream</code> 
     * returns the next byte of data from its buffer if the buffer is not 
     * empty. Otherwise, it refills the buffer from the underlying input 
     * stream and returns the next character, if the underlying stream 
     * has not returned an end-of-stream indicator. 
     *
     * @return     the next byte of data, or <code>-1</code> if the end of the
     *             stream is reached.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterInputStream#in
     * @since      JDK1.0
     */
    public synchronized int read() throws IOException {
	if (pos >= count) {
	    fill();
	    if (pos >= count)
		return -1;
	}
	return buf[pos++] & 0xff;
    }

    /**
     * Reads bytes into a portion of an array.  This method will block until
     * some input is available, an I/O error occurs, or the end of the stream
     * is reached.
     *
     * <p> If this stream's buffer is not empty, bytes are copied from it into
     * the array argument.  Otherwise, the buffer is refilled from the
     * underlying input stream and, unless the stream returns an end-of-stream
     * indication, the array argument is filled with characters from the
     * newly-filled buffer.
     *
     * <p> As an optimization, if the buffer is empty, the mark is not valid,
     * and <code>len</code> is at least as large as the buffer, then this
     * method will read directly from the underlying stream into the given
     * array.  Thus redundant <code>BufferedInputStream</code>s will not copy
     * data unnecessarily.
     *
     * @param      b     destination buffer.
     * @param      off   offset at which to start storing bytes.
     * @param      len   maximum number of bytes to read.
     * @return     the number of bytes read, or <code>-1</code> if the end of
     *             the stream has been reached.
     * @exception  IOException  if an I/O error occurs.
     */
    public synchronized int read(byte b[], int off, int len) throws IOException {
	int avail = count - pos;
	if (avail <= 0) {
	    /* If the requested length is larger than the buffer, and if there
	       is no mark/reset activity, do not bother to copy the bytes into
	       the local buffer.  In this way buffered streams will cascade
	       harmlessly. */
	    if (len >= buf.length && markpos < 0) {
		return in.read(b, off, len);
	    }
	    fill();
	    avail = count - pos;
	    if (avail <= 0)
		return -1;
	}
	int cnt = (avail < len) ? avail : len;
	System.arraycopy(buf, pos, b, off, cnt);
	pos += cnt;
	return cnt;
    }

    /**
     * Skips over and discards <code>n</code> bytes of data from the 
     * input stream. The <code>skip</code> method may, for a variety of 
     * reasons, end up skipping over some smaller number of bytes, 
     * possibly zero. The actual number of bytes skipped is returned. 
     * <p>
     * The <code>skip</code> method of <code>BufferedInputStream</code> 
     * compares the number of bytes it has available in its buffer, 
     * <i>k</i>, where <i>k</i>&nbsp;= <code>count&nbsp;- pos</code>, 
     * with <code>n</code>. If <code>n</code>&nbsp;&le;&nbsp;<i>k</i>, 
     * then the <code>pos</code> field is incremented by <code>n</code>. 
     * Otherwise, the <code>pos</code> field is incremented to have the 
     * value <code>count</code>, and the remaining bytes are skipped by 
     * calling the <code>skip</code> method on the underlying input 
     * stream, supplying the argument <code>n&nbsp;-</code>&nbsp;<i>k</i>. 
     *
     * @param      n   the number of bytes to be skipped.
     * @return     the actual number of bytes skipped.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public synchronized long skip(long n) throws IOException {
	if (n < 0) {
	    return 0;
	}
	long avail = count - pos;

	if (avail >= n) {
	    pos += n;
	    return n;
	}

	pos += avail;
	return avail + in.skip(n - avail);
    }

    /**
     * Returns the number of bytes that can be read from this input 
     * stream without blocking. 
     * <p>
     * The <code>available</code> method of 
     * <code>BufferedInputStream</code> returns the sum of the the number 
     * of bytes remaining to be read in the buffer 
     * (<code>count&nbsp;- pos</code>) 
     * and the result of calling the <code>available</code> method of the 
     * underlying input stream. 
     *
     * @return     the number of bytes that can be read from this input
     *             stream without blocking.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterInputStream#in
     * @since      JDK1.0
     */
    public synchronized int available() throws IOException {
	return (count - pos) + in.available();
    }

    /**
     * Marks the current position in this input stream. A subsequent 
     * call to the <code>reset</code> method repositions the stream at 
     * the last marked position so that subsequent reads re-read the same 
     * bytes. 
     * <p>
     * The <code>readlimit</code> argument tells the input stream to 
     * allow that many bytes to be read before the mark position gets 
     * invalidated. 
     *
     * @param   readlimit   the maximum limit of bytes that can be read before
     *                      the mark position becomes invalid.
     * @see     java.io.BufferedInputStream#reset()
     * @since   JDK1.0
     */
    public synchronized void mark(int readlimit) {
	marklimit = readlimit;
	markpos = pos;
    }

    /**
     * Repositions this stream to the position at the time the 
     * <code>mark</code> method was last called on this input stream. 
     * <p>
     * If the stream has not been marked, or if the mark has been invalidated,
     * an IOException is thrown. Stream marks are intended to be used in
     * situations where you need to read ahead a little to see what's in
     * the stream. Often this is most easily done by invoking some
     * general parser. If the stream is of the type handled by the
     * parser, it just chugs along happily. If the stream is not of
     * that type, the parser should toss an exception when it fails. If an
     * exception gets tossed within readlimit bytes, the parser will allow the
     * outer code to reset the stream and to try another parser.
     *
     * @exception  IOException  if this stream has not been marked or
     *               if the mark has been invalidated.
     * @see        java.io.BufferedInputStream#mark(int)
     * @since      JDK1.0
     */
    public synchronized void reset() throws IOException {
	if (markpos < 0)
	    throw new IOException("Resetting to invalid mark");
	pos = markpos;
    }

    /**
     * Tests if this input stream supports the <code>mark</code> 
     * and <code>reset</code> methods. The <code>markSupported</code> 
     * method of <code>BufferedInputStream</code> returns 
     * <code>true</code>. 
     *
     * @return  a <code>boolean</code> indicating if this stream type supports
     *          the <code>mark</code> and <code>reset</code> methods.
     * @see     java.io.InputStream#mark(int)
     * @see     java.io.InputStream#reset()
     * @since   JDK1.0

     */
    public boolean markSupported() {
	return true;
    }
}
