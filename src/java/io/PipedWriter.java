/*
 * @(#)PipedWriter.java	1.5 97/01/27
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 * 
 */

package java.io;


/**
 * Piped character-output streams.
 *
 * @version 	1.5, 97/01/27
 * @author	Mark Reinhold
 * @since	JDK1.1
 */

public class PipedWriter extends Writer {

    PipedOutputStream byteSource;

    private byte buf[];		/* Conversion buffer */

    /**
     * Create a writer that is not yet connected to a piped reader.
     */
    public PipedWriter() {
	byteSource = new PipedOutputStream();
    }

    /**
     * Create a writer for the specified piped character-input stream.
     */
    public PipedWriter(PipedReader sink) throws IOException {
	this();
	connect(sink);
    }

    /** Check to make sure that the stream has not been closed */
    private void ensureOpen() throws IOException {
	if (byteSource == null)
	    throw new IOException("Stream closed");
    }

    /**
     * Connect the specified piped reader to this writer.
     */
    public void connect(PipedReader sink) throws IOException {
	synchronized (lock) {
	    ensureOpen();
	    byteSource.connect(sink.byteSink);
	}
    }

    /**
     * Write a portion of an array of characters.
     *
     * @param  cbuf  Array of characters
     * @param  off   Offset from which to start writing characters
     * @param  len   Number of characters to write
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void write(char cbuf[], int off, int len) throws IOException {
	synchronized (lock) {
	    ensureOpen();

	    int nb = len * 2;
	    if ((buf == null) || (buf.length < nb))
		buf = new byte[nb];
	    for (int i = 0; i < nb; i += 2) {
		char c = cbuf[off + (i >> 1)];
		buf[i] = (byte) (c >> 8);
		buf[i + 1] = (byte) c;
	    }

	    byteSource.write(buf, 0, nb);
	}
    }

    /**
     * Flush the stream.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void flush() throws IOException {
	synchronized (lock) {
	    ensureOpen();
	    byteSource.flush();
	}
    }

    /**
     * Close the stream.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void close() throws IOException {
	synchronized (lock) {
	    if (byteSource == null)
		return;
	    byteSource.close();
	    byteSource = null;
	}
    }

}
