/*
 * @(#)Checksum.java	1.10 98/09/21
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
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
