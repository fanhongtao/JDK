/*
 * @(#)ZipInputStream.java	1.11 97/01/24
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

import java.io.InputStream;
import java.io.IOException;
import java.io.EOFException;
import java.io.PushbackInputStream;

/**
 * This class implements an input stream filter for reading files in the
 * ZIP file format. Includes support for both compressed and uncompressed
 * entries.
 *
 * @author	David Connelly
 * @version	1.11, 01/24/97
 */
public
class ZipInputStream extends InflaterInputStream implements ZipConstants {
    private ZipEntry entry;
    private CRC32 crc = new CRC32();
    private long remaining;
    private byte[] tmpbuf = new byte[512];

    private static final int STORED = ZipEntry.STORED;
    private static final int DEFLATED = ZipEntry.DEFLATED;

    /**
     * Creates a new ZIP input stream.
     * @param in the actual input stream
     */
    public ZipInputStream(InputStream in) {
	super(new PushbackInputStream(in, 512), new Inflater(true), 512);
    }

    /**
     * Reads the next ZIP file entry and positions stream at the beginning
     * of the entry data.
     * @exception ZipException if a ZIP file error has occurred
     * @exception IOException if an I/O error has occurred
     */
    public ZipEntry getNextEntry() throws IOException {
	if (entry != null) {
	    closeEntry();
	}
	crc.reset();
	inf.reset();
	if ((entry = readLOC()) == null) {
	    return null;
	}
	if (entry.method == STORED) {
	    remaining = entry.size;
	}
	return entry;
    }

    /**
     * Closes the current ZIP entry and positions the stream for reading the
     * next entry.
     * @exception ZipException if a ZIP file error has occurred
     * @exception IOException if an I/O error has occurred
     */
    public void closeEntry() throws IOException {
	while (read(tmpbuf, 0, tmpbuf.length) != -1) ;
    }
    
    /**
     * Reads from the current ZIP entry into an array of bytes. Blocks until
     * some input is available.
     * @param b the buffer into which the data is read
     * @param off the start offset of the data
     * @param len the maximum number of bytes read
     * @return the actual number of bytes read, or -1 if the end of the
     *         entry is reached
     * @exception ZipException if a ZIP file error has occurred
     * @exception IOException if an I/O error has occurred
     */
    public int read(byte[] b, int off, int len) throws IOException {
	if (entry == null) {
	    return -1;
	}
	switch (entry.method) {
	case DEFLATED:
	    len = super.read(b, off, len);
	    if (len == -1) {
		readEnd(entry);
		entry = null;
	    } else {
		crc.update(b, off, len);
	    }
	    return len;
	case STORED:
	    if (remaining <= 0) {
		entry = null;
		return -1;
	    }
	    if (len > remaining) {
		len = (int)remaining;
	    }
	    len = in.read(b, off, len);
	    if (len == -1) {
		throw new ZipException("unexpected EOF");
	    }
	    crc.update(b, off, len);
	    remaining -= len;
	    return len;
	default:
	    throw new InternalError("invalid compression method");
	}
    }

    /**
     * Skips specified number of bytes in the current ZIP entry.
     * @param n the number of bytes to skip
     * @return the actual number of bytes skipped
     * @exception ZipException if a ZIP file error has occurred
     * @exception IOException if an I/O error has occurred
     */
    public long skip(long n) throws IOException {
	if (n <= 0) {
	    return 0;
	}
	n = Math.min(n, Integer.MAX_VALUE);
	int total = 0;
	while (total < n) {
	    int len = read(tmpbuf, 0, (int)n - total);
	    if (len == -1) {
		break;
	    }
	    total += len;
	}
	return total;
    }

    /**
     * Closes the ZIP input stream.
     * @exception IOException if an I/O error has occurred
     */
    public void close() throws IOException {
	in.close();
    }

    /*
     * Reads local file (LOC) header for next entry.
     */
    private ZipEntry readLOC() throws IOException {
	try {
	    readFully(tmpbuf, 0, LOCHDR);
	} catch (EOFException e) {
	    return null;
	}
	if (get32(tmpbuf, 0) != LOCSIG) {
	    return null;
	}
	ZipEntry e = new ZipEntry();
	e.version = get16(tmpbuf, LOCVER);
	e.flag = get16(tmpbuf, LOCFLG);
	if ((e.flag & 1) == 1) {
	    throw new ZipException("encrypted ZIP entry not supported");
	}
	e.method = get16(tmpbuf, LOCHOW);
	e.time = get32(tmpbuf, LOCTIM);
	if ((e.flag & 8) == 8) {
	    /* EXT descriptor present */
	    if (e.method != DEFLATED) {
		throw new ZipException(
			"only DEFLATED entries can have EXT descriptor");
	    }
	} else {
	    e.crc = get32(tmpbuf, LOCCRC);
	    e.csize = get32(tmpbuf, LOCSIZ);
	    e.size = get32(tmpbuf, LOCLEN);
	}
	int len = get16(tmpbuf, LOCNAM);
	if (len == 0) {
	    throw new ZipException("missing entry name");
	}
	byte[] b = new byte[len];
	readFully(b, 0, len);
	e.name = new String(b, 0, 0, len);
	len = get16(tmpbuf, LOCEXT);
	if (len > 0) {
	    b = new byte[len];
	    readFully(b, 0, len);
	    e.extra = b;
	}
	return e;
    }

    /*
     * Reads end of deflated entry as well as EXT descriptor if present.
     */
    private void readEnd(ZipEntry e) throws IOException {
	int n = inf.getRemaining();
	if (n > 0) {
	    ((PushbackInputStream)in).unread(buf, len - n, n);
	}
	if ((e.flag & 8) == 8) {
	    /* EXT descriptor present */
	    readFully(tmpbuf, 0, EXTHDR);
	    long sig = get32(tmpbuf, 0);
	    if (sig != EXTSIG) {
		throw new ZipException("invalid EXT descriptor signature");
	    }
	    e.crc = get32(tmpbuf, EXTCRC);
	    e.csize = get32(tmpbuf, EXTSIZ);
	    e.size = get32(tmpbuf, EXTLEN);
	}
	if (e.size != inf.getTotalOut()) {
	    throw new ZipException(
		"invalid entry size (expected " + e.size + " but got " +
		inf.getTotalOut() + " bytes)");
	}
	if (e.csize != inf.getTotalIn()) {
	    throw new ZipException(
		"invalid entry compressed size (expected " + e.csize +
		" but got " + inf.getTotalIn() + " bytes)");
	}
	if (e.crc != crc.getValue()) {
	    throw new ZipException(
		"invalid entry CRC (expected 0x" + Long.toHexString(e.crc) +
		" but got 0x" + Long.toHexString(crc.getValue()) + ")");
	}
    }

    /*
     * Reads bytes, blocking until all bytes are read.
     */
    private void readFully(byte[] b, int off, int len) throws IOException {
	while (len > 0) {
	    int n = in.read(b, off, len);
	    if (n == -1) {
		throw new EOFException();
	    }
	    off += n;
	    len -= n;
	}
    }

    /*
     * Fetches unsigned 16-bit value from byte array at specified offset.
     * The bytes are assumed to be in Intel (little-endian) byte order.
     */
    private static final int get16(byte b[], int off) {
	return (b[off] & 0xff) | ((b[off+1] & 0xff) << 8);
    }

    /*
     * Fetches unsigned 32-bit value from byte array at specified offset.
     * The bytes are assumed to be in Intel (little-endian) byte order.
     */
    private static final long get32(byte b[], int off) {
	return get16(b, off) | ((long)get16(b, off+2) << 16);
    }
}
