/*
 * @(#)Adler32.java	1.12 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.util.zip;

/**
 * A class that can be used to compute the Adler-32 checksum of a data
 * stream. An Adler-32 checksum is almost as reliable as a CRC-32 but
 * can be computed much faster.
 *
 * @see		Checksum
 * @version 	1.12, 12/10/01
 * @author 	David Connelly
 */
public
class Adler32 implements Checksum {
    private int adler = 1;

    /*
     * Loads the ZLIB library.
     */
    static {
	System.loadLibrary("zip");
    }

    /**
     * Updates checksum with specified byte.
     */
    public void update(int b) {
	update1(b);
    }

    /**
     * Updates checksum with specified array of bytes.
     */
    public native void update(byte[] b, int off, int len);

    /**
     * Updates checksum with specified array of bytes.
     */
    public void update(byte[] b) {
	update(b, 0, b.length);
    }

    /**
     * Resets checksum to initial value.
     */
    public void reset() {
	adler = 1;
    }

    /**
     * Returns checksum value.
     */
    public long getValue() {
	return (long)adler & 0xffffffffL;
    }

    private native void update1(int b);
}
