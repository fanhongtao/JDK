/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001, 2002 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xerces" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2001, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package com.sun.org.apache.xerces.internal.util;


/**
 * This class is an unsynchronized hash table primary used for String
 * to Object mapping.
 * <p>
 * The hash code uses the same algorithm as SymbolTable class.
 * 
 * @author Elena Litani
 * @version $Id: SymbolHash.java,v 1.7 2003/05/08 20:12:00 elena Exp $
 */
public class SymbolHash {

    //
    // Constants
    //

    /** Default table size. */
    protected int fTableSize = 101;

    //
    // Data
    //

    /** Buckets. */
    protected Entry[] fBuckets; 

    /** Number of elements. */
    protected int fNum = 0;

    //
    // Constructors
    //

    /** Constructs a key table with the default size. */
    public SymbolHash() {
        fBuckets = new Entry[fTableSize];
    }

    /**
     * Constructs a key table with a given size.
     * 
     * @param size  the size of the key table.
     */
    public SymbolHash(int size) {
        fTableSize = size;
        fBuckets = new Entry[fTableSize];
    }

    //
    // Public methods
    //

    /**
     * Adds the key/value mapping to the key table. If the key already exists, 
     * the previous value associated with this key is overwritten by the new
     * value.
     * 
     * @param key
     * @param value 
     */
    public void put(Object key, Object value) {
        int bucket = (key.hashCode() & 0x7FFFFFFF) % fTableSize;
        Entry entry = search(key, bucket);

        // replace old value
        if (entry != null) {
            entry.value = value;
        }
        // create new entry
        else {
            entry = new Entry(key, value, fBuckets[bucket]);
            fBuckets[bucket] = entry;
            fNum++;
        }
    }

    /**
     * Get the value associated with the given key.
     * 
     * @param key
     * @return the value associated with the given key.
     */
    public Object get(Object key) {
        int bucket = (key.hashCode() & 0x7FFFFFFF) % fTableSize;
        Entry entry = search(key, bucket);
        if (entry != null) {
            return entry.value;
        }
        return null;
    }

    /**
     * Get the number of key/value pairs stored in this table.
     * 
     * @return the number of key/value pairs stored in this table.
     */
    public int getLength() {
        return fNum;
    }
    
    /**
     * Add all values to the given array. The array must have enough entry.
     * 
     * @param elements  the array to store the elements
     * @param from      where to start store element in the array
     * @return          number of elements copied to the array
     */
    public int getValues(Object[] elements, int from) {
        for (int i=0, j=0; i<fTableSize && j<fNum; i++) {
            for (Entry entry = fBuckets[i]; entry != null; entry = entry.next) {
                elements[from+j] = entry.value;
                j++;
            }
        }
        return fNum;
    }
    
    /**
     * Make a clone of this object.
     */
    public SymbolHash makeClone() {
        SymbolHash newTable = new SymbolHash(fTableSize);
        newTable.fNum = fNum;
        for (int i = 0; i < fTableSize; i++) {
            if (fBuckets[i] != null)
                newTable.fBuckets[i] = fBuckets[i].makeClone();
        }
        return newTable;
    }
    
    /**
     * Remove all key/value assocaition. This tries to save a bit of GC'ing
     * by at least keeping the fBuckets array around.
     */
    public void clear() {
        for (int i=0; i<fTableSize; i++) {
            fBuckets[i] = null;
        }
        fNum = 0;
    } // clear():  void

    protected Entry search(Object key, int bucket) {
        // search for identical key
        for (Entry entry = fBuckets[bucket]; entry != null; entry = entry.next) {
            if (key.equals(entry.key))
                return entry;
        }
        return null;
    }
    
    //
    // Classes
    //

    /**
     * This class is a key table entry. Each entry acts as a node
     * in a linked list.
     */
    protected static final class Entry {
        // key/value
        public Object key;
        public Object value;
        /** The next entry. */
        public Entry next;

        public Entry() {
            key = null;
            value = null;
            next = null;
        }

        public Entry(Object key, Object value, Entry next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
        
        public Entry makeClone() {
            Entry entry = new Entry();
            entry.key = key;
            entry.value = value;
            if (next != null)
                entry.next = next.makeClone();
            return entry;
        }
    } // entry

} // class SymbolHash

