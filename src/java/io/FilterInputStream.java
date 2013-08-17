/*
 * @(#)FilterInputStream.java	1.16 98/07/01
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
 * This class is the superclass of all classes that filter input 
 * streams. These streams sit on top of an already existing input 
 * stream (the <i>underlying</i> input stream), but provide 
 * additional functionality. 
 * <p>
 * The class <code>FilterInputStream</code> itself simply overrides 
 * all methods of <code>InputStream</code> with versions that pass 
 * all requests to the underlying input stream. Subclasses of 
 * <code>FilterInputStream</code> may further override some of these 
 * methods as well as provide additional methods and fields. 
 *
 * @author  Jonathan Payne
 * @version 1.16, 07/01/98
 * @since   JDK1.0
 */
public
class FilterInputStream extends InputStream {
    /**
     * The underlying input stream. 
     *
     * @since   JDK1.0
     */
    protected InputStream in;

    /**
     * Creates an input stream filter built on top of the specified 
     * input stream. 
     *
     * @param   in   the underlying input stream.
     * @since   JDK1.0
     */
    protected FilterInputStream(InputStream in) {
	this.in = in;
    }

    /**
     * Reads the next byte of data from this input stream. The value 
     * byte is returned as an <code>int</code> in the range 
     * <code>0</code> to <code>255</code>. If no byte is available 
     * because the end of the stream has been reached, the value 
     * <code>-1</code> is returned. This method blocks until input data 
     * is available, the end of the stream is detected, or an exception 
     * is thrown. 
     * <p>
     * The <code>read</code> method of <code>FilterInputStream</code> 
     * calls the <code>read</code> method of its underlying input stream 
     * and returns whatever value that method returns. 
     *
     * @return     the next byte of data, or <code>-1</code> if the end of the
     *             stream is reached.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterInputStream#in
     * @since      JDK1.0
     */
    public int read() throws IOException {
	return in.read();
    }

    /**
     * Reads up to <code>byte.length</code> bytes of data from this 
     * input stream into an array of bytes. This method blocks until some 
     * input is available. 
     * <p>
     * The <code>read</code> method of <code>FilterInputStream</code> 
     * calls the <code>read</code> method of three arguments with the 
     * arguments <code>b</code>, <code>0</code>, and 
     * <code>b.length</code>, and returns whatever value that method returns.
     * <p>
     * Note that this method does not call the one-argument 
     * <code>read</code> method of its underlying stream with the single 
     * argument <code>b</code>. Subclasses of 
     * <code>FilterInputStream</code> do not need to override this method 
     * if they have overridden the three-argument <code>read</code> method.
     *
     * @param      b   the buffer into which the data is read.
     * @return     the total number of bytes read into the buffer, or
     *             <code>-1</code> if there is no more data because the end of
     *             the stream has been reached.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterInputStream#read(byte[], int, int)
     * @since      JDK1.0
     */
    public int read(byte b[]) throws IOException {
	return read(b, 0, b.length);
    }

    /**
     * Reads up to <code>len</code> bytes of data from this input stream 
     * into an array of bytes. This method blocks until some input is 
     * available. 
     * <p>
     * The <code>read</code> method of <code>FilterInputStream</code> 
     * calls the <code>read</code> method of its underlying input stream 
     * with the same arguments and returns whatever value that method returns.
     *
     * @param      b     the buffer into which the data is read.
     * @param      off   the start offset of the data.
     * @param      len   the maximum number of bytes read.
     * @return     the total number of bytes read into the buffer, or
     *             <code>-1</code> if there is no more data because the end of
     *             the stream has been reached.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterInputStream#in
     * @since      JDK1.0
     */
    public int read(byte b[], int off, int len) throws IOException {
	return in.read(b, off, len);
    }

    /**
     * Skips over and discards <code>n</code> bytes of data from the 
     * input stream. The <code>skip</code> method may, for a variety of 
     * reasons, end up skipping over some smaller number of bytes, 
     * possibly <code>0</code>. The actual number of bytes skipped is 
     * returned. 
     * <p>
     * The <code>skip </code>method of <code>FilterInputStream</code> 
     * calls the <code>skip</code> method of its underlying input stream 
     * with the same argument, and returns whatever value that method does.
     *
     * @param      n   the number of bytes to be skipped.
     * @return     the actual number of bytes skipped.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public long skip(long n) throws IOException {
	return in.skip(n);
    }

    /**
     * Returns the number of bytes that can be read from this input 
     * stream without blocking. 
     * <p>
     * The <code>available</code> method of 
     * <code>FilterInputStream</code> calls the <code>available</code> 
     * method of its underlying input stream and returns whatever value 
     * that method returns. 
     *
     * @return     the number of bytes that can be read from the input stream
     *             without blocking.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterInputStream#in
     * @since      JDK1.0
     */
    public int available() throws IOException {
	return in.available();
    }

    /**
     * Closes this input stream and releases any system resources 
     * associated with the stream. The <code>close</code> method of 
     * <code>FilterInputStream</code> calls the <code>close</code> method 
     * of its underlying input stream. 
     *
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterInputStream#in
     * @since      JDK1.0
     */
    public void close() throws IOException {
	in.close();
    }

    /**
     * Marks the current position in this input stream. A subsequent 
     * call to the <code>reset</code> method repositions this stream at 
     * the last marked position so that subsequent reads re-read the same bytes.
     * <p>
     * The <code>readlimit</code> argument tells this input stream to 
     * allow that many bytes to be read before the mark position gets 
     * invalidated. 
     * <p>
     * The <code>mark</code> method of <code>FilterInputStream</code> 
     * calls the <code>mark</code> method of its underlying input stream 
     * with the <code>readlimit</code> argument. 
     *
     * @param   readlimit   the maximum limit of bytes that can be read before
     *                      the mark position becomes invalid.
     * @see     java.io.FilterInputStream#in
     * @see     java.io.FilterInputStream#reset()
     * @since   JDK1.0
     */
    public synchronized void mark(int readlimit) {
	in.mark(readlimit);
    }

    /**
     * Repositions this stream to the position at the time the 
     * <code>mark</code> method was last called on this input stream. 
     * <p>
     * The <code>reset</code> method of <code>FilterInputStream</code> 
     * calls the <code>reset</code> method of its underlying input stream.
     * <p>
     * Stream marks are intended to be used in
     * situations where you need to read ahead a little to see what's in
     * the stream. Often this is most easily done by invoking some
     * general parser. If the stream is of the type handled by the
     * parse, it just chugs along happily. If the stream is not of
     * that type, the parser should toss an exception when it fails.
     * If this happens within readlimit bytes, it allows the outer
     * code to reset the stream and try another parser.
     *
     * @exception  IOException  if the stream has not been marked or if the
     *               mark has been invalidated.
     * @see        java.io.FilterInputStream#in
     * @see        java.io.FilterInputStream#mark(int)
     * @since      JDK1.0
     */
    public synchronized void reset() throws IOException {
	in.reset();
    }

    /**
     * Tests if this input stream supports the <code>mark</code> 
     * and <code>reset</code> methods. The <code>markSupported</code> 
     * method of <code>FilterInputStream</code> calls the 
     * <code>markSupported</code> method of its underlying input stream 
     * and returns whatever value that method returns. 
     *
     * @return  <code>true</code> if this stream type supports the
     *          <code>mark</code> and <code>reset</code> method;
     *          <code>false</code> otherwise.
     * @see     java.io.FilterInputStream#in
     * @see     java.io.InputStream#mark(int)
     * @see     java.io.InputStream#reset()
     * @since   JDK1.0
     */
    public boolean markSupported() {
	return in.markSupported();
    }
}
