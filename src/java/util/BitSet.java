/*
 * @(#)BitSet.java	1.27 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.util;

/**
 * A set of bits. The set automatically grows as more bits are needed. 
 *
 * @version 	1.27, 07/01/98
 * @author Arthur van Hoff
 */
public final class BitSet implements Cloneable, java.io.Serializable {
    private final static int BITS_PER_UNIT = 6;
    private final static int MASK = (1<<BITS_PER_UNIT)-1;
    private long bits[];

    /**
     * Convert bitIndex to a subscript into the bits[] array.
     */
    private static int subscript(int bitIndex) {
	return bitIndex >> BITS_PER_UNIT;
    }
    /**
     * Convert a subscript into the bits[] array to a (maximum) bitIndex.
     */
    private static int bitIndex(int subscript) {
	return (subscript << BITS_PER_UNIT) + MASK;
    }

    private static boolean debugging = (System.getProperty("debug") != null);

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = 7997698588986878753L;

    /**
     * Creates an empty set.
     */
    public BitSet() {
	this(1 << BITS_PER_UNIT);
    }

    /**
     * Creates an empty set with the specified size.
     * @param nbits the size of the set
     */
    public BitSet(int nbits) {
	/* nbits can't be negative; size 0 is OK */
	if (nbits < 0) {
	    throw new NegativeArraySizeException(Integer.toString(nbits));
	}
	/* On wraparound, truncate size; almost certain to o-flo memory. */
	if (nbits + MASK < 0) {
	    nbits = Integer.MAX_VALUE - MASK;
	}
	/* subscript(nbits + MASK) is the length of the array needed to hold nbits */
	bits = new long[subscript(nbits + MASK)];
    }

    /**
     * Ensures that the BitSet can hold at least an nth bit.
     * This cannot leave the bits array at length 0.
     * @param	nth	the 0-origin number of the bit to ensure is there.
     */
    private void ensureCapacity(int nth) {
	/* Doesn't need to be synchronized because it's an internal method. */
	int required = subscript(nth) + 1;	/* +1 to get length, not index */
	if (required > bits.length) {
	    /* Ask for larger of doubled size or required size */
	    int request = Math.max(2 * bits.length, required);
	    long newBits[] = new long[request];
	    System.arraycopy(bits, 0, newBits, 0, bits.length);
	    bits = newBits;
	}
    }

    /**
     * Sets a bit.
     * @param bit the bit to be set
     */
    public void set(int bit) {
	if (bit < 0) {
	    throw new IndexOutOfBoundsException(Integer.toString(bit));
	}
	synchronized (this) {
	    ensureCapacity(bit);
	    bits[subscript(bit)] |= (1L << (bit & MASK));
	}
    }

    /**
     * Clears a bit.
     * @param bit the bit to be cleared
     */
    public void clear(int bit) {
	if (bit < 0) {
	    throw new IndexOutOfBoundsException(Integer.toString(bit));
	}
	synchronized (this) {
	    ensureCapacity(bit);
	    bits[subscript(bit)] &= ~(1L << (bit & MASK));
	}
    }

    /**
     * Gets a bit.
     * @param bit the bit to be gotten
     */
    public boolean get(int bit) {
	if (bit < 0) {
	    throw new IndexOutOfBoundsException(Integer.toString(bit));
	}
	boolean result = false;
	synchronized (this) {
	    int n = subscript(bit);		/* always positive */
	    if (n < bits.length) {
		result = ((bits[n] & (1L << (bit & MASK))) != 0);
	    }
	}
	return result;
    }

    /**
     * Logically ANDs this bit set with the specified set of bits.
     * @param set the bit set to be ANDed with
     */
    public void and(BitSet set) {
	/*
	 * Need to synchronize  both this and set.
	 * This might lead to deadlock if one thread grabs them in one order
	 * while another thread grabs them the other order.
	 * Use a trick from Doug Lea's book on concurrency,
	 * somewhat complicated because BitSet overrides hashCode().
	 */
	if (this == set) {
	    return;
	}
	BitSet first = this;
	BitSet second = set;
	if (System.identityHashCode(first) > System.identityHashCode(second)) {
	    first = set;
	    second = this;
	}
	synchronized (first) {
	    synchronized (second) {
		int bitsLength = bits.length;
		int setLength = set.bits.length;
		int n = Math.min(bitsLength, setLength);
		for (int i = n ; i-- > 0 ; ) {
		    bits[i] &= set.bits[i];
		}
		for (; n < bitsLength ; n++) {
		    bits[n] = 0;
		}
	    }
	}
    }

    /**
     * Logically ORs this bit set with the specified set of bits.
     * @param set the bit set to be ORed with
     */
    public void or(BitSet set) {
	if (this == set) {
	    return;
	}
	/* See the note about synchronization in and(), above. */
	BitSet first = this;
	BitSet second = set;
	if (System.identityHashCode(first) > System.identityHashCode(second)) {
	    first = set;
	    second = this;
	}
	synchronized (first) {
	    synchronized (second) {
		int setLength = set.bits.length;
		if (setLength > 0) {
		    ensureCapacity(bitIndex(setLength-1));
		}
		for (int i = setLength; i-- > 0 ;) {
		    bits[i] |= set.bits[i];
		}
	    }
	}
    }

    /**
     * Logically XORs this bit set with the specified set of bits.
     * @param set the bit set to be XORed with
     */
    public void xor(BitSet set) {
	/* See the note about synchronization in and(), above. */
	BitSet first = this;
	BitSet second = set;
	if (System.identityHashCode(first) > System.identityHashCode(second)) {
	    first = set;
	    second = this;
	}
	synchronized (first) {
	    synchronized (second) {
		int setLength = set.bits.length;
		if (setLength > 0) {
		    ensureCapacity(bitIndex(setLength-1));
		}
		for (int i = setLength; i-- > 0 ;) {
		    bits[i] ^= set.bits[i];
		}
	    }
	}
    }

    /**
     * Gets the hashcode.
     */
    public int hashCode() {
	long h = 1234;
	synchronized (this) {
	    for (int i = bits.length; --i >= 0; ) {
		h ^= bits[i] * (i + 1);
	    }
	}
	return (int)((h >> 32) ^ h);
    }
    
    /**
     * Calculates and returns the set's size in bits.
     * The maximum element in the set is the size - 1st element.
     */
    public int size() {
	/* This doesn't need to be synchronized, since it just reads a field. */
	return bits.length << BITS_PER_UNIT;
    }

    /**
     * Compares this object against the specified object.
     * @param obj the object to compare with
     * @return true if the objects are the same; false otherwise.
     */
    public boolean equals(Object obj) {
	if ((obj != null) && (obj instanceof BitSet)) {
	    if (this == obj) {
		return true;
	    }
	    BitSet set = (BitSet) obj;
	    /* See the note about synchronization in and(), above. */
	    BitSet first = this;
	    BitSet second = set;
	    if (System.identityHashCode(first) > System.identityHashCode(second)) {
		first = set;
		second = this;
	    }
	    synchronized (first) {
		synchronized (second) {
		    int bitsLength = bits.length;
		    int setLength = set.bits.length;
		    int n = Math.min(bitsLength, setLength);
		    for (int i = n ; i-- > 0 ;) {
			if (bits[i] != set.bits[i]) {
			    return false;
			}
		    }
		    if (bitsLength > n) {
			for (int i = bitsLength ; i-- > n ;) {
			    if (bits[i] != 0) {
				return false;
			    }
			}
		    } else if (setLength > n) {
			for (int i = setLength ; i-- > n ;) {
			    if (set.bits[i] != 0) {
				return false;
			    }
			}
		    }
		}
	    }
	    return true;
	}
	return false;
    }

    /**
     * Clones the BitSet.
     */
    public Object clone() {
	BitSet result = null;
	synchronized (this) {
	    try {
		result = (BitSet) super.clone();
	    } catch (CloneNotSupportedException e) {
		// this shouldn't happen, since we are Cloneable
		throw new InternalError();
	    }
	    result.bits = new long[bits.length];
	    System.arraycopy(bits, 0, result.bits, 0, result.bits.length);
	}
	return result;
    }

    /**
     * Converts the BitSet to a String.
     */
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	boolean needSeparator = false;
	buffer.append('{');
	synchronized (this) {
	    int limit = size();
	    for (int i = 0 ; i < limit ; i++) {
		if (get(i)) {
		    if (needSeparator) {
			buffer.append(", ");
		    } else {
			needSeparator = true;
		    }
		    buffer.append(i);
		}
	    }
	}
	buffer.append('}');
	return buffer.toString();
    }
}

