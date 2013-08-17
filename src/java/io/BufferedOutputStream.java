/*
 * @(#)BufferedOutputStream.java	1.22 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;

/**
 * The class implements a buffered output stream. By setting up such 
 * an output stream, an application can write bytes to the underlying 
 * output stream without necessarily causing a call to the underlying 
 * system for each byte written. The data is written into a buffer, 
 * and then written to the underlying stream if the buffer reaches 
 * its capacity, the buffer output stream is closed, or the buffer 
 * output stream is explicity flushed. 
 *
 * @author  Arthur van Hoff
 * @version 1.22, 12/10/01
 * @since   JDK1.0
 */
public 
class BufferedOutputStream extends FilterOutputStream {
    /**
     * The buffer where data is stored. 
     *
     * @since   JDK1.0
     */
    protected byte buf[];

    /**
     * The number of valid bytes in the buffer. 
     *
     * @since   JDK1.0
     */
    protected int count;
    
    /**
     * Creates a new buffered output stream to write data to the 
     * specified underlying output stream with a default 512-byte buffer size.
     *
     * @param   out   the underlying output stream.
     * @since   JDK1.0
     */
    public BufferedOutputStream(OutputStream out) {
	this(out, 512);
    }

    /**
     * Creates a new buffered output stream to write data to the 
     * specified underlying output stream with the specified buffer size. 
     *
     * @param   out    the underlying output stream.
     * @param   size   the buffer size.
     * @since   JDK1.0
     */
    public BufferedOutputStream(OutputStream out, int size) {
	super(out);
	buf = new byte[size];
    }

    /** Flush the internal buffer */
    private void flushBuffer() throws IOException {
        if (count > 0) {
	    out.write(buf, 0, count);
	    count = 0;
        }
    }

    /**
     * Writes the specified byte to this buffered output stream. 
     *
     * @param      b   the byte to be written.
     * @exception  IOException  if an I/O error occurs.
     * @since      JDK1.0
     */
    public synchronized void write(int b) throws IOException {
	if (count >= buf.length) {
	    flushBuffer();
	}
	buf[count++] = (byte)b;
    }

    /**
     * Writes <code>len</code> bytes from the specified byte array 
     * starting at offset <code>off</code> to this buffered output stream.
     *
     * <p> Ordinarily this method stores bytes from the given array into this
     * stream's buffer, flushing the buffer to the underlying output stream as
     * needed.  If the requested length is at least as large as this stream's
     * buffer, however, then this method will flush the buffer and write the
     * bytes directly to the underlying output stream.  Thus redundant
     * <code>BufferedOutputStream</code>s will not copy data unnecessarily.
     *
     * @param      b     the data.
     * @param      off   the start offset in the data.
     * @param      len   the number of bytes to write.
     * @exception  IOException  if an I/O error occurs.
     */
    public synchronized void write(byte b[], int off, int len) throws IOException {
	if (len >= buf.length) {
	    /* If the request length exceeds the size of the output buffer,
    	       flush the output buffer and then write the data directly.
    	       In this way buffered streams will cascade harmlessly. */
	    flushBuffer();
	    out.write(b, off, len);
	    return;
	}
	if (len > buf.length - count) {
	    flushBuffer();
	}
	System.arraycopy(b, off, buf, count, len);
	count += len;
    }

    /**
     * Flushes this buffered output stream. This forces any buffered 
     * output bytes to be written out to the underlying output stream. 
     *
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterOutputStream#out
     */
    public synchronized void flush() throws IOException {
        flushBuffer();
	out.flush();
    }
}
