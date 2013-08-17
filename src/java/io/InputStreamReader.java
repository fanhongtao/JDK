/*
 * @(#)InputStreamReader.java	1.13 98/07/01
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

import sun.io.ByteToCharConverter;
import sun.io.ConversionBufferFullException;


/**
 * An InputStreamReader is a bridge from byte streams to character streams: It
 * reads bytes and translates them into characters according to a specified
 * character encoding.  The encoding that it uses may be specified by name, or
 * the platform's default encoding may be accepted.
 *
 * <p> Each invocation of one of an InputStreamReader's read() methods may
 * cause one or more bytes to be read from the underlying byte-input stream.
 * For top efficiency, consider wrapping an InputStreamReader within a
 * BufferedReader; for example,
 *
 * <pre>
 * BufferedReader in
 *   = new BufferedReader(new InputStreamReader(System.in));
 * </pre>
 *
 * @see BufferedReader
 * @see InputStream
 *
 * @version 	1.13, 98/07/01
 * @author	Mark Reinhold
 * @since	JDK1.1
 */

public class InputStreamReader extends Reader {

    private ByteToCharConverter btc;
    private InputStream in;

    private static final int defaultByteBufferSize = 8192;
    private byte bb[];		/* Input buffer */

    /**
     * Create an InputStreamReader that uses the default character encoding.
     *
     * @param  in   An InputStream
     */
    public InputStreamReader(InputStream in) {
	this(in, ByteToCharConverter.getDefault());
    }

    /**
     * Create an InputStreamReader that uses the named character encoding.
     *
     * @param  in   An InputStream
     * @param  enc  Name of encoding to be used
     *
     * @exception  UnsupportedEncodingException
     *             If the named encoding is not supported
     */
    public InputStreamReader(InputStream in, String enc)
	throws UnsupportedEncodingException
    {
	this(in, ByteToCharConverter.getConverter(enc));
    }

    /**
     * Create an InputStreamReader that uses the specified byte-to-character
     * converter.  The converter is assumed to have been reset.
     *
     * @param  in   An InputStream
     * @param  btc  A ByteToCharConverter
     */
    private InputStreamReader(InputStream in, ByteToCharConverter btc) {
	super(in);
	this.in = in;
	this.btc = btc;
	bb = new byte[defaultByteBufferSize];
    }

    /**
     * Return the name of the encoding being used by this stream.  May return
     * null if the stream has been closed.
     */
    public String getEncoding() {
	synchronized (lock) {
	    if (btc != null)
		return btc.getCharacterEncoding();
	    else
		return null;
	}
    }


    /* Buffer handling */

    private int nBytes = 0;	/* -1 implies EOF has been reached */
    private int nextByte = 0;

    private void malfunction() {
	throw new InternalError("Converter malfunction (" +
				btc.getCharacterEncoding() +
				") -- please submit a bug report via " +
				System.getProperty("java.vendor.url.bug"));
    }

    private int convertInto(char cbuf[], int off, int end) throws IOException {
	int nc = 0;
	if (nextByte < nBytes) {
	    try {
		nc = btc.convert(bb, nextByte, nBytes,
				 cbuf, off, end);
		nextByte = nBytes;
		if (btc.nextByteIndex() != nextByte)
		    malfunction();
	    }
	    catch (ConversionBufferFullException x) {
		nextByte = btc.nextByteIndex();
		nc = btc.nextCharIndex() - off;
	    }
	}
	return nc;
    }

    private int flushInto(char cbuf[], int off, int end) throws IOException {
	int nc = 0;
	try {
	    nc = btc.flush(cbuf, off, end);
	}
	catch (ConversionBufferFullException x) {
	    nc = btc.nextCharIndex() - off;
	}
	return nc;
    }

    private int fill(char cbuf[], int off, int end) throws IOException {
	int nc = 0;

	if (nextByte < nBytes)
	    nc = convertInto(cbuf, off, end);

	while (off + nc < end) {

	    if (nBytes != -1) {
		if ((nc > 0) && !inReady())
		    break;	/* Block at most once */
		nBytes = in.read(bb);
	    }

	    if (nBytes == -1) {
		nc += flushInto(cbuf, off + nc, end);
		if (nc == 0)
		    return -1;
		else
		    break;
	    }
	    else {
		nextByte = 0;
		nc += convertInto(cbuf, off + nc, end);
	    }
	}

	return nc;
    }

    /**
     * Tell whether the underlying byte stream is ready to be read.  Return
     * false for those streams that do not support available(), such as the
     * Win32 console stream.
     */
    private boolean inReady() {
	try {
	    return in.available() > 0;
	} catch (IOException x) {
	    return false;
	}
    }


    /** Check to make sure that the stream has not been closed */
    private void ensureOpen() throws IOException {
	if (in == null)
	    throw new IOException("Stream closed");
    }

    /**
     * Read a single character.
     *
     * @return The character read, or -1 if the end of the stream has been
     *         reached
     *
     * @exception  IOException  If an I/O error occurs
     */
    public int read() throws IOException {
	char cb[] = new char[1];
	if (read(cb, 0, 1) == -1)
	    return -1;
	else
	    return cb[0];
    }

    /**
     * Read characters into a portion of an array.
     *
     * @param      cbuf  Destination buffer
     * @param      off   Offset at which to start storing characters
     * @param      len   Maximum number of characters to read
     *
     * @return     The number of characters read, or -1 if the end of the stream
     *             has been reached
     *
     * @exception  IOException  If an I/O error occurs
     */
    public int read(char cbuf[], int off, int len) throws IOException {
	synchronized (lock) {
	    ensureOpen();
	    return fill(cbuf, off, off + len);
	}
    }

    /**
     * Tell whether this stream is ready to be read.  An InputStreamReader is
     * ready if its input buffer is not empty, or if bytes are available to be
     * read from the underlying byte stream.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public boolean ready() throws IOException {
	synchronized (lock) {
	    ensureOpen();
	    return (nextByte < nBytes) || inReady();
	}
    }

    /**
     * Close the stream.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void close() throws IOException {
	synchronized (lock) {
	    if (in == null)
		return;
	    in.close();
	    in = null;
	    bb = null;
	    btc = null;
	}
    }

}
