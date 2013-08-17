/*
 * @(#)ByteArrayInputStream.java	1.23 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;

/**
 * This class allows an application to create an input stream in 
 * which the bytes read are supplied by the contents of a byte array. 
 * Applications can also read bytes from a string by using a 
 * <code>StringBufferInputStream</code>. 
 *
 * @author  Arthur van Hoff
 * @version 1.23, 12/10/01
 * @see     java.io.StringBufferInputStream
 * @since   JDK1.0
 */
public
class ByteArrayInputStream extends InputStream {
    /**
     * The byte array containing the data. 
     *
     * @since   JDK1.0
     */
    protected byte buf[];

    /**
     * The index of the next character to read from the input stream buffer.
     *
     * @since   JDK1.0
     */
    protected int pos;

    /**
     * The currently marked position in the stream.
     * ByteArrayInputStreams are marked at position zero by
     * default when constructed.  They may be marked at another
     * position within the buffer by the <code>mark()</code> method.
     * The current buffer position is set to this point by the
     * <code>reset()</code> method.
     *
     * @since   JDK1.1
     */
    protected int mark = 0;

    /**
     * The index one greater than the last valid character in the input 
     * stream buffer. 
     *
     * @since   JDK1.0
     */
    protected int count;

    /**
     * Creates a new byte array input stream that reads data from the 
     * specified byte array. The byte array is not copied. 
     *
     * @param   buf   the input buffer.
     * @since   JDK1.0
     */
    public ByteArrayInputStream(byte buf[]) {
	this.buf = buf;
        this.pos = 0;
	this.count = buf.length;
    }

    /**
     * Creates a new byte array input stream that reads data from the 
     * specified byte array. Up to <code>length</code> characters are to 
     * be read from the byte array, starting at the indicated offset. 
     * <p>
     * The byte array is not copied. 
     *
     * @param   buf      the input buffer.
     * @param   offset   the offset in the buffer of the first byte to read.
     * @param   length   the maximum number of bytes to read from the buffer.
     * @since   JDK1.0
     */
    public ByteArrayInputStream(byte buf[], int offset, int length) {
	this.buf = buf;
        this.pos = offset;
	this.count = Math.min(offset + length, buf.length);
    }

    /**
     * Reads the next byte of data from this input stream. The value 
     * byte is returned as an <code>int</code> in the range 
     * <code>0</code> to <code>255</code>. If no byte is available 
     * because the end of the stream has been reached, the value 
     * <code>-1</code> is returned. 
     * <p>
     * The <code>read</code> method of <code>ByteArrayInputStream</code> 
     * cannot block. 
     *
     * @return  the next byte of data, or <code>-1</code> if the end of the
     *          stream has been reached.
     * @since   JDK1.0
     */
    public synchronized int read() {
	return (pos < count) ? (buf[pos++] & 0xff) : -1;
    }

    /**
     * Reads up to <code>len</code> bytes of data into an array of bytes 
     * from this input stream. This <code>read</code> method cannot block. 
     *
     * @param   b     the buffer into which the data is read.
     * @param   off   the start offset of the data.
     * @param   len   the maximum number of bytes read.
     * @return  the total number of bytes read into the buffer, or
     *          <code>-1</code> if there is no more data because the end of
     *          the stream has been reached.
     * @since   JDK1.0
     */
    public synchronized int read(byte b[], int off, int len) {
	if (pos >= count) {
	    return -1;
	}
	if (pos + len > count) {
	    len = count - pos;
	}
	if (len <= 0) {
	    return 0;
	}
	System.arraycopy(buf, pos, b, off, len);
	pos += len;
	return len;
    }

    /**
     * Skips <code>n</code> bytes of input from this input stream. Fewer 
     * bytes might be skipped if the end of the input stream is reached. 
     *
     * @param   n   the number of bytes to be skipped.
     * @return  the actual number of bytes skipped.
     * @since   JDK1.0
     */
    public synchronized long skip(long n) {
	if (pos + n > count) {
	    n = count - pos;
	}
	if (n < 0) {
	    return 0;
	}
	pos += n;
	return n;
    }

    /**
     * Returns the number of bytes that can be read from this input 
     * stream without blocking. 
     * <p>
     * The <code>available</code> method of 
     * <code>ByteArrayInputStream</code> returns the value of 
     * <code>count&nbsp;- pos</code>, 
     * which is the number of bytes remaining to be read from the input buffer.
     *
     * @return  the number of bytes that can be read from the input stream
     *          without blocking.
     * @since   JDK1.0
     */
    public synchronized int available() {
	return count - pos;
    }

    /**
     * Tests if ByteArrayInputStream supports mark/reset.
     *
     * @since   JDK1.1
     */
    public boolean markSupported() {
	return true;
    }

    /**
     * Set the current marked position in the stream.
     * ByteArrayInputStreams are marked at position zero by
     * default when constructed.  They may be marked at another
     * position within the buffer by this method.
     *
     * @since   JDK1.1
     */
    public void mark(int markpos) {
	mark = pos;
    }

    /**
     * Resets the buffer to the marked position.  The marked position
     * is the beginning unless another position was marked.
     *
     * @since   JDK1.0
     */
    public synchronized void reset() {
	pos = mark;
    }
}
