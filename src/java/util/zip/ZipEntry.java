/*
 * @(#)ZipEntry.java	1.18 97/01/24
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

import java.util.Date;

/**
 * This class is used to represent a ZIP file entry.
 *
 * @version	1.18, 01/24/97
 * @author	David Connelly
 */
public
class ZipEntry implements ZipConstants {
    String name;	// entry name
    long time = -1;	// modification time (in DOS time)
    long crc = -1;	// crc-32 of entry data
    long size = -1;	// uncompressed size of entry data
    int method = -1;	// compression method
    byte[] extra;	// optional extra field data for entry
    String comment;	// optional comment string for entry
    int flag;		// general purpose bit flag
    int version;	// version of ZIP this entry was made by
    long csize = -1;   	// compressed size of entry data
    long offset;	// offset of LOC header from beginning of ZIP file

    /**
     * Compression method for uncompressed entries.
     */
    public static final int STORED = 0;

    /**
     * Compression method for compressed (deflated) entries.
     */
    public static final int DEFLATED = 8;

    /**
     * Creates a new ZIP file entry with the specified name.
     * @param name the entry name
     * @exception NullPointerException if the entry name is null
     * @exception IllegalArgumentException if the entry name is longer than
     *		  0xFFFF bytes
     */
    public ZipEntry(String name) {
	if (name == null) {
	    throw new NullPointerException();
	}
	if (name.length() > 0xFFFF) {
	    throw new IllegalArgumentException("entry name too long");
	}
	this.name = name;
    }

    /*
     * Creates a new ZIP file entry with no name.
     */
    ZipEntry() {
    }
    
    /**
     * Returns the name of the entry.
     */
    public String getName() {
	return name;
    }

    /**
     * Sets the modification time of the entry.
     * @param time the entry modification time in number of milliseconds
     *		   since the epoch
     */
    public void setTime(long time) {
	this.time = javaToDosTime(time);
    }

    /**
     * Returns the modification time of the entry, or -1 if not specified.
     */
    public long getTime() {
	return dosToJavaTime(time);
    }

    /**
     * Sets the uncompressed size of the entry data.
     * @param size the uncompressed size in bytes
     * @exception IllegalArgumentException if the specified size is less
     *		  than 0 or greater than 0xFFFFFFFF bytes
     */
    public void setSize(long size) {
	if (size < 0 || size > 0xFFFFFFFFL) {
	    throw new IllegalArgumentException("invalid entry size");
	}
	this.size = size;
    }

    /**
     * Returns the uncompressed size of the entry data, or -1 if not known.
     */
    public long getSize() {
	return size;
    }

    /**
     * Sets the CRC-32 checksum of the uncompressed entry data.
     * @param crc the CRC-32 value
     * @exception IllegalArgumentException if the specified CRC-32 value is
     *		  less than 0 or greater than 0xFFFFFFFF
     */
    public void setCrc(long crc) {
	if (crc < 0 || crc > 0xFFFFFFFFL) {
	    throw new IllegalArgumentException("invalid entry crc-32");
	}
	this.crc = crc;
    }

    /**
     * Returns the CRC-32 checksum of the uncompressed entry data, or -1 if
     * not known.
     */
    public long getCrc() {
	return crc;
    }

    /**
     * Sets the compression method for the entry.
     * @param method the compression method, either STORED or DEFLATED
     * @exception IllegalArgumentException if the specified compression
     *		  method is invalid
     */
    public void setMethod(int method) {
	if (method != STORED && method != DEFLATED) {
	    throw new IllegalArgumentException("invalid compression method");
	}
	this.method = method;
    }

    /**
     * Returns the compression method of the entry, or -1 if not specified.
     */
    public int getMethod() {
	return method;
    }

    /**
     * Sets the optional extra field data for the entry.
     * @param extra the extra field data bytes
     * @exception IllegalArgumentException if the length of the specified
     *		  extra field data is greater than 0xFFFFF bytes
     */
    public void setExtra(byte[] extra) {
	if (extra != null && extra.length > 0xFFFF) {
	    throw new IllegalArgumentException("invalid extra field length");
	}
	this.extra = extra;
    }

    /**
     * Returns the extra field data for the entry, or null if none.
     */
    public byte[] getExtra() {
	return extra;
    }

    /**
     * Sets the optional comment string for the entry.
     * @param comment the comment string
     * @exception IllegalArgumentException if the length of the specified
     *		  comment string is greater than 0xFFFF bytes
     */
    public void setComment(String comment) {
	if (comment != null && comment.length() > 0xFFFF) {
	    throw new IllegalArgumentException("invalid entry comment length");
	}
	this.comment = comment;
    }
  
    /**
     * Returns the comment string for the entry, or null if none.
     */
    public String getComment() {
	return comment;
    }

    /**
     * Returns the compressed size of the entry data, or -1 if not known.
     * In the case of a stored entry, the compressed size will be the same
     * as the uncompressed size of the entry.
     */
    public long getCompressedSize() {
	return csize;
    }

    /**
     * Returns true if this is a directory entry. A directory entry is
     * defined to be one whose name ends with a '/'.
     */
    public boolean isDirectory() {
	return name.endsWith("/");
    }

    /**
     * Returns a string representation of the ZIP entry.
     */
    public String toString() {
	return getName();
    }

    /*
     * Converts DOS time to Java time (number of milliseconds since epoch).
     */
    private static long dosToJavaTime(long dtime) {
	Date d = new Date((int)(((dtime >> 25) & 0x7f) + 80),
			  (int)(((dtime >> 21) & 0x0f) - 1),
			  (int)((dtime >> 16) & 0x1f),
			  (int)((dtime >> 11) & 0x1f),
			  (int)((dtime >> 5) & 0x3f),
			  (int)((dtime << 1) & 0x3e));
	return d.getTime();
    }

    /*
     * Converts Java time to DOS time.
     */
    private static long javaToDosTime(long time) {
	Date d = new Date(time);
	int year = d.getYear() + 1900;
	if (year < 1980) {
	    return (1 << 21) | (1 << 16);
	}
	return (year - 1980) << 25 | (d.getMonth() + 1) << 21 |
               d.getDate() << 16 | d.getHours() << 11 | d.getMinutes() << 5 |
               d.getSeconds() >> 1;
    }
}
