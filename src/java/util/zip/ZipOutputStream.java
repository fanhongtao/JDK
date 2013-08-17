/*
 * @(#)ZipOutputStream.java	1.13 97/01/27
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

import java.io.OutputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

/**
 * This class implements an output stream filter for writing files in the
 * ZIP file format. Includes support for both compressed and uncompressed
 * entries.
 *
 * @author	David Connelly
 * @version	1.13, 01/27/97
 */
public
class ZipOutputStream extends DeflaterOutputStream implements ZipConstants {
    private ZipEntry entry;
    private Vector entries = new Vector();
    private Hashtable names = new Hashtable();
    private CRC32 crc = new CRC32();
    private long written;
    private long locoff = 0;
    private String comment;
    private int method = DEFLATED;
    private boolean finished;

    /**
     * Compression method for uncompressed (STORED) entries.
     */
    public static final int STORED = ZipEntry.STORED;

    /**
     * Compression method for compressed (DEFLATED) entries.
     */
    public static final int DEFLATED = ZipEntry.DEFLATED;

    /**
     * Creates a new ZIP output stream.
     * @param out the actual output stream 
     */
    public ZipOutputStream(OutputStream out) {
	super(out, new Deflater(Deflater.DEFAULT_COMPRESSION, true));
    }

    /**
     * Sets the ZIP file comment.
     * @param comment the comment string
     * @exception IllegalArgumentException if the length of the specified
     *		  ZIP file comment is greater than 0xFFFF bytes
     */
    public void setComment(String comment) {
	if (comment.length() > 0xffff) {
	    throw new IllegalArgumentException("invalid ZIP file comment");
	}
	this.comment = comment;
    }

    /**
     * Sets the default compression method for subsequent entries. This
     * default will be used whenever the compression method is not specified
     * for an individual ZIP file entry, and is initially set to DEFLATED.
     * @param method the default compression method
     * @exception IllegalArgumentException if the specified compression method
     *		  is invalid
     */
    public void setMethod(int method) {
	if (method != DEFLATED && method != STORED) {
	    throw new IllegalArgumentException("invalid compression method");
	}
	this.method = method;
    }

    /**
     * Sets the compression level for subsequent entries which are DEFLATED.
     * The default setting is DEFAULT_COMPRESSION.
     * @param level the compression level (0-9)
     * @exception IllegalArgumentException if the compression level is invalid
     */
    public void setLevel(int level) {
	def.setLevel(level);
    }

    /**
     * Begins writing a new ZIP file entry and positions the stream to the
     * start of the entry data. Closes the current entry if still active.
     * The default compression method will be used if no compression method
     * was specified for the entry, and the current time will be used if
     * the entry has no set modification time.
     * @param e the ZIP entry to be written
     * @exception ZipException if a ZIP format error has occurred
     * @exception IOException if an I/O error has occurred
     */
    public void putNextEntry(ZipEntry e) throws IOException {
	if (entry != null) {
	    closeEntry();	// close previous entry
	}
	if (e.time == -1) {
	    e.setTime(System.currentTimeMillis());
	}
	if (e.method == -1) {
	    e.method = method;	// use default method
	}
	switch (e.method) {
	case DEFLATED:
	    if (e.size == -1 || e.csize == -1 || e.crc == -1) {
		// store size, compressed size, and crc-32 in data descriptor
		// immediately following the compressed entry data
		e.flag = 8;
	    } else if (e.size != -1 && e.csize != -1 && e.crc != -1) {
		// store size, compressed size, and crc-32 in LOC header
		e.flag = 0;
	    } else {
		throw new ZipException(
		    "DEFLATED entry missing size, compressed size, or crc-32");
	    }
	    e.version = 20;
	    break;
	case STORED:
	    // compressed size, uncompressed size, and crc-32 must all be
	    // set for entries using STORED compression method
	    if (e.size == -1) {
		e.size = e.csize;
	    } else if (e.csize == -1) {
		e.csize = e.size;
	    } else if (e.size != e.csize) {
		throw new ZipException(
		    "STORED entry where compressed != uncompressed size");
	    }
	    if (e.size == -1 || e.crc == -1) {
		throw new ZipException(
		    "STORED entry missing size, compressed size, or crc-32");
	    }
	    e.version = 10;
	    e.flag = 0;
	    break;
	default:
	    throw new ZipException("unsupported compression method");
	}
	e.offset = written;
	writeLOC(e);
	if (names.put(e.name, e) != null) {
	    throw new ZipException("duplicate entry: " + e.name);
	}
	entries.addElement(e);
	entry = e;
    }

    /**
     * Closes the current ZIP entry and positions the stream for writing
     * the next entry.
     * @exception ZipException if a ZIP format error has occurred
     * @exception IOException if an I/O error has occurred
     */
    public void closeEntry() throws IOException {
	ZipEntry e = entry;
	if (e != null) {
	    switch (e.method) {
	    case DEFLATED:
		def.finish();
		while (!def.finished()) {
		    deflate();
		}
		if ((e.flag & 8) == 0) {
		    // verify size, compressed size, and crc-32 settings
		    if (e.size != def.getTotalIn()) {
			throw new ZipException(
			    "invalid entry size (expected " + e.size +
			    " but got " + def.getTotalIn() + " bytes)");
		    }
		    if (e.csize != def.getTotalOut()) {
			throw new ZipException(
			    "invalid entry compressed size (expected " +
			    e.csize + " but got " + def.getTotalOut() +
			    " bytes)");
		    }
		    if (e.crc != crc.getValue()) {
			throw new ZipException(
			    "invalid entry CRC-32 (expected 0x" +
			    Long.toHexString(e.crc) + " but got 0x" +
			    Long.toHexString(crc.getValue()) + ")");
		    }
		} else {
		    e.size = def.getTotalIn();
		    e.csize = def.getTotalOut();
		    e.crc = crc.getValue();
		    writeEXT(e);
		}
		def.reset();
		written += e.csize;
		break;
	    case STORED:
		// we already know that both e.size and e.csize are the same
		if (e.size != written - locoff) {
		    throw new ZipException(
			"invalid entry size (expected " + e.size +
			" but got " + (written - locoff) + " bytes)");
		}
		if (e.crc != crc.getValue()) {
		    throw new ZipException(
			 "invalid entry crc-32 (expected 0x" +
			 Long.toHexString(e.size) + " but got 0x" +
			 Long.toHexString(crc.getValue()) + ")");
		}
		break;
	    default:
		throw new InternalError("invalid compression method");
	    }
	    crc.reset();
	    entry = null;
	}
    }

    /**
     * Writes an array of bytes to the current ZIP entry data. This method
     * will block until all the bytes are written.
     * @param b the data to be written
     * @param off the start offset in the data
     * @param len the number of bytes that are written
     * @exception ZipException if a ZIP file error has occurred
     * @exception IOException if an I/O error has occurred
     */
    public synchronized void write(byte[] b, int off, int len)
	throws IOException
    {
	if (entry == null) {
	    throw new ZipException("no current ZIP entry");
	}
	switch (entry.method) {
	case DEFLATED:
	    super.write(b, off, len);
	    break;
	case STORED:
	    written += len;
	    if (written - locoff > entry.size) {
		throw new ZipException(
		    "attempt to write past end of STORED entry");
	    }
	    out.write(b, off, len);
	    break;
	default:
	    throw new InternalError("invalid compression method");
	}
	crc.update(b, off, len);
    }

    /**
     * Finishes writing the contents of the ZIP output stream without closing
     * the underlying stream. Use this method when applying multiple filters
     * in succession to the same output stream.
     * @exception ZipException if a ZIP file error has occurred
     * @exception IOException if an I/O exception has occurred
     */
    public void finish() throws IOException {
	if (finished) {
	    return;
	}
	if (entry != null) {
	    closeEntry();
	}
	if (entries.size() < 1) {
	    throw new ZipException("ZIP file must have at least one entry");
	}
	// write central directory
	long off = written;
	Enumeration e = entries.elements();
	while (e.hasMoreElements()) {
	    writeCEN((ZipEntry)e.nextElement());
	}
	writeEND(off, written - off);
	finished = true;
    }

    /**
     * Closes the ZIP output stream as well as the stream being filtered.
     * @exception ZipException if a ZIP file error has occurred
     * @exception IOException if an I/O error has occurred
     */
    public void close() throws IOException {
	finish();
	out.close();
    }

    /*
     * Writes local file (LOC) header for specified entry.
     */
    private void writeLOC(ZipEntry e) throws IOException {
	writeInt(LOCSIG);	    // LOC header signature
	writeShort(e.version);      // version needed to extract
	writeShort(e.flag);         // general purpose bit flag
	writeShort(e.method);       // compression method
	writeInt(e.time);           // last modification time
	if ((e.flag & 8) == 8) {
	    // store size, uncompressed size, and crc-32 in data descriptor
	    // immediately following compressed entry data
	    writeInt(0);
	    writeInt(0);
	    writeInt(0);
	} else {
	    writeInt(e.crc);        // crc-32
	    writeInt(e.csize);      // compressed size
	    writeInt(e.size);       // uncompressed size
	}
	writeShort(e.name.length());
	writeShort(e.extra != null ? e.extra.length : 0);
	writeAscii(e.name);
	if (e.extra != null) {
	    writeBytes(e.extra, 0, e.extra.length);
	}
	locoff = written;
    }

    /*
     * Writes extra data descriptor (EXT) for specified entry.
     */
    private void writeEXT(ZipEntry e) throws IOException {
	writeInt(EXTSIG);	    // EXT header signature
	writeInt(e.crc);	    // crc-32
	writeInt(e.csize);	    // compressed size
	writeInt(e.size);	    // uncompressed size
    }

    /*
     * Write central directory (CEN) header for specified entry.
     * REMIND: add support for file attributes
     */
    private void writeCEN(ZipEntry e) throws IOException {
	writeInt(CENSIG);	    // CEN header signature
	writeShort(e.version);	    // version made by
	writeShort(e.version);	    // version needed to extract
	writeShort(e.flag);	    // general purpose bit flag
	writeShort(e.method);	    // compression method
	writeInt(e.time);	    // last modification time
	writeInt(e.crc);	    // crc-32
	writeInt(e.csize);	    // compressed size
	writeInt(e.size);	    // uncompressed size
	writeShort(e.name.length());
	writeShort(e.extra != null ? e.extra.length : 0);
	writeShort(e.comment != null ? e.comment.length() : 0);
	writeShort(0);		    // starting disk number
	writeShort(0);		    // internal file attributes (unused)
	writeInt(0);		    // external file attributes (unused)
	writeInt(e.offset);	    // relative offset of local header
	writeAscii(e.name);
	if (e.extra != null) {
	    writeBytes(e.extra, 0, e.extra.length);
	}
	if (e.comment != null) {
	    writeAscii(e.comment);
	}
    }

    /*
     * Writes end of central directory (END) header.
     */
    private void writeEND(long off, long len) throws IOException {
	writeInt(ENDSIG);	    // END record signature
	writeShort(0);		    // number of this disk
	writeShort(0);		    // central directory start disk
	writeShort(entries.size()); // number of directory entries on disk
	writeShort(entries.size()); // total number of directory entries
	writeInt(len);		    // length of central directory
	writeInt(off);		    // offset of central directory
	writeShort(comment != null ? comment.length() : 0);
	if (comment != null) {
	    writeAscii(comment);    // ZIP file comment
	}
    }

    /*
     * Writes a 16-bit short to the output stream in little-endian byte order.
     */
    private void writeShort(int v) throws IOException {
	OutputStream out = this.out;
	out.write((v >>> 0) & 0xff);
	out.write((v >>> 8) & 0xff);
	written += 2;
    }

    /*
     * Writes a 32-bit int to the output stream in little-endian byte order.
     */
    private void writeInt(long v) throws IOException {
	OutputStream out = this.out;
	out.write((int)((v >>>  0) & 0xff));
	out.write((int)((v >>>  8) & 0xff));
	out.write((int)((v >>> 16) & 0xff));
	out.write((int)((v >>> 24) & 0xff));
	written += 4;
    }

    /*
     * Writes an ASCII string to the output stream.
     */
    private void writeAscii(String s) throws IOException {
	OutputStream out = this.out;
	byte[] b = new byte[s.length()];
	s.getBytes(0, b.length, b, 0);
	writeBytes(b, 0, b.length);
    }

    /*
     * Writes an array of bytes to the output stream.
     */
    private void writeBytes(byte[] b, int off, int len) throws IOException {
	super.out.write(b, off, len);
	written += len;
    }
}
