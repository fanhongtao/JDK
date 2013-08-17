/*
 * @(#)DeflaterOutputStream.java	1.17 97/01/30
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

package java.util.zip;

import java.io.FilterOutputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 * This class implements an output stream filter for compressing data in
 * the "deflate" compression format. It is also used as the basis for other
 * types of compression filters, such as GZIPOutputStream.
 *
 * @see		Deflater
 * @version 	1.17, 01/30/97
 * @author 	David Connelly
 */
public
class DeflaterOutputStream extends FilterOutputStream {
    /**
     * Compressor for this stream.
     */
    protected Deflater def;

    /**
     * Output buffer for writing compressed data.
     */
    protected byte[] buf;
   
    /**
     * Creates a new output stream with the specified compressor and
     * buffer size.
     * @param out the output stream
     * @param def the compressor ("deflater")
     * @param len the output buffer size
     */
    public DeflaterOutputStream(OutputStream out, Deflater def, int size) {
        super(out);
        this.def = def;
        buf = new byte[size];
    }

    /**
     * Creates a new output stream with the specified compressor and
     * a default buffer size.
     * @param out the output stream
     * @param def the compressor ("deflater")
     */
    public DeflaterOutputStream(OutputStream out, Deflater def) {
	this(out, def, 512);
    }

    /**
     * Creates a new output stream with a defaul compressor and buffer size.
     */
    public DeflaterOutputStream(OutputStream out) {
	this(out, new Deflater());
    }

    /**
     * Writes a byte to the compressed output stream. This method will
     * block until the byte can be written.
     * @param b the byte to be written
     * @exception IOException if an I/O error has occurred
     */
    public void write(int b) throws IOException {
	byte[] buf = new byte[1];
	buf[0] = (byte)(b & 0xff);
	write(buf, 0, 1);
    }

    /**
     * Writes an array of bytes to the compressed output stream. This
     * method will block until all the bytes are written.
     * @param buf the data to be written
     * @param off the start offset of the data
     * @param len the length of the data
     * @exception IOException if an I/O error has occurred
     */
    public void write(byte[] b, int off, int len) throws IOException {
	if (def.finished()) {
	    throw new IOException("write beyond end of stream");
	}
	if (!def.finished()) {
	    def.setInput(b, off, len);
	    while (!def.needsInput()) {
		deflate();
	    }
	}
    }

    /**
     * Finishes writing compressed data to the output stream without closing
     * the underlying stream. Use this method when applying multiple filters
     * in succession to the same output stream.
     * @exception IOException if an I/O error has occurred
     */
    public void finish() throws IOException {
	if (!def.finished()) {
	    def.finish();
	    while (!def.finished()) {
		deflate();
	    }
	}
    }

    /**
     * Writes remaining compressed data to the output stream and closes the
     * underlying stream.
     * @exception IOException if an I/O error has occurred
     */
    public void close() throws IOException {
	finish();
	out.close();
    }

    /**
     * Writes next block of compressed data to the output stream.
     */
    protected void deflate() throws IOException {
	int len = def.deflate(buf, 0, buf.length);
	if (len > 0) {
	    out.write(buf, 0, len);
	}
    }
}
