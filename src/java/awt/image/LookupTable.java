/*
 * @(#)LookupTable.java	1.17 98/07/27
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.awt.image;


/**
 * This abstract class defines a lookup table object.  ByteLookupTable
 * and ShortLookupTable are subclasses, which
 * contain byte and short data, respectively.  A lookup table
 * contains data arrays for one or more bands (or components) of an image
 * (for example, separate arrays for R, G, and B),
 * and it contains an offset which will be subtracted from the
 * input values before indexing into the arrays.  This allows an array
 * smaller than the native data size to be provided for a
 * constrained input.  If there is only one array in the lookup
 * table, it will be applied to all bands.  All arrays must be the
 * same size.
 * 
 * @see ByteLookupTable
 * @see ShortLookupTable
 * @see LookupOp
 * @version 10 Feb 1997
 */
public abstract class LookupTable extends Object{

    /**
     * Constants
     */  
 
    int  numComponents;
    int  offset;
    int  numEntries;

    /**
     * Constructs a new LookupTable from the number of components and an offset
     * into the lookup table.
     */
    protected LookupTable(int offset, int numComponents) {
        if (offset < 0) {
            throw new
                IllegalArgumentException("Offset must be greater than 0");
        }
        if (numComponents < 1) {
            throw new IllegalArgumentException("Number of components must "+
                                               " be at least 1");
        }
        this.numComponents = numComponents;
	this.offset = offset;
    }

    /**
     * Returns the number of components in the lookup table.
     */
    public int getNumComponents() {
        return numComponents;
    }

    /**
     * Returns the offset.  
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Returns an int array of components for one pixel. Source and
     * destination may be equal.  The dest array is returned.  If dest
     * is null, a new array will be allocated.
     */
    public abstract int[] lookupPixel(int[] src, int[] dest);
    
}
