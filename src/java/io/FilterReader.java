/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;


/**
 * Abstract class for reading filtered character streams.
 *
 * @version 	1.12, 02/02/06
 * @author	Mark Reinhold
 * @since	JDK1.1
 */

public abstract class FilterReader extends Reader {

    /**
     * The underlying character-input stream, or null if the stream has been
     * closed.
     */
    protected Reader in;

    /**
     * Create a new filtered reader.
     *
     * @param in  a Reader object providing the underlying stream.
     */
    protected FilterReader(Reader in) {
	super(in);
	this.in = in;
    }

    /**
     * Read a single character.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public int read() throws IOException {
	return in.read();
    }

    /**
     * Read characters into a portion of an array.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public int read(char cbuf[], int off, int len) throws IOException {
	return in.read(cbuf, off, len);
    }

    /**
     * Skip characters.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public long skip(long n) throws IOException {
	return in.skip(n);
    }

    /**
     * Tell whether this stream is ready to be read.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public boolean ready() throws IOException {
	return in.ready();
    }

    /**
     * Tell whether this stream supports the mark() operation.
     */
    public boolean markSupported() {
	return in.markSupported();
    }

    /**
     * Mark the present position in the stream.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void mark(int readAheadLimit) throws IOException {
	in.mark(readAheadLimit);
    }

    /**
     * Reset the stream.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void reset() throws IOException {
	in.reset();
    }

    /**
     * Close the stream.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void close() throws IOException {
	in.close();
    }

}
