/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;

import sun.io.CharToByteConverter;
import sun.io.ConversionBufferFullException;


/**
 * An OutputStreamWriter is a bridge from character streams to byte streams:
 * Characters written to it are translated into bytes according to a specified
 * <a href="../lang/package-summary.html#charenc">character encoding</a>.  The
 * encoding that it uses may be specified by name, or the platform's default
 * encoding may be accepted.
 *
 * <p> Each invocation of a write() method causes the encoding converter to be
 * invoked on the given character(s).  The resulting bytes are accumulated in a
 * buffer before being written to the underlying output stream.  The size of
 * this buffer may be specified, but by default it is large enough for most
 * purposes.  Note that the characters passed to the write() methods are not
 * buffered.
 *
 * <p> For top efficiency, consider wrapping an OutputStreamWriter within a
 * BufferedWriter so as to avoid frequent converter invocations.  For example:
 *
 * <pre>
 * Writer out
 *   = new BufferedWriter(new OutputStreamWriter(System.out));
 * </pre>
 *
 * @see BufferedWriter
 * @see OutputStream
 * @see <a href="../lang/package-summary.html#charenc">Character encodings</a>
 *
 * @version 	1.29, 02/06/02
 * @author	Mark Reinhold
 * @since	JDK1.1
 */

public class OutputStreamWriter extends Writer {

    private CharToByteConverter ctb;
    private OutputStream out;

    private static final int defaultByteBufferSize = 8192;
    /* bb is a temporary output buffer into which bytes are written. */
    private byte bb[];
    /* nextByte is where the next byte will be written into bb */
    private int nextByte = 0;
    /* nBytes is the buffer size = defaultByteBufferSize in this class */
    private int nBytes = 0;

    /**
     * Create an OutputStreamWriter that uses the named character encoding.
     *
     * @param  out  An OutputStream
     * @param  enc  The name of a supported
     *              <a href="../lang/package-summary.html#charenc">character
     *              encoding</a>
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
	if (out == null) 
	    throw new NullPointerException("out is null");
	this.out = out;
	this.ctb = ctb;
	bb = new byte[defaultByteBufferSize];
	nBytes = defaultByteBufferSize;
    }

    /**
     * Returns the canonical name of the character encoding being used by this
     * stream.  If this <code>OutputStreamWriter</code> was created with the
     * {@link #OutputStreamWriter(OutputStream, String)} constructor then the
     * returned encoding name, being canonical, may differ from the encoding
     * name passed to the constructor.  May return <code>null</code> if the
     * stream has been closed.
     *
     * @return a String representing the encoding name, or possibly
     *         <code>null</code> if the stream has been closed
     *
     * @see <a href="../lang/package-summary.html#charenc">Character
     *      encodings</a>
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
            if ((off < 0) || (off > cbuf.length) || (len < 0) ||
                ((off + len) > cbuf.length) || ((off + len) < 0)) {
                throw new IndexOutOfBoundsException();
            } else if (len == 0) {
                return;
            }
	    int ci = off, end = off + len;
	    boolean bufferFlushed = false; 
	    while (ci < end) {
		boolean bufferFull = false;
		try {
		    nextByte += ctb.convertAny(cbuf, ci, end,
					    bb, nextByte, nBytes);
		    ci = end;
		}
		catch (ConversionBufferFullException x) {
		    int nci = ctb.nextCharIndex();
		    if ((nci == ci) && bufferFlushed) {
			/* If the buffer has been flushed and it 
			   still does not hold even one character */
			throw new 
			    CharConversionException("Output buffer too small");
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
	/* Check the len before creating a char buffer */
	if (len < 0)
	    throw new IndexOutOfBoundsException();

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
		    nextByte += ctb.flushAny(bb, nextByte, nBytes);
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
