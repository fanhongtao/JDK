/*
 * @(#)CheckedInputStream.java	1.19 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.util.zip;

import java.io.FilterInputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 * An input stream that also maintains a checksum of the data being read.
 * The checksum can then be used to verify the integrity of the input data.
 *
 * @see		Checksum
 * @version 	1.19, 12/19/03
 * @author 	David Connelly
 */
public
class CheckedInputStream extends FilterInputStream {
    private Checksum cksum;

    /**
     * Creates an input stream using the specified Checksum.
     * @param in the input stream
     * @param cksum the Checksum
     */
    public CheckedInputStream(InputStream in, Checksum cksum) {
	super(in);
	this.cksum = cksum;
    }

    /**
     * Reads a byte. Will block if no input is available.
     * @return the byte read, or -1 if the end of the stream is reached.
     * @exception IOException if an I/O error has occurred
     */
    public int read() throws IOException {
	int b = in.read();
	if (b != -1) {
	    cksum.update(b);
	}
	return b;
    }

    /**
     * Reads into an array of bytes. Will block until some input
     * is available.
     * @param buf the buffer into which the data is read
     * @param off the start offset of the data
     * @param len the maximum number of bytes read
     * @return    the actual number of bytes read, or -1 if the end
     *		  of the stream is reached.
     * @exception IOException if an I/O error has occurred
     */
    public int read(byte[] buf, int off, int len) throws IOException {
	len = in.read(buf, off, len);
	if (len != -1) {
	    cksum.update(buf, off, len);
	}
	return len;
    }

    /**
     * Skips specified number of bytes of input.
     * @param n the number of bytes to skip
     * @return the actual number of bytes skipped
     * @exception IOException if an I/O error has occurred
     */
    public long skip(long n) throws IOException {
	byte[] buf = new byte[512];
	long total = 0;
	while (total < n) {
	    long len = n - total;
	    len = read(buf, 0, len < buf.length ? (int)len : buf.length);
	    if (len == -1) {
		return total;
	    }
	    total += len;
	}
	return total;
    }

    /**
     * Returns the Checksum for this input stream.
     * @return the Checksum value
     */
    public Checksum getChecksum() {
	return cksum;
    }
}
