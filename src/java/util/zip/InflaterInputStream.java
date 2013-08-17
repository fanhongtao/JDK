/*
 * @(#)InflaterInputStream.java	1.16 98/03/19
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

import java.io.FilterInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.EOFException;

/**
 * This class implements a stream filter for uncompressing data in the
 * "deflate" compression format. It is also used as the basis for other
 * decompression filters, such as GZIPInputStream.
 *
 * @see		Inflater
 * @version 	1.16, 03/19/98
 * @author 	David Connelly
 */
public
class InflaterInputStream extends FilterInputStream {
    /**
     * Decompressor for this stream.
     */
    protected Inflater inf;

    /**
     * Input buffer for decompression.
     */
    protected byte[] buf;

    /**
     * Length of input buffer.
     */
    protected int len;

    /**
     * Creates a new input stream with the specified decompressor and
     * buffer size.
     * @param in the input stream
     * @param inf the decompressor ("inflater")
     * @param size the input buffer size
     */
    public InflaterInputStream(InputStream in, Inflater inf, int size) {
	super(in);
	this.inf = inf;
	buf = new byte[size];
    }

    /**
     * Creates a new input stream with the specified decompressor and a
     * default buffer size.
     * @param in the input stream
     * @param inf the decompressor ("inflater")
     */
    public InflaterInputStream(InputStream in, Inflater inf) {
	this(in, inf, 512);
    }

    /**
     * Creates a new input stream with a default decompressor and buffer size.
     */
    public InflaterInputStream(InputStream in) {
	this(in, new Inflater());
    }

    /**
     * Reads a byte of uncompressed data. This method will block until
     * enough input is available for decompression.
     * @return the byte read, or -1 if end of compressed input is reached
     * @exception IOException if an I/O error has occurred
     */
    public int read() throws IOException {
	byte[] b = new byte[1];
	return read(b, 0, 1) == -1 ? -1 : b[0] & 0xff;
    }

    /**
     * Reads uncompressed data into an array of bytes. This method will
     * block until some input can be decompressed.
     * @param b the buffer into which the data is read
     * @param off the start offset of the data
     * @param len the maximum number of bytes read
     * @return the actual number of bytes read, or -1 if the end of the
     *         compressed input is reached or a preset dictionary is needed
     * @exception ZipException if a ZIP format error has occurred
     * @exception IOException if an I/O error has occurred
     */
    public int read(byte[] b, int off, int len) throws IOException {
	if (len <= 0) {
	    return 0;
	}
	try {
	    int n;
	    while ((n = inf.inflate(b, off, len)) == 0) {
		if (inf.finished() || inf.needsDictionary()) {
		    return -1;
		}
		if (inf.needsInput()) {
		    fill();
		}
	    }
	    return n;
	} catch (DataFormatException e) {
	    String s = e.getMessage();
	    throw new ZipException(s != null ? s : "Invalid ZLIB data format");
	}
    }

    /**
     * Skips specified number of bytes of uncompressed data.
     * @param n the number of bytes to skip
     * @return the actual number of bytes skipped.
     * @exception IOException if an I/O error has occurred
     */
    public long skip(long n) throws IOException {
	byte[] b = new byte[512];
	long total = 0;
	while (total < n) {
	    long len = n - total;
	    len = read(b, 0, len < b.length ? (int)len : b.length);
	    if (len == -1) {
		break;
	    }
	    total += len;
	}
	return total;
    }

    /**
     * Fills input buffer with more data to decompress.
     * @exception IOException if an I/O error has occurred
     */
    protected void fill() throws IOException {
	len = in.read(buf, 0, buf.length);
	if (len == -1) {
	    throw new EOFException("Unexpected end of ZLIB input stream");
	}
	inf.setInput(buf, 0, len);
    }
}
