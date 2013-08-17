/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;


/**
 * A character stream that collects its output in a string buffer, which can
 * then be used to construct a string.
 *
 * @version 	1.19, 02/02/06
 * @author	Mark Reinhold
 * @since	JDK1.1
 */

public class StringWriter extends Writer {

    private StringBuffer buf;

    /**
     * Flag indicating whether the stream has been closed.
     */
    private boolean isClosed = false;

    /** Check to make sure that the stream has not been closed */
    private void ensureOpen() {
        /* This method does nothing for now.  Once we add throws clauses
	 * to the I/O methods in this class, it will throw an IOException
	 * if the stream has been closed.
	 */
    }

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
	ensureOpen();
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
	ensureOpen();
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
	ensureOpen();
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
	ensureOpen();
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
	ensureOpen();
    }

    /**
     * Close the stream.  This method does not release the buffer, since its
     * contents might still be required.
     */
    public void close() throws IOException { 
	isClosed = true;
    }

}
