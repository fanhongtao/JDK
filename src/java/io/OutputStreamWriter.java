/*
 * @(#)OutputStreamWriter.java	1.12 98/12/14
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

import sun.io.CharToByteConverter;
import sun.io.ConversionBufferFullException;


/**
 * Write characters to an output stream, translating characters into bytes
 * according to a specified character encoding.  Each OutputStreamWriter
 * incorporates its own CharToByteConverter, and is thus a bridge from
 * character streams to byte streams.
 *
 * <p> The encoding used by an OutputStreamWriter may be specified by name, by
 * providing a CharToByteConverter, or by accepting the default encoding, which
 * is defined by the system property <tt>file.encoding</tt>.
 *
 * <p> Each invocation of a write() method causes the encoding converter to be
 * invoked on the given character(s).  The resulting bytes are accumulated in a
 * buffer before being written to the underlying output stream.  The size of
 * this buffer may be specified, but by default it is large enough for most
 * purposes.  Note that the characters passed to the write() methods are not
 * buffered.  For top efficiency, consider wrapping an OutputStreamWriter
 * within a BufferedWriter so as to avoid frequent converter invocations.  For
 * example,
 *
 * <pre>
 * Writer out
 *   = new BufferedWriter(new OutputStreamWriter(System.out));
 * </pre>
 *
 * @see BufferedWriter
 * @see OutputStream
 *
 * @version 	1.10, 97/01/27
 * @author	Mark Reinhold
 * @since	JDK1.1
 */

public class OutputStreamWriter extends Writer {

    private CharToByteConverter ctb;
    private OutputStream out;

    private static final int defaultByteBufferSize = 8192;
    private byte bb[];
    private int nextByte = 0;
    private int nBytes = 0;

    /**
     * Create an OutputStreamWriter that uses the named character encoding.
     *
     * @param  out  An OutputStream
     * @param  enc  Name of the encoding to be used
     *
     * @exception  UnsupportedEncodingException
     *             If the named encoding is not supported
     */
    public OutputStreamWriter(OutputStream out, String enc)
	throws UnsupportedEncodingException
    {
	this(out, CharToByteConverter.getConverter(enc));
    }

    /**
     * Create an OutputStreamWriter that uses the default character encoding.
     *
     * @param  out  An OutputStream
     */
    public OutputStreamWriter(OutputStream out) {
	this(out, CharToByteConverter.getDefault());
    }

    /**
     * Create an OutputStreamWriter that uses the specified character-to-byte
     * converter.  The converter is assumed to have been reset.
     *
     * @param  out  An OutputStream
     * @param  ctb  A CharToByteConverter
     */
    private OutputStreamWriter(OutputStream out, CharToByteConverter ctb) {
	super(out);
	this.out = out;
	this.ctb = ctb;
	bb = new byte[defaultByteBufferSize];
	nBytes = defaultByteBufferSize;
    }

    /**
     * Return the name of the encoding being used by this stream.  May return
     * null if the stream has been closed.
     */
    public String getEncoding() {
	synchronized (lock) {
	    if (ctb != null)
		return ctb.getCharacterEncoding();
	    else
		return null;
	}
    }

    /** Check to make sure that the stream has not been closed */
    private void ensureOpen() throws IOException {
	if (out == null)
	    throw new IOException("Stream closed");
    }

    /**
     * Write a single character.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void write(int c) throws IOException {
	char cbuf[] = new char[1];
	cbuf[0] = (char) c;
	write(cbuf, 0, 1);
    }

    /**
     * Write a portion of an array of characters.
     *
     * @param  cbuf  Buffer of characters
     * @param  off   Offset from which to start writing characters
     * @param  len   Number of characters to write
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void write(char cbuf[], int off, int len) throws IOException {
	synchronized (lock) {
	    ensureOpen();

	    int ci = off, end = off + len;
            boolean bufferFlushed = false;
	    while (ci < end) {
		boolean bufferFull = false;

		try {
		    nextByte += ctb.convert(cbuf, ci, end,
					    bb, nextByte, nBytes);
		    ci = end;
		}
		catch (ConversionBufferFullException x) {
		    int nci = ctb.nextCharIndex();
		    if (nci == ci && bufferFlushed) {
                        /* Buffer has been flushed and still doesn't even
                           hold one character */
			throw new CharConversionException("Output buffer too small");
		    }
		    ci = nci;
		    bufferFull = true;
		    nextByte = ctb.nextByteIndex();
		}

		if ((nextByte >= nBytes) || bufferFull) {
		    out.write(bb, 0, nextByte);
		    nextByte = 0;
                    bufferFlushed = true;
		}
	    }
	}
    }

    /**
     * Write a portion of a string.
     *
     * @param  str  A String
     * @param  off  Offset from which to start writing characters
     * @param  len  Number of characters to write
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void write(String str, int off, int len) throws IOException {
	char cbuf[] = new char[len];
	str.getChars(off, off + len, cbuf, 0);
	write(cbuf, 0, len);
    }

    /**
     * Flush the output buffer to the underlying byte stream, without flushing
     * the byte stream itself.  This method is non-private only so that it may
     * be invoked by PrintStream.
     */
    void flushBuffer() throws IOException {
	synchronized (lock) {
	    ensureOpen();

	    for (;;) {
		try {
		    nextByte += ctb.flush(bb, nextByte, nBytes);
		}
		catch (ConversionBufferFullException x) {
		    nextByte = ctb.nextByteIndex();
		}
		if (nextByte == 0)
		    break;
		if (nextByte > 0) {
		    out.write(bb, 0, nextByte);
		    nextByte = 0;
		}
	    }
	}
    }

    /**
     * Flush the stream.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void flush() throws IOException {
	synchronized (lock) {
	    flushBuffer();
	    out.flush();
	}
    }

    /**
     * Close the stream.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void close() throws IOException {
	synchronized (lock) {
	    if (out == null)
		return;
	    flush();
	    out.close();
	    out = null;
	    bb = null;
	    ctb = null;
	}
    }

}
