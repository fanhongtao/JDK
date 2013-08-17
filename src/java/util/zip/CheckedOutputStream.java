/*
 * @(#)CheckedOutputStream.java	1.12 98/09/21
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.util.zip;

import java.io.FilterOutputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * An output stream that also maintains a checksum of the data being
 * written. The checksum can then be used to verify the integrity of
 * the output data.
 *
 * @see		Checksum
 * @version 	1.12, 09/21/98
 * @author 	David Connelly
 */
public
class CheckedOutputStream extends FilterOutputStream {
    private Checksum cksum;

    /**
     * Creates an output stream with the specified Checksum.
     * @param out the output stream
     * @param cksum the checksum
     */
    public CheckedOutputStream(OutputStream out, Checksum cksum) {
	super(out);
	this.cksum = cksum;
    }

    /**
     * Writes a byte. Will block until the byte is actually written.
     * @param b the byte to be written
     * @exception IOException if an I/O error has occurred
     */
    public void write(int b) throws IOException {
	out.write(b);
	cksum.update(b);
    }

    /**
     * Writes an array of bytes. Will block until the bytes are
     * actually written.
     * @param buf the data to be written
     * @param off the start offset of the data
     * @param len the number of bytes to be written
     * @exception IOException if an I/O error has occurred
     */
    public void write(byte[] b, int off, int len) throws IOException {
	out.write(b, off, len);
	cksum.update(b, off, len);
    }

    /**
     * Returns the Checksum for this output stream.
     */
    public Checksum getChecksum() {
	return cksum;
    }
}
