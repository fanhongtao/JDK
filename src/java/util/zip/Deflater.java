/*
 * @(#)Deflater.java	1.19 98/08/20
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
 * @see		Inflater
 * @version 	1.19, 08/20/98
 * @author 	David Connelly
 */
public
class Deflater {
    private int strm;
    private byte[] buf = new byte[0];
    private int off, len;
    private int level, strategy;
    private boolean setParams;
    private boolean finish, finished;

    /**
     * Compression method for the deflate algorithm (the only one currently
     * supported).
     */
    public static final int DEFLATED = 8;

    /**
     * Compression level for no compression.
     */
    public static final int NO_COMPRESSION = 0;

    /**
     * Compression level for fastest compression.
     */
    public static final int BEST_SPEED = 1;

    /**
     * Compression level for best compression.
     */
    public static final int BEST_COMPRESSION = 9;

    /**
     * Default compression level.
     */
    public static final int DEFAULT_COMPRESSION = -1;

    /**
     * Compression strategy best used for data consisting mostly of small
     * values with a somewhat random distribution. Forces more Huffman coding
     * and less string matching.
     */
    public static final int FILTERED = 1;

    /**
     * Compression strategy for Huffman coding only.
     */
    public static final int HUFFMAN_ONLY = 2;

    /**
     * Default compression strategy.
     */
    public static final int DEFAULT_STRATEGY = 0;

    /*
     * Loads the ZLIB library.
     */
    static {
	System.loadLibrary("zip");
    }

    /**
     * Creates a new compressor using the specified compression level.
     * If 'nowrap' is true then the ZLIB header and checksum fields will
     * not be used in order to support the compression format used in
     * both GZIP and PKZIP.
     * @param level the compression level (0-9)
     * @param nowrap if true then use GZIP compatible compression
     */
    public Deflater(int level, boolean nowrap) {
	this.level = level;
	this.strategy = DEFAULT_STRATEGY;
	init(nowrap);
    }

    /** 
     * Creates a new compressor using the specified compression level.
     * Compressed data will be generated in ZLIB format.
     * @param level the compression level (0-9)
     */
    public Deflater(int level) {
	this(level, false);
    }

    /**
     * Creates a new compressor with the default compression level.
     * Compressed data will be generated in ZLIB format.
     */
    public Deflater() {
	this(DEFAULT_COMPRESSION, false);
    }

    /**
     * Sets input data for compression. This should be called whenever
     * needsInput() returns true indicating that more input data is required.
     * @param b the input data bytes
     * @param off the start offset of the data
     * @param len the length of the data
     * @see Deflater#needsInput
     */
    public synchronized void setInput(byte[] b, int off, int len) {
	if (b== null) {
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
     * Sets input data for compression. This should be called whenever
     * needsInput() returns true indicating that more input data is required.
     * @param b the input data bytes
     * @see Deflater#needsInput
     */
    public void setInput(byte[] b) {
	setInput(b);
    }

    /**
     * Sets preset dictionary for compression. A preset dictionary is used
     * when the history buffer can be predetermined. When the data is later
     * uncompressed with Inflater.inflate(), Inflater.getAdler() can be called
     * in order to get the Adler-32 value of the dictionary required for
     * decompression.
     * @param b the dictionary data bytes
     * @param off the start offset of the data
     * @param len the length of the data
     * @see Inflater#inflate
     * @see Inflater#getAdler
     */
    public synchronized native void setDictionary(byte[] b, int off, int len);

    /**
     * Sets preset dictionary for compression. A preset dictionary is used
     * when the history buffer can be predetermined. When the data is later
     * uncompressed with Inflater.inflate(), Inflater.getAdler() can be called
     * in order to get the Adler-32 value of the dictionary required for
     * decompression.
     * @param b the dictionary data bytes
     * @see Inflater#inflate
     * @see Inflater#getAdler
     */
    public void setDictionary(byte[] b) {
	setDictionary(b, 0, b.length);
    }

    /**
     * Sets the compression strategy to the specified value.
     * @param strategy the new compression strategy
     * @exception IllegalArgumentException if the compression strategy is
     *				           invalid
     */
    public synchronized void setStrategy(int strategy) {
	switch (strategy) {
	  case DEFAULT_STRATEGY:
	  case FILTERED:
	  case HUFFMAN_ONLY:
	    break;
	  default:
	    throw new IllegalArgumentException();
	}
	if (this.strategy != strategy) {
	    this.strategy = strategy;
	    setParams = true;
	}
    }

    /**
     * Sets the current compression level to the specified value.
     * @param level the new compression level (0-9)
     * @exception IllegalArgumentException if the compression level is invalid
     */
    public synchronized void setLevel(int level) {
	if ((level < 0 || level > 9) && level != DEFAULT_COMPRESSION) {
	    throw new IllegalArgumentException("invalid compression level");
	}
	if (this.level != level) {
	    this.level = level;
	    setParams = true;
	}
    }

    /**
     * Returns true if the input data buffer is empty and setInput()
     * should be called in order to provide more input.
     */
    public boolean needsInput() {
	return len <= 0;
    }

    /**
     * When called, indicates that compression should end with the current
     * contents of the input buffer.
     */
    public synchronized void finish() {
	finish = true;
    }

    /**
     * Returns true if the end of the compressed data output stream has
     * been reached.
     */
    public synchronized boolean finished() {
	return finished;
    }

    /**
     * Fills specified buffer with compressed data. Returns actual number
     * of bytes of compressed data. A return value of 0 indicates that
     * needsInput() should be called in order to determine if more input
     * data is required.
     * @param b the buffer for the compressed data
     * @param off the start offset of the data
     * @param len the maximum number of bytes of compressed data
     * @return the actual number of bytes of compressed data
     */
    public synchronized native int deflate(byte[] b, int off, int len);

    /**
     * Fills specified buffer with compressed data. Returns actual number
     * of bytes of compressed data. A return value of 0 indicates that
     * needsInput() should be called in order to determine if more input
     * data is required.
     * @param b the buffer for the compressed data
     * @return the actual number of bytes of compressed data
     */
    public int deflate(byte[] b) {
	return deflate(b, 0, b.length);
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
     * Resets deflater so that a new set of input data can be processed.
     * Keeps current compression level and strategy settings.
     */
    public synchronized native void reset();

    /**
     * Discards unprocessed input and frees internal data.
     */
    public synchronized native void end();

    /**
     * Frees the compressor when garbage is collected.
     */
    protected void finalize() {
	end();
    }

    private native void init(boolean nowrap);
}
