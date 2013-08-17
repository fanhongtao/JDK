/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * (C) Copyright Taligent, Inc. 1996 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - All Rights Reserved
 *
 *   The original version of this source code and documentation is copyrighted
 * and owned by Taligent, Inc., a wholly-owned subsidiary of IBM. These
 * materials are provided under terms of a License Agreement between Taligent
 * and Sun. This technology is protected by multiple US and International
 * patents. This notice and attribution to Taligent may not be removed.
 *   Taligent is a registered trademark of Taligent, Inc.
 *
 */

package java.text;

/**
 * class CompactATypeArray : use only on primitive data types
 * Provides a compact way to store information that is indexed by Unicode
 * values, such as character properties, types, keyboard values, etc.This
 * is very useful when you have a block of Unicode data that contains
 * significant values while the rest of the Unicode data is unused in the
 * application or when you have a lot of redundance, such as where all 21,000
 * Han ideographs have the same value.  However, lookup is much faster than a
 * hash table.
 * A compact array of any primitive data type serves two purposes:
 * <UL type = round>
 *     <LI>Fast access of the indexed values.
 *     <LI>Smaller memory footprint.
 * </UL>
 * A compact array is composed of a index array and value array.  The index
 * array contains the indicies of Unicode characters to the value array.
 *
 * @see        CompactByteArray
 * @see        CompactIntArray
 * @see        CompactShortArray
 * @see        CompactStringArray
 * @version    1.16 02/06/02
 * @author     Helena Shih
 */
final class CompactCharArray implements Cloneable{

    /**
     * The total number of Unicode characters.
     */
    public static  final int UNICODECOUNT =65536;

    /**
     * Default constructor for CompactCharArray, the default value of the
     * compact array is '\u0000'.
     */
    public CompactCharArray()
    {
        this('\u0000');
    }

    /**
     * Contructor for CompactCharArray.
     * @param defaultValue the default value of the compact array.
     */
    public CompactCharArray(char defaultValue)
    {
        int i;
        values = new char[UNICODECOUNT];
        indices = new short[INDEXCOUNT];
        hashes = new int[INDEXCOUNT];
        for (i = 0; i < UNICODECOUNT; ++i) {
            values[i] = defaultValue;
        }
        for (i = 0; i < INDEXCOUNT; ++i) {
            indices[i] = (short)(i<<BLOCKSHIFT);
            hashes[i] = 0;
        }
        isCompact = false;
    }

    /**
     * Constructor for CompactCharArray.
     * @param indexArray the indicies of the compact array.
     * @param newValues the values of the compact array.
     * @exception IllegalArgumentException If the index is out of range.
     */
    public CompactCharArray(short indexArray[], char newValues[])
    {
        int i;
        if (indexArray.length != INDEXCOUNT)
            throw new IllegalArgumentException("Index out of bounds.");
        for (i = 0; i < INDEXCOUNT; ++i) {
            short index = indexArray[i];
            if ((index < 0) || (index >= newValues.length+BLOCKCOUNT))
                throw new IllegalArgumentException("Index out of bounds.");
        }
        indices = indexArray;
        values = newValues;
        isCompact = true;
    }

    /**
     * Get the mapped value of a Unicode character.
     * @param index the character to get the mapped value with
     * @return the mapped value of the given character
     */
   public char elementAt(char index) // parameterized on short
    {
        return (values[(indices[index >> BLOCKSHIFT] & 0xFFFF)
                       + (index & BLOCKMASK)]);
    }

    /**
     * Set a new value for a Unicode character.
     * Set automatically expands the array if it is compacted.
     * @param index the character to set the mapped value with
     * @param value the new mapped value
     */
    public void setElementAt(char index, char value)
    {
        if (isCompact)
            expand();
        values[(int)index] = value;
        touchBlock(index >> BLOCKSHIFT, value);
    }

    /**
     * Set new values for a range of Unicode character.
     * @param start the starting offset of the range
     * @param end the endding offset of the range
     * @param value the new mapped value
     */
    public void setElementAt(char start, char end, char value)
    {
        int i;
        if (isCompact) {
            expand();
        }
        for (i = start; i <= end; ++i) {
            values[i] = value;
            touchBlock(i >> BLOCKSHIFT, value);
        }
    }

    /**
      *Compact the array.
      */
    public void compact()
    {
        if (!isCompact) {
            int limitCompacted = 0;
            int iBlockStart = 0;
            short iUntouched = -1;

            for (int i = 0; i < indices.length; ++i, iBlockStart += BLOCKCOUNT) {
                indices[i] = -1;
                boolean touched = blockTouched(i);
                if (!touched && iUntouched != -1) {
                    // If no values in this block were set, we can just set its
                    // index to be the same as some other block with no values
                    // set, assuming we've seen one yet.
                    indices[i] = iUntouched;
                } else {
                    int jBlockStart = 0;
                    int j = 0;
                    for (j = 0; j < limitCompacted;
                            ++j, jBlockStart += BLOCKCOUNT) {
                        if (hashes[i] == hashes[j] && 
                                arrayRegionMatches(values, iBlockStart,
                                values, jBlockStart, BLOCKCOUNT)) {
                            indices[i] = (short)jBlockStart;
                        }
                    }
                    if (indices[i] == -1) {
                        // we didn't match, so copy & update
                        System.arraycopy(values, iBlockStart,
                            values, jBlockStart, BLOCKCOUNT);
                        indices[i] = (short)jBlockStart;
                        hashes[j] = hashes[i];
                        ++limitCompacted;

                        if (!touched) {
                            // If this is the first untouched block we've seen,
                            // remember its index.
                            iUntouched = (short)jBlockStart;
                        }
                    }
                }
            }
            // we are done compacting, so now make the array shorter
            int newSize = limitCompacted*BLOCKCOUNT;
            char[] result = new char[newSize];
            System.arraycopy(values, 0, result, 0, newSize);
            values = result;
            isCompact = true;
            hashes = null;
        }
    }

    /**
     * Convenience utility to compare two arrays of doubles.
     * @param len the length to compare.
     * The start indices and start+len must be valid.
     */
    final static boolean arrayRegionMatches(char[] source, int sourceStart,
                                            char[] target, int targetStart,
                                            int len)
    {
        int sourceEnd = sourceStart + len;
        int delta = targetStart - sourceStart;
        for (int i = sourceStart; i < sourceEnd; i++) {
            if (source[i] != target[i + delta])
            return false;
        }
        return true;
    }

    /**
     * Remember that a specified block was "touched", i.e. had a value set.
     * Untouched blocks can be skipped when compacting the array
     */
    private final void touchBlock(int i, int value) {
        hashes[i] = (hashes[i] + (value<<1)) | 1;
    }

    /**
     * Query whether a specified block was "touched", i.e. had a value set.
     * Untouched blocks can be skipped when compacting the array
     */
    private final boolean blockTouched(int i) {
        return hashes[i] != 0;
    }
     
    /** For internal use only.  Do not modify the result, the behavior of
      * modified results are undefined.
      */
    public short getIndexArray()[]
    {
        return indices;
    }
    /** For internal use only.  Do not modify the result, the behavior of
      * modified results are undefined.
      */
    public char getStringArray()[]
    {
        return values;
    }
    /**
     * Overrides Cloneable
     */
    public Object clone()
    {
        try {
            CompactCharArray other = (CompactCharArray) super.clone();
            other.values = (char[])values.clone();
            other.indices = (short[])indices.clone();
            if (hashes != null) other.hashes = (int[])hashes.clone();
            return other;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
    /**
     * Compares the equality of two compact array objects.
     * @param obj the compact array object to be compared with this.
     * @return true if the current compact array object is the same
     * as the compact array object obj; false otherwise.
     */
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj)                      // quick check
            return true;
        if (getClass() != obj.getClass())         // same class?
            return false;
        CompactCharArray other = (CompactCharArray) obj;
        for (int i = 0; i < UNICODECOUNT; i++) {
            // could be sped up later
            if (elementAt((char)i) != other.elementAt((char)i))
                return false;
        }
        return true; // we made it through the guantlet.
    }
    /**
     * Generates the hash code for the compact array object
     */

    public int hashCode() {
        int result = 0;
        int increment = Math.min(3, values.length/16);
        for (int i = 0; i < values.length; i+= increment) {
            result = result * 37 + values[i];
        }
        return result;
    }

    // --------------------------------------------------------------
    // private
    // --------------------------------------------------------------
    /**
      * Expanding takes the array back to a 65536 element array.
      */
    private void expand()
    {
        int i;
        if (isCompact) {
            char[]  tempArray;
            tempArray = new char[UNICODECOUNT];
            hashes = new int[INDEXCOUNT];
            for (i = 0; i < UNICODECOUNT; ++i) {
                char value = elementAt((char)i);
                tempArray[i] = value;
                touchBlock(i >> BLOCKSHIFT, value);
            }
            for (i = 0; i < INDEXCOUNT; ++i) {
                indices[i] = (short)(i<<BLOCKSHIFT);
            }
            values = null;
            values = tempArray;
            isCompact = false;
        }
    }

    private char getArrayValue(int n)
    {
        return values[n];
    }

    private short getIndexArrayValue(int n)
    {
        return indices[n];
    }

    private static  final int BLOCKSHIFT =7;
    private static  final int BLOCKCOUNT =(1<<BLOCKSHIFT);
    private static  final int INDEXSHIFT =(16-BLOCKSHIFT);
    private static  final int INDEXCOUNT =(1<<INDEXSHIFT);
    private static  final int BLOCKMASK = BLOCKCOUNT - 1;

    private char[] values;  // char -> short (char parameterized short)
    private short indices[];
    private int[] hashes;
    private boolean isCompact;
};
