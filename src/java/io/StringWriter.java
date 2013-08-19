/*
 * @(#)StringWriter.java	1.21 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;


/**
 * A character stream that collects its output in a string buffer, which can
 * then be used to construct a string.
 * <p>
 * Closing a <tt>StringWriter</tt> has no effect. The methods in this class
 * can be called after the stream has been closed without generating an
 * <tt>IOException</tt>.
 *
 * @version 	1.21, 03/01/23
 * @author	Mark Reinhold
 * @since	JDK1.1
 */

public class StringWriter extends Writer {

    private StringBuffer buf;

    /**
     * Create a new string writer, using the default initial string-buffer
     * size.
     */
    public StringWriter() {
	buf = new StringBuffer();
	lock = buf;
    }

    /**
     * Create a new string writer, using the specified initial string-buffer
     * size.
     *
     * @param initialSize  an int specifying the initial size of the buffer.
     */
    public StringWriter(int initialSize) {
	if (initialSize < 0) {
	    throw new IllegalArgumentException("Negative buffer size");
	}
	buf = new StringBuffer(initialSize);
	lock = buf;
    }

    /**
     * Write a single character.
     */
    public void write(int c) {
	buf.append((char) c);
    }

    /**
     * Write a portion of an array of characters.
     *
     * @param  cbuf  Array of characters
     * @param  off   Offset from which to start writing characters
     * @param  len   Number of characters to write
     */
    public void write(char cbuf[], int off, int len) {
        if ((off < 0) || (off > cbuf.length) || (len < 0) ||
            ((off + len) > cbuf.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }
        buf.append(cbuf, off, len);
    }

    /**
     * Write a string.
     */
    public void write(String str) {
	buf.append(str);
    }

    /**
     * Write a portion of a string.
     *
     * @param  str  String to be written
     * @param  off  Offset from which to start writing characters
     * @param  len  Number of characters to write
     */
    public void write(String str, int off, int len)  {
	buf.append(str.substring(off, off + len));
    }

    /**
     * Return the buffer's current value as a string.
     */
    public String toString() {
	return buf.toString();
    }

    /**
     * Return the string buffer itself.
     *
     * @return StringBuffer holding the current buffer value.
     */
    public StringBuffer getBuffer() {
	return buf;
    }

    /**
     * Flush the stream.
     */
    public void flush() { 
    }

    /**
     * Closing a <tt>StringWriter</tt> has no effect. The methods in this
     * class can be called after the stream has been closed without generating
     * an <tt>IOException</tt>.
     */
    public void close() throws IOException {
    }

}
