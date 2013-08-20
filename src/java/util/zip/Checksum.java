/*
 * @(#)Checksum.java	1.16 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.util.zip;

/**
 * An interface representing a data checksum.
 *
 * @version 	1.16, 12/19/03
 * @author 	David Connelly
 */
public
interface Checksum {
    /**
     * Updates the current checksum with the specified byte.
     *
     * @param b the byte to update the checksum with
     */
    public void update(int b);

    /**
     * Updates the current checksum with the specified array of bytes.
     * @param b the byte array to update the checksum with
     * @param off the start offset of the data
     * @param len the number of bytes to use for the update
     */
    public void update(byte[] b, int off, int len);

    /**
     * Returns the current checksum value.
     * @return the current checksum value
     */
    public long getValue();

    /**
     * Resets the checksum to its initial value.
     */
    public void reset();
}
