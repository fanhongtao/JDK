/*
 * @(#)ZipFile.java	1.18 97/01/24
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

import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Enumeration;

/**
 * This class can be used to read the contents of a ZIP file. It uses
 * RandomAccessFile for quick access to ZIP file entries, and supports
 * both compressed and uncompressed entries.

 * @version	1.18, 01/24/97
 * @author	David Connelly
 */
public
class ZipFile implements ZipConstants {
    RandomAccessFile raf;
    private String name;
    private Hashtable entries;
    long cenpos;
    private long endpos;
    long pos;

    private static final int STORED = ZipEntry.STORED;
    private static final int DEFLATED = ZipEntry.DEFLATED;

    /**
     * Opens a ZIP file for reading given the specified file name.
     * @param name the name of the zip file
     * @exception ZipException if a ZIP format error has occurred
     * @exception IOException if an I/O error has occurred
     */
    public ZipFile(String name) throws IOException {
	raf = new RandomAccessFile(name, "r");
	this.name = name;
	readCEN();
    }

    /**
     * Opens a ZIP file for reading given the specified File object.
     * @param file the ZIP file to be opened for reading
     * @exception ZipException if a ZIP error has occurred
     * @exception IOException if an I/O error has occurred
     */
    public ZipFile(File file) throws ZipException, IOException {
	this(file.getPath());
    }

    /**
     * Returns the ZIP file entry for the given path name. Returns null if
     * there is no entry corresponding to the given name.
     * @param name the name of the entry
     * @return the ZIP file entry
     */
    public ZipEntry getEntry(String name) {
	return (ZipEntry)entries.get(name);
    }

    /**
     * Returns an input stream for reading the contents of the specified
     * ZIP file entry.
     * @param ze the zip file entry
     * @exception ZipException if a ZIP format error has occurred
     * @exception IOException if an I/O error has occurred
     */
    public InputStream getInputStream(ZipEntry ze) throws IOException {
	InputStream in = new ZipFileInputStream(this, ze);
	switch (ze.method) {
	case STORED:
	    return in;
	case DEFLATED:
	    return new InflaterInputStream(in, new Inflater(true));
	default:
	    throw new ZipException("invalid compression method");
	}
    }

    /**
     * Returns the path name of the ZIP file.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns an enumeration of the ZIP file entries.
     */
    public Enumeration entries() {
	return entries.elements();
    }

    /**
     * Closes the ZIP file.
     */
    public void close() throws IOException {
	if (raf != null) {
	    raf.close();
	    raf = null;
	}
    }

    /*
     * Reads data at specified file position into an array of bytes.
     * This method will block until some input is available.
     */
    synchronized int read(long pos, byte b[], int off, int len)
	throws IOException
    {
        if (pos != this.pos) {
	    raf.seek(pos);
	}
	int n = raf.read(b, off, len);
	if (n > 0) {
	    this.pos = pos + n;
	}
	return n;
    }

    /*
     * Reads a byte of data at the specified file position. This method
     * will block until some input is available.
     */
    synchronized int read(long pos) throws IOException {
	if (pos != this.pos) {
	    raf.seek(pos);
	}
	int n = raf.read();
	if (n > 0) {
	    this.pos = pos + 1;
	}
	return n;
    }

    /*
     * Read contents of central directory (CEN) and build hash table of
     * ZIP file entries.
     */
    private void readCEN() throws IOException {
	// Find and seek to beginning of END header
	findEND();
	// Read END header and check signature
	byte[] endbuf = new byte[ENDHDR];
	raf.readFully(endbuf);
	if (get32(endbuf, 0) != ENDSIG) {
	    throw new ZipException("invalid END header signature"); 
	}
	// Get position and length of central directory
	cenpos = get32(endbuf, ENDOFF);
	int cenlen = (int)get32(endbuf, ENDSIZ);
	if (cenpos + cenlen != endpos) {
	    throw new ZipException("invalid END header format");
	}
	// Get total number of entries
	int nent = get16(endbuf, ENDTOT);
	if (nent * CENHDR > cenlen) {
	    throw new ZipException("invalid END header format");
	}
	// Check number of drives
	if (get16(endbuf, ENDSUB) != nent) {
	    throw new ZipException("cannot have more than one drive");
	}
	// Seek to first CEN record and read central directory
	raf.seek(cenpos);
	byte cenbuf[] = new byte[cenlen];
	raf.readFully(cenbuf);
	// Scan entries in central directory and build lookup table.
	entries = new Hashtable(nent);
	for (int off = 0; off < cenlen; ) {
	    // Check CEN header signature
	    if (get32(cenbuf, off) != CENSIG) {
		throw new ZipException("invalid CEN header signature");
	    }
	    ZipEntry e = new ZipEntry();
	    e.version = get16(cenbuf, off + CENVER);
	    e.flag = get16(cenbuf, off + CENFLG);
	    e.method = get16(cenbuf, off + CENHOW);
	    e.time = get32(cenbuf, off + CENTIM);
	    e.crc = get32(cenbuf, off + CENCRC);
	    e.size = get32(cenbuf, off + CENLEN);
	    e.csize = get32(cenbuf, off + CENSIZ);
	    e.offset = get32(cenbuf, off + CENOFF);
	    if (e.offset + e.csize > cenpos) {
		throw new ZipException("invalid CEN entry size");
	    }
	    int baseoff = off;
	    off += CENHDR;
	    // Get path name of entry
	    int len = get16(cenbuf, baseoff + CENNAM);
	    if (len == 0 || off + len > cenlen) {
		throw new ZipException("invalid CEN entry name");
	    }
	    e.name = new String(cenbuf, 0, off, len);
	    off += len;
	    // Get extra field data
	    len = get16(cenbuf, baseoff + CENEXT);
	    if (len > 0) {
		if (off + len > cenlen) {
		    throw new ZipException("invalid CEN entry extra data");
		}
		e.extra = new byte[len];
		System.arraycopy(cenbuf, off, e.extra, 0, len);
		off += len;
	    }
	    // Get entry comment
	    len = get16(cenbuf, baseoff + CENCOM);
	    if (len > 0) {
		if (off + len > cenlen) {
		    throw new ZipException("invalid CEN entry comment");
		}
		e.comment = new String(cenbuf, 0, off, len);
		off += len;
	    }
	    // Add entry to the hash table of entries
	    entries.put(e.name, e);
	}
	// Make sure we got the right number of entries
	if (entries.size() != nent) {
	    throw new ZipException("invalid CEN header format");
	}
    }

    private static final int INBUFSIZ = 64;

    /*
     * Find end of central directory (END) header.
     */
    private void findEND() throws IOException {
	// Start searching backwards from end of file
	long len = raf.length();
	raf.seek(len);
	// Set limit on how far back we need to search. The END header
	// must be located within the last 64K bytes of the raf.
	long markpos = Math.max(0, len - 0xffff);
	// Search backwards INBUFSIZ bytes at a time from end of file
	// stopping when the END header signature has been found. Since
	// the signature may straddle a buffer boundary, we need to stash
	// the first 4-1 bytes of the previous record at the end of
	// the current record so that the search may overlap.
	byte buf[] = new byte[INBUFSIZ + 4];
	for (pos = len; pos > markpos; ) {
	    int n = Math.min((int)(pos - markpos), INBUFSIZ);
	    pos -= n;
	    raf.seek(pos);
	    raf.readFully(buf, 0, n);
	    while (--n > 0) {
		if (get32(buf, n) == ENDSIG) {
		    // Could be END header, but we need to make sure that
		    // the record extends to the end of the raf.
		    endpos = pos + n;
		    if (len - endpos < ENDHDR) {
			continue;
		    }
		    raf.seek(endpos);
		    byte endbuf[] = new byte[ENDHDR];
		    raf.readFully(endbuf);
		    int comlen = get16(endbuf, ENDCOM);
		    if (endpos + ENDHDR + comlen != len) {
			continue;
		    }
		    // This is definitely the END record, so position
		    // the file pointer at the header and return.
		    raf.seek(endpos);
		    pos = endpos;
		    return;
		}
	    }
	}
	throw new ZipException("not a ZIP file (END header not found)");
    }

    /*
     * Fetch unsigned 16-bit value from byte array at specified offset.
     * The bytes are assumed to be in Intel (little-endian) byte order.
     */
    static final int get16(byte b[], int off) {
	return (b[off] & 0xff) | ((b[off+1] & 0xff) << 8);
    }

    /*
     * Fetch unsigned 32-bit value from byte array at specified offset.
     * The bytes are assumed to be in Intel (little-endian) byte order.
     */
    static final long get32(byte b[], int off) {
	return get16(b, off) | ((long)get16(b, off+2) << 16);
    }
}

class ZipFileInputStream extends InputStream implements ZipConstants {
    private ZipFile zf;
    private ZipEntry ze;
    private long pos;
    private long count;

    /*
     * Creates an input stream for reading the specified ZIP file entries
     * raw data.
     */
    ZipFileInputStream(ZipFile zf, ZipEntry ze) throws IOException {
	this.zf = zf;
	this.ze = ze;
	readLOC();
    }

    /**
     * Returns number of bytes available for reading.
     */
    public int available() {
	return (int)Math.min(count, Integer.MAX_VALUE);
    }

    /**
     * Reads ZIP file entry into an array of bytes. This method will
     * block until some input is available.
     * @param b the buffer into which the data is read
     * @param off the start offset of the data
     * @param len the maximum number of bytes to read
     * @return the actual number of bytes read, or -1 if the end of
     * 	       the stream has been reached.
     * @exception ZipException if a ZIP format error has occurred
     * @exception IOException if an I/O error has occurred
     */
    public int read(byte b[], int off, int len) throws IOException {
        if (count == 0) {
	    return -1;
	}
	if (len > count) {
	    len = (int)Math.min(count, Integer.MAX_VALUE);
	}
	len = zf.read(pos, b, off, len);
	if (len == -1) {
	    throw new ZipException("premature EOF");
	}
	pos += len;
	count -= len;
	return len;
    }

    /**
     * Reads a byte of data. This method will block until some input
     * is available.
     * @return the byte read, or -1 if the end of the stream has been
     *	       reached.
     * @exception ZipException if a ZIP format error has occurred
     * @exception IOException if an I/O error has occurred
     */
    public int read() throws IOException {
	if (count == 0) {
	    return -1;
	}
	int n = zf.read(pos);
	if (n == -1) {
	    throw new ZipException("premature EOF");
	}
	pos += 1;
	count -= 1;
	return n;
    }

    /**
     * Skips n bytes of input.
     * @param n	the number of bytes to skip
     * @return the actual number of bytes skipped
     */
    public long skip(long n) {
	if (n > count) {
	    n = count;
	}
	pos += n;
	count -= n;
	return n;
    }

    /*
     * Read and verify LOC header, and position input stream at beginning of
     * entry data.
     */
    private void readLOC() throws IOException {
	// Read LOC header and check signature
	byte locbuf[] = new byte[LOCHDR];
	zf.read(ze.offset, locbuf, 0, LOCHDR);
	if (zf.get32(locbuf, 0) != LOCSIG) {
	    throw new ZipException("invalid LOC header signature");
	}
	// Get length and position of entry data
	count = ze.csize;
	pos = ze.offset + LOCHDR + ZipFile.get16(locbuf, LOCNAM) +
				   ZipFile.get16(locbuf, LOCEXT);
	if (pos + count > zf.cenpos) {
	    throw new ZipException("invalid LOC header format");
	}
    }
}
