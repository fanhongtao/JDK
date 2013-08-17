/*
 * @(#)CRC32.java	1.11 97/01/27
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
 * A class that can be used to compute the CRC-32 of a data stream.
 *
 * @see		Checksum
 * @version 	1.11, 01/27/97
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
