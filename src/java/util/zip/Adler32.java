/*
 * @(#)Adler32.java	1.11 97/01/27
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
 * A class that can be used to compute the Adler-32 checksum of a data
 * stream. An Adler-32 checksum is almost as reliable as a CRC-32 but
 * can be computed much faster.
 *
 * @see		Checksum
 * @version 	1.11, 01/27/97
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
