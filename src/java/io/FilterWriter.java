/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;


/**
 * Abstract class for writing filtered character streams.
 *
 * @version 	1.12, 02/02/06
 * @author	Mark Reinhold
 * @since	JDK1.1
 */

public abstract class FilterWriter extends Writer {

    /**
     * The underlying character-output stream.
     */
    protected Writer out;

    /**
     * Create a new filtered writer.
     *
     * @param out  a Writer object to provide the underlying stream.
     */
    protected FilterWriter(Writer out) {
	super(out);
	this.out = out;
    }

    /**
     * Write a single character.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void write(int c) throws IOException {
	out.write(c);
    }

    /**
     * Write a portion of an array of characters.
     *
     * @param  cbuf  Buffer of characters to be written
     * @param  off   Offset from which to start reading characters
     * @param  len   Number of characters to be written
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void write(char cbuf[], int off, int len) throws IOException {
	out.write(cbuf, off, len);
    }

    /**
     * Write a portion of a string.
     *
     * @param  str  String to be written
     * @param  off  Offset from which to start reading characters
     * @param  len  Number of characters to be written
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void write(String str, int off, int len) throws IOException {
	out.write(str, off, len);
    }

    /**
     * Flush the stream.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void flush() throws IOException {
	out.flush();
    }

    /**
     * Close the stream.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void close() throws IOException {
	out.close();
    }

}
