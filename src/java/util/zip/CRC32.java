/*
 * @(#)CRC32.java	1.12 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.util.zip;

/**
 * A class that can be used to compute the CRC-32 of a data stream.
 *
 * @see		Checksum
 * @version 	1.12, 12/10/01
 * @author 	David Connelly
 */
public
class CRC32 implements Checksum {
    private int crc;

    /*
     * Loads the ZLIB library.
     */
    static {
	System.loadLibrary("zip");
    }

    /**
     * Updates CRC-32 with specified byte.
     */
    public void update(int b) {
	update1(b);
    }

    /**
     * Updates CRC-32 with specified array of bytes.
     */
    public native void update(byte[] b, int off, int len);

    /**
     * Updates CRC-32 with specified array of bytes.
     */
    public void update(byte[] b) {
	update(b, 0, b.length);
    }

    /**
     * Resets CRC-32 to initial value.
     */
    public void reset() {
	crc = 0;
    }

    /**
     * Returns CRC-32 value.
     */
    public long getValue() {
	return (long)crc & 0xffffffffL;
    }

    private native void update1(int b);
}
