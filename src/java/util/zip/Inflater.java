/*
 * @(#)Inflater.java	1.19 98/08/20
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

/**
 * This class provides support for general purpose decompression using
 * the popular ZLIB compression library. The ZLIB compression library
 * was initially developed as part of the PNG graphics standard and is
 * not protected by patents. It is fully described in RFCs 1950, 1951,
 * and 1952, which can be found at 
 * <a href="http://info.internet.isi.edu:80/in-notes/rfc/files/">
 * http://info.internet.isi.edu:80/in-notes/rfc/files/
 * </a> in the files rfc1950.txt (zlib format),
 * rfc1951.txt (deflate format) and rfc1952.txt (gzip format).
 * 
 * @see		Deflater
 * @version 	1.19, 08/20/98
 * @author 	David Connelly
 *
 */
public
class Inflater {
    private int strm;
    private byte[] buf = new byte[0];
    private int off, len;
    private boolean finished;
    private boolean needsDictionary;

    /*
     * Loads the ZLIB library.
     */
    static {
	System.loadLibrary("zip");
    }

    /**
     * Creates a new decompressor. If the parameter 'nowrap' is true then
     * the ZLIB header and checksum fields will not be used in order to
     * support the compression format used by both GZIP and PKZIP.
     * @param nowrap if true then support GZIP compatible compression
     */
    public Inflater(boolean nowrap) {
	init(nowrap);
    }

    /**
     * Creates a new decompressor.
     */
    public Inflater() {
	this(false);
    }

    /**
     * Sets input data for decompression. Should be called whenever
     * needsInput() returns true indicating that more input data is
     * required.
     * @param b the input data bytes
     * @param off the start offset of the input data
     * @param len the length of the input data
     * @see Inflater#needsInput
     */
    public synchronized void setInput(byte[] b, int off, int len) {
	if (b == null) {
	    throw new NullPointerException();
	}
	if (off < 0 || len < 0 || off + len > b.length) {
	    throw new ArrayIndexOutOfBoundsException();
	}
	this.buf = b;
	this.off = off;
	this.len = len;
    }

    /**
     * Sets input data for decompression. Should be called whenever
     * needsInput() returns true indicating that more input data is
     * required.
     * @param b the input data bytes
     * @see Inflater#needsInput
     */
    public void setInput(byte[] b) {
	setInput(b, 0, b.length);
    }

    /**
     * Sets the preset dictionary to the given array of bytes. Should be
     * called when inflate() returns 0 and needsDictionary() returns true
     * indicating that a preset dictionary is required. The method getAdler()
     * can be used to get the Adler-32 value of the dictionary needed.
     * @param b the dictionary data bytes
     * @param off the start offset of the data
     * @param len the length of the data
     * @see Inflater#needsDictionary
     * @see Inflater#getAdler
     */
    public synchronized native void setDictionary(byte[] b, int off, int len);

    /**
     * Sets the preset dictionary to the given array of bytes. Should be
     * called when inflate() returns 0 and needsDictionary() returns true
     * indicating that a preset dictionary is required. The method getAdler()
     * can be used to get the Adler-32 value of the dictionary needed.
     * @param b the dictionary data bytes
     * @see Inflater#needsDictionary
     * @see Inflater#getAdler
     */
    public void setDictionary(byte[] b) {
	setDictionary(b, 0, b.length);
    }

    /**
     * Returns the total number of bytes remaining in the input buffer.
     * This can be used to find out what bytes still remain in the input
     * buffer after decompression has finished.
     */
    public synchronized int getRemaining() {
	return len;
    }

    /**
     * Returns true if no data remains in the input buffer. This can
     * be used to determine if #setInput should be called in order
     * to provide more input.
     */
    public synchronized boolean needsInput() {
	return len <= 0;
    }

    /**
     * Returns true if a preset dictionary is needed for decompression.
     * @see InflatesetDictionary
     */
    public synchronized boolean needsDictionary() {
	return needsDictionary;
    }

    /**
     * Return true if the end of the compressed data stream has been
     * reached.
     */
    public synchronized boolean finished() {
	return finished;
    }

    /**
     * Uncompresses bytes into specified buffer. Returns actual number
     * of bytes uncompressed. A return value of 0 indicates that
     * needsInput() or needsDictionary() should be called in order to
     * determine if more input data or a preset dictionary is required.
     * In the later case, getAdler() can be used to get the Adler-32
     * value of the dictionary required.
     * @param b the buffer for the uncompressed data
     * @param off the start offset of the data
     * @param len the maximum number of uncompressed bytes
     * @return the actual number of uncompressed bytes
     * @exception DataFormatException if the compressed data format is invalid
     * @see Inflater#needsInput
     * @see Inflater#needsDictionary
     */
    public synchronized native int inflate(byte[] b, int off, int len)
	    throws DataFormatException;

    /**
     * Uncompresses bytes into specified buffer. Returns actual number
     * of bytes uncompressed. A return value of 0 indicates that
     * needsInput() or needsDictionary() should be called in order to
     * determine if more input data or a preset dictionary is required.
     * In the later case, getAdler() can be used to get the Adler-32
     * value of the dictionary required.
     * @param b the buffer for the uncompressed data
     * @return the actual number of uncompressed bytes
     * @exception DataFormatException if the compressed data format is invalid
     * @see Inflater#needsInput
     * @see Inflater#needsDictionary
     */
    public int inflate(byte[] b) throws DataFormatException {
	return inflate(b, 0, b.length);
    }

    /**
     * Returns the ADLER-32 value of the uncompressed data.
     */
    public synchronized native int getAdler();

    /**
     * Returns the total number of bytes input so far.
     */
    public synchronized native int getTotalIn();

    /**
     * Returns the total number of bytes output so far.
     */
    public synchronized native int getTotalOut();

    /**
     * Resets inflater so that a new set of input data can be processed.
     */
    public synchronized native void reset();

    /**
     * Discards unprocessed input and frees internal data.
     */
    public synchronized native void end();

    /**
     * Frees the decompressor when garbage is collected.
     */
    protected void finalize() {
	end();
    }

    private native void init(boolean nowrap);
}
