/*
 * @(#)StringWriter.java	1.7 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;


/**
 * A character stream that collects its output in a string buffer, which can
 * then be used to construct a string.
 *
 * @version 	1.7, 01/12/10
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
     */
    protected StringWriter(int initialSize) {
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
	char cbuf[] = new char[len];
	str.getChars(off, len, cbuf, 0);
	buf.append(cbuf);
    }

    /**
     * Return the buffer's current value as a string.
     */
    public String toString() {
	return buf.toString();
    }

    /**
     * Return the string buffer itself.
     */
    public StringBuffer getBuffer() {
	return buf;
    }

    /**
     * Flush the stream.
     */
    public void flush() { }

    /**
     * Close the stream.  This method does not release the buffer, since its
     * contents might still be required.
     */
    public void close() { }

}
