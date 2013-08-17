/*
 * @(#)InputStream.java	1.22 98/07/01
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
 * This abstract class is the superclass of all classes representing 
 * an input stream of bytes. 
 * <p>
 * Applications that need to define a subclass of 
 * <code>InputStream</code> must always provide a method that returns 
 * the next byte of input.
 * 
 * @author  Arthur van Hoff
 * @version 1.22, 07/01/98
 * @see     java.io.BufferedInputStream
 * @see     java.io.ByteArrayInputStream
 * @see     java.io.DataInputStream
 * @see     java.io.FilterInputStream
 * @see     java.io.InputStream#read()
 * @see     java.io.OutputStream
 * @see     java.io.PushbackInputStream
 * @since   JDK1.0
 */
public abstract class InputStream {
    /**
     * Reads the next byte of data from this input stream. The value 
     * byte is returned as an <code>int</code> in the range 
     * <code>0</code> to <code>255</code>. If no byte is available 
     * because the end of the stream has been reached, the value 
     * <code>-1</code> is returned. This method blocks until input data 
     * is available, the end of the stream is detected, or an exception 
     * is thrown. 
     * <p>
     * A subclass must provide an implementation of this method. 
     *
     * @return     the next byte of data, or <code>-1</code> if the end of the
     *             stream is reached.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public abstract int read() throws IOException;

    /**
     * Reads up to <code>b.length</code> bytes of data from this input 
     * stream into an array of bytes. 
     * <p>
     * The <code>read</code> method of <code>InputStream</code> calls 
     * the <code>read</code> method of three arguments with the arguments 
     * <code>b</code>, <code>0</code>, and <code>b.length</code>. 
     *
     * @param      b   the buffer into which the data is read.
     * @return     the total number of bytes read into the buffer, or
     *             <code>-1</code> is there is no more data because the end of
     *             the stream has been reached.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.InputStream#read(byte[], int, int)
     * @since      JDK1.0
     */
    public int read(byte b[]) throws IOException {
	return read(b, 0, b.length);
    }

    /**
     * Reads up to <code>len</code> bytes of data from this input stream 
     * into an array of bytes. This method blocks until some input is 
     * available. If the argument <code>b</code> is <code>null</code>, a  
     * <code>NullPointerException</code> is thrown.
     * <p>
     * The <code>read</code> method of <code>InputStream</code> reads a 
     * single byte at a time using the read method of zero arguments to 
     * fill in the array. Subclasses are encouraged to provide a more 
     * efficient implementation of this method. 
     *
     * @param      b     the buffer into which the data is read.
     * @param      off   the start offset of the data.
     * @param      len   the maximum number of bytes read.
     * @return     the total number of bytes read into the buffer, or
     *             <code>-1</code> if there is no more data because the end of
     *             the stream has been reached.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.InputStream#read()
     * @since      JDK1.0
     */
    public int read(byte b[], int off, int len) throws IOException {
	if (len <= 0) {
	    return 0;
	}

	int c = read();
	if (c == -1) {
	    return -1;
	}
	b[off] = (byte)c;

	int i = 1;
	try {
	    for (; i < len ; i++) {
		c = read();
		if (c == -1) {
		    break;
		}
		if (b != null) {
		    b[off + i] = (byte)c;
		}
	    }
	} catch (IOException ee) {
	}
	return i;
    }

    /**
     * Skips over and discards <code>n</code> bytes of data from this 
     * input stream. The <code>skip</code> method may, for a variety of 
     * reasons, end up skipping over some smaller number of bytes, 
     * possibly <code>0</code>. The actual number of bytes skipped is 
     * returned. 
     * <p>
     * The <code>skip</code> method of <code>InputStream</code> creates 
     * a byte array of length <code>n</code> and then reads into it until 
     * <code>n</code> bytes have been read or the end of the stream has 
     * been reached. Subclasses are encouraged to provide a more 
     * efficient implementation of this method. 
     *
     * @param      n   the number of bytes to be skipped.
     * @return     the actual number of bytes skipped.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public long skip(long n) throws IOException {
	/* ensure that the number is a positive int */
	byte data[] = new byte[(int) (n & 0xEFFFFFFF)];
	return read(data);
    }

    /**
     * Returns the number of bytes that can be read from this input 
     * stream without blocking. The available method of 
     * <code>InputStream</code> returns <code>0</code>. This method 
     * <B>should</B> be overridden by subclasses. 
     *
     * @return     the number of bytes that can be read from this input stream
     *             without blocking.
     * @exception  IOException  if an I/O error occurs.
     * @since	   JDK1.0
     */
    public int available() throws IOException {
	return 0;
    }

    /**
     * Closes this input stream and releases any system resources 
     * associated with the stream. 
     * <p>
     * The <code>close</code> method of <code>InputStream</code> does nothing.
     *
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public void close() throws IOException {}

    /**
     * Marks the current position in this input stream. A subsequent 
     * call to the <code>reset</code> method repositions this stream at 
     * the last marked position so that subsequent reads re-read the same 
     * bytes. 
     * <p>
     * The <code>readlimit</code> arguments tells this input stream to 
     * allow that many bytes to be read before the mark position gets 
     * invalidated. 
     * <p>
     * The <code>mark</code> method of <code>InputStream</code> does nothing.
     *
     * @param   readlimit   the maximum limit of bytes that can be read before
     *                      the mark position becomes invalid.
     * @see     java.io.InputStream#reset()
     * @since   JDK1.0
     */
    public synchronized void mark(int readlimit) {}

    /**
     * Repositions this stream to the position at the time the 
     * <code>mark</code> method was last called on this input stream. 
     * <p>
     * The <code>reset</code> method of <code>InputStream</code> throws 
     * an <code>IOException</code>, because input streams, by default, do 
     * not support <code>mark</code> and <code>reset</code>.
     * <p>
     * Stream marks are intended to be used in
     * situations where you need to read ahead a little to see what's in
     * the stream. Often this is most easily done by invoking some
     * general parser. If the stream is of the type handled by the
     * parser, it just chugs along happily. If the stream is not of
     * that type, the parser should toss an exception when it fails,
     * which, if it happens within readlimit bytes, allows the outer
     * code to reset the stream and try another parser.
     *
     * @exception  IOException  if this stream has not been marked or if the
     *               mark has been invalidated.
     * @see     java.io.InputStream#mark(int)
     * @see     java.io.IOException
     * @since   JDK1.0
     */
    public synchronized void reset() throws IOException {
	throw new IOException("mark/reset not supported");
    }

    /**
     * Tests if this input stream supports the <code>mark</code> 
     * and <code>reset</code> methods. The <code>markSupported</code> 
     * method of <code>InputStream</code> returns <code>false</code>. 
     *
     * @return  <code>true</code> if this true type supports the mark and reset
     *          method; <code>false</code> otherwise.
     * @see     java.io.InputStream#mark(int)
     * @see     java.io.InputStream#reset()
     * @since   JDK1.0
     */
    public boolean markSupported() {
	return false;
    }
}
