/*
 * @(#)CollationKey.java	1.16 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
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
 * A <code>CollationKey</code> represents a <code>String</code> under the
 * rules of a specific <code>Collator</code> object. Comparing two
 * <code>CollationKey</code>s returns the relative order of the
 * <code>String</code>s they represent. Using <code>CollationKey</code>s
 * to compare <code>String</code>s is generally faster than using
 * <code>Collator.compare</code>. Thus, when the <code>String</code>s
 * must be compared multiple times, for example when sorting a list
 * of <code>String</code>s. It's more efficient to use <code>CollationKey</code>s.
 *
 * <p>
 * You can not create <code>CollationKey</code>s directly. Rather,
 * generate them by calling <code>Collator.getCollationKey</code>.
 * You can only compare <code>CollationKey</code>s generated from
 * the same <code>Collator</code> object.
 *
 * <p>
 * Generating a <code>CollationKey</code> for a <code>String</code>
 * involves examining the entire <code>String</code>
 * and converting it to series of bits that can be compared bitwise. This
 * allows fast comparisons once the keys are generated. The cost of generating
 * keys is recouped in faster comparisons when <code>String</code>s need
 * to be compared many times. On the other hand, the result of a comparison
 * is often determined by the first couple of characters of each <code>String</code>.
 * <code>Collator.compare</code> examines only as many characters as it needs which
 * allows it to be faster when doing single comparisons.
 * <p>
 * The following example shows how <code>CollationKey</code>s might be used
 * to sort a list of <code>String</code>s.
 * <blockquote>
 * <pre>
 * // Create an array of CollationKeys for the Strings to be sorted.
 * Collator myCollator = Collator.getInstance();
 * CollationKey[] keys = new CollationKey[3];
 * keys[0] = myCollator.getCollationKey("Tom");
 * keys[1] = myCollator.getCollationKey("Dick");
 * keys[2] = myCollator.getCollationKey("Harry");
 * sort( keys );
 * <br>
 * //...
 * <br>
 * // Inside body of sort routine, compare keys this way
 * if( keys[i].compareTo( keys[j] ) > 0 )
 *    // swap keys[i] and keys[j]
 * <br>
 * //...
 * <br>
 * // Finally, when we've returned from sort.
 * System.out.println( keys[0].getSourceString() );
 * System.out.println( keys[1].getSourceString() );
 * System.out.println( keys[2].getSourceString() );
 * </pre>
 * </blockquote>
 *
 * @see          Collator
 * @see          RuleBasedCollator
 * @version      1.16, 01/23/03
 * @author       Helena Shih
 */

public final class CollationKey implements Comparable {
    /**
     * Compare this CollationKey to the target CollationKey. The collation rules of the
     * Collator object which created these keys are applied. <strong>Note:</strong>
     * CollationKeys created by different Collators can not be compared.
     * @param target target CollationKey
     * @return Returns an integer value. Value is less than zero if this is less
     * than target, value is zero if this and target are equal and value is greater than
     * zero if this is greater than target.
     * @see java.text.Collator#compare
     */
    public int compareTo(CollationKey target)
    {
        int result = key.compareTo(target.key);
        if (result <= Collator.LESS)
            return Collator.LESS;
        else if (result >= Collator.GREATER)
            return Collator.GREATER;
        return Collator.EQUAL;
    }

    /**
     * Compares this CollationKey with the specified Object for order.  Returns
     * a negative integer, zero, or a positive integer as this CollationKey
     * is less than, equal to, or greater than the given Object.
     * 
     * @param   o the Object to be compared.
     * @return  a negative integer, zero, or a positive integer as this
     *		Collation Key is less than, equal to, or greater than the given
     *		Object. 
     * @exception ClassCastException the specified Object is not a
     *		  CollationKey.
     * @see   Comparable
     * @since 1.2
     */
    public int compareTo(Object o) {
 	return compareTo((CollationKey)o);
    }

    /**
     * Compare this CollationKey and the target CollationKey for equality.
     * The collation rules of the Collator object which created these keys are applied.
     * <strong>Note:</strong> CollationKeys created by different Collators can not be
     * compared.
     * @param target the CollationKey to compare to.
     * @return Returns true if two objects are equal, false otherwise.
     */
    public boolean equals(Object target) {
        if (this == target) return true;
        if (target == null || !getClass().equals(target.getClass())) {
            return false;
        }
        CollationKey other = (CollationKey)target;
        return key.equals(other.key);
    }

    /**
     * Creates a hash code for this CollationKey. The hash value is calculated on the
     * key itself, not the String from which the key was created.  Thus
     * if x and y are CollationKeys, then x.hashCode(x) == y.hashCode() if
     * x.equals(y) is true.  This allows language-sensitive comparison in a hash table.
     * See the CollatinKey class description for an example.
     * @return the hash value based on the string's collation order.
     */
    public int hashCode() {
        return (key.hashCode());
    }


    /**
     * Returns the String that this CollationKey represents.
     */
    public String getSourceString() {
        return source;
    }


    /**
     * Converts the CollationKey to a sequence of bits. If two CollationKeys
     * could be legitimately compared, then one could compare the byte arrays
     * for each of those keys to obtain the same result.  Byte arrays are
     * organized most significant byte first.
     */
    public byte[] toByteArray() {

        char[] src = key.toCharArray();
        byte[] dest = new byte[ 2*src.length ];
        int j = 0;
        for( int i=0; i<src.length; i++ ) {
            dest[j++] = (byte)(src[i] >>> 8);
            dest[j++] = (byte)(src[i] & 0x00ff);
        }
        return dest;
    }

    /**
     * A CollationKey can only be generated by Collator objects.
     */
    CollationKey(String source, String key) {
        this.source = source;
        this.key = key;
    }

    private String source = null;
    private String key = null;
}



