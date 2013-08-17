/*
 * @(#)Checksum.java	1.2 00/01/12
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.util.zip;

/**
 * An interface representing a data checksum.
 *
 * @version 	1.10, 09/21/98
 * @author 	David Connelly
 */
public
interface Checksum {
    /**
     * Updates the current checksum with the specified byte.
     */
    public void update(int b);

    /**
     * Updates the current checksum with the specified array of bytes.
     */
    public void update(byte[] b, int off, int len);

    /**
     * Returns the current checksum value.
     */
    public long getValue();

    /**
     * Resets the checksum to its initial value.
     */
    public void reset();
}
