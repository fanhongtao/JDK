/*
 * @(#)Segment.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package javax.swing.text;

/**
 * A segment of a character array representing a fragment
 * of text.  It should be treated as immutable even though
 * the array is directly accessable.  This gives fast access
 * to fragments of text without the overhead of copying
 * around characters.  This is effectively an unprotected
 * String.
 */
public class Segment {

    /**
     * This is the array containing the text of
     * interest.  This array should never be modified;
     * it is available only for efficiency.
     */
    public char[] array;

    /**
     * This is the offset into the array that
     * the desired text begins.
     */
    public int offset;

    /**
     * This is the number of array elements that
     * make up the text of interest.
     */
    public int count;

    /**
     * Creates a new segment.
     */
    public Segment() {
	array = null;
	offset = 0;
	count = 0;
    }

    /**
     * Creates a new segment referring to an existing array.
     *
     * @param array the array to refer to
     * @param offset the offset into the array
     * @param count the number of characters
     */
    public Segment(char[] array, int offset, int count) {
	this.array = array;
	this.offset = offset;
	this.count = count;
    }

    /**
     * Converts a segment into a String.
     *
     * @return the string
     */
    public String toString() {
	if (array != null) {
	    return new String(array, offset, count);
	}
	return new String();
    }

}


