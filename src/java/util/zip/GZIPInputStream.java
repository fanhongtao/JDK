/*
 * @(#)GZIPInputStream.java	1.25 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.util.zip;

import java.io.SequenceInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.EOFException;

/**
 * This class implements a stream filter for reading compressed data in
 * the GZIP format.
 *
 * @see		InflaterInputStream
 * @version 	1.25, 01/23/03
 * @author 	David Connelly
 *
 */
public
class GZIPInputStream extends InflaterInputStream {
    /**
     * CRC-32 for uncompressed data.
     */
    protected CRC32 crc = new CRC32();

    /**
     * Indicates end of input stream.
     */
    protected boolean eos;

    private boolean closed = false;
    
    /**
     * Check to make sure that this stream has not been closed
     */
    private void ensureOpen() throws IOException {
	if (closed) {
	    throw new IOException("Stream closed");
        }
    }

    /**
     * Creates a new input stream with the specified buffer size.
     * @param in the input stream
     * @param size the input buffer size
     * @exception IOException if an I/O error has occurred
     * @exception IllegalArgumentException if size is <= 0
     */
    public GZIPInputStream(InputStream in, int size) throws IOException {
	super(in, new Inflater(true), size);
        usesDefaultInflater = true;
	readHeader();
	crc.reset();
    }

    /**
     * Creates a new input stream with a default buffer size.
     * @param in the input stream
     * @exception IOException if an I/O error has occurred
     */
    public GZIPInputStream(InputStream in) throws IOException {
	this(in, 512);
    }

    /**
     * Reads uncompressed data into an array of bytes. Blocks until enough
     * input is available for decompression.
     * @param buf the buffer into which the data is read
     * @param off the start offset of the data
     * @param len the maximum number of bytes read
     * @return	the actual number of bytes read, or -1 if the end of the
     *		compressed input stream is reached
     * @exception IOException if an I/O error has occurred or the compressed
     *			      input data is corrupt
     */
    public int read(byte[] buf, int off, int len) throws IOException {
        ensureOpen();
	if (eos) {
	    return -1;
	}
	len = super.read(buf, off, len);
	if (len == -1) {
	    readTrailer();
	    eos = true;
	} else {
	    crc.update(buf, off, len);
	}
	return len;
    }

    /**
     * Closes the input stream.
     * @exception IOException if an I/O error has occurred
     */
    public void close() throws IOException {
        if (!closed) {
            super.close();	
            eos = true;
            closed = true;
        }
    }

    /**
     * GZIP header magic number.
     */
    public final static int GZIP_MAGIC = 0x8b1f;

    /*
     * File header flags.
     */
    private final static int FTEXT	= 1;	// Extra text
    private final static int FHCRC	= 2;	// Header CRC
    private final static int FEXTRA	= 4;	// Extra field
    private final static int FNAME	= 8;	// File name
    private final static int FCOMMENT	= 16;	// File comment

    /*
     * Reads GZIP member header.
     */
    private void readHeader() throws IOException {
	CheckedInputStream in = new CheckedInputStream(this.in, crc);
	crc.reset();
	// Check header magic
	if (readUShort(in) != GZIP_MAGIC) {
	    throw new IOException("Not in GZIP format");
	}
	// Check compression method
	if (readUByte(in) != 8) {
	    throw new IOException("Unsupported compression method");
	}
	// Read flags
	int flg = readUByte(in);
	// Skip MTIME, XFL, and OS fields
	skipBytes(in, 6);
	// Skip optional extra field
	if ((flg & FEXTRA) == FEXTRA) {
	    skipBytes(in, readUShort(in));
	}
	// Skip optional file name
	if ((flg & FNAME) == FNAME) {
	    while (readUByte(in) != 0) ;
	}
	// Skip optional file comment
	if ((flg & FCOMMENT) == FCOMMENT) {
	    while (readUByte(in) != 0) ;
	}
	// Check optional header CRC
	if ((flg & FHCRC) == FHCRC) {
	    int v = (int)crc.getValue() & 0xffff;
	    if (readUShort(in) != v) {
		throw new IOException("Corrupt GZIP header");
	    }
	}
    }

    /*
     * Reads GZIP member trailer.
     */
    private void readTrailer() throws IOException {
	InputStream in = this.in;
	int n = inf.getRemaining();
	if (n > 0) {
	    in = new SequenceInputStream(
			new ByteArrayInputStream(buf, len - n, n), in);
	}
	long v = crc.getValue();
	if (readUInt(in) != v || readUInt(in) != inf.getTotalOut()) {
	    throw new IOException("Corrupt GZIP trailer");
	}
    }

    /*
     * Reads unsigned integer in Intel byte order.
     */
    private long readUInt(InputStream in) throws IOException {
	long s = readUShort(in);
	return ((long)readUShort(in) << 16) | s;
    }

    /*
     * Reads unsigned short in Intel byte order.
     */
    private int readUShort(InputStream in) throws IOException {
	int b = readUByte(in);
	return ((int)readUByte(in) << 8) | b;
    }

    /*
     * Reads unsigned byte.
     */
    private int readUByte(InputStream in) throws IOException {
	int b = in.read();
	if (b == -1) {
	    throw new EOFException();
	}
	return b;
    }


    private byte[] tmpbuf = new byte[128];

    /*
     * Skips bytes of input data blocking until all bytes are skipped.
     * Does not assume that the input stream is capable of seeking.
     */
    private void skipBytes(InputStream in, int n) throws IOException {
	while (n > 0) {
	    int len = in.read(tmpbuf, 0, n < tmpbuf.length ? n : tmpbuf.length);
	    if (len == -1) {
		throw new EOFException();
	    }
	    n -= len;
	}
    }
}
