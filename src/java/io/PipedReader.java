/*
 * @(#)PipedReader.java	1.6 98/07/01
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


/**
 * Piped character-input streams.
 *
 * @version 	1.6, 98/07/01
 * @author	Mark Reinhold
 * @since	JDK1.1
 */

public class PipedReader extends Reader {

    PipedInputStream byteSink;

    private byte buf[];		/* Conversion buffer */
    private int leftOver = 0;

    /**
     * Create a reader that is not yet connected to a piped writer.
     */
    public PipedReader() {
	byteSink = new PipedInputStream();
	lock = byteSink;
    }

    /**
     * Create a reader for the specified piped character-output stream.
     */
    public PipedReader(PipedWriter src) throws IOException {
	this();
	connect(src);
    }

    /** Check to make sure that the stream has not been closed */
    private void ensureOpen() throws IOException {
	if (byteSink == null)
	    throw new IOException("Stream closed");
    }

    /**
     * Connect the specified piped writer to this reader.
     *
     * @exception  IOException  If this reader is already connected
     */
    public void connect(PipedWriter src) throws IOException {
	synchronized (lock) {
	    ensureOpen();
	    src.connect(this);
	}
    }

    /**
     * Read characters into a portion of an array.
     *
     * @param      cbuf  Destination buffer
     * @param      off   Offset at which to start storing characters
     * @param      len   Maximum number of characters to read
     *
     * @return     The number of characters read, or -1 if the end of the
     *             stream has been reached
     *
     * @exception  IOException  If an I/O error occurs
     */
    public int read(char cbuf[], int off, int len) throws IOException {
	synchronized (lock) {
	    ensureOpen();

	    int blen = leftOver + len * 2;
	    if ((buf == null) || (buf.length < blen))
		buf = new byte[blen];
	    int nb = byteSink.read(buf, leftOver, blen);
	    if (nb < 0)
		return -1;
	    nb += leftOver;
	    for (int i = 0; i < nb; i += 2)
		cbuf[i >> 1] = (char) (((buf[i] & 0xff) << 8)
				       | (buf[i + 1] & 0xff));
	    if (nb % 2 != 0) {
		buf[0] = buf[nb - 1];
		leftOver = 1;
	    }
	    else
		leftOver = 0;
	    return nb / 2;
	}
    }

    /**
     * Close the stream.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void close() throws IOException {
	synchronized (lock) {
	    if (byteSink == null)
		return;
	    byteSink.close();
	    byteSink = null;
	}
    }

}
