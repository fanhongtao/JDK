/*
 * @(#)SocketInputStream.java	1.16 98/07/01
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

package java.net;

import java.io.IOException;
import java.io.FileInputStream;

/**
 * This stream extends FileInputStream to implement a
 * SocketInputStream. Note that this class should <b>NOT</b> be
 * public.
 *
 * @version     1.16, 07/01/98
 * @author	Jonathan Payne
 * @author	Arthur van Hoff
 */
class SocketInputStream extends FileInputStream
{
    private boolean eof;
    private SocketImpl impl;
    private byte temp[] = new byte[1];

    /**
     * Creates a new SocketInputStream. Can only be called
     * by a Socket. This method needs to hang on to the owner Socket so
     * that the fd will not be closed.
     * @param impl the implemented socket input stream
     */
    SocketInputStream(SocketImpl impl) throws IOException {
	super(impl.getFileDescriptor());
	this.impl = impl;
    }

    /** 
     * Reads into an array of bytes at the specified offset using
     * the received socket primitive. 
     * @param b the buffer into which the data is read
     * @param off the start offset of the data
     * @param len the maximum number of bytes read
     * @return the actual number of bytes read, -1 is
     *          returned when the end of the stream is reached. 
     * @exception IOException If an I/O error has occurred.
     */
    private native int socketRead(byte b[], int off, int len)
	throws IOException;

    /** 
     * Reads into a byte array data from the socket. 
     * @param b the buffer into which the data is read
     * @return the actual number of bytes read, -1 is
     *          returned when the end of the stream is reached. 
     * @exception IOException If an I/O error has occurred. 
     */
    public int read(byte b[]) throws IOException {
	return read(b, 0, b.length);
    }

    /** 
     * Reads into a byte array <i>b</i> at offset <i>off</i>, 
     * <i>length</i> bytes of data.
     * @param b the buffer into which the data is read
     * @param off the start offset of the data
     * @param len the maximum number of bytes read
     * @return the actual number of bytes read, -1 is
     *          returned when the end of the stream is reached. 
     * @exception IOException If an I/O error has occurred.
     */
    public int read(byte b[], int off, int length) throws IOException {
	if (eof) {
	    return -1;
	}
	int n = socketRead(b, off, length);
	if (n <= 0) {
	    eof = true;
	    return -1;
	}
	return n;
    }

    /** 
     * Reads a single byte from the socket. 
     */
    public int read() throws IOException {
	if (eof) {
	    return -1;
	}

 	int n = read(temp, 0, 1);
	if (n <= 0) {
	    return -1;
	}
	return temp[0] & 0xff;
    }

    /** 
     * Skips n bytes of input.
     * @param n the number of bytes to skip
     * @return	the actual number of bytes skipped.
     * @exception IOException If an I/O error has occurred.
     */
    public long skip(long numbytes) throws IOException {
	if (numbytes <= 0) {
	    return 0;
	}
	long n = numbytes;
	int buflen = (int) Math.min(1024, n);
	byte data[] = new byte[buflen];
	while (n > 0) {
	    int r = read(data, 0, (int) Math.min((long) buflen, n));
	    if (r < 0) {
		break;
	    }
	    n -= r;
	}
	return numbytes - n;
    }

    /**
     * Returns the number of bytes that can be read without blocking.
     * @return the number of immediately available bytes
     */
    public int available() throws IOException {
	return impl.available();
    }

    /**
     * Closes the stream.
     */
    public void close() throws IOException {
	impl.close();
    }

    /** 
     * Overrides finalize, the fd is closed by the Socket.
     */
    protected void finalize() {}
}

